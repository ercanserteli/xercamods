package xerca.xercamusic.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

import static xerca.xercamusic.client.MusicClientTests.*;

/**
 * Client gametest for the music sheet's playback and signing controls ({@link GuiMusicSheet}),
 * porting those steps of {@code testItemsInCreative}: the preview toggle, the record toggle, the
 * preview-instrument taken from the offhand, locking the previous instrument, and the sign flow
 * (entering a title and finalizing).
 */
public final class MusicSheetPlaybackClientTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            // Hold an instrument in the offhand so the sheet adopts it as the preview instrument.
            int fluteIndex = Items.INSTRUMENTS.indexOf((IItemInstrument) Items.FLUTE);
            context.runOnClient(client ->
                    client.player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.FLUTE)));
            context.setScreen(() -> new GuiMusicSheet(Minecraft.getInstance().player,
                    new ItemStack(Items.MUSIC_SHEET), Component.literal("test")));
            context.waitForScreen(GuiMusicSheet.class);
            context.waitTicks(2);

            check(intField(context, s -> s.previewInstrument) == fluteIndex,
                    "Expected the sheet to preview with the offhand instrument");

            // Place a few notes so preview has something to play.
            for (int offset = 0; offset < 3; offset++) {
                pressNoteKey(context, offset, 0);
                releaseNoteKey(context, offset, 0);
            }

            // Preview toggles on and off.
            context.runOnClient(client -> ((GuiMusicSheet) client.screen).editCursor = 0);
            context.runOnClient(client -> ((GuiMusicSheet) client.screen).previewButton());
            check(boolField(context, s -> s.previewing), "Expected preview to start");
            context.runOnClient(client -> ((GuiMusicSheet) client.screen).previewButton());
            check(!boolField(context, s -> s.previewing), "Expected a second press to stop preview");

            // Record toggles on (pre-recording) and off.
            context.runOnClient(client -> ((GuiMusicSheet) client.screen).recordButton());
            check(boolField(context, s -> s.preRecording), "Expected the record button to enter pre-recording");
            context.runOnClient(client -> ((GuiMusicSheet) client.screen).recordButton());
            check(!boolField(context, s -> s.preRecording) && !boolField(context, s -> s.recording),
                    "Expected a second record press to stop recording");

            // Lock the previous instrument.
            pressButton(context, "buttonLockPrevIns");
            check(MusicClientTests.<Boolean>readField(sheet(context), GuiMusicSheet.class, "prevInsLocked"),
                    "Expected the lock button to lock the previous instrument");

            // Sign flow: enter signing mode, type a title, then finalize (which closes the screen).
            pressButton(context, "buttonSign");
            check(boolField(context, s -> s.gettingSigned), "Expected the sign button to enter signing mode");
            context.getInput().typeChars("Song");
            context.waitTick();
            check("Song".equals(strField(context, s -> s.noteTitle)), "Expected typed characters to build the title");
            pressButton(context, "buttonFinalize");
            context.waitForScreen(null);
        }
    }

    private static void pressButton(ClientGameTestContext context, String fieldName) {
        context.runOnClient(client -> {
            Button button = readField(client.screen, GuiMusicSheet.class, fieldName);
            button.onPress(new net.minecraft.client.input.MouseButtonInfo(0, 0));
        });
        context.waitTick();
    }

    private static Object sheet(ClientGameTestContext context) {
        return context.computeOnClient(client -> client.screen);
    }

    private static int intField(ClientGameTestContext context, java.util.function.ToIntFunction<GuiMusicSheet> getter) {
        return context.computeOnClient(client -> getter.applyAsInt((GuiMusicSheet) client.screen));
    }

    private static boolean boolField(ClientGameTestContext context, java.util.function.Predicate<GuiMusicSheet> getter) {
        return context.computeOnClient(client -> getter.test((GuiMusicSheet) client.screen));
    }

    private static String strField(ClientGameTestContext context, java.util.function.Function<GuiMusicSheet, String> getter) {
        return context.computeOnClient(client -> getter.apply((GuiMusicSheet) client.screen));
    }
}
