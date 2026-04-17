package xerca.xercamusic.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.packets.serverbound.SingleNotePacket;

import javax.annotation.Nullable;
import java.util.Objects;

import static xerca.xercamusic.client.ClientStuff.sendToServer;

public class GuiInstrument extends Screen {
    private static final ResourceLocation INS_GUI_TEXTURES = Mod.id("textures/gui/instrument_gui.png");
    private static final int GUI_HEIGHT = 201;
    private static final int GUI_WIDTH = 401;
    private static final int GUI_MARGIN_WIDTH = 7;
    private static final int GUI_NOTE_WIDTH = 8;
    private static final int GUI_OCTAVE_WIDTH = GUI_NOTE_WIDTH * 12 + 1;
    private static final int GUI_OCTAVE_HIGHLIGHT_Y = 212;
    private static final int GUI_OCTAVE_HIGHLIGHT_WIDTH = 98;
    private static final int GUI_OCTAVE_HIGHLIGHT_HEIGHT = 92;
    private static final int GUI_TOP_KEYBOARD_BOTTOM = 94;
    private static final int GUI_BOTTOM_KEYBOARD_TOP = 105;
    private static final int GUI_OCTAVE_BLOCK_X = 99;
    private static final int GUI_OCTAVE_BLOCK_Y = 212;
    private static final int GUI_OCTAVE_BLOCK_WIDTH = 95;
    private static final int GUI_OCTAVE_BLOCK_HEIGHT = 82;
    private static final int OCTAVE_BUTTON_Y = 30;
    private static int currentKeyboardOctave;
    private final boolean[] buttonPushStates;
    private final NoteSound[] noteSounds;
    private final Player player;
    private final IItemInstrument instrument;
    @Nullable
    private final BlockPos blockInsPos;
    private final MidiHandler midiHandler;
    private int guiBaseX = 45;
    private int guiBaseY = 80;
    private int octaveButtonX;

    GuiInstrument(Player player, IItemInstrument instrument, Component title, @Nullable BlockPos blockInsPos) {
        super(title);
        this.player = player;
        this.instrument = instrument;
        this.buttonPushStates = new boolean[IItemInstrument.TOTAL_NOTES];
        this.noteSounds = new NoteSound[IItemInstrument.TOTAL_NOTES];
        this.midiHandler = new MidiHandler(this::playSound, this::stopSound);
        this.blockInsPos = blockInsPos;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        guiBaseX = (this.width - GUI_WIDTH) / 2;
        guiBaseY = (this.height - GUI_HEIGHT) / 2;
        octaveButtonX = guiBaseX - 10;

        if (currentKeyboardOctave < instrument.getMinOctave()) {
            currentKeyboardOctave = instrument.getMinOctave();
        } else if (currentKeyboardOctave > instrument.getMaxOctave()) {
            currentKeyboardOctave = instrument.getMaxOctave();
        }
        midiHandler.currentOctave = currentKeyboardOctave;

        this.addRenderableWidget(Button.builder(Component.translatable("note.upButton"), button -> increaseOctave()).
                bounds(octaveButtonX, OCTAVE_BUTTON_Y, 10, 10).
                tooltip(Tooltip.create(Component.translatable("ins.octaveTooltip"))).build());

        this.addRenderableWidget(Button.builder(Component.translatable("note.downButton"), button -> decreaseOctave()).
                bounds(octaveButtonX, OCTAVE_BUTTON_Y + 25, 10, 10).
                tooltip(Tooltip.create(Component.translatable("ins.octaveTooltip"))).build());
    }

    @Override
    public void tick() {
        super.tick();
        Minecraft client = minecraft;
        if (blockInsPos != null && client != null) {
            if (player.level().getBlockState(blockInsPos).getBlock() instanceof BlockInstrument blockIns) {
                if (!Objects.equals(blockIns.getItemInstrument(), instrument)) {
                    client.setScreen(null);
                }
            } else {
                client.setScreen(null);
            }
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // All rendering is handled in render()
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, INS_GUI_TEXTURES);

        guiGraphics.blit(INS_GUI_TEXTURES, guiBaseX, guiBaseY, 0, 0, 0, GUI_WIDTH, GUI_HEIGHT, 512, 512);

        for (int i = 0; i < buttonPushStates.length; i++) {
            if (buttonPushStates[i]) {
                int pushedOctave = i / 12;
                int x = guiBaseX + GUI_MARGIN_WIDTH + i * GUI_NOTE_WIDTH + pushedOctave;
                int y = guiBaseY + 11;
                if (pushedOctave > 3) {
                    x -= 4 + 48 * GUI_NOTE_WIDTH;
                    y = guiBaseY + GUI_BOTTOM_KEYBOARD_TOP + 2;
                }
                guiGraphics.blit(INS_GUI_TEXTURES, x, y, 0, 402, 11, 7, 82, 512, 512);
            }
        }

        int currentKeyboardOctaveDraw = Math.max(0, currentKeyboardOctave);
        int octaveHighlightX = guiBaseX + GUI_MARGIN_WIDTH + currentKeyboardOctaveDraw * GUI_OCTAVE_WIDTH - 1;
        int octaveHighlightY = guiBaseY + 3;
        if (currentKeyboardOctave > 3) {
            octaveHighlightX -= 4 * GUI_OCTAVE_WIDTH;
            octaveHighlightY = guiBaseY + GUI_BOTTOM_KEYBOARD_TOP - 6;
        }
        guiGraphics.blit(INS_GUI_TEXTURES, octaveHighlightX, octaveHighlightY, 0, 0, 0, GUI_OCTAVE_HIGHLIGHT_Y, GUI_OCTAVE_HIGHLIGHT_WIDTH, GUI_OCTAVE_HIGHLIGHT_HEIGHT, 512, 512);

        for (int i = 0; i < 8; i++) {
            if (i < instrument.getMinOctave() || i > instrument.getMaxOctave()) {
                int x = guiBaseX + GUI_MARGIN_WIDTH + i * GUI_OCTAVE_WIDTH;
                int y = guiBaseY + 11;
                if (i > 3) {
                    x -= 4 * GUI_OCTAVE_WIDTH;
                    y = guiBaseY + GUI_BOTTOM_KEYBOARD_TOP + 2;
                }
                guiGraphics.blit(INS_GUI_TEXTURES, x, y, 0, 0, GUI_OCTAVE_BLOCK_X, GUI_OCTAVE_BLOCK_Y, GUI_OCTAVE_BLOCK_WIDTH, GUI_OCTAVE_BLOCK_HEIGHT, 512, 512);
            }
        }

        guiGraphics.drawCenteredString(this.font, Integer.toString(currentKeyboardOctave), octaveButtonX + 4, OCTAVE_BUTTON_Y + 14, 0xFFFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private int noteIdFromPos(int mouseX, int mouseY) {
        int buttonBaseX = guiBaseX + GUI_MARGIN_WIDTH;
        if (mouseX >= buttonBaseX && mouseX <= buttonBaseX + GUI_WIDTH - 14
                && mouseY >= guiBaseY + 9 && mouseY <= guiBaseY + GUI_HEIGHT - 10
                && (mouseY < guiBaseY + GUI_TOP_KEYBOARD_BOTTOM || mouseY > guiBaseY + GUI_BOTTOM_KEYBOARD_TOP)) {
            int octavePlus = (mouseY < guiBaseY + GUI_TOP_KEYBOARD_BOTTOM) ? 0 : 4;
            int octave = octavePlus + (mouseX - buttonBaseX) / GUI_OCTAVE_WIDTH;
            int note = ((mouseX - buttonBaseX) % GUI_OCTAVE_WIDTH) / GUI_NOTE_WIDTH;
            if (note < 12) {
                return octave * 12 + note;
            }
        }
        return -1;
    }

    private void playSound(int noteId) {
        playSound(new MidiHandler.MidiData(noteId, 0.8f));
    }

    private void playSound(MidiHandler.MidiData data) {
        int noteId = data.noteId();

        if (noteId >= 0 && noteId < buttonPushStates.length && !buttonPushStates[noteId]) {
            int note = IItemInstrument.idToNote(noteId);

            IItemInstrument.InsSound noteSound = instrument.getSound(note);
            if (noteSound == null) {
                return;
            }
            noteSounds[noteId] = ClientStuff.playNote(noteSound.sound(), player.getX(), player.getY(), player.getZ(), data.volume(), noteSound.pitch());
            player.level().addParticle(ParticleTypes.NOTE, player.getX(), player.getY() + 2.2D, player.getZ(), note / 24.0D, 0.0D, 0.0D);
            buttonPushStates[noteId] = true;

            SingleNotePacket pack = new SingleNotePacket(note, instrument, false, data.volume());
            sendToServer(pack);
        }
    }

    private void stopSound(int noteId) {
        if (noteId >= 0 && noteId < buttonPushStates.length && buttonPushStates[noteId] && noteSounds[noteId] != null) {
            noteSounds[noteId].stopSound();
            noteSounds[noteId] = null;
            buttonPushStates[noteId] = false;

            int note = IItemInstrument.idToNote(noteId);
            SingleNotePacket pack = new SingleNotePacket(note, instrument, true, 1f);
            sendToServer(pack);
        }
    }

    private void stopAllSounds() {
        for (int noteId = 0; noteId < buttonPushStates.length; noteId++) {
            stopSound(noteId);
        }
    }

    @Override
    public boolean mouseClicked(double dmouseX, double dmouseY, int mouseButton) {
        int mouseX = (int) Math.round(dmouseX);
        int mouseY = (int) Math.round(dmouseY);

        int noteId = noteIdFromPos(mouseX, mouseY);
        playSound(noteId);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double dmouseX, double dmouseY, int mouseButton) {
        int mouseX = (int) Math.round(dmouseX);
        int mouseY = (int) Math.round(dmouseY);

        int noteId = noteIdFromPos(mouseX, mouseY);
        stopSound(noteId);

        return super.mouseReleased(dmouseX, dmouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        int mouseX = (int) Math.round(posX);
        int mouseY = (int) Math.round(posY);
        int prevMouseX = (int) Math.round(posX - deltaX);
        int prevMouseY = (int) Math.round(posY - deltaY);

        int prevNoteId = noteIdFromPos(prevMouseX, prevMouseY);
        int currentNoteId = noteIdFromPos(mouseX, mouseY);
        if (prevNoteId != currentNoteId) {
            stopSound(prevNoteId);
            playSound(currentNoteId);
        }

        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        setFocused(null);
        super.keyPressed(keyCode, scanCode, modifiers);

        if (scanCode >= 16 && scanCode <= 27) {
            int noteId = scanCode - 16 + 12 * Math.max(0, currentKeyboardOctave);
            playSound(noteId);
        }

        if (keyCode == GLFW.GLFW_KEY_A) {
            decreaseOctave();
        } else if (keyCode == GLFW.GLFW_KEY_S) {
            increaseOctave();
        }
        return true;
    }

    private void decreaseOctave() {
        if (currentKeyboardOctave > -3) {
            currentKeyboardOctave--;
            midiHandler.currentOctave = currentKeyboardOctave;
            stopAllSounds();
        }
    }

    private void increaseOctave() {
        if (currentKeyboardOctave < instrument.getMaxOctave()) {
            currentKeyboardOctave++;
            midiHandler.currentOctave = currentKeyboardOctave;
            stopAllSounds();
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (scanCode >= 16 && scanCode <= 27) {
            int noteId = scanCode - 16 + 12 * Math.max(0, currentKeyboardOctave);
            stopSound(noteId);
        }
        return true;
    }

    @Override
    public void removed() {
        midiHandler.closeDevices();
    }
}
