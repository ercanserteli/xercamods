package xerca.xercamusic.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.MusicClipboard;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.item.Items;

import static xerca.xercamusic.client.MusicClientTests.*;

/**
 * Client gametest for editing an unsigned music sheet ({@link GuiMusicSheet}), porting the sheet
 * editing steps of {@code testItemsInCreative}: placing notes with the keyboard and the note grid,
 * the brush volume, tempo and measure-length buttons, the octave scroll wheel, the help panel, the
 * copy / paste / select-all / undo shortcuts, and the shift-modified crescendo marker and
 * rectangular selection. The modifier-gated mouse actions are simulatable because
 * {@code SheetInputHandler.isShiftHeld()} reads {@code InputConstants.isKeyDown}, which the
 * client-gametest harness intercepts for {@link net.fabricmc.fabric.api.client.gametest.v1.TestInput#holdShift()}.
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class MusicSheetEditClientTest implements FabricClientGameTest {
    // Note-grid layout constants, mirroring GuiMusicSheet (3 screen px per beat).
    private static final int NOTE_REGION_LEFT = 44;
    private static final int NOTE_REGION_TOP = 39;
    private static final int PX_PER_BEAT = 3;
    private static final int MOUSE_NOTE_BEAT = 10;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            context.setScreen(() -> new GuiMusicSheet(Minecraft.getInstance().player,
                    new ItemStack(Items.MUSIC_SHEET), Component.literal("test")));
            context.waitForScreen(GuiMusicSheet.class);
            context.waitTicks(2);
            check(!boolField(context, s -> s.isSigned), "Fresh sheet should be unsigned");

            // Keyboard note placement: three keys place three notes at consecutive beats.
            context.runOnClient(client -> GuiMusicSheet.brushVolume = 0.5f);
            for (int offset = 0; offset < 3; offset++) {
                pressNoteKey(context, offset, 0);
                releaseNoteKey(context, offset, 0);
            }
            check(noteCount(context) == 3, "Expected three notes after three keyboard key presses");
            check(intField(context, s -> (int) s.lengthBeats) > 0, "Expected sheet length to grow with notes");

            // Brush volume: a newly placed note takes its volume from the brush.
            context.runOnClient(client -> GuiMusicSheet.brushVolume = 0.75f);
            pressNoteKey(context, 4, 0);
            releaseNoteKey(context, 4, 0);
            byte expectedVolume = (byte) (127.f * 0.75f);
            check(hasNoteWithVolume(context, expectedVolume),
                    "Expected the placed note to use the brush volume " + expectedVolume);

            // Note-grid mouse placement: a left click in the grid adds a note at that beat.
            int notesBeforeMouse = noteCount(context);
            int[] layout = layout(context);
            double sx = layout[0] + NOTE_REGION_LEFT + MOUSE_NOTE_BEAT * PX_PER_BEAT;
            double sy = layout[1] + NOTE_REGION_TOP + 30;
            clickAt(context, sx, sy, 0);
            check(noteCount(context) == notesBeforeMouse + 1, "Expected a mouse click in the grid to add a note");
            check(hasNoteAtTime(context, MOUSE_NOTE_BEAT), "Expected the mouse-placed note to land on beat " + MOUSE_NOTE_BEAT);

            // Tempo buttons change the beats-per-second.
            int bpsBefore = bps(context);
            pressButton(context, "bpmUp", 3);
            check(bps(context) == bpsBefore + 3, "Expected tempo up to raise bps by three");
            pressButton(context, "bpmDown", 1);
            check(bps(context) == bpsBefore + 2, "Expected tempo down to lower bps by one");

            // Measure-length (highlight interval) buttons.
            int hlBefore = highlightInterval(context);
            pressButton(context, "hlUp", 3);
            check(highlightInterval(context) == hlBefore + 3, "Expected measure length up to raise the highlight interval");
            pressButton(context, "hlDown", 1);
            check(highlightInterval(context) == hlBefore + 2, "Expected measure length down to lower the highlight interval");

            // Octave scroll wheel moves the visible octave window.
            int octaveBefore = intField(context, s -> s.currentOctavePos);
            scrollVertically(context, 1);
            check(intField(context, s -> s.currentOctavePos) == octaveBefore + 1,
                    "Expected scrolling up to raise the octave position");
            scrollVertically(context, -1);
            check(intField(context, s -> s.currentOctavePos) == octaveBefore,
                    "Expected scrolling down to restore the octave position");

            // Help panel toggles with H.
            pressKey(context, GLFW.GLFW_KEY_H, 0, 0);
            check(boolField(context, s -> s.helpOn), "Expected H to open the help panel");
            pressKey(context, GLFW.GLFW_KEY_H, 0, 0);
            check(!boolField(context, s -> s.helpOn), "Expected H to close the help panel");

            // Copy / paste / undo.
            pressKey(context, GLFW.GLFW_KEY_A, 0, GLFW.GLFW_MOD_CONTROL);
            pressKey(context, GLFW.GLFW_KEY_C, 0, GLFW.GLFW_MOD_CONTROL);
            MusicClipboard.ParsedMusic copied = context.computeOnClient(client -> {
                String clip = GLFW.glfwGetClipboardString(client.getWindow().handle());
                return clip == null ? null : MusicClipboard.decode(clip);
            });
            check(copied != null && !copied.notes().isEmpty(), "Expected Ctrl+C to place the selection on the clipboard");

            pressKey(context, GLFW.GLFW_KEY_RIGHT, 0, 0);
            int notesBeforePaste = noteCount(context);
            pressKey(context, GLFW.GLFW_KEY_V, 0, GLFW.GLFW_MOD_CONTROL);
            check(noteCount(context) > notesBeforePaste, "Expected Ctrl+V to paste the copied notes");

            pressKey(context, GLFW.GLFW_KEY_Z, 0, GLFW.GLFW_MOD_CONTROL);
            check(noteCount(context) == notesBeforePaste, "Expected Ctrl+Z to undo the paste");

            // Select all + backspace deletes, then undo restores.
            int notesBeforeDelete = noteCount(context);
            pressKey(context, GLFW.GLFW_KEY_A, 0, GLFW.GLFW_MOD_CONTROL);
            pressKey(context, GLFW.GLFW_KEY_BACKSPACE, 0, 0);
            check(noteCount(context) < notesBeforeDelete, "Expected select-all + backspace to delete notes");
            pressKey(context, GLFW.GLFW_KEY_Z, 0, GLFW.GLFW_MOD_CONTROL);
            check(noteCount(context) == notesBeforeDelete, "Expected undo to restore the deleted notes");

            // Shift + left drag creates a crescendo volume marker. The note grid ignores drags until
            // the GUI has ticked at least 10 times.
            context.waitTicks(12);
            int[] grid = layout(context);
            double markerY = grid[1] + NOTE_REGION_TOP + 30;
            int markersBefore = markerCount(context);
            context.getInput().holdShift();
            mouseDown(context, grid[0] + NOTE_REGION_LEFT + 20 * PX_PER_BEAT, markerY, 0);
            mouseDragTo(context, grid[0] + NOTE_REGION_LEFT + 25 * PX_PER_BEAT, markerY, 0, 15, 0);
            mouseUp(context, grid[0] + NOTE_REGION_LEFT + 25 * PX_PER_BEAT, markerY, 0);
            context.getInput().releaseShift();
            check(markerCount(context) == markersBefore + 1, "Expected shift+drag to add a crescendo volume marker");
            // Dismiss the marker edit box so it doesn't intercept the next clicks.
            context.runOnClient(client -> ((GuiMusicSheet) client.screen).updateButtons());

            // Shift + right drag starts a rectangular (note-bounded) selection.
            context.getInput().holdShift();
            mouseDown(context, grid[0] + NOTE_REGION_LEFT + 5 * PX_PER_BEAT, grid[1] + NOTE_REGION_TOP + 20, 1);
            mouseDragTo(context, grid[0] + NOTE_REGION_LEFT + 15 * PX_PER_BEAT, grid[1] + NOTE_REGION_TOP + 50, 1, 30, 30);
            mouseUp(context, grid[0] + NOTE_REGION_LEFT + 15 * PX_PER_BEAT, grid[1] + NOTE_REGION_TOP + 50, 1);
            context.getInput().releaseShift();
            check(boolField(context, s -> s.rectSelection), "Expected shift+right-drag to start a rectangular selection");
            check(intField(context, s -> s.editCursorEnd - s.editCursor) > 0,
                    "Expected the rectangular selection to span multiple beats");

            // Signing overlay: the prompt text must actually render over the white box
            // (regression: color-0 text became invisible with the 1.21.6 GUI rework).
            pressButton(context, "buttonSign", 1);
            check(boolField(context, s -> s.gettingSigned), "Expected the sign button to enter signing mode");
            context.waitTicks(2);
            int[] box = signingBoxFramebufferRect(context);
            assertSigningTextRendered(context.takeScreenshot("music_sheet_signing_live"), box);
            pressButton(context, "buttonCancel", 1);
            check(!boolField(context, s -> s.gettingSigned), "Expected cancel to leave signing mode");

            context.setScreen(() -> null);
        }
    }

    // The drawSigning box (noteImageLeftX+100, noteImageY+40, 120x100) in framebuffer pixels, inset to skip edges.
    private static int[] signingBoxFramebufferRect(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            GuiMusicSheet sheet = (GuiMusicSheet) client.screen;
            int scale = client.getWindow().getGuiScale();
            return new int[]{(sheet.noteImageLeftX + 102) * scale, (sheet.noteImageY + 42) * scale, 116 * scale, 96 * scale};
        });
    }

    private static void assertSigningTextRendered(java.nio.file.Path screenshot, int[] rect) {
        java.awt.image.BufferedImage image;
        try {
            image = javax.imageio.ImageIO.read(screenshot.toFile());
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        int dark = 0;
        int light = 0;
        for (int y = rect[1]; y < rect[1] + rect[3]; y++) {
            for (int x = rect[0]; x < rect[0] + rect[2]; x++) {
                int rgb = image.getRGB(x, y);
                int luminance = ((rgb >> 16 & 0xFF) + (rgb >> 8 & 0xFF) + (rgb & 0xFF)) / 3;
                if (luminance < 100) {
                    dark++;
                } else if (luminance > 220) {
                    light++;
                }
            }
        }
        int area = rect[2] * rect[3];
        check(light > area / 2, "Expected the signing box to render as a light panel, got " + light + "/" + area + " light pixels");
        check(dark >= 40, "Expected the signing prompt text to render dark pixels over the box, got " + dark);
    }

    private static void pressButton(ClientGameTestContext context, String fieldName, int times) {
        for (int i = 0; i < times; i++) {
            context.runOnClient(client -> {
                Button button = readField(client.screen, GuiMusicSheet.class, fieldName);
                button.onPress(new net.minecraft.client.input.MouseButtonInfo(0, 0));
            });
            context.waitTick();
        }
    }

    private static int noteCount(ClientGameTestContext context) {
        return context.computeOnClient(client -> ((GuiMusicSheet) client.screen).notes.size());
    }

    private static int markerCount(ClientGameTestContext context) {
        return context.computeOnClient(client -> ((GuiMusicSheet) client.screen).volumeMarkers.size());
    }

    private static boolean hasNoteAtTime(ClientGameTestContext context, int time) {
        return context.computeOnClient(client -> {
            for (NoteEvent note : ((GuiMusicSheet) client.screen).notes) {
                if (note.time == time) {
                    return true;
                }
            }
            return false;
        });
    }

    private static boolean hasNoteWithVolume(ClientGameTestContext context, byte volume) {
        return context.computeOnClient(client -> {
            for (NoteEvent note : ((GuiMusicSheet) client.screen).notes) {
                if (note.volume == volume) {
                    return true;
                }
            }
            return false;
        });
    }

    private static int bps(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                (int) MusicClientTests.<Byte>readField(client.screen, GuiMusicSheet.class, "bps"));
    }

    private static int highlightInterval(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                (int) MusicClientTests.<Byte>readField(client.screen, GuiMusicSheet.class, "highlightInterval"));
    }

    private static int[] layout(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            GuiMusicSheet sheet = (GuiMusicSheet) client.screen;
            return new int[]{sheet.noteImageLeftX, sheet.noteImageY};
        });
    }

    private static int intField(ClientGameTestContext context, java.util.function.ToIntFunction<GuiMusicSheet> getter) {
        return context.computeOnClient(client -> getter.applyAsInt((GuiMusicSheet) client.screen));
    }

    private static boolean boolField(ClientGameTestContext context, java.util.function.Predicate<GuiMusicSheet> getter) {
        return context.computeOnClient(client -> getter.test((GuiMusicSheet) client.screen));
    }
}
