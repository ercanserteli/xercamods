package xerca.xercamusic.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.item.Items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xerca.xercamusic.client.MusicClientTests.check;

/**
 * Client gametest for the {@code /musicexport} and {@code /musicimport} commands, porting that step of
 * {@code testItemsInCreative}. Unlike the server-side export/import unit tests (which call
 * {@code doExport}/{@code doImport} directly), this drives the full client round trip: the player
 * sends the chat command, the server bounces a packet back to the client, and the client performs the
 * file IO and re-import.
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class MusicCommandsClientTest implements FabricClientGameTest {
    private static final String EXPORT_NAME = "clienttest_export";
    private static final String EXPORT_FILE = "music_sheets/" + EXPORT_NAME + ".sheet";

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            UUID id = UUID.randomUUID();
            int version = 1;
            int length = 8;
            List<NoteEvent> notes = new ArrayList<>();
            notes.add(new NoteEvent((byte) 64, (short) 0, (byte) 100, (byte) 1));

            // Seed the music data on both sides and give the player a matching signed sheet to export.
            context.runOnClient(client -> MusicManagerClient.setMusicData(id, version, notes, null));
            singleplayer.getServer().runOnServer(server -> {
                MusicManager.setMusicData(id, version, notes, null, server);
                ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                player.setItemSlot(EquipmentSlot.MAINHAND, signedSheet(id, version, length));
            });
            context.waitFor(client -> client.player != null
                    && client.player.getMainHandItem().is(Items.MUSIC_SHEET)
                    && client.player.getMainHandItem().get(Items.SHEET_ID) != null);

            // Export: the client sends the command, the server bounces it back, the client writes the file.
            context.runOnClient(client -> {
                File exportFile = new File(EXPORT_FILE);
                if (exportFile.exists() && !exportFile.delete()) {
                    throw new IllegalStateException("Could not remove stale music export");
                }
            });
            sendCommand(context, "musicexport " + EXPORT_NAME);
            check(pollClient(context, client -> new File(EXPORT_FILE).exists()),
                    "Expected /musicexport to write " + EXPORT_FILE);

            // Import: clear the (creative) player's inventory, then re-import the exported file.
            singleplayer.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                player.setGameMode(GameType.CREATIVE);
                player.getInventory().clearContent();
            });
            context.waitTicks(2);
            sendCommand(context, "musicimport " + EXPORT_NAME);
            check(pollServer(context, singleplayer, MusicCommandsClientTest::hasImportedSheet),
                    "Expected /musicimport to give the player an imported music sheet");
        }
    }

    private static ItemStack signedSheet(UUID id, int version, int length) {
        ItemStack stack = new ItemStack(Items.MUSIC_SHEET);
        stack.set(Items.SHEET_ID, id);
        stack.set(Items.SHEET_VERSION, version);
        stack.set(Items.SHEET_GENERATION, 1);
        stack.set(Items.SHEET_LENGTH, length);
        stack.set(Items.SHEET_BPS, (byte) 8);
        stack.set(Items.SHEET_VOLUME, 1.0f);
        stack.set(Items.SHEET_TITLE, "Song");
        stack.set(Items.SHEET_AUTHOR, "Tester");
        return stack;
    }

    private static boolean hasImportedSheet(ServerPlayer player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(Items.MUSIC_SHEET) && stack.get(Items.SHEET_ID) != null) {
                return true;
            }
        }
        return false;
    }

    private static void sendCommand(ClientGameTestContext context, String command) {
        context.runOnClient(client -> client.player.connection.sendCommand(command));
        context.waitTicks(2);
    }

    private static boolean pollClient(ClientGameTestContext context, java.util.function.Predicate<net.minecraft.client.Minecraft> predicate) {
        for (int i = 0; i < 20; i++) {
            if (context.computeOnClient(predicate::test)) {
                return true;
            }
            context.waitTicks(5);
        }
        return false;
    }

    private static boolean pollServer(ClientGameTestContext context, TestSingleplayerContext singleplayer,
                                      java.util.function.Predicate<ServerPlayer> predicate) {
        for (int i = 0; i < 20; i++) {
            boolean satisfied = singleplayer.getServer().computeOnServer(server ->
                    predicate.test(server.getPlayerList().getPlayers().getFirst()));
            if (satisfied) {
                return true;
            }
            context.waitTicks(5);
        }
        return false;
    }
}
