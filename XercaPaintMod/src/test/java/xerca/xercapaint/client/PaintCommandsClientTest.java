package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static xerca.xercapaint.client.PaintClientTests.check;

/**
 * Client gametest for the {@code /paintexport} and {@code /paintimport} commands, porting that step of
 * {@code testDrawOnCanvas}. Both drive the full client round trip: the player sends the chat command,
 * the server bounces a packet back to the client, the client performs the file IO, and (for import) the
 * client sends the parsed painting back to the server, which puts a fresh canvas in the inventory.
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class PaintCommandsClientTest implements FabricClientGameTest {
    private static final int BLACK = 0xFF1D1D21;
    private static final int WHITE = 0xFFF9FFFE;
    private static final int SMALL_PIXELS = 16 * 16;
    private static final String EXPORT_NAME = "clienttest_paint";
    private static final String EXPORT_FILE = "paintings/" + EXPORT_NAME + ".paint";

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            // Give the (server) player a painted canvas and wait for the synced components on the client,
            // since the export runs client-side from the held stack.
            singleplayer.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                player.setItemSlot(EquipmentSlot.MAINHAND, paintedCanvas());
            });
            context.waitFor(client -> client.player != null
                    && client.player.getMainHandItem().is(Items.ITEM_CANVAS)
                    && client.player.getMainHandItem().get(Items.CANVAS_PIXELS) != null
                    && client.player.getMainHandItem().get(Items.CANVAS_ID) != null);

            // Export: the client sends the command, the server bounces it back, the client writes the file.
            context.runOnClient(client -> {
                File exportFile = new File(EXPORT_FILE);
                if (exportFile.exists() && !exportFile.delete()) {
                    throw new IllegalStateException("Could not remove stale painting export");
                }
            });
            sendCommand(context, "paintexport " + EXPORT_NAME);
            check(pollClient(context, client -> new File(EXPORT_FILE).exists()),
                    "Expected /paintexport to write " + EXPORT_FILE);

            // Import: clear the (now creative) player's inventory, then re-import the exported file.
            singleplayer.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                player.setGameMode(GameType.CREATIVE);
                player.getInventory().clearContent();
            });
            context.waitTicks(2);
            sendCommand(context, "paintimport " + EXPORT_NAME);
            check(pollServer(context, singleplayer, PaintCommandsClientTest::hasImportedCanvas),
                    "Expected /paintimport to give the player an imported canvas");
        }
    }

    private static ItemStack paintedCanvas() {
        ItemStack canvas = new ItemStack(Items.ITEM_CANVAS);
        List<Integer> pixels = new ArrayList<>(Collections.nCopies(SMALL_PIXELS, WHITE));
        pixels.set(0, BLACK);
        canvas.set(Items.CANVAS_PIXELS, pixels);
        canvas.set(Items.CANVAS_ID, "clienttest_canvas");
        canvas.set(Items.CANVAS_VERSION, 1);
        return canvas;
    }

    private static boolean hasImportedCanvas(ServerPlayer player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.getItem() instanceof ItemCanvas && stack.get(Items.CANVAS_PIXELS) != null) {
                return true;
            }
        }
        return false;
    }

    private static void sendCommand(ClientGameTestContext context, String command) {
        context.runOnClient(client -> client.player.connection.sendCommand(command));
        context.waitTicks(2);
    }

    private static boolean pollClient(ClientGameTestContext context, Predicate<Minecraft> predicate) {
        for (int i = 0; i < 20; i++) {
            if (context.computeOnClient(predicate::test)) {
                return true;
            }
            context.waitTicks(5);
        }
        return false;
    }

    private static boolean pollServer(ClientGameTestContext context, TestSingleplayerContext singleplayer,
                                      Predicate<ServerPlayer> predicate) {
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
