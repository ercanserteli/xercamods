package xerca.xercamusic.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.tests.SsimComparisonAlgorithm;

import static xerca.xercamusic.client.MusicClientTests.check;

/**
 * Client gametest for the instrument keyboard GUI ({@link GuiInstrument}), porting the instrument
 * half of {@code testItemsInCreative}: opening the GUI, playing notes from the keyboard, and moving
 * the octave with the on-screen buttons (with clamping).
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class InstrumentGuiClientTest implements FabricClientGameTest {
    private static final double SSIM_THRESHOLD = 0.95;
    private static final String GOLDEN = "instrument_gui_guitar";
    // GuiInstrument hardcodes note-key scancodes 16..27 (the QWERTY row), independent of glfw layout.
    private static final int FIRST_NOTE_SCANCODE = 16;
    // GuiInstrument.decreaseOctave allows the keyboard octave down to -3 regardless of the instrument.
    private static final int MIN_KEYBOARD_OCTAVE = -3;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            IItemInstrument instrument = (IItemInstrument) Items.GUITAR;
            context.setScreen(() -> new GuiInstrument(Minecraft.getInstance().player, instrument,
                    Component.literal("test"), null));
            context.waitForScreen(GuiInstrument.class);
            context.waitTicks(2);

            int maxOctave = instrument.getMaxOctave();

            // Octave up clamps at the instrument's max octave.
            for (int i = 0; i < maxOctave + MIN_KEYBOARD_OCTAVE * -1 + 5; i++) {
                context.clickScreenButton("note.upButton");
            }
            check(keyboardOctave(context) == maxOctave,
                    "Expected keyboard octave to clamp up at the instrument max " + maxOctave
                            + " but was " + keyboardOctave(context));

            // A note key presses the corresponding key on the keyboard, then releasing it lets go.
            MusicClientTests.pressKey(context, GLFW.GLFW_KEY_UNKNOWN, FIRST_NOTE_SCANCODE, 0);
            check(pushedNoteCount(context) == 1, "Expected exactly one note to be held after a key press");
            MusicClientTests.releaseKey(context, GLFW.GLFW_KEY_UNKNOWN, FIRST_NOTE_SCANCODE, 0);
            check(pushedNoteCount(context) == 0, "Expected the note to be released after the key is released");

            // Octave down clamps at -3.
            for (int i = 0; i < maxOctave - MIN_KEYBOARD_OCTAVE + 5; i++) {
                context.clickScreenButton("note.downButton");
            }
            check(keyboardOctave(context) == MIN_KEYBOARD_OCTAVE,
                    "Expected keyboard octave to clamp down at " + MIN_KEYBOARD_OCTAVE
                            + " but was " + keyboardOctave(context));

            // Stabilize the world behind the (transparent-background) GUI, then snapshot it.
            singleplayer.getServer().runCommand("time set 1000");
            singleplayer.getServer().runCommand("tp @p 0.0 200.0 0.0 0.0 90.0");
            context.waitTicks(5);
            context.assertScreenshotEquals(TestScreenshotComparisonOptions.of(GOLDEN)
                    .withAlgorithm(SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD))
                    .save());

            context.setScreen(() -> null);
        }
    }

    private static int keyboardOctave(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                MusicClientTests.<Integer>readField(null, GuiInstrument.class, "currentKeyboardOctave"));
    }

    private static int pushedNoteCount(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            boolean[] states = MusicClientTests.readField(client.gui.screen(), GuiInstrument.class, "buttonPushStates");
            int count = 0;
            for (boolean pushed : states) {
                if (pushed) {
                    count++;
                }
            }
            return count;
        });
    }
}
