package xerca.xercamusic.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringUtil;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.MusicClipboard;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.item.IItemInstrument;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

/**
 * Handles all user input (keyboard and mouse) for the music sheet GUI.
 * Extracted from {@link GuiMusicSheet} to improve readability and separation of concerns.
 */
class SheetInputHandler {
    private final GuiMusicSheet gui;

    SheetInputHandler(GuiMusicSheet gui) {
        this.gui = gui;
    }

    // --------- Mouse Input ----------

    boolean handleMouseClicked(double dmouseX, double dmouseY, int mouseButton) {
        if (gui.helpOn) {
            int mx = (int) Math.round(dmouseX);
            int my = (int) Math.round(dmouseY);
            if (!gui.handleHelpClick(mx, my)) {
                gui.helpOn = false;
                gui.updateButtons();
            }
            return true;
        }

        if (handleTopLevelMouseClicked(dmouseX, dmouseY, mouseButton)) {
            return true;
        }

        if (gui.callSuperMouseClicked(dmouseX, dmouseY, mouseButton)) {
            gui.setDragging(true);
            return true;
        }

        int mouseX = (int) Math.round(dmouseX);
        int mouseY = (int) Math.round(dmouseY);

        boolean viewingSelfSigned = gui.isSigned && gui.selfSigned;
        boolean composing = !gui.isSigned && !gui.gettingSigned;

        if (!gui.gettingSigned && mouseButton == 1) {
            // Right click: cancel/finish glissando mode, or set cursor
            if (gui.glissandoMode) {
                finishGlissando();
                return true;
            }
            int mx = mouseX - gui.noteImageLeftX;
            int my = mouseY - gui.noteImageY;
            if (validClick(mx, my)) {
                gui.editCursor = ((mx - GuiMusicSheet.NOTE_REGION_LEFT) / 3) + gui.sliderPosition;
                gui.editCursorEnd = gui.editCursor;
                gui.selectionStart = gui.editCursor;
                if (isShiftHeld()) {
                    byte note = pixelToNote(my);
                    gui.rectSelection = true;
                    gui.rectSelectNoteStart = note;
                    gui.rectSelectNoteTop = note;
                    gui.rectSelectNoteBottom = note;
                } else {
                    gui.rectSelection = false;
                }
            }
        }

        if (viewingSelfSigned && mouseButton == 0) {
            gui.editCursorEnd = gui.editCursor;
        }

        if (composing) {
            int mx = mouseX - gui.noteImageLeftX;
            int my = mouseY - gui.noteImageY;
            if (validClick(mx, my)) {
                int nrx = mx - GuiMusicSheet.NOTE_REGION_LEFT;
                int nry = my - GuiMusicSheet.NOTE_REGION_TOP;

                int time = (nrx / 3) + gui.sliderPosition;
                int note = 47 - (nry / 3) + IItemInstrument.MIN_NOTE + gui.currentOctavePos * 12;
                if (mouseButton == 0) {
                    // Check for Shift (crescendo) or Ctrl (decrescendo) modifiers
                    boolean shiftHeld = isShiftHeld();
                    boolean ctrlHeld = isCtrlHeld();

                    if (shiftHeld || ctrlHeld) {
                        // Start creating a volume marker
                        gui.creatingCrescendo = shiftHeld;
                        gui.markerStartTime = (short) time;
                        gui.markerStartNote = (byte) note;
                        byte startVol = gui.creatingCrescendo ? (byte) 32 : (byte) 96;
                        byte endVol = gui.creatingCrescendo ? (byte) 96 : (byte) 32;
                        VolumeMarker marker = new VolumeMarker(gui.markerStartTime, (short) (gui.markerStartTime + 1),
                                startVol, endVol, gui.markerStartNote, gui.markerStartNote);
                        gui.currentlyAddedMarker = isMarkerPlacementAvailable(marker) ? marker : null;
                    } else if (gui.glissandoMode) {
                        // Glissando placement mode
                        if (gui.glissandoSourceNote == null) {
                            // First click: find source note
                            int idx = findNote((byte) note, (short) time);
                            if (idx >= 0) {
                                NoteEvent clicked = gui.notes.get(idx);
                                if (clicked.hasGlissando()) {
                                    // Already has glissando: clear it
                                    pushUndo();
                                    clicked.setGlissando(false, (byte) 0);
                                    gui.dirtyFlag.hasNotes = true;
                                    gui.glissandoMode = false;
                                    gui.updateButtons();
                                } else {
                                    gui.glissandoSourceNote = clicked;
                                    gui.glissandoPendingWaypoints = new ArrayList<>();
                                    gui.glissandoPendingPositions = new ArrayList<>();
                                }
                            }
                        } else {
                            // Subsequent click: add or replace a waypoint snapped to a beat within the note
                            byte interval = (byte) (note - gui.glissandoSourceNote.note);
                            int beatIndex = getGlissandoBeatIndex(nrx);
                            List<Byte> pendingPositions = requirePendingPositions();
                            List<Byte> pendingWaypoints = requirePendingWaypoints();
                            int existingIndex = pendingPositions.indexOf((byte) beatIndex);
                            if (existingIndex >= 0) {
                                pendingWaypoints.set(existingIndex, interval);
                            } else {
                                int insertIndex = 0;
                                while (insertIndex < pendingPositions.size()
                                        && (pendingPositions.get(insertIndex) & 0xFF) < beatIndex) {
                                    insertIndex++;
                                }
                                pendingPositions.add(insertIndex, (byte) beatIndex);
                                pendingWaypoints.add(insertIndex, interval);
                            }
                            if (gui.glissandoSourceNote.length > 0
                                    && beatIndex >= (gui.glissandoSourceNote.length & 0xFF)) {
                                finishGlissando();
                            }
                        }
                    } else {
                        // Normal note adding
                        addNote((byte) note, (short) time);
                        gui.dirtyFlag.hasNotes = true;
                        gui.dirtyFlag.hasLength = true;
                    }

                    gui.editCursorEnd = gui.editCursor;
                } else if (mouseButton == 2) {
                    int i = findNote((byte) note, (short) time);
                    if (i >= 0) {
                        NoteEvent event = gui.notes.get(i);
                        GuiMusicSheet.requireWidget(gui.noteEditBox, "noteEditBox").appear(mouseX, mouseY, event);
                    } else {
                        // Check if clicking on a volume marker
                        VolumeMarker clickedMarker = findVolumeMarker((byte) note, (short) time);
                        if (clickedMarker != null) {
                            GuiMusicSheet.requireWidget(gui.markerEditBox, "markerEditBox").appear(mouseX, mouseY, clickedMarker);
                        }
                    }
                }
            } else {
                // Test current octave clicks
                for (int i = 0; i < 4; i++) {
                    final int x = GuiMusicSheet.NOTE_REGION_LEFT - 24;
                    final int y = GuiMusicSheet.NOTE_REGION_BOTTOM - 18 - i * 36;
                    if (mx >= x - 10 && mx <= x + 10 && my >= y - 4 && my <= y + 12) {
                        GuiMusicSheet.setCurrentOctave(gui.currentOctavePos + i);
                        gui.midiHandler.setCurrentOctave(GuiMusicSheet.getCurrentOctave());
                        if (gui.recording) {
                            gui.recordingNotes.clear();
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    boolean handleMouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        if (handleTopLevelMouseDragged(posX, posY, mouseButton, deltaX, deltaY)) {
            return true;
        }

        GuiEventListener focused = gui.getFocused();
        if (focused != null && gui.isDragging()) {
            focused.mouseDragged(new MouseButtonEvent(posX, posY, new MouseButtonInfo(mouseButton, 0)), deltaX, deltaY);
            return true;
        }

        if (gui.tickCount < 10) {
            return gui.callSuperMouseDragged(posX, posY, mouseButton, deltaX, deltaY);
        }
        int mouseX = (int) Math.round(posX);
        int mouseY = (int) Math.round(posY);

        int mx = mouseX - gui.noteImageLeftX;
        int my = mouseY - gui.noteImageY;

        // if right button is pressed
        if (mouseButton == 1) {
            if (validClick(mx, my)) {
                int noteX = ((mx - GuiMusicSheet.NOTE_REGION_LEFT) / 3) + gui.sliderPosition;
                if (gui.selectionStart > noteX) {
                    gui.editCursor = noteX;
                } else {
                    gui.editCursorEnd = noteX;
                }
                if (gui.rectSelection) {
                    int nry = my - GuiMusicSheet.NOTE_REGION_TOP;
                    byte note = (byte) (47 - (nry / 3) + IItemInstrument.MIN_NOTE + gui.currentOctavePos * 12);
                    gui.rectSelectNoteTop = (byte) Math.max(gui.rectSelectNoteStart, note);
                    gui.rectSelectNoteBottom = (byte) Math.min(gui.rectSelectNoteStart, note);
                }
            }
        } else if (mouseButton == 0) {
            if (gui.currentlyAddedMarker != null && validClick(mx, my)) {
                // Update volume marker being created
                VolumeMarker marker = getVolumeMarker(mx, my);
                if (isMarkerPlacementAvailable(marker)) {
                    gui.currentlyAddedMarker = marker;
                }
            } else if (gui.currentlyAddedNote != null && validClick(mx, my)) {
                int time = ((mx - GuiMusicSheet.NOTE_REGION_LEFT) / 3) + gui.sliderPosition;
                if (gui.currentlyAddedNote.time < time && time - gui.currentlyAddedNote.time <= GuiMusicSheet.MAX_PLACED_NOTE_LENGTH) {
                    gui.currentlyAddedNote.length = (byte) (time - gui.currentlyAddedNote.time);
                }
            }
        }

        return true;
    }

    private VolumeMarker getVolumeMarker(int mx, int my) {
        assert gui.currentlyAddedMarker != null;

        int nrx = mx - GuiMusicSheet.NOTE_REGION_LEFT;
        int nry = my - GuiMusicSheet.NOTE_REGION_TOP;
        int time = (nrx / 3) + gui.sliderPosition;
        int note = 47 - (nry / 3) + IItemInstrument.MIN_NOTE + gui.currentOctavePos * 12;

        // Update marker bounds
        short newStartTime = (short) Math.min(gui.markerStartTime, time);
        short newEndTime = (short) Math.max(gui.markerStartTime + 1, time + 1);
        byte newLowNote = (byte) Math.min(gui.markerStartNote, note);
        byte newHighNote = (byte) Math.max(gui.markerStartNote, note);

        return new VolumeMarker(newStartTime, newEndTime, gui.currentlyAddedMarker.startVolume,
                gui.currentlyAddedMarker.endVolume, newLowNote, newHighNote);
    }

    boolean handleMouseReleased(double posX, double posY, int mouseButton) {
        gui.setDragging(false);
        if (handleTopLevelMouseReleased(posX, posY, mouseButton)) {
            return true;
        }

        if (mouseButton == 0) {
            finishAddingNote();
            finishAddingMarker((int) posX, (int) posY);
        }

        return true;
    }

    private boolean handleTopLevelMouseClicked(double mouseX, double mouseY, int mouseButton) {
        MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(mouseButton, 0));
        if (isActive(gui.markerEditBox)) {
            gui.markerEditBox.mouseClicked(event, false);
            return true;
        }
        if (isActive(gui.noteEditBox)) {
            gui.noteEditBox.mouseClicked(event, false);
            return true;
        }
        return false;
    }

    private boolean handleTopLevelMouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        MouseButtonEvent event = new MouseButtonEvent(posX, posY, new MouseButtonInfo(mouseButton, 0));
        if (isActive(gui.markerEditBox)) {
            gui.markerEditBox.mouseDragged(event, deltaX, deltaY);
            return true;
        }
        if (isActive(gui.noteEditBox)) {
            gui.noteEditBox.mouseDragged(event, deltaX, deltaY);
            return true;
        }
        return false;
    }

    private boolean handleTopLevelMouseReleased(double posX, double posY, int mouseButton) {
        MouseButtonEvent event = new MouseButtonEvent(posX, posY, new MouseButtonInfo(mouseButton, 0));
        if (isActive(gui.markerEditBox)) {
            gui.markerEditBox.mouseReleased(event);
            return true;
        }
        if (isActive(gui.noteEditBox)) {
            gui.noteEditBox.mouseReleased(event);
            return true;
        }
        return false;
    }

    private static boolean isActive(GuiMusicSheet.@Nullable NoteEditBox editBox) {
        return editBox != null && editBox.active && editBox.visible;
    }

    private static boolean isActive(GuiMusicSheet.@Nullable MarkerEditBox editBox) {
        return editBox != null && editBox.active && editBox.visible;
    }

    boolean handleMouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (gui.helpOn) {
            if (scrollY != 0) {
                gui.helpScrollOffset -= (int) (scrollY * 10);
            }
            return true;
        }
        if (isShiftHeld() && scrollY != 0.d) {
            // Shift+Scroll: horizontal scrolling
            int scrollAmount = 8;
            if (scrollY > 0) {
                gui.setSliderPos(gui.sliderPosition - scrollAmount);
            } else {
                gui.setSliderPos(gui.sliderPosition + scrollAmount);
            }
            return true;
        }
        // Also handle native horizontal scroll (scrollX) for mice with horizontal scroll wheels
        if (scrollX != 0.d) {
            int scrollAmount = 8;
            if (scrollX > 0) {
                gui.setSliderPos(gui.sliderPosition + scrollAmount);
            } else {
                gui.setSliderPos(gui.sliderPosition - scrollAmount);
            }
            return true;
        }
        if (scrollY != 0.d) {
            if (scrollY > 0) {
                Button octaveUp = GuiMusicSheet.requireWidget(gui.octaveUp, "octaveUp");
                octaveUp.playDownSound(Minecraft.getInstance().getSoundManager());
                octaveUp.onPress(new MouseButtonInfo(0, 0));
            } else if (scrollY < 0) {
                Button octaveDown = GuiMusicSheet.requireWidget(gui.octaveDown, "octaveDown");
                octaveDown.playDownSound(Minecraft.getInstance().getSoundManager());
                octaveDown.onPress(new MouseButtonInfo(0, 0));
            }
            return true;
        }
        return gui.callSuperMouseScrolled(x, y, scrollX, scrollY);
    }

    // --------- Keyboard Input ---------

    boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        // Intercept ESC to cancel glissando mode without closing the screen
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && gui.glissandoMode) {
            gui.glissandoMode = false;
            gui.glissandoSourceNote = null;
            gui.glissandoPendingWaypoints = null;
            gui.glissandoPendingPositions = null;
            gui.updateButtons();
            return true;
        }

        gui.setFocused(null);
        gui.callSuperKeyPressed(keyCode, scanCode, modifiers);

        // Copying when viewing self-signed
        boolean viewingSelfSigned = gui.isSigned && gui.selfSigned;
        if (viewingSelfSigned) {
            if (keyCode == GLFW.GLFW_KEY_C && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                encodeToClipboard();
            }
            if (keyCode == GLFW.GLFW_KEY_A && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                gui.editCursor = 0;
                gui.editCursorEnd = gui.lengthBeats - 1;
                gui.rectSelection = false;
            }
        }

        if (!gui.isSigned) {
            if (gui.gettingSigned) {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_BACKSPACE -> {
                        if (!gui.noteTitle.isEmpty()) {
                            gui.noteTitle = gui.noteTitle.substring(0, gui.noteTitle.length() - 1);
                            gui.updateButtons();
                        }
                    }
                    case GLFW.GLFW_KEY_ENTER -> {
                        if (!gui.noteTitle.isEmpty()) {
                            gui.dirtyFlag.hasSigned = true;
                            gui.dirtyFlag.hasTitle = true;
                            gui.isSigned = true;
                            Minecraft.getInstance().gui.setScreen(null);
                        }
                    }
                    default -> {
                        // do nothing
                    }
                }
                return true;
            } else {
                int x = gui.editCursor;
                boolean resetEditCursorEnd = true;
                switch (keyCode) {
                    case GLFW.GLFW_KEY_DELETE -> {
                        if (gui.lengthBeats == 0 || gui.lengthBeats <= x) break;
                        pushUndo();
                        gui.dirtyFlag.hasNotes = true;
                        gui.dirtyFlag.hasLength = true;
                        if (gui.editCursorEnd == x) {
                            delAtCursor(x);
                        } else {
                            deleteSelected();
                            gui.updateLength();
                        }
                    }
                    case GLFW.GLFW_KEY_BACKSPACE -> {
                        if (gui.editCursorEnd == x) {
                            if (x == 0) {
                                break;
                            }
                            if (gui.lengthBeats == 0 || gui.lengthBeats < x) {
                                addEditCursor(-1);
                            } else {
                                pushUndo();

                                gui.dirtyFlag.hasNotes = true;
                                gui.dirtyFlag.hasLength = true;
                                addEditCursor(-1);
                                delAtCursor(gui.editCursor);
                            }
                        } else {
                            pushUndo();

                            gui.dirtyFlag.hasNotes = true;
                            gui.dirtyFlag.hasLength = true;
                            deleteSelected();
                            gui.updateLength();
                        }
                    }
                    case GLFW.GLFW_KEY_SPACE -> putSpace(x - 1);
                    case GLFW.GLFW_KEY_RIGHT -> {
                        addEditCursor(1);
                        if (gui.editCursor > GuiMusicSheet.MAX_LENGTH_BEATS - 1)
                            setEditCursor(GuiMusicSheet.MAX_LENGTH_BEATS - 1);
                    }
                    case GLFW.GLFW_KEY_LEFT -> {
                        addEditCursor(-1);
                        if (gui.editCursor < 0) setEditCursor(0);
                    }
                    case GLFW.GLFW_KEY_ENTER -> {
                        if (gui.glissandoMode && gui.glissandoPendingWaypoints != null && !gui.glissandoPendingWaypoints.isEmpty()) {
                            finishGlissando();
                        } else {
                            gui.previewButton();
                        }
                    }
                    case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> gui.recordButton();
                    case GLFW.GLFW_KEY_C -> {
                        if ((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                            encodeToClipboard();
                        }
                    }
                    case GLFW.GLFW_KEY_V -> {
                        if ((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                            decodeFromClipboard((modifiers & GLFW.GLFW_MOD_SHIFT) != GLFW.GLFW_MOD_SHIFT);
                        }
                    }
                    case GLFW.GLFW_KEY_H -> gui.toggleHelp();
                    case GLFW.GLFW_KEY_G -> {
                        if (gui.glissandoMode) {
                            finishGlissando();
                        } else {
                            // Enter glissando placement mode
                            gui.glissandoMode = true;
                            gui.glissandoSourceNote = null;
                            gui.glissandoPendingWaypoints = null;
                            gui.glissandoPendingPositions = null;
                            gui.updateButtons();
                        }
                    }
                    case GLFW.GLFW_KEY_Z -> {
                        if ((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                            if (gui.noteEditBox != null && gui.noteEditBox.active) {
                                break;
                            }
                            // Exit glissando mode on undo so state stays consistent
                            if (gui.glissandoMode) {
                                gui.glissandoMode = false;
                                gui.glissandoSourceNote = null;
                                gui.glissandoPendingWaypoints = null;
                                gui.glissandoPendingPositions = null;
                                gui.updateButtons();
                            }
                            if (!gui.undoStack.isEmpty()) {
                                GuiMusicSheet.UndoState state = gui.undoStack.pop();
                                gui.notes = state.notes();
                                gui.volumeMarkers = state.volumeMarkers();
                                gui.updateLength(false);
                            }
                        }
                    }
                    case GLFW.GLFW_KEY_A -> {
                        if ((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                            gui.editCursor = 0;
                            gui.editCursorEnd = gui.lengthBeats - 1;
                            gui.rectSelection = false;
                            resetEditCursorEnd = false;
                        } else {
                            if (gui.editCursor == gui.editCursorEnd) {
                                GuiMusicSheet.setCurrentOctave(GuiMusicSheet.getCurrentOctave() - 1);
                                if (GuiMusicSheet.getCurrentOctave() < -2) {
                                    GuiMusicSheet.setCurrentOctave(-2);
                                }
                                gui.midiHandler.setCurrentOctave(GuiMusicSheet.getCurrentOctave());
                                if (gui.recording) {
                                    gui.recordingNotes.clear();
                                }
                            } else {
                                if (shiftSelectedOctave(-12)) resetEditCursorEnd = false;
                            }
                        }
                    }
                    case GLFW.GLFW_KEY_S -> {
                        if (gui.editCursor == gui.editCursorEnd) {
                            GuiMusicSheet.setCurrentOctave(GuiMusicSheet.getCurrentOctave() + 1);
                            if (GuiMusicSheet.getCurrentOctave() > 7) {
                                GuiMusicSheet.setCurrentOctave(7);
                            }
                            gui.midiHandler.setCurrentOctave(GuiMusicSheet.getCurrentOctave());
                            if (gui.recording) {
                                gui.recordingNotes.clear();
                            }
                        } else {
                            if (shiftSelectedOctave(12)) resetEditCursorEnd = false;
                        }
                    }
                    case GLFW.GLFW_KEY_D -> {
                        if (gui.editCursor != gui.editCursorEnd && shiftSelectedOctave(-1)) resetEditCursorEnd = false;
                    }
                    case GLFW.GLFW_KEY_F -> {
                        if (gui.editCursor != gui.editCursorEnd && shiftSelectedOctave(1)) resetEditCursorEnd = false;
                    }
                    case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> resetEditCursorEnd = false;
                    default -> {
                        int firstScanCode = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
                        int lastScanCode = firstScanCode + 11;
                        if (scanCode >= firstScanCode && scanCode <= lastScanCode && GuiMusicSheet.getCurrentOctave() >= 0) {
                            if (gui.recording) {
                                gui.startSound(IItemInstrument.noteToId((byte) ((scanCode - firstScanCode + IItemInstrument.MIN_NOTE) + 12 * GuiMusicSheet.getCurrentOctave())), (byte) 100);
                            } else {
                                putSpace(x - 1);
                                addNote((byte) ((scanCode - firstScanCode + IItemInstrument.MIN_NOTE) + 12 * GuiMusicSheet.getCurrentOctave()), (short) x, false);
                                finishAddingNote();
                            }
                        }
                    }
                }
                if (resetEditCursorEnd) {
                    gui.editCursorEnd = gui.editCursor;
                    gui.rectSelection = false;
                }
            }
        }
        return true;
    }

    boolean handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        gui.setFocused(null);
        gui.callSuperKeyReleased(keyCode, scanCode, modifiers);
        int firstScanCode = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
        int lastScanCode = firstScanCode + 11;
        if (scanCode >= firstScanCode && scanCode <= lastScanCode && GuiMusicSheet.getCurrentOctave() >= 0 && gui.recording) {
            gui.endSound(IItemInstrument.noteToId((byte) ((scanCode - firstScanCode + IItemInstrument.MIN_NOTE) + 12 * GuiMusicSheet.getCurrentOctave())));
        }
        return true;
    }

    boolean handleCharTyped(char typedChar) {
        gui.callSuperCharTyped(typedChar);

        if (!gui.isSigned) {
            if (gui.gettingSigned && gui.noteTitle.length() < 16 && StringUtil.isAllowedChatCharacter(typedChar)) {
                gui.noteTitle = gui.noteTitle + typedChar;
                gui.updateButtons();
            }
            return true;
        }
        return false;
    }

    // ----------- Editing Operations -------------

    private void putSpace(int x) {
        if (x == GuiMusicSheet.MAX_LENGTH_BEATS - 1) {
            return;
        }
        addEditCursor(1);
        if (gui.lengthBeats == 0 || gui.lengthBeats <= x) {
            return;
        }

        pushUndo();
        gui.dirtyFlag.hasNotes = true;
        gui.dirtyFlag.hasLength = true;
        for (int i = gui.notes.size() - 1; i >= 0; i--) {
            NoteEvent event = gui.notes.get(i);
            if (event.time > x) {
                event.time += (short) 1;
                if (event.time + event.length > GuiMusicSheet.MAX_LENGTH_BEATS) {
                    gui.notes.remove(i);
                }
            }
        }
        // Shift/expand volume markers
        for (VolumeMarker m : gui.volumeMarkers) {
            if (m.startTime > x) {
                m.startTime++;
                m.endTime++;
            } else if (m.endTime > x) {
                // Marker spans the insertion point: expand it
                m.endTime++;
            }
        }
        gui.updateLength();
    }

    private void delAtCursor(int x) {
        boolean doSort = false;
        for (int i = gui.notes.size() - 1; i >= 0; i--) {
            NoteEvent event = gui.notes.get(i);
            int start = event.time;
            int end = start + event.length;

            if (start > x) {
                event.time -= (short) 1;
                doSort = true;
            } else if (start < x && end > x) {
                event.length--;
            } else if (start == x) {
                if (event.length == 1) {
                    gui.notes.remove(i);
                } else {
                    event.length--;
                }
            }
        }
        // Shift/shrink volume markers
        for (int i = gui.volumeMarkers.size() - 1; i >= 0; i--) {
            VolumeMarker m = gui.volumeMarkers.get(i);
            if (m.startTime > x) {
                m.startTime--;
                m.endTime--;
            } else if (m.endTime > x) {
                m.endTime--;
                if (m.endTime - m.startTime < 2) {
                    gui.volumeMarkers.remove(i);
                }
            }
        }
        if (doSort) {
            NoteEvent.sortNotes(gui.notes);
        }
        gui.updateLength();
    }

    private void deleteSelected() {
        boolean doSort = false;
        int selectionEndExclusive = gui.editCursorEnd + 1;
        Iterator<NoteEvent> it = gui.notes.iterator();
        while (it.hasNext()) {
            NoteEvent event = it.next();
            if (event.time >= gui.editCursor && event.endTime() <= gui.editCursorEnd) {
                it.remove();
            } else if (event.time < gui.editCursor && event.endTime() >= gui.editCursor && event.endTime() <= gui.editCursorEnd) {
                event.length = (byte) (gui.editCursor - event.time);
            } else if (event.time >= gui.editCursor && event.time <= gui.editCursorEnd && event.endTime() > gui.editCursorEnd) {
                event.length = (byte) (event.endTime() - gui.editCursorEnd);
                event.time = (short) (gui.editCursor + 1);
                doSort = true;
            } else if (event.time < gui.editCursor && event.endTime() > gui.editCursorEnd) {
                // spans across the whole cut -> trim to before cut
                event.length = (byte) (gui.editCursor - event.time);
            } else if (event.time > gui.editCursorEnd) {
                // after cut -> shift left
                event.time -= (short) (gui.editCursorEnd - gui.editCursor + 1);
                doSort = true;
            }
        }
        // Handle volume markers for the deleted selection
        int selLen = gui.editCursorEnd - gui.editCursor + 1;
        for (int i = gui.volumeMarkers.size() - 1; i >= 0; i--) {
            VolumeMarker m = gui.volumeMarkers.get(i);
            if (m.startTime >= gui.editCursor && m.endTime <= selectionEndExclusive) {
                // Fully inside selection: remove
                gui.volumeMarkers.remove(i);
            } else if (m.startTime < gui.editCursor && m.endTime > selectionEndExclusive) {
                // Spans entire selection: shrink
                m.endTime -= (short) selLen;
                if (m.endTime - m.startTime < 2) gui.volumeMarkers.remove(i);
            } else if (m.startTime < gui.editCursor && m.endTime > gui.editCursor) {
                // Starts before, ends inside: trim end
                m.endTime = (short) gui.editCursor;
                if (m.endTime - m.startTime < 2) gui.volumeMarkers.remove(i);
            } else if (m.startTime >= gui.editCursor && m.startTime < selectionEndExclusive) {
                // Starts inside selection, ends after: keep the tail, shift to editCursor
                short origEnd = m.endTime;
                m.startTime = (short) gui.editCursor;
                m.endTime = (short) (gui.editCursor + origEnd - selectionEndExclusive);
                if (m.endTime - m.startTime < 2) gui.volumeMarkers.remove(i);
            } else if (m.startTime >= selectionEndExclusive) {
                // Entirely after selection: shift left
                m.startTime -= (short) selLen;
                m.endTime -= (short) selLen;
            }
        }
        if (doSort) {
            NoteEvent.sortNotes(gui.notes);
        }
    }

    /**
     * Shift the octave of all selected notes by the given amount.
     * @return true if any note was changed
     */
    private boolean shiftSelectedOctave(int semitones) {
        boolean changed = false;
        for (NoteEvent event : gui.notes) {
            if (event.endTime() >= gui.editCursor && event.time <= gui.editCursorEnd) {
                // If rectangular selection is active, only shift notes within the note range
                if (gui.rectSelection && (event.note < gui.rectSelectNoteBottom || event.note > gui.rectSelectNoteTop)) {
                    continue;
                }
                byte newNote = (byte) (event.note + semitones);
                int newId = IItemInstrument.noteToId(newNote);
                if (newId >= 0 && newId < IItemInstrument.MAX_NOTE - IItemInstrument.MIN_NOTE + 1) {
                    if (!changed) {
                        pushUndo();
                        gui.dirtyFlag.hasNotes = true;
                        gui.dirtyFlag.hasLength = true;
                        changed = true;
                    }
                    event.note = newNote;
                }
            }
        }
        // Shift volume marker note ranges to follow the shifted notes
        if (changed) {
            for (VolumeMarker m : gui.volumeMarkers) {
                // Marker overlaps the time selection
                if (m.startTime <= gui.editCursorEnd && m.endTime > gui.editCursor) {
                    // Only shift if the marker's note range overlaps the rect selection
                    if (gui.rectSelection && (m.highNote < gui.rectSelectNoteBottom - semitones || m.lowNote > gui.rectSelectNoteTop - semitones)) {
                        continue;
                    }
                    m.lowNote += (byte) semitones;
                    m.highNote += (byte) semitones;
                }
            }
        }
        // Update rectangular selection bounds to follow the shifted notes
        if (changed && gui.rectSelection) {
            gui.rectSelectNoteTop += (byte) semitones;
            gui.rectSelectNoteBottom += (byte) semitones;
            gui.rectSelectNoteStart += (byte) semitones;
        }
        return changed;
    }

    // ---------- Clipboard -------

    private void encodeToClipboard() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        ArrayList<NoteEvent> toBeCopied = new ArrayList<>();
        for (NoteEvent event : gui.notes) {
            if (event.time >= gui.editCursor && event.time <= gui.editCursorEnd && event.endTime() >= gui.editCursor && event.endTime() <= gui.editCursorEnd) {
                // For rectangular selection, also filter by note pitch
                if (gui.rectSelection && (event.note < gui.rectSelectNoteBottom || event.note > gui.rectSelectNoteTop)) {
                    continue;
                }
                toBeCopied.add(event);
            }
        }
        buffer.writeByte(MusicClipboard.COPY_BEGIN_BYTE);
        buffer.writeInt(gui.editCursorEnd - gui.editCursor);
        buffer.writeInt(toBeCopied.size());
        for (NoteEvent event : toBeCopied) {
            NoteEvent copy = new NoteEvent(event);
            copy.time -= (short) gui.editCursor;
            copy.encodeToBuffer(buffer);
        }

        // Collect volume markers that fall within the selection
        ArrayList<VolumeMarker> markersToCopy = new ArrayList<>();
        for (VolumeMarker marker : gui.volumeMarkers) {
            if (marker.startTime >= gui.editCursor && marker.endTime <= gui.editCursorEnd + 1) {
                markersToCopy.add(marker);
            }
        }
        buffer.writeInt(markersToCopy.size());
        for (VolumeMarker marker : markersToCopy) {
            VolumeMarker copy = new VolumeMarker(marker);
            copy.startTime = toSaturatedShort(copy.startTime - gui.editCursor);
            copy.endTime = toSaturatedShort(copy.endTime - gui.editCursor);
            copy.encodeToBuffer(buffer);
        }

        int index = buffer.writerIndex();
        byte[] bytes = new byte[index];
        buffer.getBytes(0, bytes);
        String encodeBytes = Base64.getEncoder().encodeToString(bytes);
        GLFW.glfwSetClipboardString(Minecraft.getInstance().getWindow().handle(), encodeBytes);

        gui.editCursorEnd = gui.editCursor;
    }

    private void decodeFromClipboard(boolean pushBack) {
        String encodedMusic = GLFW.glfwGetClipboardString(Minecraft.getInstance().getWindow().handle());
        if (encodedMusic != null && !encodedMusic.isEmpty()) {
            MusicClipboard.ParsedMusic parsedMusic = MusicClipboard.decode(encodedMusic);
            if (parsedMusic == null) {
                return;
            }
            int length = parsedMusic.length();
            List<NoteEvent> toBePasted = parsedMusic.notes();
            List<VolumeMarker> markersToPaste = parsedMusic.volumeMarkers();

            pushUndo();
            if (pushBack) {
                // Push back the existing future note events
                for (NoteEvent event : gui.notes) {
                    if (event.time >= gui.editCursor) {
                        event.time += (short) length;
                    }
                }
                // Push back the existing future volume markers
                for (VolumeMarker marker : gui.volumeMarkers) {
                    if (marker.startTime >= gui.editCursor) {
                        marker.startTime = toSaturatedShort(marker.startTime + length);
                        marker.endTime = toSaturatedShort(marker.endTime + length);
                    }
                }
            }

            for (NoteEvent event : toBePasted) {
                event.time += (short) gui.editCursor;
                gui.notes.add(event);
            }
            for (VolumeMarker marker : markersToPaste) {
                marker.startTime = toSaturatedShort(marker.startTime + gui.editCursor);
                marker.endTime = toSaturatedShort(marker.endTime + gui.editCursor);
                gui.volumeMarkers.add(marker);
            }

            NoteEvent.sortNotes(gui.notes);
            NoteEvent.removeDuplicates(gui.notes);

            gui.updateLength();
            gui.editCursor += length;
            gui.editCursorEnd = gui.editCursor;

            gui.dirtyFlag.hasNotes = true;
            gui.dirtyFlag.hasLength = true;
        }
    }

    private static short toSaturatedShort(int value) {
        if (value > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        if (value < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        return (short) value;
    }

    // ----------- Undo ------------

    private void pushUndo() {
        gui.pushUndo();
    }

    // ---------- Note Management ------------

    private void addNote(byte note, short time) {
        addNote(note, time, true);
    }

    private void addNote(byte note, short time, boolean doPushUndo) {
        if (doPushUndo) {
            pushUndo();
        }
        int i = findNote(note, time);
        if (i < 0) {
            addNote(note, time, (byte) (127.f * GuiMusicSheet.brushVolume));
        } else {
            gui.notes.remove(i);
        }
        gui.updateLength();
    }

    private void addNote(byte note, short time, byte volume) {
        NoteEvent newEvent = new NoteEvent(note, time, volume, (byte) 1);
        gui.currentlyAddedNote = newEvent;
        gui.insertNoteSorted(newEvent);
    }

    private void finishAddingNote() {
        if (gui.currentlyAddedNote == null) {
            return;
        }
        gui.playSound(gui.currentlyAddedNote, gui.previewInstrument);
        gui.currentlyAddedNote = null;
        gui.updateLength();
    }

    private void finishAddingMarker(int mouseX, int mouseY) {
        VolumeMarker marker = gui.currentlyAddedMarker;
        if (marker == null) {
            return;
        }
        // Only add if the marker has some meaningful size
        if (marker.isValid() && isMarkerPlacementAvailable(marker)) {
            pushUndo();
            // Add the marker to the list and show the edit box
            gui.volumeMarkers.add(marker);
            gui.dirtyFlag.hasNotes = true;  // Volume markers are saved with notes
            GuiMusicSheet.requireWidget(gui.markerEditBox, "markerEditBox").appear(mouseX, mouseY, marker);
        }
        gui.currentlyAddedMarker = null;
    }

    /**
     * Finish and apply pending multi-point glissando waypoints, then reset glissando state.
     */
    private void finishGlissando() {
        if (gui.glissandoPendingWaypoints != null && !gui.glissandoPendingWaypoints.isEmpty() && gui.glissandoSourceNote != null) {
            pushUndo();
            byte[] waypoints = new byte[gui.glissandoPendingWaypoints.size()];
            for (int i = 0; i < waypoints.length; i++) {
                waypoints[i] = gui.glissandoPendingWaypoints.get(i);
            }
            byte[] positions = null;
            if (gui.glissandoPendingPositions != null && gui.glissandoPendingPositions.size() == waypoints.length) {
                positions = new byte[gui.glissandoPendingPositions.size()];
                int noteLength = gui.glissandoSourceNote.length & 0xFF;
                for (int i = 0; i < positions.length; i++) {
                    int beatIndex = gui.glissandoPendingPositions.get(i) & 0xFF;
                    int posPct = Math.clamp(Math.round(beatIndex * 100.0f / noteLength), 1, 100);
                    positions[i] = (byte) posPct;
                }
            }
            gui.glissandoSourceNote.setGlissandoWaypoints(waypoints, positions);
            gui.dirtyFlag.hasNotes = true;
        }
        gui.glissandoMode = false;
        gui.glissandoSourceNote = null;
        gui.glissandoPendingWaypoints = null;
        gui.glissandoPendingPositions = null;
        gui.updateButtons();
    }

    private int findNote(byte note, short time) {
        for (int i = gui.notes.size() - 1; i >= 0; i--) {
            NoteEvent event = gui.notes.get(i);
            if ((event.time <= time && event.endTime() >= time) && event.note == note) {
                return i;
            }
        }
        return -1;
    }

    private @Nullable VolumeMarker findVolumeMarker(byte note, short time) {
        for (VolumeMarker marker : gui.volumeMarkers) {
            if (marker.affects(time, note)) {
                return marker;
            }
        }
        return null;
    }

    private boolean isMarkerPlacementAvailable(VolumeMarker candidate) {
        for (VolumeMarker marker : gui.volumeMarkers) {
            if (candidate.overlaps(marker)) {
                return false;
            }
        }
        return true;
    }

    private List<Byte> requirePendingWaypoints() {
        return GuiMusicSheet.requireWidget(gui.glissandoPendingWaypoints, "glissandoPendingWaypoints");
    }

    private List<Byte> requirePendingPositions() {
        return GuiMusicSheet.requireWidget(gui.glissandoPendingPositions, "glissandoPendingPositions");
    }

    private int getGlissandoBeatIndex(int noteRegionX) {
        if (gui.glissandoSourceNote == null || gui.glissandoSourceNote.length <= 0) {
            return 1;
        }
        float exactTime = noteRegionX / 3.0f + gui.sliderPosition;
        int relativeBeat = (int) Math.floor(exactTime - gui.glissandoSourceNote.time);
        int noteLength = gui.glissandoSourceNote.length & 0xFF;
        return Math.clamp(relativeBeat + 1L, 1, noteLength);
    }

    // ------------ Cursor & Selection ---------------

    private void setEditCursor(int x) {
        if (gui.editCursor != gui.editCursorEnd) {
            gui.editCursor = x;
        } else {
            gui.editCursor = x;
            gui.editCursorEnd = x;
        }
    }

    private void addEditCursor(int x) {
        setEditCursor(gui.editCursor + x);
    }

    // ---------- Utility ----------

    private boolean validClick(int x, int y) {
        return x <= GuiMusicSheet.NOTE_REGION_RIGHT && x >= GuiMusicSheet.NOTE_REGION_LEFT
                && y <= GuiMusicSheet.NOTE_REGION_BOTTOM && y >= GuiMusicSheet.NOTE_REGION_TOP;
    }

    static boolean isShiftHeld() {
        com.mojang.blaze3d.platform.Window window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    static boolean isCtrlHeld() {
        com.mojang.blaze3d.platform.Window window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL) ||
                InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    private byte pixelToNote(int mouseRelY) {
        return (byte) (47 - ((mouseRelY - GuiMusicSheet.NOTE_REGION_TOP) / 3) + IItemInstrument.MIN_NOTE + gui.currentOctavePos * 12);
    }
}
