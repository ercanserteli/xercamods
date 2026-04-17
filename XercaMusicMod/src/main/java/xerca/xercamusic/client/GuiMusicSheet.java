package xerca.xercamusic.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.VolumeMarker;
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
    static final int NOTE_REGION_LEFT = 44;
    static final int NOTE_REGION_TOP = 39;
    static final int NOTE_REGION_RIGHT = 316;
    static final int NOTE_REGION_BOTTOM = 182;
    private static final int BPM_BUT_W = 10;
    private static final int BPM_BUT_H = 10;
    private static final int BPM_BUT_X = 245;
    private static final int BPM_BUT_Y = 12;
    private static final int HL_BUT_X = 261;
    private static final int HL_BUT_Y = 23;
    private static final int[] OCTAVE_COLORS = {0xFF5B3200, 0xFFFF0000, 0xFF0AEE00, 0xFF0059FF, 0xFF7B00FF, 0xFFEF00B7, 0xFF00E2DF, 0XFFF4E800};
    private static final int[] OCTAVE_COLORS_TRANS = {0x165B3200, 0x16FF0000, 0x160AEE00, 0x160059FF, 0x167B00FF, 0x16EF00B7, 0x1600E2DF, 0X16F4E800};
    static final int MAX_LENGTH_BEATS = 32000;
    static final byte COPY_BEGIN_BYTE = (byte) 50;
    private static final int MAX_NOTE_LENGTH = 60;
    static final int MAX_UNDO_LENGTH = 16;
    private static final String NOTE_LEFT_STR_KEY = "note.leftButton";
    private static final String NOTE_RIGHT_STR_KEY = "note.rightButton";
    static int currentOctave = 1;
    static float brushVolume = 0.5f;
    private final Player editingPlayer;
    private final NoteSound[] notePlaySounds;
    final ArrayList<NoteEvent> recordingNotes = new ArrayList<>();
    private final boolean[] buttonPushStates = new boolean[IItemInstrument.TOTAL_NOTES];
    private final UUID id;
    final MusicUpdatePacket.FieldFlag dirtyFlag = new MusicUpdatePacket.FieldFlag();
    record UndoState(ArrayList<NoteEvent> notes, ArrayList<VolumeMarker> volumeMarkers) {}
    final Deque<UndoState> undoStack = new ArrayDeque<>(MAX_UNDO_LENGTH);
    private final ArrayList<ArrayList<NoteEvent>> neighborNotes = new ArrayList<>();
    private final ArrayList<Float> neighborVolumes = new ArrayList<>();
    private final ArrayList<Integer> neighborPreviewNextNoteIDs = new ArrayList<>();
    private final ArrayList<Integer> neighborPrevInstruments = new ArrayList<>();
    final MidiHandler midiHandler;
    private int noteImageX;
    int noteImageLeftX;
    int noteImageY;
    private byte highlightInterval = 12;
    boolean isSigned;
    private int generation;
    boolean gettingSigned;
    boolean previewing;
    private boolean previewStarted;
    boolean recording;
    boolean preRecording;
    private boolean preRecordPlayTick;
    private int previewCursor;
    private int previewCursorStart;
    private int oldPreRecordBeat;
    int editCursor;
    int editCursorEnd;
    int selectionStart; // where the first right click happened when selecting
    int tickCount;
    String noteTitle = "";
    private Button bpmUp;
    private Button bpmDown;
    Button octaveUp;
    Button octaveDown;
    private Button hlUp;
    private Button hlDown;
    private Button buttonSign;
    private Button buttonCancel;
    private Button buttonFinalize;
    private Button buttonHelp;
    private BetterSlider sliderTime;
    private BetterSlider sliderSheetVolume;
    private BetterSlider sliderNoteVolume;
    NoteEditBox noteEditBox;
    MarkerEditBox markerEditBox;
    private ChangeableImageButton buttonPreview;
    private ChangeableImageButton buttonRecord;
    private ChangeableImageButton buttonHideNeighbors;
    private LockImageButton buttonLockPrevIns;
    private boolean neighborsHidden;
    private boolean prevInsLocked;
    boolean selfSigned;
    private int version;
    ArrayList<NoteEvent> notes = new ArrayList<>();
    ArrayList<VolumeMarker> volumeMarkers = new ArrayList<>();
    short lengthBeats = 0;
    private byte bps = 8;
    private int bpm;
    int previewInstrument = -1;
    private long lastMillis;
    private long cumMillis;
    private int previewNextNoteID;
    NoteEvent currentlyAddedNote;
    VolumeMarker currentlyAddedMarker;  // For crescendo/decrescendo being placed
    short markerStartTime;  // Starting time when creating a volume marker
    byte markerStartNote;   // Starting note when creating a volume marker
    boolean creatingCrescendo;  // true for crescendo, false for decrescendo
    boolean glissandoMode;          // Whether we're in glissando placement mode
    NoteEvent glissandoSourceNote;  // The source note for glissando (after first click)
    ArrayList<Byte> glissandoPendingWaypoints; // Accumulated waypoints during multi-point placement
    ArrayList<Byte> glissandoPendingPositions; // Beat index (1..note length) for each pending waypoint while editing
    private int renderMouseX = -1;
    private int renderMouseY = -1;
    int sliderPosition = 0;
    private int maxSliderPosition = 500;
    int currentOctavePos = 1;
    private float volume = 1.f;
    static final int maxNoteLength = 120;  // Max note length in beats (byte max is 127)
    boolean helpOn = false;
    int helpScrollOffset = 0;
    private int helpContentHeight = 0;
    private final int[] helpSectionContentY = new int[7];
    private int helpPanelX, helpPanelY, helpPanelW, helpPanelBottom;
    private int helpContentTop, helpContentBottomY;
    private int helpTabY;
    private final int[] helpTabX = new int[7];
    private final int[] helpTabW = new int[7];
    private static final int HELP_TAB_H = 12;
    private static final String[][] HELP_SECTIONS = {
        {"note.helpSection.mouse",
         "note.helpMouse1a", "note.helpMouse1b",
         "note.helpMouse2a", "note.helpMouse2b",
         "note.helpMouse3a", "note.helpMouse3b",
         "note.helpMouse4a", "note.helpMouse4b"},
        {"note.helpSection.navigation",
         "note.helpNav1a", "note.helpNav1b",
         "note.helpNav2a", "note.helpNav2b",
         "note.helpNav3a", "note.helpNav3b",
         "note.helpNav4a", "note.helpNav4b",
         "note.helpNav5a", "note.helpNav5b"},
        {"note.helpSection.playback",
         "note.helpPlay1a", "note.helpPlay1b",
         "note.helpPlay2a", "note.helpPlay2b",
         "note.helpPlay3a", "note.helpPlay3b"},
        {"note.helpSection.editing",
         "note.helpEdit1a", "note.helpEdit1b",
         "note.helpEdit2a", "note.helpEdit2b",
         "note.helpEdit3a", "note.helpEdit3b",
         "note.helpEdit4a", "note.helpEdit4b",
         "note.helpEdit5a", "note.helpEdit5b",
         "note.helpEdit6a", "note.helpEdit6b",
         "note.helpEdit7a", "note.helpEdit7b",
         "note.helpEdit8a", "note.helpEdit8b",
         "note.helpEdit9a", "note.helpEdit9b"},
        {"note.helpSection.effects",
         "note.helpFx1a", "note.helpFx1b",
         "note.helpFx2a", "note.helpFx2b",
         "note.helpFx3a", "note.helpFx3b"},
        {"note.helpSection.tempo",
         "note.helpTempo1a", "note.helpTempo1b",
         "note.helpTempo2a", "note.helpTempo2b"},
        {"note.helpSection.misc",
         "note.helpMisc1a", "note.helpMisc1b",
         "note.helpMisc2a", "note.helpMisc2b"}
    };
    boolean rectSelection = false;  // Whether current selection is rectangular (note-bounded)
    byte rectSelectNoteTop;          // Highest note in rectangular selection
    byte rectSelectNoteBottom;       // Lowest note in rectangular selection
    byte rectSelectNoteStart;        // The note where rect selection started (for drag direction)

    // Track currently playing sounds for dynamic volume updates during preview
    private record PreviewActiveSound(NoteSound sound, NoteEvent event, VolumeMarker marker) {}
    private final ArrayList<PreviewActiveSound> previewActiveSounds = new ArrayList<>();
    private final SheetInputHandler inputHandler;

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
                if(data.volumeMarkers() != null){
                    volumeMarkers.addAll(data.volumeMarkers());
                }
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
        this.inputHandler = new SheetInputHandler(this);
    }

    // ---- Super call wrappers for SheetInputHandler ----

    boolean callSuperMouseClicked(double x, double y, int btn) {
        return super.mouseClicked(x, y, btn);
    }

    boolean callSuperMouseDragged(double x, double y, int btn, double dx, double dy) {
        return super.mouseDragged(x, y, btn, dx, dy);
    }

    boolean callSuperKeyPressed(int key, int scan, int mods) {
        return super.keyPressed(key, scan, mods);
    }

    boolean callSuperKeyReleased(int key, int scan, int mods) {
        return super.keyReleased(key, scan, mods);
    }

    boolean callSuperCharTyped(char c, int mods) {
        return super.charTyped(c, mods);
    }

    boolean callSuperMouseScrolled(double x, double y, double sx, double sy) {
        return super.mouseScrolled(x, y, sx, sy);
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

    void startSound(int noteId, byte volume) {
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

    void endSound(int noteId) {
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
                sliderPosition = (int) (value * maxSliderPosition);
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
        this.markerEditBox = this.addRenderableWidget(new MarkerEditBox(0, 0, 90, 75, Component.empty()));

        this.buttonHelp = this.addRenderableWidget(Button.builder(Component.literal("?"), button -> toggleHelp()).
                bounds(noteImageLeftX + NOTE_REGION_RIGHT + 30, noteImageY + BPM_BUT_Y, 20, 20).build());

        updateButtons();

        updateLength();
    }

    void previewButton() {
        if (!previewing) {
            startPreview();
        } else {
            stopPreview();
        }
    }

    void recordButton() {
        if (!recording && !preRecording) {
            startPreRecording();
        } else {
            stopRecording();
        }
    }

    void updateButtons() {
        boolean notRecording = !this.recording && !this.preRecording;
        boolean editable = !this.isSigned || this.selfSigned || this.generation > 1;
        boolean hideForHelp = helpOn;
        boolean hideForGlissando = glissandoMode;
        boolean showNormal = !hideForHelp && !hideForGlissando && !this.gettingSigned;

        if (!this.isSigned) {
            this.buttonSign.visible = !this.gettingSigned;
            this.buttonSign.active = !helpOn && notRecording;
            this.buttonCancel.visible = this.gettingSigned;
            this.buttonFinalize.visible = this.gettingSigned;
            this.buttonFinalize.active = !this.noteTitle.trim().isEmpty();
        }
        this.bpmDown.visible = this.bpmUp.visible = showNormal && editable;
        this.bpmDown.active = this.bpmUp.active = notRecording;
        this.buttonPreview.visible = showNormal;
        this.buttonPreview.active = notRecording;
        this.buttonLockPrevIns.visible = showNormal;
        this.buttonLockPrevIns.active = editable && notRecording;
        this.sliderSheetVolume.visible = showNormal;
        this.sliderSheetVolume.active = editable && notRecording;
        this.noteEditBox.visible = false;
        this.noteEditBox.active = false;
        this.markerEditBox.visible = false;
        this.markerEditBox.active = false;
        this.octaveDown.visible = showNormal;
        this.octaveUp.visible = showNormal;
        this.sliderTime.visible = showNormal;
        this.sliderTime.active = notRecording;
        this.hlUp.visible = showNormal && editable;
        this.hlUp.active = editable && notRecording;
        this.hlDown.visible = showNormal && editable;
        this.hlDown.active = editable && notRecording;
        this.sliderNoteVolume.active = (this.sliderNoteVolume.visible = !hideForHelp && !hideForGlissando && !this.isSigned && !this.gettingSigned) && notRecording;
        this.buttonHelp.active = (this.buttonHelp.visible = !this.isSigned && !this.gettingSigned) && notRecording;
        this.buttonHideNeighbors.visible = showNormal && !this.neighborNotes.isEmpty();
        this.buttonHideNeighbors.active = notRecording;
        this.buttonRecord.visible = showNormal && !this.isSigned;
        this.buttonRecord.active = this.recording || this.preRecording || !this.previewing;
    }

    void toggleHelp() {
        helpOn = !helpOn;
        helpScrollOffset = 0;
        updateButtons();
    }

    boolean handleHelpClick(int mouseX, int mouseY) {
        // Check tab clicks
        if (mouseY >= helpTabY && mouseY < helpTabY + HELP_TAB_H) {
            for (int i = 0; i < HELP_SECTIONS.length; i++) {
                if (mouseX >= helpTabX[i] && mouseX < helpTabX[i] + helpTabW[i]) {
                    helpScrollOffset = helpSectionContentY[i];
                    return true;
                }
            }
        }
        // Click inside panel absorbs the click (keep help open)
        if (mouseX >= helpPanelX && mouseX < helpPanelX + helpPanelW
                && mouseY >= helpPanelY && mouseY < helpPanelBottom) {
            return true;
        }
        return false;
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

    NoteSound playSound(NoteEvent event, int previewInstrument) {
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

        // Check if any volume marker fully contains this note
        float noteVolume = event.floatVolume();
        for (VolumeMarker marker : volumeMarkers) {
            if (marker.fullyContains(event.time, event.length, event.note)) {
                noteVolume = marker.getVolumeAt(event.time);
                break;  // First matching marker wins
            }
        }
        final float effectiveVolume = noteVolume;

        NoteSound sound;
        try {
            sound = onlyCallOnClient(() -> () ->
                    ClientStuff.playNote(insSound.sound(), editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(),
                            sheetVolume*effectiveVolume, insSound.pitch(), (byte)beatsToTicks(event.length)));
        } catch (Exception e) {
            Mod.LOGGER.error("Error playing preview sound", e);
            return null;
        }

        // Apply glissando (smooth pitch slide)
        if (event.hasGlissando() && sound != null) {
            byte[] wps = event.getEffectiveWaypoints();
            if (wps != null && wps.length > 0) {
                float[] pitchWaypoints = new float[wps.length];
                for (int i = 0; i < wps.length; i++) {
                    pitchWaypoints[i] = insSound.pitch() * (float)Math.pow(2.0, wps[i] / 12.0);
                }
                byte[] posBuf = event.getEffectivePositions();
                if (posBuf != null && posBuf.length == wps.length) {
                    float[] posFloats = new float[posBuf.length];
                    for (int i = 0; i < posBuf.length; i++) {
                        posFloats[i] = (posBuf[i] & 0xFF) / 100.0f;
                    }
                    sound.setGlissando(pitchWaypoints, posFloats, beatsToTicks(event.length));
                } else {
                    sound.setGlissando(pitchWaypoints, beatsToTicks(event.length));
                }
            }
        }

        return sound;
    }

    private int beatsToTicks(int beats) {
        return Math.round(beats * 20.0f / bps);
    }

    private void playPreviewSound(int curStart, int curEnd) {
        if (previewNextNoteID < notes.size()) {
            NoteEvent event = notes.get(previewNextNoteID);
            while (event.time >= curStart && event.time < curEnd) {
                if(!recordingNotes.contains(event)){
                    NoteSound sound = playSound(event, previewInstrument);
                    // Track sustained notes inside volume markers for dynamic volume
                    if (sound != null && event.length > 1) {
                        for (VolumeMarker marker : volumeMarkers) {
                            if (marker.fullyContains(event.time, event.length, event.note)) {
                                previewActiveSounds.add(new PreviewActiveSound(sound, event, marker));
                                break;
                            }
                        }
                    }
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
        renderMouseX = mouseX;
        renderMouseY = mouseY;
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
                        updatePreviewActiveSounds(previewCursor);
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
        } else if (!helpOn) {
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
                final int y = noteImageY + NOTE_REGION_BOTTOM - 18 - i*36;
                if(currentOctave == i + currentOctavePos){
                    // Prominent active octave indicator: 50% transparent border + filled background
                    int color = OCTAVE_COLORS[i + currentOctavePos];
                    int halfAlpha = (color & 0x00FFFFFF) | 0x80000000; // 50% transparent
                    int bgColor = (color & 0x00FFFFFF) | 0x30000000;  // Light background fill
                    guiGraphics.fill(x-11, y-5, x+11, y+13, halfAlpha);        // Outer border
                    guiGraphics.fill(x-10, y-4, x+10, y+12, bgColor);          // Filled background
                    guiGraphics.fill(x-10, y-4, x+10, y-3, halfAlpha);         // Top edge
                    guiGraphics.fill(x-10, y+11, x+10, y+12, halfAlpha);       // Bottom edge
                    guiGraphics.fill(x-10, y-4, x-9, y+12, halfAlpha);         // Left edge
                    guiGraphics.fill(x+9, y-4, x+10, y+12, halfAlpha);         // Right edge
                    // Draw a small triangle/arrow indicator
                    guiGraphics.fill(x+11, y+1, x+13, y+7, halfAlpha);
                    guiGraphics.fill(x+13, y+2, x+14, y+6, halfAlpha);
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

            // Draw volume markers (crescendo/decrescendo)
            for(VolumeMarker marker : volumeMarkers) {
                drawVolumeMarker(guiGraphics, marker, false);
            }
            if(currentlyAddedMarker != null) {
                drawVolumeMarker(guiGraphics, currentlyAddedMarker, true);
            }

            guiGraphics.drawString(font, "M:", noteImageLeftX + HL_BUT_X + 14, noteImageY + HL_BUT_Y + 2, 0xFF000000, false);
            guiGraphics.drawString(font, "" + (highlightInterval > 1 ? highlightInterval : "-"), noteImageLeftX + HL_BUT_X + 22, noteImageY + HL_BUT_Y + 2, 0xFF000000, false);

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

        if(helpOn) {
            // === Scrollable help panel ===
            int panelW = 380;
            helpPanelX = (this.width - panelW) / 2;
            helpPanelY = 5;
            helpPanelBottom = this.height - 5;
            helpPanelW = panelW;

            // Panel background + border
            guiGraphics.fill(helpPanelX, helpPanelY, helpPanelX + panelW, helpPanelBottom, 0xF0222222);
            guiGraphics.fill(helpPanelX, helpPanelY, helpPanelX + panelW, helpPanelY + 1, 0xFF555555);
            guiGraphics.fill(helpPanelX, helpPanelBottom - 1, helpPanelX + panelW, helpPanelBottom, 0xFF555555);
            guiGraphics.fill(helpPanelX, helpPanelY, helpPanelX + 1, helpPanelBottom, 0xFF555555);
            guiGraphics.fill(helpPanelX + panelW - 1, helpPanelY, helpPanelX + panelW, helpPanelBottom, 0xFF555555);

            // Title (centered, bold)
            String title = I18n.get("note.helpTitle");
            guiGraphics.drawCenteredString(font, "\u00a7l" + title, helpPanelX + panelW / 2, helpPanelY + 4, 0xFFFFCC00);

            // Tab bar
            helpTabY = helpPanelY + 16;
            guiGraphics.fill(helpPanelX + 1, helpTabY, helpPanelX + panelW - 1, helpTabY + HELP_TAB_H, 0xFF333333);

            int tabX = helpPanelX + 4;
            for (int i = 0; i < HELP_SECTIONS.length; i++) {
                String tabLabel = I18n.get(HELP_SECTIONS[i][0]);
                int tw = font.width(tabLabel);
                helpTabX[i] = tabX;
                helpTabW[i] = tw + 6;

                // Highlight active section
                boolean active;
                if (i < HELP_SECTIONS.length - 1) {
                    active = helpScrollOffset >= helpSectionContentY[i]
                          && helpScrollOffset < helpSectionContentY[i + 1];
                } else {
                    active = helpScrollOffset >= helpSectionContentY[i];
                }
                if (active) {
                    guiGraphics.fill(tabX, helpTabY, tabX + helpTabW[i], helpTabY + HELP_TAB_H, 0xFF444477);
                }
                guiGraphics.drawString(font, tabLabel, tabX + 3, helpTabY + 2,
                        active ? 0xFFFFFF55 : 0xFFAAAAAA, false);
                tabX += helpTabW[i] + 2;
            }

            // Content area
            helpContentTop = helpTabY + HELP_TAB_H + 2;
            helpContentBottomY = helpPanelBottom - 2;
            int contentX = helpPanelX + 6;
            int lineH = 10;
            int sectionGap = 8;
            int scrollAreaH = helpContentBottomY - helpContentTop;

            // Clamp scroll
            int maxScroll = Math.max(0, helpContentHeight - scrollAreaH);
            helpScrollOffset = Math.max(0, Math.min(helpScrollOffset, maxScroll));

            // Scissored scrollable content
            guiGraphics.enableScissor(helpPanelX + 1, helpContentTop, helpPanelX + panelW - 6, helpContentBottomY);

            int cy = helpContentTop - helpScrollOffset;
            for (int s = 0; s < HELP_SECTIONS.length; s++) {
                helpSectionContentY[s] = cy - helpContentTop + helpScrollOffset;
                String[] section = HELP_SECTIONS[s];

                // Section header
                guiGraphics.drawString(font, "\u00a7n\u00a7e" + I18n.get(section[0]),
                        contentX, cy, 0xFFFFCC00, false);
                cy += lineH + 2;

                // Key-description entries
                for (int i = 1; i < section.length; i += 2) {
                    drawHelpLine(guiGraphics, contentX, cy,
                            I18n.get(section[i]), I18n.get(section[i + 1]));
                    cy += lineH;
                }
                cy += sectionGap;
            }
            helpContentHeight = cy - helpContentTop + helpScrollOffset;

            guiGraphics.disableScissor();

            // Scrollbar
            if (helpContentHeight > scrollAreaH) {
                int sbX = helpPanelX + panelW - 5;
                float frac = (float) helpScrollOffset / Math.max(1, helpContentHeight - scrollAreaH);
                int thumbH = Math.max(8, scrollAreaH * scrollAreaH / helpContentHeight);
                int thumbY = helpContentTop + (int) ((scrollAreaH - thumbH) * frac);
                guiGraphics.fill(sbX, helpContentTop, sbX + 3, helpContentBottomY, 0xFF333333);
                guiGraphics.fill(sbX, thumbY, sbX + 3, thumbY + thumbH, 0xFF888888);
            }
        }
        else{
            if(buttonHideNeighbors.isHovered()){
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

        // Glissando mode header swap - replace control bar with glissando indicator
        if (glissandoMode) {
            String modeText;
            if (glissandoSourceNote == null) {
                modeText = I18n.get("note.glissando.start");
            } else if (glissandoPendingWaypoints == null || glissandoPendingWaypoints.isEmpty()) {
                modeText = I18n.get("note.glissando.target");
            } else {
                modeText = I18n.get("note.glissando.points", glissandoPendingWaypoints.size());
            }

            // Draw glissando bar with text wrapping and centering
            int barLeft = noteImageLeftX + 45;
            int barRight = noteImageLeftX + NOTE_IMAGE_WIDTH + NOTE_IMAGE_LEFT_WIDTH - 20;
            int maxBarWidth = barRight - barLeft;
            int barTop = noteImageY + 7;
            
            // Wrap text to two lines if needed
            String[] textLines = wrapGlissandoText(modeText, maxBarWidth - 8);
            int barHeight = textLines.length == 1 ? 18 : 28;  // 28px for two lines
            int barBottom = barTop + barHeight;

            // Background and borders
            guiGraphics.fill(barLeft, barTop, barRight, barBottom, 0xEE1144AA);
            guiGraphics.fill(barLeft, barTop, barRight, barTop + 1, 0xFF7733FF);
            guiGraphics.fill(barLeft, barBottom - 1, barRight, barBottom, 0xFF0033AA);
            guiGraphics.fill(barLeft, barTop, barLeft + 1, barBottom, 0xFF4433BB);
            guiGraphics.fill(barRight - 1, barTop, barRight, barBottom, 0xFF4433BB);

            // Centered text - supports single or double line
            if (textLines.length == 1) {
                int textWidth = font.width(textLines[0]);
                int textX = barLeft + (maxBarWidth - textWidth) / 2;
                int textY = barTop + (barHeight - 8) / 2 + 1;
                guiGraphics.drawString(font, textLines[0], textX, textY, 0xFFFFFFFF, true);
            } else {
                // Two lines: center each vertically with spacing
                int line1Width = font.width(textLines[0]);
                int line2Width = font.width(textLines[1]);
                int textX1 = barLeft + (maxBarWidth - line1Width) / 2;
                int textX2 = barLeft + (maxBarWidth - line2Width) / 2;
                int textY1 = barTop + 3;
                int textY2 = barTop + 12;
                guiGraphics.drawString(font, textLines[0], textX1, textY1, 0xFFFFFFFF, true);
                guiGraphics.drawString(font, textLines[1], textX2, textY2, 0xFFFFFFFF, true);
            }
        }
    }

    private String[] wrapGlissandoText(String text, int maxWidth) {
        // Check if text fits in one line
        if (font.width(text) <= maxWidth) {
            return new String[]{text};
        }
        
        // Split by spaces and find the best break point
        String[] words = text.split(" ");
        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        boolean firstLine = true;
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String testLine = (firstLine ? line1 : line2).toString();
            if (!testLine.isEmpty()) {
                testLine += " ";
            }
            testLine += word;
            
            if (font.width(testLine) <= maxWidth) {
                // Word fits on current line
                if (firstLine) {
                    if (!line1.isEmpty()) line1.append(" ");
                    line1.append(word);
                } else {
                    if (!line2.isEmpty()) line2.append(" ");
                    line2.append(word);
                }
            } else if (firstLine && !line1.isEmpty()) {
                // Start second line
                firstLine = false;
                line2.append(word);
            } else {
                // Word is too long, force it to the current line anyway
                if (firstLine) {
                    if (!line1.isEmpty()) line1.append(" ");
                    line1.append(word);
                } else {
                    if (!line2.isEmpty()) line2.append(" ");
                    line2.append(word);
                }
            }
        }
        
        if (line2.length() == 0) {
            return new String[]{line1.toString()};
        }
        return new String[]{line1.toString(), line2.toString()};
    }

    private void drawHelpLine(GuiGraphics guiGraphics, int x, int y, String key, String desc) {
        guiGraphics.drawString(font, key + ": ", x, y, 0xFFDDDD44, false);
        guiGraphics.drawString(font, desc, x + font.width(key + ": "), y, 0xFFCCCCCC, false);
    }

    private void drawSelectionRect(GuiGraphics guiGraphics) {
        if (inScreen(editCursor) || inScreen(editCursorEnd) || (editCursor < sliderPosition && editCursorEnd >= sliderPosition + BEATS_IN_SCREEN)) {
            final int selectionColor = 0x882222AA;
            int timeDrawBeginning = Math.max(editCursor - sliderPosition, 0);
            int timeDrawEnd = Math.min(editCursorEnd - sliderPosition, BEATS_IN_SCREEN);

            int x1 = noteToPixelX(timeDrawBeginning);
            int x2 = noteToPixelX(timeDrawEnd);
            int y1, y2;
            if (rectSelection) {
                // Rectangular selection: only cover the selected note range
                y1 = noteImageY + NOTE_REGION_TOP + (47 - (rectSelectNoteTop - IItemInstrument.MIN_NOTE - currentOctavePos * 12)) * 3;
                y2 = noteImageY + NOTE_REGION_TOP + (47 - (rectSelectNoteBottom - IItemInstrument.MIN_NOTE - currentOctavePos * 12)) * 3 + 3;
                // Clamp to note region bounds
                y1 = Math.max(y1, noteImageY + NOTE_REGION_TOP);
                y2 = Math.min(y2, noteImageY + NOTE_REGION_TOP + 36 * 4);
            } else {
                y1 = noteImageY + NOTE_REGION_TOP;
                y2 = y1 + 36*4;
            }

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
            guiGraphics.fill(xFillBegin, y+1, xFillEnd, y + 2, fillColor);

            // Draw glissando indicator (lines showing pitch path through waypoints)
            if (!isNeighbor && event.hasGlissando()) {
                byte[] wps = event.getEffectiveWaypoints();
                byte[] positions = event.getEffectivePositions();
                if (wps != null && wps.length > 0) {
                    int notePixelWidth = xEnd - xBegin;
                    int numSegments = wps.length;
                    int startY = y + 1;
                    int prevSegY = startY;
                    for (int seg = 0; seg < numSegments; seg++) {
                        int targetNote = event.note + wps[seg];
                        int targetOctave = octaveFromNote((byte) targetNote);
                        if (targetOctave < currentOctavePos || targetOctave >= currentOctavePos + 4) continue;
                        int targetY = noteImageY + NOTE_REGION_TOP + (47 - targetNote + IItemInstrument.MIN_NOTE) * 3 + currentOctavePos * 36 + 1;
                        // Calculate x range for this segment
                        int segStartX, segEndX;
                        if (positions != null && positions.length == numSegments) {
                            segStartX = xBegin + (seg == 0 ? 0 : (positions[seg - 1] & 0xFF) * notePixelWidth / 100);
                            segEndX = xBegin + (positions[seg] & 0xFF) * notePixelWidth / 100;
                        } else {
                            segStartX = xBegin + (seg * notePixelWidth) / numSegments;
                            segEndX = xBegin + ((seg + 1) * notePixelWidth) / numSegments;
                        }
                        int segWidth = segEndX - segStartX;
                        if (segWidth < 1) segWidth = 1;
                        // Draw line from previous pitch to this waypoint's pitch
                        int fromY = seg == 0 ? startY : prevSegY;
                        int dy = targetY - fromY;
                        for (int step = 0; step < segWidth; step++) {
                            int px = segStartX + step;
                            int py = fromY + (step * dy) / Math.max(segWidth, 1);
                            guiGraphics.fill(px, py, px + 1, py + 1, 0xFF4488FF);
                        }
                        prevSegY = targetY;
                    }
                }
            }

            // Draw pending waypoints preview during glissando placement, including the live cursor waypoint.
            if (glissandoMode && event == glissandoSourceNote) {
                drawPendingGlissandoPreview(guiGraphics, event, xBegin, xEnd, y);
            }

            // Highlight source note in glissando mode
            if (glissandoMode && event == glissandoSourceNote) {
                guiGraphics.fill(xBegin - 1, y - 1, xEnd + 1, y + 4, 0x664488FF);
            }
        }
    }

    private void drawVolumeMarker(GuiGraphics guiGraphics, VolumeMarker marker, boolean isBeingAdded) {
        // Check if any part of the marker is visible in the current octave range
        int lowOctave = octaveFromNote(marker.lowNote);
        int highOctave = octaveFromNote(marker.highNote);
        
        // Check if marker is visible horizontally and vertically
        boolean verticallyVisible = (highOctave >= currentOctavePos && lowOctave < currentOctavePos + 4);
        boolean horizontallyVisible = marker.startTime < sliderPosition + BEATS_IN_SCREEN
                && marker.endTime > sliderPosition;

        if (!verticallyVisible || !horizontallyVisible) {
            return;
        }
        
        // Calculate horizontal bounds
        int timeDrawBeginning = Math.max(marker.startTime - sliderPosition, 0);
        int timeDrawEnd = Math.min(marker.endTime - sliderPosition, BEATS_IN_SCREEN);
        
        int xBegin = noteImageLeftX + NOTE_REGION_LEFT + timeDrawBeginning * 3;
        int xEnd = noteImageLeftX + NOTE_REGION_LEFT + timeDrawEnd * 3;
        
        if (xBegin >= xEnd) {
            return;
        }
        
        // Calculate vertical bounds (clipped to visible octave range)
        int visibleLowNote = Math.max(marker.lowNote, (byte)(IItemInstrument.MIN_NOTE + currentOctavePos * 12));
        int visibleHighNote = Math.min(marker.highNote, (byte)(IItemInstrument.MIN_NOTE + (currentOctavePos + 4) * 12 - 1));
        
        int yTop = noteImageY + NOTE_REGION_TOP + (47 - visibleHighNote + IItemInstrument.MIN_NOTE) * 3 + currentOctavePos * 36;
        int yBottom = noteImageY + NOTE_REGION_TOP + (47 - visibleLowNote + IItemInstrument.MIN_NOTE) * 3 + currentOctavePos * 36 + 3;
        
        // Choose color based on crescendo (red) or decrescendo (green)
        // Use semi-transparent colors so notes are still visible
        int alpha = isBeingAdded ? 0x66 : 0x55;
        int color;
        if (marker.isCrescendo()) {
            // Red for crescendo (getting louder)
            color = (alpha << 24) | 0xFF4444;
        } else {
            // Green for decrescendo (getting softer)
            color = (alpha << 24) | 0x44FF44;
        }
        
        guiGraphics.fill(xBegin, yTop, xEnd, yBottom, color);
        
        // Draw a thin border to make the marker more visible
        int borderAlpha = isBeingAdded ? 0xAA : 0x88;
        int borderColor = marker.isCrescendo() ? ((borderAlpha << 24) | 0xAA2222) : ((borderAlpha << 24) | 0x22AA22);
        
        // Top and bottom borders
        guiGraphics.fill(xBegin, yTop, xEnd, yTop + 1, borderColor);
        guiGraphics.fill(xBegin, yBottom - 1, xEnd, yBottom, borderColor);
        
        // Left and right borders (only if visible)
        if (timeDrawBeginning == marker.startTime - sliderPosition) {
            guiGraphics.fill(xBegin, yTop, xBegin + 1, yBottom, borderColor);
        }
        if (timeDrawEnd == marker.endTime - sliderPosition) {
            guiGraphics.fill(xEnd - 1, yTop, xEnd, yBottom, borderColor);
        }
    }

    private void drawPendingGlissandoPreview(GuiGraphics guiGraphics, NoteEvent event, int xBegin, int xEnd, int y) {
        int noteLength = event.length & 0xFF;
        if (noteLength <= 0) {
            return;
        }

        ArrayList<Byte> previewWaypoints = glissandoPendingWaypoints == null
                ? new ArrayList<>()
                : new ArrayList<>(glissandoPendingWaypoints);
        ArrayList<Byte> previewPositions = glissandoPendingPositions == null
                ? new ArrayList<>()
                : new ArrayList<>(glissandoPendingPositions);
        if (previewPositions.size() != previewWaypoints.size()) {
            previewWaypoints.clear();
            previewPositions.clear();
        }

        int hoverIndex = -1;
        GlissandoPreviewPoint hoverPoint = getHoveredGlissandoPreviewPoint(event);
        if (hoverPoint != null) {
            hoverIndex = upsertPreviewWaypoint(previewWaypoints, previewPositions, hoverPoint.interval(), hoverPoint.beatIndex());
        }

        if (previewWaypoints.isEmpty()) {
            return;
        }

        int notePixelWidth = xEnd - xBegin;
        int startY = y + 1;
        int prevY = startY;
        for (int seg = 0; seg < previewWaypoints.size(); seg++) {
            int targetNote = event.note + previewWaypoints.get(seg);
            int targetOctave = octaveFromNote((byte) targetNote);
            if (targetOctave < currentOctavePos || targetOctave >= currentOctavePos + 4) {
                continue;
            }

            int beatIndex = previewPositions.get(seg) & 0xFF;
            int prevBeatIndex = seg == 0 ? 0 : (previewPositions.get(seg - 1) & 0xFF);
            int targetY = noteToPixelY(targetNote) + 1;
            int segStartX = xBegin + prevBeatIndex * notePixelWidth / noteLength;
            int segEndX = xBegin + beatIndex * notePixelWidth / noteLength;
            int segWidth = Math.max(segEndX - segStartX, 1);
            int fromY = seg == 0 ? startY : prevY;
            int dy = targetY - fromY;
            int lineColor = seg == hoverIndex ? 0xCC66BBFF : 0x994488FF;

            for (int step = 0; step < segWidth; step++) {
                int px = segStartX + step;
                int py = fromY + (step * dy) / Math.max(segWidth, 1);
                guiGraphics.fill(px, py, px + 1, py + 1, lineColor);
            }

            int dotX = Math.max(xBegin, Math.min(xEnd - 1, segEndX - 1));
            int dotColor = seg == hoverIndex ? 0xFF99D6FF : 0xCC66BBFF;
            guiGraphics.fill(dotX - 1, targetY - 1, dotX + 2, targetY + 2, dotColor);
            prevY = targetY;
        }
    }

    private GlissandoPreviewPoint getHoveredGlissandoPreviewPoint(NoteEvent event) {
        int mouseRelX = renderMouseX - noteImageLeftX;
        int mouseRelY = renderMouseY - noteImageY;
        if (mouseRelX < NOTE_REGION_LEFT || mouseRelX > NOTE_REGION_RIGHT
                || mouseRelY < NOTE_REGION_TOP || mouseRelY > NOTE_REGION_BOTTOM) {
            return null;
        }

        int noteLength = event.length & 0xFF;
        if (noteLength <= 0) {
            return null;
        }

        int noteRegionX = mouseRelX - NOTE_REGION_LEFT;
        float exactTime = noteRegionX / 3.0f + sliderPosition;
        int relativeBeat = (int) Math.floor(exactTime - event.time);
        int beatIndex = Math.max(1, Math.min(noteLength, relativeBeat + 1));
        byte hoveredNote = (byte) (47 - ((mouseRelY - NOTE_REGION_TOP) / 3) + IItemInstrument.MIN_NOTE + currentOctavePos * 12);
        return new GlissandoPreviewPoint(beatIndex, (byte) (hoveredNote - event.note));
    }

    private int upsertPreviewWaypoint(List<Byte> previewWaypoints, List<Byte> previewPositions, byte interval, int beatIndex) {
        int existingIndex = previewPositions.indexOf((byte) beatIndex);
        if (existingIndex >= 0) {
            previewWaypoints.set(existingIndex, interval);
            return existingIndex;
        }

        int insertIndex = 0;
        while (insertIndex < previewPositions.size() && (previewPositions.get(insertIndex) & 0xFF) < beatIndex) {
            insertIndex++;
        }
        previewPositions.add(insertIndex, (byte) beatIndex);
        previewWaypoints.add(insertIndex, interval);
        return insertIndex;
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

    void startPreview() {
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

    void startPreRecording() {
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

    void stopRecording() {
        this.preRecording = false;
        this.previewing = false;
        this.recording = false;
        this.previewStarted = false;
        this.buttonRecord.setTexStarts(176, 0);
        updateLength();
        recordingNotes.clear();
        updateButtons();
    }

    void setSliderPos(int time) {
        time = Math.min(Math.max(time, 0), maxSliderPosition);

        sliderTime.setValue((float) time / (float) maxSliderPosition);
        sliderTime.applyValue();
    }

    void stopPreview() {
        this.previewing = false;
        this.previewStarted = false;
        this.buttonPreview.setTexStarts(224, 0);
        this.previewActiveSounds.clear();
        updateButtons();
    }

    /**
     * Update volumes of all currently playing sustained notes based on volume markers.
     */
    private void updatePreviewActiveSounds(int currentBeat) {
        Iterator<PreviewActiveSound> it = previewActiveSounds.iterator();
        while (it.hasNext()) {
            PreviewActiveSound active = it.next();
            int noteEndBeatExclusive = active.event.time + active.event.length;
            if (currentBeat >= noteEndBeatExclusive || active.sound.isStopped()) {
                it.remove();
                continue;
            }
            float markerVolume = active.marker.getVolumeAt((short) currentBeat);
            if (markerVolume >= 0) {
                active.sound.setDynamicVolume(volume * markerVolume);
            }
        }
    }

    void updateLength() {
        updateLength(true);
    }

    void updateLength(boolean updateSliderPos) {
        lengthBeats = 0;
        if(!notes.isEmpty()){
            // Notes are sorted by time, so scan from the end to find max endTime quickly
            for (int i = notes.size() - 1; i >= 0; i--) {
                NoteEvent event = notes.get(i);
                short endTime = (short)(event.time + event.length);
                if (endTime > lengthBeats) lengthBeats = endTime;
                // Once we're far enough from the tail that no earlier note can beat current max, stop
                if (event.time + 127 < lengthBeats) break; // 127 = max possible note length (byte)
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
        return inputHandler.handleMouseClicked(dmouseX, dmouseY, mouseButton);
    }

    void pushUndo() {
        if (undoStack.size() >= MAX_UNDO_LENGTH) {
            undoStack.removeLast();
        }
        ArrayList<NoteEvent> stackNotes = new ArrayList<>(notes.size());
        for (NoteEvent note : notes) {
            stackNotes.add(new NoteEvent(note));
        }
        ArrayList<VolumeMarker> stackMarkers = new ArrayList<>(volumeMarkers.size());
        for (VolumeMarker marker : volumeMarkers) {
            stackMarkers.add(new VolumeMarker(marker.startTime, marker.endTime,
                    marker.startVolume, marker.endVolume, marker.lowNote, marker.highNote));
        }
        undoStack.push(new UndoState(stackNotes, stackMarkers));
    }

    void insertNoteSorted(NoteEvent newEvent) {
        int lo = 0, hi = notes.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (notes.get(mid).time <= newEvent.time) lo = mid + 1;
            else hi = mid;
        }
        notes.add(lo, newEvent);
    }

    private void addRecordingNote(NoteEvent noteEvent) {
        insertNoteSorted(noteEvent);
        recordingNotes.add(noteEvent);
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        return inputHandler.handleMouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        return inputHandler.handleMouseReleased(posX, posY, mouseButton);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return inputHandler.handleKeyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        return inputHandler.handleKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int something) {
        return inputHandler.handleCharTyped(typedChar, something);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY){
        return inputHandler.handleMouseScrolled(x, y, scrollX, scrollY);
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
                dirtyFlag.hasVolumeMarkers = true;
                MusicManagerClient.setMusicData(id, version, notes, volumeMarkers);
            }

            try {
                MusicUpdatePacket pack = MusicUpdatePacket.create(dirtyFlag, notes, dirtyFlag.hasVolumeMarkers ? volumeMarkers : null, lengthBeats, bps, volume, isSigned,
                        noteTitle, (byte)previewInstrument, prevInsLocked, id, version, highlightInterval);
                sendToServer(pack);
            } catch (ImportMusicSendPacket.NotesTooLargeException e) {
                int partsCount = (int) Math.ceil((double) notes.size() / (double) MAX_NOTES_IN_PACKET);

                try {
                    MusicUpdatePacket pack = MusicUpdatePacket.create(dirtyFlag, null, dirtyFlag.hasVolumeMarkers ? volumeMarkers : null, lengthBeats, bps, volume, isSigned,
                            noteTitle, (byte)previewInstrument, prevInsLocked, id, version, highlightInterval);
                    NotesPartAckFromServerPacketHandler.addCallback(id, ()-> sendToServer(pack));
                    for(int i=0; i<partsCount; i++) {
                        SendNotesPartToServerPacket partPack = new SendNotesPartToServerPacket(id, partsCount, i, notes.subList(i*MAX_NOTES_IN_PACKET, Math.min((i+1)*MAX_NOTES_IN_PACKET, notes.size())));
                        sendToServer(partPack);
                    }
                } catch (ImportMusicSendPacket.NotesTooLargeException ex) {
                    Mod.LOGGER.error("Could not send partial notes to server:", ex);
                }
            }
        }

        if (midiHandler != null) {
            midiHandler.closeDevices();
        }

        if (SoundEvents.CLOSE_SCROLL != null) {
            editingPlayer.playSound(SoundEvents.CLOSE_SCROLL, 1.0f, 0.8f + editingPlayer.level().random.nextFloat() * 0.4f);
        }
    }

    private int noteToPixelY(int note) {
        return noteImageY + NOTE_REGION_TOP + (47 - note + IItemInstrument.MIN_NOTE) * 3 + currentOctavePos * 36;
    }

    private record GlissandoPreviewPoint(int beatIndex, byte interval) {}

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

    /**
     * Edit box widget for adjusting volume marker (crescendo/decrescendo) properties.
     */
    public class MarkerEditBox extends AbstractWidget {
        public final BetterSlider sliderStartVolume;
        public final BetterSlider sliderEndVolume;
        public final Button buttonDelete;
        public final Button buttonExit;
        private VolumeMarker marker;
        private final AbstractWidget[] children = new AbstractWidget[4];
        private boolean changed = false;

        public MarkerEditBox(int x, int y, int w, int h, Component msg) {
            super(x, y, w, h, msg);
            sliderStartVolume = new BetterSlider(10, 0, 70, 10, Component.literal("Start "), Component.literal(" Vol"), 0, 100, 50, true) {
                @Override public void applyValue() {
                    setChanged();
                    marker.startVolume = (byte)Math.round(value * 127.0f);
                }
            };
            sliderEndVolume = new BetterSlider(10, 0, 70, 10, Component.literal("End "), Component.literal(" Vol"), 0, 100, 50, true) {
                @Override public void applyValue() {
                    setChanged();
                    marker.endVolume = (byte)Math.round(value * 127.0f);
                }
            };
            buttonDelete = Button.builder(Component.literal("Del"), (button) -> {
                volumeMarkers.remove(marker);
                dirtyFlag.hasNotes = true;
                this.visible = false;
                this.active = false;
            }).bounds(0, 0, 25, 12).build();
            buttonExit = Button.builder(Component.translatable("note.exitButton"), (button) -> {
                this.visible = false;
                this.active = false;
            }).bounds(0, 0, 10, 10).build();

            children[0] = sliderStartVolume;
            children[1] = sliderEndVolume;
            children[2] = buttonDelete;
            children[3] = buttonExit;
        }

        private void setChanged() {
            if(!changed) {
                changed = true;
                dirtyFlag.hasNotes = true;
            }
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            if (this.visible && marker != null) {
                guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFFEEEEEE);
                Minecraft minecraft = Minecraft.getInstance();
                Font font = minecraft.font;
                
                // Draw marker type label
                String typeLabel = marker.isCrescendo() ? "Crescendo" : "Decrescendo";
                int typeColor = marker.isCrescendo() ? 0xFFAA2222 : 0xFF22AA22;
                guiGraphics.drawString(font, typeLabel, getX() + 5, getY() + 5, typeColor, false);
                
                // Draw duration info
                int duration = marker.endTime - marker.startTime;
                guiGraphics.drawString(font, duration + " beats", getX() + 5, getY() + 18, 0xFF333333, false);

                for(AbstractWidget widget : children) {
                    widget.render(guiGraphics, mouseX, mouseY, partialTicks);
                }
            }
        }

        public void appear(int x, int y, VolumeMarker marker) {
            changed = false;
            this.setX(x);
            this.setY(y);
            this.visible = true;
            this.active = true;
            this.marker = marker;
            
            sliderStartVolume.setX(x + 10);
            sliderStartVolume.setY(y + 30);
            sliderStartVolume.setValue(marker.startVolume / 127.0f * 100.0f);
            
            sliderEndVolume.setX(x + 10);
            sliderEndVolume.setY(y + 45);
            sliderEndVolume.setValue(marker.endVolume / 127.0f * 100.0f);

            buttonDelete.setX(x + 5);
            buttonDelete.setY(y + 60);

            buttonExit.setX(x + width - 15);
            buttonExit.setY(y + 2);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (this.active && this.visible) {
                if(mouseButton == 2){
                    this.visible = false;
                    this.active = false;
                }

                for(AbstractWidget widget : children) {
                    if(mouseX >= widget.getX() && mouseX < widget.getX() + widget.getWidth() &&
                            mouseY >= widget.getY() && mouseY < widget.getY() + widget.getHeight()){
                        widget.mouseClicked(mouseX, mouseY, mouseButton);
                        return true;
                    }
                }

                boolean flag = this.clicked(mouseX, mouseY);
                if (!flag) {
                    this.visible = false;
                    this.active = false;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
            if(posX >= sliderStartVolume.getX() && posX < sliderStartVolume.getX() + sliderStartVolume.getWidth() &&
                    posY >= sliderStartVolume.getY() && posY < sliderStartVolume.getY() + sliderStartVolume.getHeight()){
                sliderStartVolume.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            }
            if(posX >= sliderEndVolume.getX() && posX < sliderEndVolume.getX() + sliderEndVolume.getWidth() &&
                    posY >= sliderEndVolume.getY() && posY < sliderEndVolume.getY() + sliderEndVolume.getHeight()){
                sliderEndVolume.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            }
            return true;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        @Override
        public boolean mouseReleased(double posX, double posY, int mouseButton) {
            sliderStartVolume.onRelease(posX, posY);
            sliderEndVolume.onRelease(posX, posY);
            return true;
        }
    }
}
