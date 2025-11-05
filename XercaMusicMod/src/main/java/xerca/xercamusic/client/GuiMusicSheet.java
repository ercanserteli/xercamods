package xerca.xercamusic.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.NotesPartAckFromServerPacketHandler;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket;
import xerca.xercamusic.common.packets.serverbound.MusicUpdatePacket;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;

import java.util.*;

import static xerca.xercamusic.client.ClientStuff.sendToServer;
import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.Mod.onlyCallOnClient;

public class GuiMusicSheet extends Screen {
    public static final int BEATS_IN_SCREEN = 91;
    private static final String[] OCTAVE_NAMES = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII"};
    private static final ResourceLocation NOTE_GUI_LEFT_TEXTURE = Mod.id("textures/gui/music_sheet_left.png");
    private static final ResourceLocation NOTE_GUI_TEXTURES = Mod.id("textures/gui/music_sheet.png");
    private static final ResourceLocation INSTRUMENT_TEXTURES = Mod.id("textures/gui/instruments.png");
    private static final int NOTE_IMAGE_LEFT_TEX_X = 175;
    private static final int NOTE_IMAGE_LEFT_TEX_Y = 51;
    private static final int NOTE_IMAGE_LEFT_WIDTH = 81;
    private static final int NOTE_IMAGE_LEFT_HEIGHT = 205;
    private static final int NOTE_IMAGE_TEX_X = 0;
    private static final int NOTE_IMAGE_TEX_Y = 44;
    private static final int NOTE_IMAGE_WIDTH = 256;
    private static final int NOTE_IMAGE_HEIGHT = 210;
    private static final int NOTE_REGION_LEFT = 44;
    private static final int NOTE_REGION_TOP = 39;
    private static final int NOTE_REGION_RIGHT = 316;
    private static final int NOTE_REGION_BOTTOM = 182;
    private static final int BPM_BUT_W = 10;
    private static final int BPM_BUT_H = 10;
    private static final int BPM_BUT_X = 245;
    private static final int BPM_BUT_Y = 12;
    private static final int HL_BUT_X = 261;
    private static final int HL_BUT_Y = 23;
    private static final int[] OCTAVE_COLORS = {0xFF5B3200, 0xFFFF0000, 0xFF0AEE00, 0xFF0059FF, 0xFF7B00FF, 0xFFEF00B7, 0xFF00E2DF, 0XFFF4E800};
    private static final int[] OCTAVE_COLORS_TRANS = {0x165B3200, 0x16FF0000, 0x160AEE00, 0x160059FF, 0x167B00FF, 0x16EF00B7, 0x1600E2DF, 0X16F4E800};
    private static final int MAX_LENGTH_BEATS = 32000;
    private static final byte COPY_BEGIN_BYTE = (byte) 50;
    private static final int MAX_NOTE_LENGTH = 60;
    private static final int MAX_UNDO_LENGTH = 16;
    private static final String NOTE_LEFT_STR_KEY = "note.leftButton";
    private static final String NOTE_RIGHT_STR_KEY = "note.rightButton";
    private static int currentOctave = 1;
    private static float brushVolume = 0.5f;
    private final Player editingPlayer;
    private final NoteSound[] notePlaySounds;
    private final ArrayList<NoteEvent> recordingNotes = new ArrayList<>();
    private final boolean[] buttonPushStates = new boolean[IItemInstrument.TOTAL_NOTES];
    private final UUID id;
    private final MusicUpdatePacket.FieldFlag dirtyFlag = new MusicUpdatePacket.FieldFlag();
    private final Deque<ArrayList<NoteEvent>> undoStack = new ArrayDeque<>(MAX_UNDO_LENGTH);
    private final ArrayList<ArrayList<NoteEvent>> neighborNotes = new ArrayList<>();
    private final ArrayList<Float> neighborVolumes = new ArrayList<>();
    private final ArrayList<Integer> neighborPreviewNextNoteIDs = new ArrayList<>();
    private final ArrayList<Integer> neighborPrevInstruments = new ArrayList<>();
    private final MidiHandler midiHandler;
    private int noteImageX;
    private int noteImageLeftX;
    private int noteImageY;
    private byte highlightInterval = 12;
    private boolean isSigned;
    private int generation;
    private boolean gettingSigned;
    private boolean previewing;
    private boolean previewStarted;
    private boolean recording;
    private boolean preRecording;
    private boolean preRecordPlayTick;
    private int previewCursor;
    private int previewCursorStart;
    private int oldPreRecordBeat;
    private int editCursor;
    private int editCursorEnd;
    private int selectionStart; // where the first right click happened when selecting
    private int tickCount;
    private String noteTitle = "";
    private Button bpmUp;
    private Button bpmDown;
    private Button octaveUp;
    private Button octaveDown;
    private Button hlUp;
    private Button hlDown;
    private Button buttonSign;
    private Button buttonCancel;
    private Button buttonFinalize;
    private Button buttonHelp;
    private BetterSlider sliderTime;
    private BetterSlider sliderSheetVolume;
    private BetterSlider sliderNoteVolume;
    private NoteEditBox noteEditBox;
    private ChangeableImageButton buttonPreview;
    private ChangeableImageButton buttonRecord;
    private ChangeableImageButton buttonHideNeighbors;
    private LockImageButton buttonLockPrevIns;
    private boolean neighborsHidden;
    private boolean prevInsLocked;
    private boolean selfSigned;
    private int version;
    private ArrayList<NoteEvent> notes = new ArrayList<>();
    private short lengthBeats;
    private byte bps = 8;
    private int bpm;
    private int previewInstrument = -1;
    private long lastMillis;
    private long cumMillis;
    private int previewNextNoteID;
    private NoteEvent currentlyAddedNote;
    private int sliderPosition;
    private int maxSliderPosition = 500;
    private int currentOctavePos = 1;
    private float volume = 1.f;
    private boolean helpOn;

    GuiMusicSheet(Player player, ItemStack sheet, Component title) {
        super(title);
        this.editingPlayer = player;
        UUID sheetId = sheet.get(Items.SHEET_ID);
        this.version = sheet.getOrDefault(Items.SHEET_VERSION, -1);
        if (sheetId != null && this.version >= 0) {
            // Read notes from cache or server using id
            MusicManager.MusicData data = MusicManagerClient.getMusicData(sheetId, version);
            if (data != null) {
                notes.addAll(data.notes());
            }

            this.lengthBeats = (short) (int) sheet.getOrDefault(Items.SHEET_LENGTH, 0);
            this.bps = sheet.getOrDefault(Items.SHEET_BPS, (byte) 8);
            this.volume = sheet.getOrDefault(Items.SHEET_VOLUME, 1.f);
            this.generation = sheet.getOrDefault(Items.SHEET_GENERATION, 0);
            this.isSigned = generation > 0;
            this.noteTitle = sheet.getOrDefault(Items.SHEET_TITLE, "");
            String authorName = sheet.getOrDefault(Items.SHEET_AUTHOR, "");
            this.prevInsLocked = sheet.getOrDefault(Items.SHEET_PREV_INSTRUMENT_LOCKED, false);
            Byte prevIns = sheet.get(Items.SHEET_PREV_INSTRUMENT);
            if (prevIns != null) {
                this.previewInstrument = prevIns;
            }
            this.highlightInterval = sheet.getOrDefault(Items.SHEET_HIGHLIGHT_INTERVAL, (byte) 12);

            if (authorName.equals(player.getName().getString())) {
                this.selfSigned = true;
            }
        } else {
            this.isSigned = false;
            sheetId = UUID.randomUUID();
            this.version = 0;
            dirtyFlag.hasId = true;
            dirtyFlag.hasVersion = true;
        }

        this.id = sheetId;
        if (this.notes.isEmpty()) {
            this.lengthBeats = 0;
        }
        this.bpm = bps * 60;
        this.tickCount = 0;

        if (!prevInsLocked) {
            int index = getCurrentOffhandInsIndex();
            if (index != previewInstrument) {
                previewInstrument = index;
                if (!isSigned || selfSigned || generation > 1) {
                    dirtyFlag.hasPrevIns = true;
                }
            }
        }

        // Neighbor sheets
        int currentSlot = player.getInventory().selected;
        boolean added = addNeighborSheet(getStackInSlot(currentSlot - 1));
        if (added) {
            addNeighborSheet(getStackInSlot(currentSlot - 2));
        }
        added = addNeighborSheet(getStackInSlot(currentSlot + 1));
        if (added) {
            addNeighborSheet(getStackInSlot(currentSlot + 2));
        }

        this.midiHandler = new MidiHandler(this::startSound, this::endSound, this::midiControlCommand);
        midiHandler.currentOctave = currentOctave;
        this.notePlaySounds = new NoteSound[IItemInstrument.TOTAL_NOTES];
    }

    private static int octaveFromNote(byte note) {
        return (note - IItemInstrument.MIN_NOTE) / 12;
    }

    private int getCurrentOffhandInsIndex() {
        Item offhand = editingPlayer.getOffhandItem().getItem();
        if (offhand instanceof IItemInstrument ins) {
            return Items.INSTRUMENTS.indexOf(ins);
        }
        return -1;
    }

    private void startSound(MidiHandler.MidiData data) {
        int noteId = data.noteId();
        startSound(noteId, (byte) (data.volume() * 128.f));
    }

    private void startSound(int noteId, byte volume) {
        //TEMP
        if (noteId >= 0 && noteId < buttonPushStates.length && buttonPushStates[noteId]) {
            Mod.LOGGER.warn("Key pushed twice noteId: {} vol: {}", noteId, volume);
        }
        if (noteId >= 0 && noteId < buttonPushStates.length && !buttonPushStates[noteId]) {
            buttonPushStates[noteId] = true;
            int note = IItemInstrument.idToNote(noteId);
            for (NoteEvent noteEvent : recordingNotes) {
                if (noteEvent.note == note) {
                    Mod.LOGGER.warn("Existing note pushed? {} vol: {}", noteId, volume);
                    return;
                }
            }

            IItemInstrument.InsSound noteSound;
            if (previewInstrument >= 0 && previewInstrument < Items.INSTRUMENTS.size()) {
                IItemInstrument ins = Items.INSTRUMENTS.get(previewInstrument);
                noteSound = ins.getSound(note);
            } else {
                noteSound = ((IItemInstrument) Items.HARP_MC).getSound(note);
            }
            if (noteSound == null) {
                Mod.LOGGER.warn("noteSound not found - noteId: {} vol: {}", noteId, volume);
                return;
            }
            try {
                notePlaySounds[noteId] = onlyCallOnClient(() -> () -> ClientStuff.playNote(noteSound.sound(), editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(), ((float) volume) / 128.f, noteSound.pitch()));
            } catch (Exception e) {
                Mod.LOGGER.error("Error playing sound", e);
            }
            if (recording) {
                NoteEvent newNote = new NoteEvent((byte) note, (short) Math.max(0, previewCursor - 1), volume, (byte) 1);
                addRecordingNote(newNote);
                previewNextNoteID++;
                dirtyFlag.hasNotes = true;
                dirtyFlag.hasLength = true;
            }
        }
    }

    private void endSound(int noteId) {
        if (noteId >= 0 && noteId < buttonPushStates.length && buttonPushStates[noteId]) {
            buttonPushStates[noteId] = false;
            if (notePlaySounds[noteId] != null) {
                notePlaySounds[noteId].stopSound();
                notePlaySounds[noteId] = null;
            }
            if (recording) {
                int note = IItemInstrument.idToNote(noteId);
                for (int i = 0; i < recordingNotes.size(); i++) {
                    if (recordingNotes.get(i).note == note) {
                        recordingNotes.remove(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean addNeighborSheet(ItemStack neighbor) {
        if (!neighbor.isEmpty() && neighbor.getItem() instanceof ItemMusicSheet) {
            byte neighborBPS = ItemMusicSheet.getBPS(neighbor);
            if (neighborBPS == bps) {
                UUID uuid = neighbor.get(Items.SHEET_ID);
                int ver = neighbor.getOrDefault(Items.SHEET_VERSION, -1);
                if (uuid != null && ver >= 0) {
                    MusicManagerClient.checkMusicDataAndRun(uuid, ver, () -> {
                        MusicManager.MusicData data = MusicManagerClient.getMusicData(uuid, ver);
                        if (data != null) {
                            neighborNotes.add(new ArrayList<>(data.notes()));
                            neighborPrevInstruments.add(ItemMusicSheet.getPrevInstrument(neighbor));
                            neighborPreviewNextNoteIDs.add(-1);
                            neighborVolumes.add(ItemMusicSheet.getVolume(neighbor));
                        }
                    });
                }
                return true;
            }
        }
        return false;
    }

    private ItemStack getStackInSlot(int slot) {
        if (slot >= 0 && slot < editingPlayer.getInventory().getContainerSize()) {
            return editingPlayer.getInventory().getItem(slot);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void init() {
        noteImageX = ((this.width - (NOTE_IMAGE_WIDTH + NOTE_IMAGE_LEFT_WIDTH)) / 2) + NOTE_IMAGE_LEFT_WIDTH;
        noteImageLeftX = noteImageX - NOTE_IMAGE_LEFT_WIDTH;
        noteImageY = 2;
        if (!this.isSigned) {
            this.buttonSign = this.addRenderableWidget(Button.builder(Component.translatable("note.signButton"), button -> {
                if (!isSigned) {
                    gettingSigned = true;
                    updateButtons();
                }
            }).bounds(noteImageLeftX + 112, noteImageY + NOTE_IMAGE_HEIGHT, 98, 20).build());
            this.buttonFinalize = this.addRenderableWidget(Button.builder(Component.translatable("note.finalizeButton"), button -> {
                if (!isSigned) {
                    dirtyFlag.hasSigned = true;
                    dirtyFlag.hasTitle = true;
                    isSigned = true;
                    if (minecraft != null) {
                        minecraft.setScreen(null);
                    }
                }
            }).bounds(noteImageLeftX + 112, 145, 98, 20).build());
            this.buttonCancel = this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
                if (!isSigned) {
                    gettingSigned = false;
                    updateButtons();
                }
            }).bounds(noteImageLeftX + 112, 170, 98, 20).build());
        }
        this.buttonPreview = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 50, 16, 16, 16, 224, 0, 16, NOTE_GUI_TEXTURES, button -> previewButton()));

        this.buttonRecord = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 70, 16, 16, 16, 176, 0, 16, NOTE_GUI_TEXTURES, button -> recordButton()));

        this.buttonHideNeighbors = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 90, 16, 16, 16, 192, 0, 16, NOTE_GUI_TEXTURES, button -> {
            neighborsHidden = !neighborsHidden;
            if (neighborsHidden) {
                this.buttonHideNeighbors.setTexStarts(208, 0);
            } else {
                this.buttonHideNeighbors.setTexStarts(192, 0);
            }
        }));

        this.buttonLockPrevIns = this.addRenderableWidget(new LockImageButton(noteImageLeftX + 110, 16, 16, 16, previewInstrument * 16 + 16, 32 * ((previewInstrument + 1) / 16), 16, INSTRUMENT_TEXTURES, button -> {
            if (!isSigned || selfSigned || generation > 1) {
                prevInsLocked = !prevInsLocked;
                dirtyFlag.hasPrevInsLocked = true;
                if (!prevInsLocked) {
                    int index = getCurrentOffhandInsIndex();
                    if (index != previewInstrument) {
                        previewInstrument = index;
                        this.buttonLockPrevIns.setTexStarts(previewInstrument * 16 + 16, 32 * ((previewInstrument + 1) / 16));
                        dirtyFlag.hasPrevIns = true;
                    }
                }
            }
        }));

        this.bpmUp = this.addRenderableWidget(Button.builder(Component.translatable("note.upButton"), button -> {
            if (!isSigned || selfSigned || generation > 1) {
                if (hasShiftDown()) {
                    int mult = hasControlDown() ? 3 : 2;
                    if (bps * mult <= 50) {
                        pushUndo();

                        bps *= (byte) mult;
                        dirtyFlag.hasBps = true;
                        previewing = false;
                        previewCursor = previewCursorStart;
                        for (NoteEvent note : notes) {
                            note.time *= (short) mult;
                            note.length = (byte) Math.min(MAX_NOTE_LENGTH, note.length * mult);
                        }
                        updateLength();
                    }
                } else {
                    if (bps < 50) {
                        bps++;
                        dirtyFlag.hasBps = true;
                        cumMillis *= (long) ((float) (bps - 1) / (float) bps);
                    }
                }
                bpm = 60 * bps;
            }
        }).bounds(noteImageLeftX + BPM_BUT_X, noteImageY + BPM_BUT_Y, BPM_BUT_W, BPM_BUT_H).build());
        this.bpmDown = this.addRenderableWidget(Button.builder(Component.translatable("note.downButton"), button -> {
            if (!isSigned || selfSigned || generation > 1) {
                if (hasShiftDown()) {
                    float mult = hasControlDown() ? 0.33f : 0.5f;
                    if (Math.round(bps * mult) >= 1) {
                        pushUndo();

                        bps = (byte) Math.round(bps * mult);
                        dirtyFlag.hasBps = true;
                        previewing = false;
                        previewCursor = previewCursorStart;
                        for (NoteEvent note : notes) {
                            note.time = (short) Math.round(note.time * mult);
                            note.length = (byte) Math.max(Math.round(note.length * mult), 1);
                        }
                        updateLength();
                        NoteEvent.removeDuplicates(notes);
                    }
                } else {
                    if (bps > 1) {
                        bps--;
                        dirtyFlag.hasBps = true;
                        cumMillis *= (long) ((float) (bps + 1) / (float) bps);
                    }
                }
                bpm = 60 * bps;
            }
        }).bounds(noteImageLeftX + BPM_BUT_X, noteImageY + BPM_BUT_Y + 1 + BPM_BUT_H, BPM_BUT_W, BPM_BUT_H).build());

        this.octaveUp = this.addRenderableWidget(Button.builder(Component.translatable("note.upButton"), button -> {
            if (currentOctavePos < 4) {
                currentOctavePos++;
            }
        }).bounds(noteImageLeftX + 15, noteImageY + 30, BPM_BUT_W, BPM_BUT_H).build());

        this.octaveDown = this.addRenderableWidget(Button.builder(Component.translatable("note.downButton"), button -> {
            if (currentOctavePos > 0) {
                currentOctavePos--;
            }
        }).bounds(noteImageLeftX + 15, noteImageY + NOTE_REGION_BOTTOM, BPM_BUT_W, BPM_BUT_H).build());

        this.sliderTime = this.addRenderableWidget(new BetterSlider(noteImageLeftX + NOTE_REGION_LEFT, noteImageY + NOTE_REGION_BOTTOM + 4, NOTE_REGION_RIGHT - NOTE_REGION_LEFT, 10,
                Component.empty(), Component.empty(), 0, 1, 0, 0.001, false) {
            @Override
            public void applyValue() {
                sliderPosition = (int) (value * (double) maxSliderPosition);
            }
        });

        this.sliderSheetVolume = this.addRenderableWidget(new BetterSlider(noteImageLeftX + NOTE_REGION_RIGHT - 55, noteImageY + 12, 54, 10,
                Component.literal("S Vol "), Component.empty(), 0, 100, volume * 100.f, true) {
            @Override
            public void applyValue() {
                if (!isSigned || selfSigned || generation > 1) {
                    volume = ((float) value);
                    dirtyFlag.hasVolume = true;
                }
            }
        });

        this.sliderNoteVolume = this.addRenderableWidget(new BetterSlider(noteImageLeftX + 130, noteImageY + 12, 54, 10,
                Component.literal("N Vol "), Component.empty(), 0, 100, brushVolume * 100.f, true) {
            @Override
            public void applyValue() {
                if (!isSigned) {
                    brushVolume = ((float) value);
                }
            }
        });

        this.hlDown = this.addRenderableWidget(Button.builder(Component.translatable(NOTE_LEFT_STR_KEY), button -> {
            if ((!isSigned || selfSigned || generation > 1) && highlightInterval > 1) {
                highlightInterval--;
                dirtyFlag.hasHlInterval = true;
            }
        }).bounds(noteImageLeftX + HL_BUT_X, noteImageY + HL_BUT_Y, BPM_BUT_W, BPM_BUT_H).build());
        this.hlUp = this.addRenderableWidget(Button.builder(Component.translatable(NOTE_RIGHT_STR_KEY), button -> {
            if ((!isSigned || selfSigned || generation > 1) && highlightInterval < 24) {
                highlightInterval++;
                dirtyFlag.hasHlInterval = true;
            }
        }).bounds(noteImageLeftX + HL_BUT_X + 44, noteImageY + HL_BUT_Y, BPM_BUT_W, BPM_BUT_H).build());

        this.noteEditBox = this.addRenderableWidget(new NoteEditBox(0, 0, 70, 55, Component.empty()));

        this.buttonHelp = this.addRenderableWidget(Button.builder(Component.literal("?"), button -> toggleHelp()).
                bounds(noteImageLeftX + NOTE_REGION_RIGHT + 30, noteImageY + BPM_BUT_Y, 20, 20).build());

        updateButtons();

        updateLength();
    }

    private void previewButton() {
        if (!previewing) {
            startPreview();
        } else {
            stopPreview();
        }
    }

    private void recordButton() {
        if (!recording && !preRecording) {
            startPreRecording();
        } else {
            stopRecording();
        }
    }

    private void updateButtons() {
        if (!this.isSigned) {
            this.buttonSign.visible = !this.gettingSigned;
            this.buttonSign.active = !helpOn && (!this.recording && !this.preRecording);
            this.buttonCancel.visible = this.gettingSigned;
            this.buttonFinalize.visible = this.gettingSigned;
            this.buttonFinalize.active = !this.noteTitle.trim().isEmpty();
        }
        this.bpmDown.visible = this.bpmUp.visible = !this.gettingSigned && (!this.isSigned || this.selfSigned || this.generation > 1);
        this.bpmDown.active = this.bpmUp.active = (!this.recording && !this.preRecording);
        this.buttonPreview.visible = !this.gettingSigned;
        this.buttonPreview.active = (!this.recording && !this.preRecording);
        this.buttonLockPrevIns.visible = !this.gettingSigned;
        this.buttonLockPrevIns.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.sliderSheetVolume.visible = !this.gettingSigned;
        this.sliderSheetVolume.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.noteEditBox.visible = false;
        this.noteEditBox.active = false;
        this.octaveDown.visible = !this.gettingSigned;
        this.octaveUp.visible = !this.gettingSigned;
        this.sliderTime.visible = !this.gettingSigned;
        this.sliderTime.active = (!this.recording && !this.preRecording);
        this.hlUp.visible = !this.gettingSigned && (!this.isSigned || this.selfSigned || this.generation > 1);
        this.hlUp.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.hlDown.visible = !this.gettingSigned && (!this.isSigned || this.selfSigned || this.generation > 1);
        this.hlDown.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.sliderNoteVolume.visible = !this.isSigned && !this.gettingSigned;
        this.sliderNoteVolume.active = this.sliderNoteVolume.visible && (!this.recording && !this.preRecording);
        this.buttonHelp.visible = !this.isSigned && !this.gettingSigned;
        this.buttonHelp.active = this.buttonHelp.visible && (!this.recording && !this.preRecording);
        this.buttonHideNeighbors.visible = !this.neighborNotes.isEmpty() && !this.gettingSigned;
        this.buttonHideNeighbors.active = (!this.recording && !this.preRecording);
        this.buttonRecord.visible = !this.gettingSigned && !this.isSigned;
        this.buttonRecord.active = this.recording || this.preRecording || !this.previewing;
    }

    private void toggleHelp() {
        helpOn = !helpOn;
        updateButtons();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick() {
        super.tick();
        ++this.tickCount;
    }

    private void playMetronomeTick() {
        try {
            onlyCallOnClient(() -> () ->
                    ClientStuff.playNote(SoundEvents.TICK, editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(), SoundSource.PLAYERS, 1.0f, 0.975f + editingPlayer.level().random.nextFloat() * 0.05f, (byte) -1));
        } catch (Exception e) {
            Mod.LOGGER.error("Exception in playMetronomeTick", e);
        }
    }

    private NoteSound playSound(NoteEvent event, int previewInstrument) {
        return playSound(event, previewInstrument, volume);
    }

    private NoteSound playSound(NoteEvent event, int previewInstrument, float sheetVolume) {
        if (event.note < IItemInstrument.MIN_NOTE || event.note > IItemInstrument.MAX_NOTE) {
            Mod.LOGGER.warn("Note is invalid: {}", event.note);
            return null;
        }

        IItemInstrument.InsSound insSound;
        if (previewInstrument >= 0 && previewInstrument < Items.INSTRUMENTS.size()) {
            IItemInstrument ins = Items.INSTRUMENTS.get(previewInstrument);
            insSound = ins.getSound(event.note);
        } else {
            insSound = ((IItemInstrument) Items.HARP_MC).getSound(event.note);
        }
        if (insSound == null) {
            return null;
        }

        try {
            return onlyCallOnClient(() -> () ->
                    ClientStuff.playNote(insSound.sound(), editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(),
                            sheetVolume * event.floatVolume(), insSound.pitch(), (byte) beatsToTicks(event.length)));
        } catch (Exception e) {
            Mod.LOGGER.error("Exception in playSound", e);
            return null;
        }
    }

    private int beatsToTicks(int beats) {
        return Math.round(beats * 20.0f / bps);
    }

    private void playPreviewSound(int curStart, int curEnd) {
        if (previewNextNoteID < notes.size()) {
            NoteEvent event = notes.get(previewNextNoteID);
            while (event.time >= curStart && event.time < curEnd) {
                if (!recordingNotes.contains(event)) {
                    playSound(event, previewInstrument);
                }
                previewNextNoteID++;
                if (previewNextNoteID >= notes.size()) {
                    break;
                }
                event = notes.get(previewNextNoteID);
            }
        }

        // Play neighbors too
        if (!neighborsHidden) {
            for (int i = 0; i < neighborNotes.size(); i++) {
                ArrayList<NoteEvent> nn = neighborNotes.get(i);
                int nPrevNoteID = neighborPreviewNextNoteIDs.get(i);
                if (nPrevNoteID >= 0 && nPrevNoteID < nn.size()) {
                    NoteEvent n = nn.get(nPrevNoteID);
                    int ins = neighborPrevInstruments.get(i);
                    while (n.time >= curStart && n.time < curEnd) {
                        playSound(n, ins, neighborVolumes.get(i));
                        nPrevNoteID++;
                        if (nPrevNoteID >= nn.size()) {
                            break;
                        }
                        n = nn.get(nPrevNoteID);
                    }
                    neighborPreviewNextNoteIDs.set(i, nPrevNoteID);
                }
            }
        }
    }

    private void drawSigning(GuiGraphics guiGraphics) {
        int i = noteImageLeftX;
        int j = noteImageY;

        final int left = i + 100;
        final int top = j + 40;
        final int width = 120;
        final int height = 100;
        guiGraphics.fill(left, top, left + width, top + height, 0xFFFFFFFF);
        String titleStr = this.noteTitle;

        if (!this.isSigned) {
            if (this.tickCount / 6 % 2 == 0) {
                titleStr = titleStr + ChatFormatting.BLACK + "_";
            } else {
                titleStr = titleStr + ChatFormatting.GRAY + "_";
            }
        }
        String writeTitleStr = I18n.get("note.editTitle");
        int k = this.font.width(writeTitleStr);
        guiGraphics.drawString(font, writeTitleStr, (int) (left + (width - k) / 2.0f), top + 16, 0, false);
        int l = this.font.width(titleStr);
        guiGraphics.drawString(font, titleStr, (int) (left + (width - l) / 2.0f), top + 30, 0, false);
        String authorStr = I18n.get("note.byAuthor", this.editingPlayer.getName().getString());
        int i1 = this.font.width(authorStr);
        guiGraphics.drawString(font, ChatFormatting.DARK_GRAY + authorStr, (int) (left + (116 - i1) / 2.0f), top + 42, 0, false);
        guiGraphics.drawWordWrap(font, Component.translatable("note.finalizeWarning"), left + 10, top + 60, 116, 0);
    }

    private int noteToPixelX(int noteX) {
        return noteImageLeftX + NOTE_REGION_LEFT + noteX * 3;
    }

    private void drawCursor(GuiGraphics guiGraphics, int cursorX, int color) {
        if (inScreen(cursorX)) {
            int x = noteToPixelX(cursorX - sliderPosition);
            int y = noteImageY + NOTE_REGION_TOP;

            guiGraphics.fill(x + 1, y, x + 2, y + 48 * 3, color);
        }
    }

    public void midiControlCommand(MidiControl controlType) {
        switch (controlType) {
            case BEGINNING -> {
                if (!previewing && !recording && !preRecording) {
                    editCursor = 0;
                    editCursorEnd = 0;
                    if (!inScreen(editCursor)) {
                        setSliderPos(editCursor);
                    }
                }
            }
            case END -> {
                if (!previewing && !recording && !preRecording) {
                    editCursor = lengthBeats - 1;
                    editCursorEnd = lengthBeats - 1;
                    if (!inScreen(editCursor)) {
                        setSliderPos(editCursor);
                    }
                }
            }
            case STOP -> {
                if (recording || preRecording) {
                    stopRecording();
                } else if (previewing) {
                    stopPreview();
                }
            }
            case PREVIEW -> {
                if (buttonPreview.active) {
                    previewButton();
                }
            }
            case RECORD -> {
                if (buttonRecord.active) {
                    recordButton();
                }
            }
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Rendering is handled in render()
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack stack = guiGraphics.pose();
        if (previewing || recording || preRecording) {
            long currentMillis = System.currentTimeMillis();

            long delta = currentMillis - lastMillis;
            lastMillis = currentMillis;
            cumMillis += delta;
            int currentBeat = (int) (cumMillis * bps) / 1000;
            if (preRecording) {
                if (currentBeat > oldPreRecordBeat) {
                    for (int i = 0; i < currentBeat - oldPreRecordBeat; i++) {
                        if (preRecordPlayTick) {
                            preRecordPlayTick = false;
                            playMetronomeTick();
                        } else {
                            preRecordPlayTick = true;
                        }
                    }
                    if ((currentBeat % 8 == 0 && cumMillis > 1500) || (currentBeat % 4 == 0 && cumMillis > 1950)) {
                        startRecording();
                    }
                    oldPreRecordBeat = currentBeat;
                }
            } else {
                // Previewing or recording
                int oldPreviewCursor = previewCursor;
                previewCursor = previewCursorStart + currentBeat;

                if (previewCursor > sliderPosition + BEATS_IN_SCREEN - 12 && (lengthBeats > sliderPosition + BEATS_IN_SCREEN || recording)) {
                    setSliderPos(previewCursor - 24);
                }

                if (oldPreviewCursor != previewCursor) {
                    if (!recording && (previewCursor > lengthBeats || (editCursorEnd != editCursor && previewCursor > editCursorEnd + 1))) {
                        stopPreview();
                    } else {
                        previewStarted = true;
                        playPreviewSound(oldPreviewCursor, previewCursor);
                    }

                    if (recording) {
                        for (NoteEvent note : recordingNotes) {
                            if (previewCursor - note.time > 1) {
                                note.length += (byte) (previewCursor - oldPreviewCursor);
                                if (note.length > MAX_NOTE_LENGTH) {
                                    note.length = MAX_NOTE_LENGTH;
                                }
                            }
                        }
                    }
                }
            }
        }

        guiGraphics.blit(NOTE_GUI_LEFT_TEXTURE, noteImageLeftX, noteImageY + 7, NOTE_IMAGE_LEFT_TEX_X, NOTE_IMAGE_LEFT_TEX_Y, NOTE_IMAGE_LEFT_WIDTH, NOTE_IMAGE_LEFT_HEIGHT);
        guiGraphics.blit(NOTE_GUI_TEXTURES, noteImageX, noteImageY, NOTE_IMAGE_TEX_X, NOTE_IMAGE_TEX_Y, NOTE_IMAGE_WIDTH, NOTE_IMAGE_HEIGHT);
        if (gettingSigned) {
            drawSigning(guiGraphics);
        } else {
            // Draw octave tints
            int x1 = noteImageLeftX + NOTE_REGION_LEFT;
            int x2 = noteImageLeftX + NOTE_REGION_RIGHT;
            for (int i = 0; i < 4; i++) {
                int y1 = noteImageY + NOTE_REGION_TOP + (4 - i) * 12 * 3;
                int y2 = noteImageY + NOTE_REGION_TOP + (3 - i) * 12 * 3;
                guiGraphics.fill(x1, y1, x2, y2, OCTAVE_COLORS_TRANS[i + currentOctavePos]);
            }

            // Draw octave lines
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 6; j++) {
                    int y = noteImageY + NOTE_REGION_BOTTOM - i * 36 - j * 6;
                    guiGraphics.fill(x1, y - 1, x2 + 1, y, OCTAVE_COLORS[i + currentOctavePos]);
                }
            }

            // Draw octave names
            for (int i = 0; i < 4; i++) {
                final int x = x1 - 24;
                final int y = noteImageY + NOTE_REGION_BOTTOM - 18 - i * 36;
                if (currentOctave == i + currentOctavePos) {
                    guiGraphics.fill(x - 10, y - 4, x + 10, y + 12, 0xAAFFFFAA);
                }
                guiGraphics.drawCenteredString(font, OCTAVE_NAMES[i + currentOctavePos], x, y, OCTAVE_COLORS[i + currentOctavePos]);
            }

            // Draw measure lines
            if (highlightInterval > 1) {
                for (int i = sliderPosition; i < sliderPosition + BEATS_IN_SCREEN; i++) {
                    int x = (i - sliderPosition) * 3 + noteImageLeftX + NOTE_REGION_LEFT + 1;
                    if (i % highlightInterval == 0) {
                        guiGraphics.fill(x, noteImageY + NOTE_REGION_TOP - 1, x + 1, noteImageY + NOTE_REGION_BOTTOM + 3, 0xFF88796A);
                    }
                }

                // Draw measure numbers
                stack.pushPose();
                stack.scale(0.5f, 0.5f, 0.5f);
                for (int i = sliderPosition; i < sliderPosition + BEATS_IN_SCREEN; i++) {
                    if (i % highlightInterval == 0) {
                        final int x = (i - sliderPosition) * 3 + noteImageLeftX + NOTE_REGION_LEFT;
                        final int y = noteImageY + NOTE_REGION_TOP - 5;
                        final String name = Integer.toString((i / highlightInterval) + 1);
                        final int w = font.width(name);
                        guiGraphics.drawString(font, name, (int) ((x - (w - 6.0f) / 4.0f) * 2.f), y * 2, 0xFF444400, false);
                    }
                }
                stack.popPose();
            }

            guiGraphics.drawString(font, "M:", noteImageLeftX + HL_BUT_X + 14, noteImageY + HL_BUT_Y + 2, 0xFF000000, false);
            guiGraphics.drawString(font, highlightInterval > 1 ? Integer.toString(highlightInterval) : "-", noteImageLeftX + HL_BUT_X + 22, noteImageY + HL_BUT_Y + 2, 0xFF000000, false);

            guiGraphics.drawString(font, "Tempo", noteImageLeftX + BPM_BUT_X - 30, noteImageY + BPM_BUT_Y, 0xFF000000, false);
            guiGraphics.drawString(font, Integer.toString(bpm), noteImageLeftX + BPM_BUT_X - 30, noteImageY + BPM_BUT_Y + 10, 0xFF000000, false);
            drawCursor(guiGraphics, editCursor, 0xFFAA2222);
            if (!this.isSigned) {
                if (editCursor != editCursorEnd) {
                    drawCursor(guiGraphics, editCursorEnd, 0xFFAA2222);
                    drawSelectionRect(guiGraphics);
                }
            } else {
                int k = this.font.width(noteTitle);
                guiGraphics.drawString(font, noteTitle, (int) (noteImageLeftX + (NOTE_IMAGE_WIDTH + NOTE_IMAGE_LEFT_WIDTH - k) / 2.0f), noteImageY + 14, 0xFF990000, false);

                if (this.selfSigned) {
                    drawCursor(guiGraphics, editCursor, 0xFFAA2222);

                    if (editCursor != editCursorEnd) {
                        drawCursor(guiGraphics, editCursorEnd, 0xFFAA2222);
                        drawSelectionRect(guiGraphics);
                    }
                }
            }

            // Neighbor notes
            if (!neighborsHidden) {
                for (ArrayList<NoteEvent> nn : neighborNotes) {
                    for (NoteEvent event : nn) {
                        drawNote(guiGraphics, event, true);
                    }
                }
            }

            // The notes
            for (NoteEvent note : notes) {
                drawNote(guiGraphics, note, false);
            }
        }
        if (previewStarted) {
            int i = previewCursor - 1;

            drawCursor(guiGraphics, i, 0xFFAA8822);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (buttonHelp.isHovered()) {
            guiGraphics.renderTooltip(font, Component.translatable("note.helpTooltip"), mouseX, mouseY);
        }

        if (helpOn) {
            int x = noteImageLeftX + 15;
            int y = noteImageY;
            guiGraphics.fill(x, y, x + 315, y + 220, 0xEE333333);
            stack.pushPose();
            stack.scale(1.2f, 1.2f, 1.2f);
            guiGraphics.drawString(font, Component.translatable("note.helpText0"), (int) ((x + 10) / 1.2f), (int) ((y + 5) / 1.2f), 0xFFEEEE11, false);
            stack.popPose();
            for (int i = 1; i <= 19; i++) {
                Component leftSide = Component.translatable("note.helpText" + i + "a");
                Component rightSide = Component.translatable("note.helpText" + i + "b");
                guiGraphics.drawString(font, leftSide, x + 10, y + 10 + 10 * i, 0xFFEEEE11, false);
                guiGraphics.drawString(font, rightSide, x + 10 + font.width(leftSide), y + 10 + 10 * i, 0xFFEEEEEE, false);
            }
        } else {
            if (buttonHideNeighbors.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.toggleTooltip"), mouseX, mouseY);
            } else if (buttonLockPrevIns.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.lockTooltip"), mouseX, mouseY);
            } else if (buttonPreview.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.previewTooltip"), mouseX, mouseY);
            } else if (buttonRecord.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.recordTooltip"), mouseX, mouseY);
            } else if (bpmDown.isHovered() || bpmUp.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.tempoTooltip"), mouseX, mouseY);
            } else if (hlDown.isHovered() || hlUp.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.measureTooltip"), mouseX, mouseY);
            } else if (sliderSheetVolume.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.sheetVolumeTooltip"), mouseX, mouseY);
            } else if (sliderNoteVolume.isHovered()) {
                guiGraphics.renderTooltip(font, Component.translatable("note.noteVolumeTooltip"), mouseX, mouseY);
            }
        }
    }

    private void drawSelectionRect(GuiGraphics guiGraphics) {
        if (inScreen(editCursor) || inScreen(editCursorEnd) || (editCursor < sliderPosition && editCursorEnd >= sliderPosition + BEATS_IN_SCREEN)) {
            final int selectionColor = 0x882222AA;
            int timeDrawBeginning = Math.max(editCursor - sliderPosition, 0);
            int timeDrawEnd = Math.min(editCursorEnd - sliderPosition, BEATS_IN_SCREEN);

            int x1 = noteToPixelX(timeDrawBeginning);
            int x2 = noteToPixelX(timeDrawEnd);
            int y1 = noteImageY + NOTE_REGION_TOP;
            int y2 = y1 + 36 * 4;

            guiGraphics.fill(x1 + 1, y1, x2 + 2, y2, selectionColor);
        }
    }

    private boolean inScreen(int time) {
        return time >= sliderPosition && time < sliderPosition + BEATS_IN_SCREEN;
    }

    private void drawNote(GuiGraphics guiGraphics, NoteEvent event, boolean isNeighbor) {
        int octave = octaveFromNote(event.note);
        if ((octave >= currentOctavePos && octave < currentOctavePos + 4) && (inScreen(event.time) || inScreen(event.time + event.length))) {
            int timeDrawBeginning = Math.max(event.time - sliderPosition, 0);
            int timeDrawEnd = Math.min(event.time - sliderPosition + event.length, BEATS_IN_SCREEN);

            int xBegin = noteImageLeftX + NOTE_REGION_LEFT + timeDrawBeginning * 3;
            int xEnd = noteImageLeftX + NOTE_REGION_LEFT + timeDrawEnd * 3;
            if (xBegin == xEnd) {
                return;
            }
            int xFillBegin = timeDrawBeginning == event.time - sliderPosition ? xBegin + 1 : xBegin;
            int xFillEnd = timeDrawEnd == event.time - sliderPosition + event.length ? xEnd - 1 : xEnd;

            int y = noteImageY + NOTE_REGION_TOP + (47 - event.note + IItemInstrument.MIN_NOTE) * 3 + currentOctavePos * 36;
            final int outlineColor = (event == currentlyAddedNote || isNeighbor) ? 0x77000000 : 0xFF000000;
            int red = event.volume >= 64 ? 255 : event.volume * 4;
            int green = event.volume < 64 ? 255 : 255 - event.volume * 4;
            final int fillColor = ((event == currentlyAddedNote || isNeighbor) ? 0x77000000 : 0xFF000000) | red << 16 | green << 8;

            guiGraphics.fill(xBegin, y, xEnd, y + 3, outlineColor);
            guiGraphics.fill(xFillBegin, y + 1, xFillEnd, y + 2, fillColor);
        }
    }

    private void setNeighborNextNodeIDs() {
        if (!neighborsHidden) {
            for (int i = 0; i < neighborNotes.size(); i++) {
                ArrayList<NoteEvent> nn = neighborNotes.get(i);
                for (int j = 0; j < nn.size(); j++) {
                    if (nn.get(j).time >= previewCursor) {
                        neighborPreviewNextNoteIDs.set(i, j);
                        break;
                    }
                }
            }
        }

        this.previewCursorStart = editCursor;
        this.previewStarted = false;
    }

    private void startPreview() {
        this.previewCursor = editCursor;
        boolean noteFound = false;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).time >= previewCursor) {
                previewNextNoteID = i;
                noteFound = true;
                break;
            }
        }
        // Only start preview if you find a note to play next
        if (noteFound) {
            setNeighborNextNodeIDs();
            this.previewing = true;
            this.cumMillis = 0;
            this.lastMillis = System.currentTimeMillis();
            this.buttonPreview.setTexStarts(240, 0);

            if (!inScreen(previewCursorStart)) {
                setSliderPos(previewCursorStart);
            }
        }
        updateButtons();
    }

    private void startPreRecording() {
        preRecording = true;
        this.previewCursor = editCursor;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).time >= previewCursor) {
                previewNextNoteID = i;
                break;
            }
        }
        setNeighborNextNodeIDs();
        this.cumMillis = 0;
        this.oldPreRecordBeat = 0;
        this.lastMillis = System.currentTimeMillis();
        this.buttonRecord.setTexStarts(240, 0);

        if (!inScreen(previewCursorStart)) {
            setSliderPos(previewCursorStart);
        }
        updateButtons();
    }

    private void startRecording() {
        this.preRecording = false;
        this.previewing = true;
        this.recording = true;
        this.cumMillis = 0;
        this.lastMillis = System.currentTimeMillis();
        this.buttonRecord.setTexStarts(240, 0);

        if (!inScreen(previewCursorStart)) {
            setSliderPos(previewCursorStart);
        }
        updateButtons();
    }

    private void stopRecording() {
        this.preRecording = false;
        this.previewing = false;
        this.recording = false;
        this.previewStarted = false;
        this.buttonRecord.setTexStarts(176, 0);
        updateLength();
        recordingNotes.clear();
        updateButtons();
    }

    private void setSliderPos(int time) {
        time = Math.min(Math.max(time, 0), maxSliderPosition);

        sliderTime.setValue((float) time / (float) maxSliderPosition);
        sliderTime.applyValue();
    }

    private void stopPreview() {
        this.previewing = false;
        this.previewStarted = false;
        this.buttonPreview.setTexStarts(224, 0);
        updateButtons();
    }

    private void updateLength() {
        updateLength(true);
    }

    private void updateLength(boolean updateSliderPos) {
        lengthBeats = 0;
        if (!notes.isEmpty()) {
            for (NoteEvent event : notes) {
                lengthBeats = (short) (event.time + event.length) > lengthBeats ? (short) (event.time + event.length) : lengthBeats;
            }

            if (updateSliderPos) {
                // Update slider
                int oldMaxSliderPos = maxSliderPosition;
                maxSliderPosition = Math.min(Math.max(lengthBeats + BEATS_IN_SCREEN, 680), MAX_LENGTH_BEATS);
                if (maxSliderPosition > oldMaxSliderPos) {
                    setSliderPos(sliderPosition);
                } else if (maxSliderPosition < oldMaxSliderPos) {
                    setSliderPos(maxSliderPosition - BEATS_IN_SCREEN);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double dmouseX, double dmouseY, int mouseButton) {
        if (helpOn) {
            helpOn = false;
            return true;
        }

        // mouseButton: 0 = left click, 1 = right click, 2 = middle click
        if (super.mouseClicked(dmouseX, dmouseY, mouseButton)) {
            this.setDragging(true);
            return true;
        }

        int mouseX = (int) Math.round(dmouseX);
        int mouseY = (int) Math.round(dmouseY);

        boolean viewingSelfSigned = isSigned && selfSigned;
        boolean composing = !isSigned && !gettingSigned;

        if (!gettingSigned && mouseButton == 1) {
            int mx = mouseX - noteImageLeftX;
            int my = mouseY - noteImageY;
            if (validClick(mx, my)) {
                selectionStart = editCursorEnd = editCursor = ((mx - NOTE_REGION_LEFT) / 3) + sliderPosition;
            }
        }


        if (viewingSelfSigned && mouseButton == 0) {
            editCursorEnd = editCursor;
        }

        if (composing) {
            int mx = mouseX - noteImageLeftX;
            int my = mouseY - noteImageY;
            if (validClick(mx, my)) {
                int nrx = mx - NOTE_REGION_LEFT;
                int nry = my - NOTE_REGION_TOP;

                int time = (nrx / 3) + sliderPosition;
                int note = 47 - (nry / 3) + IItemInstrument.MIN_NOTE + currentOctavePos * 12;
                if (mouseButton == 0) {
                    addNote((byte) note, (short) time);
                    dirtyFlag.hasNotes = true;
                    dirtyFlag.hasLength = true;

                    editCursorEnd = editCursor;
                } else if (mouseButton == 2) {
                    int i = findNote((byte) note, (short) time);
                    if (i >= 0) {
                        NoteEvent event = notes.get(i);
                        noteEditBox.appear(mouseX, mouseY, event);
                    }
                }
            } else {
                // Test current octave clicks
                for (int i = 0; i < 4; i++) {
                    final int x = NOTE_REGION_LEFT - 24;
                    final int y = NOTE_REGION_BOTTOM - 18 - i * 36;
                    if (mx >= x - 10 && mx <= x + 10 && my >= y - 4 && my <= y + 12) {
                        currentOctave = currentOctavePos + i;
                        midiHandler.currentOctave = currentOctave;
                        if (recording) {
                            recordingNotes.clear();
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    private void pushUndo() {
        if (undoStack.size() >= MAX_UNDO_LENGTH) {
            undoStack.removeLast();
        }
        ArrayList<NoteEvent> stackNotes = new ArrayList<>(notes.size());
        for (NoteEvent note : notes) {
            stackNotes.add(new NoteEvent(note));
        }
        undoStack.push(stackNotes);
    }

    private void addNote(byte note, short time) {
        addNote(note, time, true);
    }

    private void addNote(byte note, short time, boolean pushUndo) {
        if (pushUndo) {
            pushUndo();
        }
        int i = findNote(note, time);
        if (i < 0) {
            addNote(note, time, (byte) (127.f * brushVolume));
        } else {
            notes.remove(i);
        }
        updateLength();
    }

    private void addNote(byte note, short time, byte volume) {
        NoteEvent newEvent = new NoteEvent(note, time, volume, (byte) 1);
        currentlyAddedNote = newEvent;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).time > time) {
                notes.add(i, newEvent);
                return;
            }
        }
        notes.add(newEvent);
    }

    private void addRecordingNote(NoteEvent noteEvent) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).time > noteEvent.time) {
                notes.add(i, noteEvent);
                recordingNotes.add(noteEvent);
                return;
            }
        }
        notes.add(noteEvent);
        recordingNotes.add(noteEvent);
    }

    private void finishAddingNote() {
        if (currentlyAddedNote == null) {
            return;
        }
        playSound(currentlyAddedNote, previewInstrument);
        currentlyAddedNote = null;
        updateLength();
    }

    private int findNote(byte note, short time) {
        for (int i = notes.size() - 1; i >= 0; i--) {
            NoteEvent event = notes.get(i);
            if ((event.time <= time && event.endTime() >= time) && event.note == note) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        GuiEventListener focused = getFocused();
        if (focused != null && this.isDragging()) {
            focused.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            return true;
        }

        if (this.tickCount < 10) {
            // This check is for preventing the annoying selection at start problem
            return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
        }
        int mouseX = (int) Math.round(posX);
        int mouseY = (int) Math.round(posY);

        int mx = mouseX - noteImageLeftX;
        int my = mouseY - noteImageY;

        switch (mouseButton) {
            case 0 -> {  // Left-click
                if (currentlyAddedNote != null && validClick(mx, my)) {
                    int time = ((mx - NOTE_REGION_LEFT) / 3) + sliderPosition;
                    if (currentlyAddedNote.time < time && time - currentlyAddedNote.time <= MAX_NOTE_LENGTH) {
                        currentlyAddedNote.length = (byte) (time - currentlyAddedNote.time);
                    }
                }
            }
            case 1 -> {  // Right-click
                if (validClick(mx, my)) {
                    int noteX = ((mx - NOTE_REGION_LEFT) / 3) + sliderPosition;
                    if (selectionStart > noteX) {
                        editCursor = noteX;
                    } else {
                        editCursorEnd = noteX;
                    }
                }
            }
            default -> {
                // Do nothing
            }
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        this.setDragging(false);
        if (noteEditBox.active) {
            noteEditBox.mouseReleased(posX, posY, mouseButton);
            return true;
        }
        if (mouseButton == 0) {
            finishAddingNote();
        }

        return true;
    }

    private void putSpace(int x) {
        if (x == MAX_LENGTH_BEATS - 1) {
            return;
        }
        addEditCursor(1);
        if (lengthBeats == 0 || lengthBeats <= x) {
            return;
        }

        pushUndo();
        dirtyFlag.hasNotes = true;
        dirtyFlag.hasLength = true;
        for (int i = notes.size() - 1; i >= 0; i--) {
            NoteEvent event = notes.get(i);
            if (event.time > x) {
                event.time += (short) 1;
                if (event.time + event.length > MAX_LENGTH_BEATS) {
                    notes.remove(i);
                }
            }
        }
        updateLength();
    }

    private void encodeToClipboard() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        ArrayList<NoteEvent> toBeCopied = new ArrayList<>();
        for (NoteEvent event : notes) {
            if (event.time >= editCursor && event.time <= editCursorEnd && event.endTime() >= editCursor && event.endTime() <= editCursorEnd) {
                toBeCopied.add(event);
            }
        }
        buffer.writeByte(COPY_BEGIN_BYTE);
        buffer.writeInt(editCursorEnd - editCursor);
        buffer.writeInt(toBeCopied.size());
        for (NoteEvent event : toBeCopied) {
            event.time -= (short) editCursor; // Convert time to according to cursor
            event.encodeToBuffer(buffer);
            event.time += (short) editCursor; // Convert time back to normal
        }
        int index = buffer.writerIndex();
        byte[] bytes = new byte[index];
        buffer.getBytes(0, bytes);
        String encodeBytes = Base64.getEncoder().encodeToString(bytes);
        GLFW.glfwSetClipboardString(Minecraft.getInstance().getWindow().getWindow(), encodeBytes);

        editCursorEnd = editCursor;
    }

    private void decodeFromClipboard(boolean pushBack) {
        String encodedMusic = GLFW.glfwGetClipboardString(Minecraft.getInstance().getWindow().getWindow());
        if (encodedMusic != null && !encodedMusic.isEmpty()) {
            byte[] byteArray;
            try { // Try because this can fail with weird clipboard content
                byteArray = Base64.getDecoder().decode(encodedMusic);
            } catch (IllegalArgumentException ex) {
                return;
            }

            int length = 0;
            List<NoteEvent> toBePasted;
            // Check begin byte
            if (byteArray[0] != COPY_BEGIN_BYTE) {
                // Old version

                // Check if all values are valid
                for (byte b : byteArray) {
                    if (b < 0 || b > 48) {
                        Mod.LOGGER.info("User tried to copy invalid data into music: {}", b);
                        return;
                    }
                }

                // Copy values
                toBePasted = ItemMusicSheet.oldMusicToNotes(byteArray);
                if (!toBePasted.isEmpty()) {
                    for (NoteEvent event : toBePasted) {
                        length = (short) (event.time + event.length) > length ? (short) (event.time + event.length) : length;
                    }
                }
            } else {
                // New version
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.copiedBuffer(byteArray));
                buffer.readByte();

                // Read copied time length and note event count
                length = buffer.readInt() + 1;
                int count = buffer.readInt();

                // Read the note events into an array
                toBePasted = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    toBePasted.add(NoteEvent.fromBuffer(buffer));
                }
            }

            pushUndo();
            if (pushBack) {
                // Push back the existing future note events
                for (NoteEvent event : notes) {
                    if (event.time >= editCursor) {
                        event.time += (short) length;
                    }
                }
            }

            for (NoteEvent event : toBePasted) {
                event.time += (short) editCursor;
                notes.add(event);
            }

            NoteEvent.sortNotes(notes);
            NoteEvent.removeDuplicates(notes);

            updateLength();
            editCursor += length;
            editCursorEnd = editCursor;

            dirtyFlag.hasNotes = true;
            dirtyFlag.hasLength = true;
        }
    }

    private void delAtCursor(int x) {
        boolean doSort = false;
        for (int i = notes.size() - 1; i >= 0; i--) {
            NoteEvent event = notes.get(i);
            int start = event.time;
            int end = start + event.length;

            if (start > x) {
                event.time -= (short) 1;
                doSort = true;
            } else if (start < x && end > x) {
                event.length--;
            } else if (start == x) {
                if (event.length == 1) {
                    notes.remove(i);
                } else {
                    event.length--;
                }
            }
        }
        if (doSort) {
            NoteEvent.sortNotes(notes);
        }
        updateLength();
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        setFocused(null);
        super.keyReleased(keyCode, scanCode, modifiers);
        int firstScanCode = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
        int lastScanCode = firstScanCode + 11;
        if (scanCode >= firstScanCode && scanCode <= lastScanCode && currentOctave >= 0 && recording) {
            endSound(IItemInstrument.noteToId((byte) ((scanCode - firstScanCode + IItemInstrument.MIN_NOTE) + 12 * currentOctave)));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        setFocused(null);
        super.keyPressed(keyCode, scanCode, modifiers);

        // Copying when viewing self-signed
        boolean viewingSelfSigned = isSigned && selfSigned;
        if (viewingSelfSigned) {
            if (keyCode == GLFW.GLFW_KEY_C && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                encodeToClipboard();
            }
            if (keyCode == GLFW.GLFW_KEY_A && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                editCursor = 0;
                editCursorEnd = lengthBeats - 1;
            }
        }

        if (!this.isSigned) {
            if (this.gettingSigned) {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_BACKSPACE -> {
                        if (!this.noteTitle.isEmpty()) {
                            this.noteTitle = this.noteTitle.substring(0, this.noteTitle.length() - 1);
                            this.updateButtons();
                        }
                    }
                    case GLFW.GLFW_KEY_ENTER -> {
                        if (!this.noteTitle.isEmpty()) {
                            dirtyFlag.hasSigned = true;
                            dirtyFlag.hasTitle = true;
                            this.isSigned = true;
                            if (this.minecraft != null) {
                                this.minecraft.setScreen(null);
                            }
                        }
                    }
                    default -> {
                        // do nothing
                    }
                }
                return true;
            } else {
                int x = editCursor;
                boolean resetEditCursorEnd = true;
                switch (keyCode) {
                    case GLFW.GLFW_KEY_DELETE -> {
                        if (lengthBeats == 0 || lengthBeats <= x) break;
                        pushUndo();
                        dirtyFlag.hasNotes = true;
                        dirtyFlag.hasLength = true;
                        if (editCursorEnd == x) {
                            delAtCursor(x);
                        } else {
                            deleteSelected();
                            updateLength();
                        }
                    }
                    case GLFW.GLFW_KEY_BACKSPACE -> {
                        if (editCursorEnd == x) {
                            if (x == 0) {
                                break;
                            }
                            if (lengthBeats == 0 || lengthBeats < x) {
                                addEditCursor(-1);
                            } else {
                                pushUndo();

                                dirtyFlag.hasNotes = true;
                                dirtyFlag.hasLength = true;
                                addEditCursor(-1);
                                delAtCursor(editCursor);
                            }
                        } else {
                            pushUndo();

                            dirtyFlag.hasNotes = true;
                            dirtyFlag.hasLength = true;
                            deleteSelected();
                            updateLength();
                        }
                    }
                    case GLFW.GLFW_KEY_SPACE -> putSpace(x - 1);
                    case GLFW.GLFW_KEY_RIGHT -> {
                        addEditCursor(1);
                        if (editCursor > MAX_LENGTH_BEATS - 1) setEditCursor(MAX_LENGTH_BEATS - 1);
                    }
                    case GLFW.GLFW_KEY_LEFT -> {
                        addEditCursor(-1);
                        if (editCursor < 0) setEditCursor(0);
                    }
                    case GLFW.GLFW_KEY_ENTER -> previewButton();
                    case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> recordButton();
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
                    case GLFW.GLFW_KEY_H -> toggleHelp();
                    case GLFW.GLFW_KEY_Z -> {
                        if ((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                            if (noteEditBox.active) {
                                break;
                            }
                            if (!undoStack.isEmpty()) {
                                notes = undoStack.pop();
                                updateLength(false);
                            }
                        }
                    }
                    case GLFW.GLFW_KEY_A -> {
                        if ((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                            editCursor = 0;
                            editCursorEnd = lengthBeats - 1;
                            resetEditCursorEnd = false;
                        } else {
                            if (editCursor == editCursorEnd) {
                                currentOctave--;
                                if (currentOctave < -2) {
                                    currentOctave = -2;
                                }
                                midiHandler.currentOctave = currentOctave;
                                if (recording) {
                                    recordingNotes.clear();
                                }
                            } else {
                                boolean changed = false;
                                for (NoteEvent event : notes) {
                                    if (event.endTime() >= editCursor && event.time <= editCursorEnd && IItemInstrument.noteToId(event.note) / 12 > 0) {
                                        if (!changed) {
                                            pushUndo();
                                            dirtyFlag.hasNotes = true;
                                            dirtyFlag.hasLength = true;

                                            changed = true;
                                        }
                                        event.note -= (byte) 12;
                                    }
                                }
                                resetEditCursorEnd = false;
                            }
                        }
                    }
                    case GLFW.GLFW_KEY_S -> {
                        if (editCursor == editCursorEnd) {
                            currentOctave++;
                            if (currentOctave > 7) {
                                currentOctave = 7;
                            }
                            midiHandler.currentOctave = currentOctave;
                            if (recording) {
                                recordingNotes.clear();
                            }
                        } else {
                            boolean changed = false;
                            for (NoteEvent event : notes) {
                                if (event.endTime() >= editCursor && event.time <= editCursorEnd && IItemInstrument.noteToId(event.note) / 12 < 7) {
                                    if (!changed) {
                                        pushUndo();
                                        dirtyFlag.hasNotes = true;
                                        dirtyFlag.hasLength = true;

                                        changed = true;
                                    }
                                    event.note += (byte) 12;
                                }
                            }
                            resetEditCursorEnd = false;
                        }
                    }
                    case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> resetEditCursorEnd = false;
                    default -> {
                        int firstScanCode = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
                        int lastScanCode = firstScanCode + 11;
                        if (scanCode >= firstScanCode && scanCode <= lastScanCode && currentOctave >= 0) {
                            if (recording) {
                                startSound(IItemInstrument.noteToId((byte) ((scanCode - firstScanCode + IItemInstrument.MIN_NOTE) + 12 * currentOctave)), (byte) 100);
                            } else {
                                putSpace(x - 1);
                                addNote((byte) ((scanCode - firstScanCode + IItemInstrument.MIN_NOTE) + 12 * currentOctave), (short) x, false);
                                finishAddingNote();
                            }
                        }
                    }
                }
                if (resetEditCursorEnd) {
                    editCursorEnd = editCursor;
                }
            }
        }
        return true;
    }

    private void deleteSelected() {
        boolean doSort = false;
        int cutLen = editCursorEnd - editCursor;

        for (int i = notes.size() - 1; i >= 0; i--) {
            NoteEvent event = notes.get(i);
            int start = event.time;
            int end = event.endTime();

            if (start >= editCursor && end <= editCursorEnd) {
                // fully inside -> remove
                notes.remove(i);
            } else if (start < editCursor && end >= editCursor && end <= editCursorEnd) {
                // overlaps tail -> trim to editCursor
                event.length = (byte) (editCursor - start);
            } else if (start >= editCursor && start <= editCursorEnd) {
                // starts in cut, continues after -> shift the start to after cut, shorten
                event.length = (byte) (end - editCursorEnd);
                event.time = (short) (editCursor + 1);
                doSort = true;
            } else if (start < editCursor && end > editCursorEnd) {
                // spans across the whole cut -> trim to before cut
                event.length = (byte) (editCursor - start);
            } else if (start > editCursorEnd) {
                // after cut -> shift left
                event.time -= (short) cutLen;
                doSort = true;
            }
        }
        if (doSort) {
            NoteEvent.sortNotes(notes);
        }
    }

    private void setEditCursor(int x) {
        if (editCursor != editCursorEnd) {
            editCursor = x;
        } else {
            editCursor = x;
            editCursorEnd = x;
        }
    }

    private void addEditCursor(int x) {
        setEditCursor(editCursor + x);
    }

    @Override
    public boolean charTyped(char typedChar, int something) {
        super.charTyped(typedChar, something);

        if (!this.isSigned) {
            if (this.gettingSigned && this.noteTitle.length() < 16 && StringUtil.isAllowedChatCharacter(typedChar)) {
                this.noteTitle = this.noteTitle + typedChar;
                this.updateButtons();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (scrollY != 0.d) {
            if (scrollY > 0) {
                octaveUp.playDownSound(Minecraft.getInstance().getSoundManager());
                octaveUp.onPress();
            } else if (scrollY < 0) {
                octaveDown.playDownSound(Minecraft.getInstance().getSoundManager());
                octaveDown.onPress();
            }
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void removed() {
        if (dirtyFlag.hasAny()) {
            if (recording) {
                stopRecording();
            }
            if (dirtyFlag.hasNotes || dirtyFlag.hasLength) {
                version++;
                dirtyFlag.hasVersion = true;
                MusicManagerClient.setMusicData(id, version, notes);
            }

            try {
                MusicUpdatePacket pack = MusicUpdatePacket.create(dirtyFlag, notes, lengthBeats, bps, volume, isSigned,
                        noteTitle, (byte) previewInstrument, prevInsLocked, id, version, highlightInterval);
                sendToServer(pack);
            } catch (ImportMusicSendPacket.NotesTooLargeException e) {
                int partsCount = (int) Math.ceil((double) notes.size() / (double) MAX_NOTES_IN_PACKET);

                try {
                    MusicUpdatePacket pack = MusicUpdatePacket.create(dirtyFlag, null, lengthBeats, bps, volume, isSigned,
                            noteTitle, (byte) previewInstrument, prevInsLocked, id, version, highlightInterval);
                    NotesPartAckFromServerPacketHandler.addCallback(id, () -> sendToServer(pack));
                    for (int i = 0; i < partsCount; i++) {
                        List<NoteEvent> part = notes.subList(i * MAX_NOTES_IN_PACKET, Math.min((i + 1) * MAX_NOTES_IN_PACKET, notes.size()));
                        sendToServer(new SendNotesPartToServerPacket(id, partsCount, i, part));
                    }
                } catch (ImportMusicSendPacket.NotesTooLargeException ex) {
                    Mod.LOGGER.error("Could not send partial notes to server:", ex);
                }
            }
        }
        if (SoundEvents.CLOSE_SCROLL != null) {
            editingPlayer.playSound(SoundEvents.CLOSE_SCROLL, 1.0f, 0.8f + editingPlayer.level().random.nextFloat() * 0.4f);
        }
    }

    private boolean validClick(int x, int y) {
        return x <= NOTE_REGION_RIGHT && x >= NOTE_REGION_LEFT && y <= NOTE_REGION_BOTTOM && y >= NOTE_REGION_TOP;
    }

    public enum MidiControl {
        BEGINNING, END, STOP, PREVIEW, RECORD
    }

    public static class ChangeableImageButton extends Button {
        protected final ResourceLocation resourceLocation;
        protected final int yDiffText;
        protected final int texWidth;
        protected final int texHeight;
        protected int xTexStart;
        protected int yTexStart;

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, OnPress onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, 256, 256, onClick);
        }

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, Component.empty());
        }

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick, Component message) {
            super(x, y, width, height, message, onClick, Button.DEFAULT_NARRATION);
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.xTexStart = xTexStart;
            this.yTexStart = yTexStart;
            this.yDiffText = yDiffText;
            this.resourceLocation = texture;
        }

        public void setTexStarts(int x, int y) {
            this.xTexStart = x;
            this.yTexStart = y;
        }

        protected int preRender() {
            RenderSystem.setShaderTexture(0, this.resourceLocation);
            GlStateManager._disableDepthTest();
            int yTexStartNew = this.yTexStart;
            if (this.isHovered && this.active) {
                yTexStartNew += this.yDiffText;
            }
            return yTexStartNew;
        }

        protected void postRender() {
            GlStateManager._enableDepthTest();
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int yTexStartNew = preRender();
            guiGraphics.blit(resourceLocation, this.getX(), this.getY(), this.xTexStart, yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
            postRender();
        }
    }

    public class LockImageButton extends ChangeableImageButton {

        public LockImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, OnPress onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, 256, 256, onClick);
        }

        public LockImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, Component.empty());
        }

        public LockImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick, Component message) {
            super(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, message);
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int yTexStartNew = preRender();

            guiGraphics.blit(resourceLocation, this.getX(), this.getY(), this.xTexStart, yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
            if (prevInsLocked) {
                guiGraphics.blit(resourceLocation, this.getX(), this.getY(), 0, (float) this.texHeight - this.height, this.width, this.height, this.texWidth, this.texHeight);
            }

            postRender();
        }
    }

    public class NoteEditBox extends AbstractWidget {
        public final Button buttonNoteDown;
        public final Button buttonNoteUp;
        public final Button buttonLengthDown;
        public final Button buttonLengthUp;
        public final Button buttonExit;
        public final Button buttonPrev;
        public final BetterSlider sliderVelocity;
        private static final int NOTE_Y = 18;
        private static final int LENGTH_Y = 29;
        private final AbstractWidget[] children = new AbstractWidget[7];
        private static final String[] noteNames = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
        private static final String[] noteNamesSolfege = {"La", "La#", "Si", "Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#"};
        private NoteEvent event;
        private NoteSound previewSound;
        private boolean changed;

        public NoteEditBox(int x, int y, int w, int h, Component msg) {
            super(x, y, w, h, msg);
            sliderVelocity = new BetterSlider(10, 0, 50, 10, Component.literal("Vol "), Component.empty(), 0, 100, 50, true) {
                @Override
                public void applyValue() {
                    setChanged();
                    event.volume = (byte) Math.round(value * 127.0f);
                }
            };
            buttonNoteDown = Button.builder(Component.translatable(NOTE_LEFT_STR_KEY), button -> {
                if (event.note > 0) {
                    setChanged();
                    event.note--;
                    playPrev();
                }
            }).bounds(0, 0, 10, 8).build();
            buttonNoteUp = Button.builder(Component.translatable(NOTE_RIGHT_STR_KEY), button -> {
                if (event.note < 95) {
                    setChanged();
                    event.note++;
                    playPrev();
                }
            }).bounds(0, 0, 10, 8).build();
            buttonLengthDown = Button.builder(Component.translatable(NOTE_LEFT_STR_KEY), button -> {
                if (event.length > 1) {
                    setChanged();
                    event.length--;
                    playPrev();
                }
            }).bounds(0, 0, 10, 8).build();
            buttonLengthUp = Button.builder(Component.translatable(NOTE_RIGHT_STR_KEY), button -> {
                if (event.length < MAX_NOTE_LENGTH) {
                    setChanged();
                    event.length++;
                    playPrev();
                }
            }).bounds(0, 0, 10, 8).build();
            buttonExit = Button.builder(Component.translatable("note.exitButton"), button -> {
                this.visible = false;
                this.active = false;
                if (previewSound != null && !previewSound.isStopped()) {
                    previewSound.stopSound();
                }
            }).bounds(0, 0, 10, 10).build();
            buttonPrev = Button.builder(Component.translatable("note.startPreviewButton"), button -> playPrev()).
                    bounds(0, 0, 10, 10).build();

            children[0] = sliderVelocity;
            children[1] = buttonNoteDown;
            children[2] = buttonNoteUp;
            children[3] = buttonLengthDown;
            children[4] = buttonLengthUp;
            children[5] = buttonExit;
            children[6] = buttonPrev;
        }

        private void setChanged() {
            if (!changed) {
                changed = true;
                dirtyFlag.hasNotes = true;
                dirtyFlag.hasLength = true;
                pushUndo();
            }
        }

        private void playPrev() {
            if (previewSound != null && !previewSound.isStopped()) {
                previewSound.stopSound();
            }
            previewSound = playSound(event, previewInstrument);
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            if (this.visible && event != null) {
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFFEEEEEE);
                Minecraft minecraft = Minecraft.getInstance();
                Font font = minecraft.font;
                int noteId = IItemInstrument.noteToId(event.note);
                int octave = noteId / 12;
                guiGraphics.drawString(font, noteNames[noteId % 12] + (noteId % 12 < 3 ? octave : octave + 1), getX() + 15, getY() + NOTE_Y, 0xFFD3C200, false);
                guiGraphics.drawString(font, noteNamesSolfege[noteId % 12], getX() + 35, getY() + NOTE_Y, 0xFFD3C200, false);
                guiGraphics.drawString(font, event.length + (event.length == 1 ? " Beat" : " Beats"), getX() + 15, getY() + LENGTH_Y, 0xFF495EE5, false);

                for (AbstractWidget widget : children) {
                    widget.render(guiGraphics, mouseX, mouseY, partialTicks);
                }

                if (buttonPrev.isHovered()) {
                    guiGraphics.renderTooltip(font, Component.translatable("note.previewNoteTooltip"), mouseX, mouseY);
                } else if (buttonExit.isHovered()) {
                    guiGraphics.renderTooltip(font, Component.translatable("note.closeNoteTooltip"), mouseX, mouseY);
                }
            }
        }

        public void appear(int x, int y, NoteEvent event) {
            changed = false;
            this.setX(x);
            this.setY(y);
            this.visible = true;
            this.active = true;
            this.event = event;
            sliderVelocity.setX(x + 10);
            int sliderY = 41;
            sliderVelocity.setY(y + sliderY);
            sliderVelocity.setValue(event.floatVolume() * 100.0f);
            sliderVelocity.applyValue();

            buttonNoteDown.setX(x + 3);
            buttonNoteDown.setY(y + NOTE_Y);
            buttonNoteUp.setX(x + 59);
            buttonNoteUp.setY(y + NOTE_Y);

            buttonLengthDown.setX(x + 3);
            buttonLengthDown.setY(y + LENGTH_Y);
            buttonLengthUp.setX(x + 59);
            buttonLengthUp.setY(y + LENGTH_Y);

            buttonExit.setX(x + 59);
            buttonExit.setY(y + 2);

            buttonPrev.setX(x + 3);
            buttonPrev.setY(y + 2);

            playPrev();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (this.active && this.visible) {
                if (mouseButton == 2) {
                    this.visible = false;
                    this.active = false;
                }

                for (AbstractWidget widget : children) {
                    if (mouseX >= widget.getX() && mouseX < widget.getX() + widget.getWidth() &&
                            mouseY >= widget.getY() && mouseY < widget.getY() + widget.getHeight()) {
                        widget.mouseClicked(mouseX, mouseY, mouseButton);
                        return true;
                    }
                }

                boolean flag = this.clicked(mouseX, mouseY);
                if (flag) {
                    this.onClick(mouseX, mouseY);
                } else {
                    this.visible = false;
                    this.active = false;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
            if (posX >= sliderVelocity.getX() && posX < sliderVelocity.getX() + sliderVelocity.getWidth() &&
                    posY >= sliderVelocity.getY() && posY < sliderVelocity.getY() + sliderVelocity.getHeight()) {
                sliderVelocity.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            }
            return true;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        @Override
        public boolean mouseReleased(double posX, double posY, int mouseButton) {
            sliderVelocity.onRelease(posX, posY);
            return true;
        }
    }
}
