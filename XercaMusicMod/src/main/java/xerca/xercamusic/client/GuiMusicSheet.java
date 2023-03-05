package xerca.xercamusic.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.ImportMusicSendPacket;
import xerca.xercamusic.common.packets.MusicUpdatePacket;
import xerca.xercamusic.common.packets.NotesPartAckFromServerPacketHandler;
import xerca.xercamusic.common.packets.SendNotesPartToServerPacket;

import java.util.*;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class GuiMusicSheet extends Screen {
    public enum MidiControl {
        BEGINNING, END, STOP, PREVIEW, RECORD
    }
    private static final String[] octaveNames = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII"};
    private static final ResourceLocation noteGuiLeftTexture = new ResourceLocation(XercaMusic.MODID, "textures/gui/music_sheet_left.png");
    private static final ResourceLocation noteGuiTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/music_sheet.png");
    private static final ResourceLocation instrumentTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/instruments.png");
    private final Player editingPlayer;
    public final static int beatsInScreen = 91;
    private final static int noteImageLeftTexX = 175;
    private final static int noteImageLeftTexY = 51;
    private final static int noteImageLeftWidth = 81;
    private final static int noteImageLeftHeight = 205;
    private final static int noteImageTexX = 0;
    private final static int noteImageTexY = 44;
    private final static int noteImageWidth = 256;
    private final static int noteImageHeight = 210;
    private final static int noteRegionLeft = 44;
    private final static int noteRegionTop = 39;
    private final static int noteRegionRight = 316;
    private final static int noteRegionBottom = 182;
    private final static int bpmButW = 10;
    private final static int bpmButH = 10;
    private final static int bpmButX = 245;
    private final static int bpmButY = 12;
    private final static int hlButX = 261;
    private final static int hlButY = 23;
    private final static int[] octaveColors = {0xFF5B3200, 0xFFFF0000, 0xFF0AEE00, 0xFF0059FF, 0xFF7B00FF, 0xFFEF00B7, 0xFF00E2DF, 0XFFF4E800};
    private final static int[] octaveColorsTrans = {0x165B3200,0x16FF0000, 0x160AEE00, 0x160059FF, 0x167B00FF, 0x16EF00B7, 0x1600E2DF, 0X16F4E800};
    private static final int maxLengthBeats = 32000;
    private static final byte copyBeginByte = (byte)50;
    private int noteImageX;
    private int noteImageLeftX;
    private int noteImageY;
    static private int currentOctave = 1;
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
    private boolean neighborsHidden = false;
    private boolean prevInsLocked = false;
    private boolean selfSigned = false;
    private final NoteSound[] notePlaySounds;
    private final ArrayList<NoteEvent> recordingNotes = new ArrayList<>();
    private final boolean[] buttonPushStates = new boolean[IItemInstrument.totalNotes];

    private final UUID id;
    private int version;
    private ArrayList<NoteEvent> notes = new ArrayList<>();
    private short lengthBeats = 0;
    private byte bps;
    private int bpm;
    private final MusicUpdatePacket.FieldFlag dirtyFlag = new MusicUpdatePacket.FieldFlag();
    private int previewInstrument = -1;
    private long lastMillis = 0;
    private long cumMillis = 0;
    private int previewNextNoteID = 0;
    private NoteEvent currentlyAddedNote;
    private int sliderPosition = 0;
    private int maxSliderPosition = 500;
    private int currentOctavePos = 1;
    private float volume = 1.f;
    private static float brushVolume = 0.5f;
    private static final int maxNoteLength = 60;
    private boolean helpOn = false;

    private static final int maxUndoLength = 16;
    private final Deque<ArrayList<NoteEvent>> undoStack = new ArrayDeque<>(maxUndoLength);

    private final ArrayList<ArrayList<NoteEvent>> neighborNotes = new ArrayList<>();
    private final ArrayList<Float> neighborVolumes = new ArrayList<>();
    private final ArrayList<Integer> neighborPreviewNextNoteIDs = new ArrayList<>();
    private final ArrayList<Integer> neighborPrevInstruments = new ArrayList<>();
    private final MidiHandler midiHandler;

    private int getCurrentOffhandInsIndex(){
        Item offhand = editingPlayer.getOffhandItem().getItem();
        if (offhand instanceof IItemInstrument ins) {
            return ArrayUtils.indexOf(Items.instruments, ins);
        }
        return -1;
    }

    GuiMusicSheet(Player player, CompoundTag noteTag, Component title) {
        super(title);
        this.editingPlayer = player;
        if (noteTag != null && !noteTag.isEmpty() && noteTag.contains("id") && noteTag.contains("ver")) {
            this.id = noteTag.getUUID("id");
            this.version = noteTag.getInt("ver");
            // Read notes from cache or server using id
            MusicManager.MusicData data = MusicManagerClient.getMusicData(id, version);
            if(data != null){
                notes.addAll(data.notes);
            }

            this.lengthBeats = noteTag.getShort("l");
            this.bps = noteTag.getByte("bps");
            if(noteTag.contains("vol")){
                this.volume = noteTag.getFloat("vol");
            }
            this.generation = noteTag.getInt("generation");
            this.isSigned = generation > 0;
            this.noteTitle = noteTag.getString("title");
            String authorName = noteTag.getString("author");
            this.prevInsLocked = noteTag.getBoolean("piLocked");
            if(noteTag.contains("prevIns")){
                this.previewInstrument = noteTag.getByte("prevIns");
            }
            if(noteTag.contains("hl")) {
                this.highlightInterval = noteTag.getByte("hl");
            }

            if(authorName.equals(player.getName().getString())){
                this.selfSigned = true;
            }
        } else {
            this.isSigned = false;
            this.id = UUID.randomUUID();
            this.version = 0;
            dirtyFlag.hasId = true;
            dirtyFlag.hasVersion = true;
        }

        if(this.notes.isEmpty()){
            this.lengthBeats = 0;
        }
        if(this.bps == 0){
            this.bps = 8;
        }
        this.bpm = bps*60;
        this.tickCount = 0;

        if(!prevInsLocked) {
            int index = getCurrentOffhandInsIndex();
            if (index != previewInstrument) {
                previewInstrument = index;
                if(!isSigned || selfSigned || generation > 1){
                    dirtyFlag.hasPrevIns = true;
                }
            }
        }

        // Neighbor sheets
        int currentSlot = player.getInventory().selected;
        boolean added = addNeighborSheet(getStackInSlot(currentSlot - 1));
        if(added){
            addNeighborSheet(getStackInSlot(currentSlot - 2));
        }
        added = addNeighborSheet(getStackInSlot(currentSlot + 1));
        if(added){
            addNeighborSheet(getStackInSlot(currentSlot + 2));
        }

        this.midiHandler = new MidiHandler(this::startSound, this::endSound, this::midiControlCommand);
        midiHandler.currentOctave = currentOctave;
        this.notePlaySounds = new NoteSound[IItemInstrument.totalNotes];
    }

    private void startSound(MidiHandler.MidiData data){
        int noteId = data.noteId();
        byte volume = (byte)(data.volume()*128.f);
        startSound(noteId, volume);
    }

    private void startSound(int noteId, byte volume) {
        //TEMP
        if(noteId >= 0 && noteId < buttonPushStates.length && buttonPushStates[noteId]) {
            XercaMusic.LOGGER.warn("Key pushed twice noteId: " + noteId + " vol: " + volume);
        }
        if(noteId >= 0 && noteId < buttonPushStates.length && !buttonPushStates[noteId]) {
            buttonPushStates[noteId] = true;
            int note = IItemInstrument.idToNote(noteId);
            for(NoteEvent noteEvent : recordingNotes){
                if(noteEvent.note == note){
                    XercaMusic.LOGGER.warn("Existing note pushed? " + noteId + " vol: " + volume);
                    return;
                }
            }

            IItemInstrument.InsSound noteSound;
            if(previewInstrument >= 0 && previewInstrument < Items.instruments.length){
                IItemInstrument ins = Items.instruments[previewInstrument];
                noteSound = ins.getSound(note);
            }
            else {
                noteSound = Items.HARP_MC.get().getSound(note);
            }
            if(noteSound == null){
                XercaMusic.LOGGER.warn("noteSound not found - noteId: " + noteId + " vol: " + volume);
                return;
            }
            notePlaySounds[noteId] = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(noteSound.sound, editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(), ((float)volume)/128.f, noteSound.pitch));
            if(recording) {
                NoteEvent newNote = new NoteEvent((byte)note, (short)(Math.max(0, previewCursor-1)), volume, (byte)1);
                addRecordingNote(newNote);
                previewNextNoteID++;
                dirtyFlag.hasNotes = true;
                dirtyFlag.hasLength = true;
            }
        }
    }

    private void endSound(int noteId){
        if(noteId >= 0 && noteId < buttonPushStates.length && buttonPushStates[noteId]) {
            buttonPushStates[noteId] = false;
            if (notePlaySounds[noteId] != null) {
                notePlaySounds[noteId].stopSound();
                notePlaySounds[noteId] = null;
            }
            if(recording) {
                int note = IItemInstrument.idToNote(noteId);
                for(int i=0; i<recordingNotes.size(); i++){
                    if(recordingNotes.get(i).note == note){
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

    private boolean addNeighborSheet(ItemStack neighbor){
        if(!neighbor.isEmpty() && neighbor.getItem() instanceof ItemMusicSheet){
            int neighborBPS = ItemMusicSheet.getBPS(neighbor);
            if(neighborBPS == bps){
                CompoundTag tag = neighbor.getTag();
                if(tag != null && tag.contains("id") && tag.contains("ver")) {
                    UUID id = tag.getUUID("id");
                    int ver = tag.getInt("ver");
                    MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                        MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                        if(data != null){
                            neighborNotes.add(new ArrayList<>(data.notes));
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

    private ItemStack getStackInSlot(int slot){
        if(slot >= 0 && slot < editingPlayer.getInventory().getContainerSize()) {
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
        noteImageX = ((this.width - (noteImageWidth + noteImageLeftWidth)) / 2) + noteImageLeftWidth;
        noteImageLeftX = noteImageX - noteImageLeftWidth;
        noteImageY = 2;
        if (!this.isSigned) {
            this.buttonSign = this.addRenderableWidget(new Button( noteImageLeftX + 112, noteImageY + noteImageHeight, 98, 20, Component.translatable("note.signButton"), button -> {
                if (!isSigned) {
                    gettingSigned = true;
                    updateButtons();
                }
            }));
            this.buttonFinalize = this.addRenderableWidget(new Button( noteImageLeftX + 112, 145, 98, 20, Component.translatable("note.finalizeButton"), button -> {
                if (!isSigned) {
                    dirtyFlag.hasSigned = true;
                    dirtyFlag.hasTitle = true;
                    isSigned = true;
                    if(minecraft != null){
                        minecraft.setScreen(null);
                    }
                }
            }));
            this.buttonCancel = this.addRenderableWidget(new Button( noteImageLeftX + 112, 170, 98, 20, Component.translatable("gui.cancel"), button -> {
                if (!isSigned) {
                    gettingSigned = false;
                    updateButtons();
                }
            }));
        }
        this.buttonPreview = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 50, 16, 16, 16, 224, 0, 16, noteGuiTextures, button -> previewButton()));

        this.buttonRecord = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 70, 16, 16, 16, 176, 0, 16, noteGuiTextures, button -> recordButton()));

        this.buttonHideNeighbors = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 90, 16, 16, 16, 192, 0, 16, noteGuiTextures, button -> {
            neighborsHidden = !neighborsHidden;
            if (neighborsHidden) {
                this.buttonHideNeighbors.setTexStarts(208, 0);
            } else {
                this.buttonHideNeighbors.setTexStarts(192, 0);
            }
        }));

        this.buttonLockPrevIns = this.addRenderableWidget(new LockImageButton( noteImageLeftX + 110, 16, 16, 16, previewInstrument*16 + 16, 32*((previewInstrument+1)/16), 16, instrumentTextures, button -> {
            if(!isSigned || selfSigned || generation > 1){
                prevInsLocked = !prevInsLocked;
                dirtyFlag.hasPrevInsLocked = true;
                if(!prevInsLocked){
                    int index = getCurrentOffhandInsIndex();
                    if (index != previewInstrument) {
                        previewInstrument = index;
                        this.buttonLockPrevIns.setTexStarts(previewInstrument*16 + 16, 32*((previewInstrument+1)/16));
                        dirtyFlag.hasPrevIns = true;
                    }
                }
            }
        }));

        this.bpmUp = this.addRenderableWidget(new Button(noteImageLeftX + bpmButX, noteImageY + bpmButY, bpmButW, bpmButH, Component.translatable("note.upButton"), button -> {
            if (!isSigned || selfSigned || generation > 1) {
                if(hasShiftDown()){
                    int mult = hasControlDown() ? 3 : 2;
                    if(bps*mult <= 50){
                        pushUndo();

                        bps *= mult;
                        dirtyFlag.hasBps = true;
                        previewing = false;
                        previewCursor = previewCursorStart;
                        for(NoteEvent note : notes){
                            note.time *= mult;
                            note.length = (byte)Math.min(maxNoteLength, note.length*mult);
                        }
                        updateLength();
                    }
                }
                else {
                    if(bps < 50){
                        bps ++;
                        dirtyFlag.hasBps = true;
                        cumMillis *= (float)(bps-1)/(float)bps;
                    }
                }
                bpm = 60*bps;
            }
        }));
        this.bpmDown = this.addRenderableWidget(new Button(noteImageLeftX + bpmButX, noteImageY + bpmButY + 1 + bpmButH, bpmButW, bpmButH, Component.translatable("note.downButton"), button -> {
            if (!isSigned || selfSigned || generation > 1) {
                if(hasShiftDown()){
                    float mult = hasControlDown() ? 0.33f : 0.5f;
                    if(Math.round(bps*mult) >= 1){
                        pushUndo();

                        bps = (byte)Math.round(bps*mult);
                        dirtyFlag.hasBps = true;
                        previewing = false;
                        previewCursor = previewCursorStart;
                        for(NoteEvent note : notes){
                            note.time = (short)Math.round(note.time*mult);
                            note.length = (byte)Math.max(Math.round(note.length*mult), 1);
                        }
                        updateLength();
                        removeDuplicates();
                    }
                }
                else {
                    if(bps > 1){
                        bps --;
                        dirtyFlag.hasBps = true;
                        cumMillis *= (float)(bps+1)/(float)bps;
                    }
                }
                bpm = 60*bps;
            }
        }));

        this.octaveUp = this.addRenderableWidget(new Button(noteImageLeftX + 15, noteImageY + 30, bpmButW, bpmButH, Component.translatable("note.upButton"), button -> {
            if(currentOctavePos < 4){
                currentOctavePos ++;
            }
        }));

        this.octaveDown = this.addRenderableWidget(new Button(noteImageLeftX + 15, noteImageY + noteRegionBottom, bpmButW, bpmButH, Component.translatable("note.downButton"), button -> {
            if(currentOctavePos > 0){
                currentOctavePos --;
            }
        }));

        this.sliderTime = this.addRenderableWidget(new BetterSlider(noteImageLeftX + noteRegionLeft,
                noteImageY + noteRegionBottom + 4, noteRegionRight-noteRegionLeft, 10,
                Component.empty(), Component.empty(), 0, 1, 0, 0.001, 3, false){
            @Override public void applyValue() {sliderPosition = (int)(this.value * (double)maxSliderPosition);}
        });

        this.sliderSheetVolume = this.addRenderableWidget(new BetterSlider(noteImageLeftX + noteRegionRight - 55, noteImageY + 12, 54, 10,
                Component.literal("S Vol "), Component.empty(), 0, 100, volume*100.f, true){
            @Override public void applyValue() {
                if(!isSigned || selfSigned || generation > 1) {
                    volume = ((float) this.value);
                    dirtyFlag.hasVolume = true;
                }
            }
        });

        this.sliderNoteVolume = this.addRenderableWidget(new BetterSlider(noteImageLeftX + 130, noteImageY + 12, 54, 10,
                Component.literal("N Vol "), Component.empty(), 0, 100, brushVolume*100.f, true){
            @Override public void applyValue() {
                if(!isSigned) {
                    brushVolume = ((float) this.value);
                }
            }
        });

        this.hlDown = this.addRenderableWidget(new Button(noteImageLeftX + hlButX, noteImageY + hlButY, bpmButW, bpmButH, Component.translatable("note.leftButton"), button -> {
            if((!isSigned || selfSigned || generation > 1) && highlightInterval > 1){
                highlightInterval --;
                dirtyFlag.hasHlInterval = true;
            }
        }));
        this.hlUp = this.addRenderableWidget(new Button(noteImageLeftX + hlButX + 44, noteImageY + hlButY, bpmButW, bpmButH, Component.translatable("note.rightButton"), button -> {
            if((!isSigned || selfSigned || generation > 1) && highlightInterval < 24){
                highlightInterval ++;
                dirtyFlag.hasHlInterval = true;
            }
        }));

        this.noteEditBox = this.addRenderableWidget(new NoteEditBox(0, 0, 70, 55, Component.empty()));

        this.buttonHelp = this.addRenderableWidget(new Button(noteImageLeftX + noteRegionRight + 30, noteImageY + bpmButY, 20, 20, Component.literal("?"), button -> toggleHelp()));

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
        if(!recording && !preRecording){
            startPreRecording();
        }
        else{
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
        this.buttonLockPrevIns.visible =  !this.gettingSigned;
        this.buttonLockPrevIns.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.sliderSheetVolume.visible =  !this.gettingSigned;
        this.sliderSheetVolume.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.noteEditBox.visible = false;
        this.noteEditBox.active = false;
        this.octaveDown.visible =  !this.gettingSigned;
        this.octaveUp.visible =  !this.gettingSigned;
        this.sliderTime.visible =  !this.gettingSigned;
        this.sliderTime.active =  (!this.recording && !this.preRecording);
        this.hlUp.visible =  !this.gettingSigned && (!this.isSigned || this.selfSigned || this.generation > 1);
        this.hlUp.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.hlDown.visible =  !this.gettingSigned && (!this.isSigned || this.selfSigned || this.generation > 1);
        this.hlDown.active = (!this.isSigned || this.selfSigned || this.generation > 1) && (!this.recording && !this.preRecording);
        this.sliderNoteVolume.active = (this.sliderNoteVolume.visible = !this.isSigned && !this.gettingSigned) && (!this.recording && !this.preRecording);
        this.buttonHelp.active = (this.buttonHelp.visible = !this.isSigned && !this.gettingSigned) && (!this.recording && !this.preRecording);
        this.buttonHideNeighbors.visible = (this.neighborNotes.size() > 0) && !this.gettingSigned;
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

    private void playMetronomeTick(){
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                ClientStuff.playNote(SoundEvents.TICK, editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(), SoundSource.PLAYERS, 1.0f, 0.975f + editingPlayer.level.random.nextFloat()*0.05f, (byte)-1));
    }

    private NoteSound playSound(NoteEvent event, int previewInstrument){
        return playSound(event, previewInstrument, volume);
    }

    private NoteSound playSound(NoteEvent event, int previewInstrument, float sheetVolume){
        if(event.note < IItemInstrument.minNote || event.note > IItemInstrument.maxNote){
            XercaMusic.LOGGER.warn("Note is invalid: " + event.note);
            return null;
        }

        IItemInstrument.InsSound insSound;
        if(previewInstrument >= 0 && previewInstrument < Items.instruments.length){
            IItemInstrument ins = Items.instruments[previewInstrument];
            insSound = ins.getSound(event.note);
        }
        else {
            insSound = Items.HARP_MC.get().getSound(event.note);
        }
        if(insSound == null){
            return null;
        }

        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                ClientStuff.playNote(insSound.sound, editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(), sheetVolume*event.floatVolume(), insSound.pitch, (byte)beatsToTicks(event.length)));
    }

    private int beatsToTicks(int beats){
        return Math.round(((float)beats) * 20.0f / ((float)bps));
    }

    private void playPreviewSound(int curStart, int curEnd) {
        if(previewNextNoteID < notes.size()) {
            NoteEvent event = notes.get(previewNextNoteID);
            while (event.time >= curStart && event.time < curEnd) {
                if(!recordingNotes.contains(event)){
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
        if(!neighborsHidden){
            for(int i = 0; i < neighborNotes.size(); i++){
                ArrayList<NoteEvent> nn = neighborNotes.get(i);
                int nPrevNoteID = neighborPreviewNextNoteIDs.get(i);
                if(nPrevNoteID >= 0 && nPrevNoteID < nn.size()){
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

    private void drawSigning(PoseStack matrixStack) {
        int i = noteImageLeftX;
        int j = noteImageY;

        final int left = i + 100;
        final int top = j + 40;
        final int width = 120;
        final int height = 100;
        fill(matrixStack, left, top, left + width, top + height, 0xFFFFFFFF);
        String titleStr = this.noteTitle;

        if (!this.isSigned) {
            if (this.tickCount / 6 % 2 == 0) {
                titleStr = titleStr + "" + ChatFormatting.BLACK + "_";
            } else {
                titleStr = titleStr + "" + ChatFormatting.GRAY + "_";
            }
        }
        String writeTitleStr = I18n.get("note.editTitle");
        int k = this.font.width(writeTitleStr);
        this.font.draw(matrixStack, writeTitleStr, left + (width - k) / 2.0f, top + 16, 0);
        int l = this.font.width(titleStr);
        this.font.draw(matrixStack, titleStr, left + (width - l) / 2.0f, top + 30, 0);
        String authorStr = I18n.get("note.byAuthor", this.editingPlayer.getName().getString());
        int i1 = this.font.width(authorStr);
        this.font.draw(matrixStack, ChatFormatting.DARK_GRAY + authorStr, left + (116 - i1) / 2.0f, top + 42, 0);
        this.font.drawWordWrap(Component.translatable("note.finalizeWarning"), left + 10, top + 60, 116, 0);
    }

    private int noteToPixelX(int noteX) {
        return noteImageLeftX + noteRegionLeft + noteX*3;
    }

    private void drawCursor(PoseStack matrixStack, int cursorX, int color){
        if(inScreen(cursorX)){
            int x = noteToPixelX(cursorX - sliderPosition);
            int y = noteImageY + noteRegionTop;

            fill(matrixStack, x + 1, y, x + 2, y + 48*3, color);
        }
    }

    public void midiControlCommand(MidiControl controlType) {
        switch (controlType){
            case BEGINNING -> {
                if(!previewing && !recording && !preRecording){
                    editCursor = 0;
                    editCursorEnd = 0;
                    if(!inScreen(editCursor)){
                        setSliderPos(editCursor);
                    }
                }
            }
            case END -> {
                if(!previewing && !recording && !preRecording){
                    editCursor = lengthBeats-1;
                    editCursorEnd = lengthBeats-1;
                    if(!inScreen(editCursor)){
                        setSliderPos(editCursor);
                    }
                }
            }
            case STOP -> {
                if(recording || preRecording){
                    stopRecording();
                }
                else if(previewing){
                    stopPreview();
                }
            }
            case PREVIEW -> {
                if(buttonPreview.active){
                    previewButton();
                }
            }
            case RECORD -> {
                if(buttonRecord.active){
                    recordButton();
                }
            }
        }
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (previewing || recording || preRecording) {
            long currentMillis = System.currentTimeMillis();

            long delta = currentMillis - lastMillis;
            lastMillis = currentMillis;
            cumMillis += delta;
            int currentBeat = (int)(cumMillis*bps)/1000;
            if(preRecording){
                if(currentBeat > oldPreRecordBeat){
                    for(int i=0; i<currentBeat-oldPreRecordBeat; i++){
                        if(preRecordPlayTick){
                            preRecordPlayTick = false;
                            playMetronomeTick();
                        }
                        else{
                            preRecordPlayTick = true;
                        }
                    }
                    if((currentBeat % 8 == 0 && cumMillis > 1500) || (currentBeat % 4 == 0 && cumMillis > 1950)){
                        startRecording();
                    }
                    oldPreRecordBeat = currentBeat;
                }
            }
            else{
                // Previewing or recording
                int oldPreviewCursor = previewCursor;
                previewCursor = previewCursorStart + currentBeat;

                if(previewCursor > sliderPosition + beatsInScreen - 12 && (lengthBeats > sliderPosition + beatsInScreen || recording)){
                    setSliderPos(previewCursor - 24);
                }

                if(oldPreviewCursor != previewCursor){
                    if (!recording && (previewCursor > lengthBeats || (editCursorEnd != editCursor && previewCursor > editCursorEnd+1))) {
                        stopPreview();
                    }
                    else{
                        previewStarted = true;
                        playPreviewSound(oldPreviewCursor, previewCursor);
                    }

                    if(recording){
                        for(NoteEvent note : recordingNotes){
                            if(previewCursor - note.time > 1){
                                note.length += previewCursor - oldPreviewCursor;
                                if(note.length > maxNoteLength) {
                                    note.length = maxNoteLength;
                                }
                            }
                        }
                    }
                }
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, noteGuiLeftTexture);
        blit(matrixStack, noteImageLeftX, noteImageY + 7, noteImageLeftTexX, noteImageLeftTexY, noteImageLeftWidth, noteImageLeftHeight);
        RenderSystem.setShaderTexture(0, noteGuiTextures);
        blit(matrixStack, noteImageX, noteImageY, noteImageTexX, noteImageTexY, noteImageWidth, noteImageHeight);
        if (gettingSigned) {
            drawSigning(matrixStack);
        } else {
            // Draw octave tints
            int x1 = noteImageLeftX + noteRegionLeft;
            int x2 = noteImageLeftX + noteRegionRight;
            for(int i=0; i<4; i++){
                int y1 = noteImageY + noteRegionTop + (4-i)*12*3;
                int y2 = noteImageY + noteRegionTop + (3-i)*12*3;
                fill(matrixStack, x1, y1, x2, y2, octaveColorsTrans[i + currentOctavePos]);
            }

            // Draw octave lines
            for(int i=0; i<4; i++){
                for(int j=0; j<6; j++){
                    int y = noteImageY + noteRegionBottom - i*36 - j*6;
                    fill(matrixStack, x1, y-1, x2+1, y, octaveColors[i + currentOctavePos]);
                }
            }

            // Draw octave names
            for(int i=0; i<4; i++){
                final int x = x1 - 24;
                final int y = noteImageY + noteRegionBottom - 18 - i*36;
                if(currentOctave == i + currentOctavePos){
                    fill(matrixStack, x-10, y-4, x+10, y+12, 0xAAFFFFAA);
                }
                drawCenteredString(matrixStack, font, octaveNames[i + currentOctavePos], x, y, octaveColors[i + currentOctavePos]);
            }

            // Draw measure lines
            if(highlightInterval > 1){
                for(int i = sliderPosition; i<sliderPosition + beatsInScreen; i++){
                    int x = (i - sliderPosition)*3 + noteImageLeftX + noteRegionLeft + 1;
                    if(i % highlightInterval == 0){
                        fill(matrixStack, x, noteImageY + noteRegionTop - 1, x+1, noteImageY + noteRegionBottom + 3, 0xFF88796A);
                    }
                }

                // Draw measure numbers
                matrixStack.pushPose();
                matrixStack.scale(0.5f, 0.5f, 0.5f);
                for(int i = sliderPosition; i<sliderPosition + beatsInScreen; i++) {
                    if (i % highlightInterval == 0) {
                        final int x = (i - sliderPosition) * 3 + noteImageLeftX + noteRegionLeft;
                        final int y = noteImageY + noteRegionTop - 5;
                        final String name = ""+((i/highlightInterval)+1);
                        final int w = font.width(name);
                        font.draw(matrixStack, name, (x - (w-6.0f)/4.0f)*2.f, y*2, 0xFF444400);
                    }
                }
                matrixStack.popPose();
            }

            this.font.draw(matrixStack, "M:", noteImageLeftX + hlButX + 14, noteImageY + hlButY + 2, 0xFF000000);
            this.font.draw(matrixStack, "" + (highlightInterval > 1 ? highlightInterval : "-"), noteImageLeftX + hlButX + 22, noteImageY + hlButY + 2, 0xFF000000);

            this.font.draw(matrixStack, "Tempo", noteImageLeftX + bpmButX - 30, noteImageY + bpmButY, 0xFF000000);
            this.font.draw(matrixStack, "" + bpm, noteImageLeftX + bpmButX - 30, noteImageY + bpmButY + 10, 0xFF000000);
            drawCursor(matrixStack, editCursor, 0xFFAA2222);
            if (!this.isSigned) {
                if(editCursor != editCursorEnd){
                    drawCursor(matrixStack, editCursorEnd, 0xFFAA2222);
                    drawSelectionRect(matrixStack);
                }
            } else {
                int k = this.font.width(noteTitle);
                this.font.draw(matrixStack, noteTitle, (noteImageLeftX + (noteImageWidth + noteImageLeftWidth - k) / 2.0f), noteImageY + 14, 0xFF990000);

                if(this.selfSigned){
                    drawCursor(matrixStack, editCursor, 0xFFAA2222);

                    if(editCursor != editCursorEnd){
                        drawCursor(matrixStack, editCursorEnd, 0xFFAA2222);
                        drawSelectionRect(matrixStack);
                    }
                }
            }

            // Neighbor notes
            if(!neighborsHidden) {
                for (ArrayList<NoteEvent> nn : neighborNotes) {
                    for (NoteEvent event : nn) {
                        drawNote(matrixStack, event, true);
                    }
                }
            }

            // The notes
            for (NoteEvent note : notes) {
                drawNote(matrixStack, note, false);
            }
        }
        if (previewStarted) {
            int i = previewCursor - 1;

            drawCursor(matrixStack, i, 0xFFAA8822);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if(buttonHideNeighbors.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.toggleTooltip"), mouseX, mouseY);
        }
        else if(buttonLockPrevIns.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.lockTooltip"), mouseX, mouseY);
        }
        else if(buttonPreview.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.previewTooltip"), mouseX, mouseY);
        }
        else if(buttonRecord.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.recordTooltip"), mouseX, mouseY);
        }
        else if(bpmDown.isHoveredOrFocused() || bpmUp.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.tempoTooltip"), mouseX, mouseY);
        }
        else if(hlDown.isHoveredOrFocused() || hlUp.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.measureTooltip"), mouseX, mouseY);
        }
        else if(sliderSheetVolume.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.sheetVolumeTooltip"), mouseX, mouseY);
        }
        else if(sliderNoteVolume.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.noteVolumeTooltip"), mouseX, mouseY);
        }
        else if(buttonHelp.isHoveredOrFocused()){
            renderTooltip(matrixStack, Component.translatable("note.helpTooltip"), mouseX, mouseY);
        }

        if(helpOn) {
            int x = noteImageLeftX + 15;
            int y = noteImageY;
            fill(matrixStack, x, y, x + 315, y + 220, 0xEE333333);
            matrixStack.pushPose();
            matrixStack.scale(1.2f, 1.2f, 1.2f);
            drawString(matrixStack, font, Component.translatable("note.helpText0"), (int)((x + 10)/1.2f), (int)((y + 5)/1.2f), 0xFFEEEE11);
            matrixStack.popPose();
            for(int i=1; i<=19; i++){
                Component leftSide = Component.translatable("note.helpText" + i + "a");
                Component rightSide = Component.translatable("note.helpText" + i + "b");
                drawString(matrixStack, font, leftSide, x + 10, y + 10 + 10*i, 0xFFEEEE11);
                drawString(matrixStack, font, rightSide, x + 10 + font.width(leftSide), y + 10 + 10*i, 0xFFEEEEEE);
            }
        }
    }

    private void drawSelectionRect(PoseStack matrixStack) {
        if(inScreen(editCursor) || inScreen(editCursorEnd) || (editCursor < sliderPosition && editCursorEnd >= sliderPosition + beatsInScreen)){
            final int selectionColor = 0x882222AA;
            int timeDrawBeginning = Math.max(editCursor - sliderPosition, 0);
            int timeDrawEnd = Math.min(editCursorEnd - sliderPosition, beatsInScreen);

            int x1 = noteToPixelX(timeDrawBeginning);
            int x2 = noteToPixelX(timeDrawEnd);
            int y1 = noteImageY + noteRegionTop;
            int y2 = y1 + 36*4;

            fill(matrixStack, x1 + 1, y1, x2 + 2, y2, selectionColor);
        }
    }

    private boolean inScreen(int time){
        return time >= sliderPosition && time < sliderPosition + beatsInScreen;
    }

    private static int octaveFromNote(byte note) {
        return (note - IItemInstrument.minNote) / 12;
    }

    private void drawNote(PoseStack matrixStack, NoteEvent event, boolean isNeighbor) {
        int octave = octaveFromNote(event.note);
        if((octave >= currentOctavePos && octave < currentOctavePos + 4) && (inScreen(event.time) || inScreen(event.time + event.length))){
            int timeDrawBeginning = Math.max(event.time - sliderPosition, 0);
            int timeDrawEnd = Math.min(event.time - sliderPosition + event.length, beatsInScreen);

            int xBegin = noteImageLeftX + noteRegionLeft + timeDrawBeginning*3;
            int xEnd = noteImageLeftX + noteRegionLeft + timeDrawEnd*3;
            if(xBegin == xEnd){
                return;
            }
            int xFillBegin = timeDrawBeginning == event.time - sliderPosition ? xBegin + 1 : xBegin;
            int xFillEnd = timeDrawEnd == event.time - sliderPosition + event.length ? xEnd - 1 : xEnd;

            int y = noteImageY + noteRegionTop + (47 - event.note + IItemInstrument.minNote)*3 + currentOctavePos*36;
            final int outlineColor = (event == currentlyAddedNote || isNeighbor) ? 0x77000000 : 0xFF000000;
            int red = event.volume >= 64 ? 255 : event.volume * 4;
            int green = event.volume < 64 ? 255 : 255 - event.volume * 4;
            final int fillColor = ((event == currentlyAddedNote || isNeighbor) ? 0x77000000 : 0xFF000000) | red << 16 | green << 8;

            fill(matrixStack, xBegin, y, xEnd, y + 3, outlineColor);
            fill(matrixStack, xFillBegin, y+1, xFillEnd, y + 2, fillColor);
        }
    }

    private void startPreview() {
        this.previewCursor = editCursor;
        boolean noteFound = false;
        for(int i=0; i<notes.size(); i++){
            if(notes.get(i).time >= previewCursor){
                previewNextNoteID = i;
                noteFound = true;
                break;
            }
        }
        // Only start preview if you find a note to play next
        if(noteFound){
            if(!neighborsHidden) {
                for (int i=0; i<neighborNotes.size(); i++){
                    ArrayList<NoteEvent> nn = neighborNotes.get(i);
                    for(int j=0; j<nn.size(); j++){
                        if(nn.get(j).time >= previewCursor){
                            neighborPreviewNextNoteIDs.set(i, j);
                            break;
                        }
                    }
                }
            }

            this.previewCursorStart = editCursor;
            this.previewStarted = false;
            this.previewing = true;
            this.cumMillis = 0;
            this.lastMillis = System.currentTimeMillis();
            this.buttonPreview.setTexStarts(240, 0);

            if(!inScreen(previewCursorStart)){
                setSliderPos(previewCursorStart);
            }
        }
        updateButtons();
    }

    private void startPreRecording() {
        preRecording = true;
        this.previewCursor = editCursor;
        for(int i=0; i<notes.size(); i++){
            if(notes.get(i).time >= previewCursor){
                previewNextNoteID = i;
                break;
            }
        }
        if(!neighborsHidden) {
            for (int i=0; i<neighborNotes.size(); i++){
                ArrayList<NoteEvent> nn = neighborNotes.get(i);
                for(int j=0; j<nn.size(); j++){
                    if(nn.get(j).time >= previewCursor){
                        neighborPreviewNextNoteIDs.set(i, j);
                        break;
                    }
                }
            }
        }

        this.previewCursorStart = editCursor;
        this.previewStarted = false;
        this.cumMillis = 0;
        this.oldPreRecordBeat = 0;
        this.lastMillis = System.currentTimeMillis();
        this.buttonRecord.setTexStarts(240, 0);

        if(!inScreen(previewCursorStart)){
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

        if(!inScreen(previewCursorStart)){
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

    private void setSliderPos(int time){
        time = Math.min(Math.max(time, 0), maxSliderPosition);

        sliderTime.setValue((float)(time)/(float)maxSliderPosition);
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
        if(!notes.isEmpty()){
            for(NoteEvent event : notes){
                lengthBeats = (short)(event.time + event.length) > lengthBeats ? (short)(event.time + event.length) : lengthBeats;
            }

            if(updateSliderPos) {
                // Update slider
                int oldMaxSliderPos = maxSliderPosition;
                maxSliderPosition = Math.min(Math.max(lengthBeats + beatsInScreen, 680), maxLengthBeats);
                if(maxSliderPosition > oldMaxSliderPos){
                    setSliderPos(sliderPosition);
                }
                else if(maxSliderPosition < oldMaxSliderPos){
                    setSliderPos(maxSliderPosition - beatsInScreen);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double dmouseX, double dmouseY, int mouseButton) {
        if(helpOn) {
            helpOn = false;
            return true;
        }

        // mouseButton: 0 = left click, 1 = right click, 2 = middle click
        if(super.mouseClicked(dmouseX, dmouseY, mouseButton)){
            this.setDragging(true);
            return true;
        }

        int mouseX = (int)Math.round(dmouseX);
        int mouseY = (int)Math.round(dmouseY);

        boolean viewingSelfSigned = isSigned && selfSigned;
        boolean composing = !isSigned && !gettingSigned;

        if(!gettingSigned){
            if (mouseButton == 1) {
                int mx = mouseX - noteImageLeftX;
                int my = mouseY - noteImageY;
                if (validClick(mx, my)) {
                    selectionStart = editCursorEnd = editCursor = ((mx - noteRegionLeft)/3) + sliderPosition;
                }
            }
        }

        if(viewingSelfSigned){
            if (mouseButton == 0){
                editCursorEnd = editCursor;
            }
        }
        if (composing) {
            int mx = mouseX - noteImageLeftX;
            int my = mouseY - noteImageY;
            if (validClick(mx, my)) {
                int nrx = mx - noteRegionLeft;
                int nry = my - noteRegionTop;

                int time = (nrx / 3) + sliderPosition;
                int note = 47 - (nry / 3) + IItemInstrument.minNote + currentOctavePos*12;
                if (mouseButton == 0) {
                    addNote((byte) note, (short) time);
                    dirtyFlag.hasNotes = true;
                    dirtyFlag.hasLength = true;

                    editCursorEnd = editCursor;
                }
                else if (mouseButton == 2) {
                    int i = findNote((byte) note, (short) time);
                    if(i >= 0){
                        NoteEvent event = notes.get(i);
                        noteEditBox.appear(mouseX, mouseY, event);
                    }
                }
            }
            else {
                // Test current octave clicks
                for(int i=0; i<4; i++) {
                    final int x = noteRegionLeft - 24;
                    final int y = noteRegionBottom - 18 - i*36;
                    if(mx >= x-10 && mx <= x+10 && my >= y-4 && my <= y+12) {
                        currentOctave =  currentOctavePos + i;
                        midiHandler.currentOctave = currentOctave;
                        if(recording) {
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
        if(undoStack.size() >= maxUndoLength){
            undoStack.removeLast();
        }
        ArrayList<NoteEvent> stackNotes = new ArrayList<>(notes.size());
        for(NoteEvent note : notes) {
            stackNotes.add(note.clone());
        }
        undoStack.push(stackNotes);
    }


    private void addNote(byte note, short time) {
        addNote(note, time, true);
    }

    private void addNote(byte note, short time, boolean pushUndo) {
        if(pushUndo) {
            pushUndo();
        }
        int i = findNote(note, time);
        if(i < 0){
            addNote(note, time, (byte)(127.f*brushVolume));
        }
        else{
            notes.remove(i);
        }
        updateLength();
    }

    private void addNote(byte note, short time, byte volume) {
        NoteEvent newEvent = new NoteEvent(note, time, volume, (byte) 1);
        currentlyAddedNote = newEvent;
        for(int i=0; i<notes.size(); i++){
            if(notes.get(i).time > time){
                notes.add(i, newEvent);
                return;
            }
        }
        notes.add(newEvent);
    }

    private void addRecordingNote(NoteEvent noteEvent) {
        for(int i=0; i<notes.size(); i++){
            if(notes.get(i).time > noteEvent.time){
                notes.add(i, noteEvent);
                recordingNotes.add(noteEvent);
                return;
            }
        }
        notes.add(noteEvent);
        recordingNotes.add(noteEvent);
    }

    private void finishAddingNote() {
        if(currentlyAddedNote == null){
            return;
        }
        playSound(currentlyAddedNote, previewInstrument);
        currentlyAddedNote = null;
        updateLength();
    }

    private int findNote(byte note, short time) {
        for(int i=notes.size()-1; i>=0; i--) {
            NoteEvent event = notes.get(i);
            if((event.time <= time && event.endTime() >= time) && event.note == note) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        if(this.getFocused() != null && this.isDragging()){
            this.getFocused().mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            return true;
        }

        if(this.tickCount < 10) {
            // This check is for preventing the annoying selection at start problem
            return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
        }
        int mouseX = (int) Math.round(posX);
        int mouseY = (int) Math.round(posY);

        int mx = mouseX - noteImageLeftX;
        int my = mouseY - noteImageY;

        // if right button is pressed
        if(mouseButton == 1){
            if (validClick(mx, my)) {
                int noteX = ((mx - noteRegionLeft)/3) + sliderPosition;
                if (selectionStart > noteX) {
                    editCursor = noteX;
                } else {
                    editCursorEnd = noteX;
                }
            }
        }
        else if(mouseButton == 0) {
            if (currentlyAddedNote != null && validClick(mx, my)) {
                int time = ((mx - noteRegionLeft)/3) + sliderPosition;
                if(currentlyAddedNote.time < time && time - currentlyAddedNote.time <= maxNoteLength){
                    currentlyAddedNote.length = (byte)(time - currentlyAddedNote.time);
                }
            }
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        this.setDragging(false);
        if(noteEditBox.active){
            noteEditBox.mouseReleased(posX, posY, mouseButton);
            return true;
        }
        if(mouseButton == 0) {
            finishAddingNote();
        }

        return true;
    }

    private void putSpace(int x) {
        if (x == maxLengthBeats - 1) return;
        addEditCursor(1);
        if (lengthBeats == 0 || lengthBeats <= x) {
            return;
        }

        pushUndo();
        dirtyFlag.hasNotes = true;
        dirtyFlag.hasLength = true;
        for(int i=0; i<notes.size(); i++){
            NoteEvent event = notes.get(i);
            if(event.time > x) {
                event.time += 1;
                if(event.time + event.length > maxLengthBeats) {
                    notes.remove(i);
                    i --;
                }
            }
        }
        updateLength();
    }

    private void encodeToClipboard(){
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        ArrayList<NoteEvent> toBeCopied = new ArrayList<>();
        for (NoteEvent event : notes) {
            if (event.time >= editCursor && event.time <= editCursorEnd && event.endTime() >= editCursor && event.endTime() <= editCursorEnd) {
                toBeCopied.add(event);
            }
        }
        buffer.writeByte(copyBeginByte);
        buffer.writeInt(editCursorEnd - editCursor);
        buffer.writeInt(toBeCopied.size());
        for(NoteEvent event : toBeCopied){
            event.time -= editCursor; // Convert time to according to cursor
            event.encodeToBuffer(buffer);
            event.time += editCursor; // Convert time back to normal
        }
        String encodeBytes = Base64.getEncoder().encodeToString(buffer.accessByteBufWithCorrectSize());
        GLFW.glfwSetClipboardString(Minecraft.getInstance().getWindow().getWindow(), encodeBytes);

        editCursorEnd = editCursor;
    }

    private void decodeFromClipboard(boolean pushBack){
        String encodedMusic = GLFW.glfwGetClipboardString(Minecraft.getInstance().getWindow().getWindow());
        if(encodedMusic != null && !encodedMusic.isEmpty()){
            byte[] byteArray;
            try { // Try because this can fail with weird clipboard content
                byteArray = Base64.getDecoder().decode(encodedMusic);
            }
            catch (IllegalArgumentException ex){
                return;
            }

            int length = 0;
            ArrayList<NoteEvent> toBePasted;
            // Check begin byte
            if(byteArray[0] != copyBeginByte){
                // Old version

                // Check if all values are valid
                for (byte b : byteArray) {
                    if (b < 0 || b > 48) {
                        XercaMusic.LOGGER.info("User tried to copy invalid data into music: " + b);
                        return;
                    }
                }

                // Copy values
                toBePasted = ItemMusicSheet.oldMusicToNotes(byteArray);
                if(!toBePasted.isEmpty()) {
                    for (NoteEvent event : toBePasted) {
                        length = (short) (event.time + event.length) > length ? (short) (event.time + event.length) : length;
                    }
                }
            }
            else {
                // New version
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.copiedBuffer(byteArray));
                buffer.readByte();

                // Read copied time length and note event count
                length = buffer.readInt();
                int count = buffer.readInt();

                // Read the note events into an array
                toBePasted = new ArrayList<>(count);
                for(int i=0; i<count; i++){
                    toBePasted.add(NoteEvent.fromBuffer(buffer));
                }
            }

            pushUndo();
            if(pushBack) {
                // Push back the existing future note events
                for(NoteEvent event : notes){
                    if(event.time >= editCursor){
                        event.time += length;
                    }
                }
            }

            for(NoteEvent event : toBePasted){
                event.time += editCursor;
                notes.add(event);
            }

            sortNotes();
            if(!pushBack) {
                removeDuplicates();
            }

            updateLength();
            editCursor += length + 1;
            editCursorEnd = editCursor;

            dirtyFlag.hasNotes = true;
            dirtyFlag.hasLength = true;
        }
    }

    private void removeDuplicates() {
        for(int i=0; i<notes.size()-1; i++) {
            NoteEvent event1 = notes.get(i);
            NoteEvent event2 = notes.get(i+1);
            if (event1.time == event2.time && event1.note == event2.note &&  event1.length == event2.length) {
                notes.remove(i);
                i--;
            }
        }
    }

    private void delAtCursor(int x){
        boolean doSort = false;
        for(int i=0; i<notes.size(); i++){
            NoteEvent event = notes.get(i);
            if(event.time > x) {
                event.time -= 1;
                doSort = true;
            }
            else if(event.time < x && event.time + event.length > x){
                event.length --;
            }
            else if(event.time == x) {
                if(event.length == 1){
                    notes.remove(i);
                    i--;
                }
                else{
                    event.length --;
                }
            }
        }
        if(doSort){
            sortNotes();
        }
        updateLength();
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        setFocused(null);
        super.keyReleased(keyCode, scanCode, modifiers);
        int firstScanCode = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
        int lastScanCode = firstScanCode + 11;
        if (scanCode >= firstScanCode && scanCode <= lastScanCode) {
            if(currentOctave >= 0){
                if(recording){
                    endSound(IItemInstrument.noteToId((byte) ((scanCode - firstScanCode + 1 + IItemInstrument.minNote) + 12 * currentOctave)));
                }
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        setFocused(null);
        super.keyPressed(keyCode, scanCode, modifiers);

        // Copying when viewing self-signed
        boolean viewingSelfSigned = isSigned && selfSigned;
        if(viewingSelfSigned){
            if(keyCode == GLFW.GLFW_KEY_C && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                encodeToClipboard();
            }
            if(keyCode == GLFW.GLFW_KEY_A && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL){
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
                        if (editCursor > maxLengthBeats - 1) setEditCursor(maxLengthBeats - 1);
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
                            decodeFromClipboard(!((modifiers & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT));
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
                                    if (event.endTime() >= editCursor && event.time <= editCursorEnd) {
                                        if (IItemInstrument.noteToId(event.note) / 12 > 0) {
                                            if (!changed) {
                                                pushUndo();
                                                dirtyFlag.hasNotes = true;
                                                dirtyFlag.hasLength = true;

                                                changed = true;
                                            }
                                            event.note -= 12;
                                        }
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
                                if (event.endTime() >= editCursor && event.time <= editCursorEnd) {
                                    if (IItemInstrument.noteToId(event.note) / 12 < 7) {
                                        if (!changed) {
                                            pushUndo();
                                            dirtyFlag.hasNotes = true;
                                            dirtyFlag.hasLength = true;

                                            changed = true;
                                        }
                                        event.note += 12;
                                    }
                                }
                            }
                            resetEditCursorEnd = false;
                        }
                    }
                    case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> resetEditCursorEnd = false;
                    default -> {
                        int firstScanCode = GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q);
                        int lastScanCode = firstScanCode + 11;
                        if (scanCode >= firstScanCode && scanCode <= lastScanCode) {
                            if (currentOctave >= 0) {
                                if (recording) {
                                    startSound(IItemInstrument.noteToId((byte) ((scanCode - firstScanCode + 1 + IItemInstrument.minNote) + 12 * currentOctave)), (byte) 100);
                                } else {
                                    putSpace(x - 1);
                                    addNote((byte) ((scanCode - firstScanCode + 1 + IItemInstrument.minNote) + 12 * currentOctave), (short) (x), false);
                                    finishAddingNote();
                                }
                            }
                        }
                    }
                }
                if(resetEditCursorEnd) {
                    editCursorEnd = editCursor;
                }
            }
        }
        return true;
    }

    private void deleteSelected() {
        boolean doSort = false;
        for(int i=0; i<notes.size(); i++) {
            NoteEvent event = notes.get(i);
            if(event.time >= editCursor && event.endTime() <= editCursorEnd) {
                notes.remove(i);
                i--;
            }
            else if(event.time < editCursor && event.endTime() >= editCursor && event.endTime() <= editCursorEnd) {
                event.length = (byte)(editCursor - event.time);
            }
            else if(event.time >= editCursor && event.time <= editCursorEnd && event.endTime() > editCursorEnd) {
                event.length = (byte)(event.endTime() - editCursorEnd);
                event.time = (short)(editCursor + 1);
                doSort = true;
            }
            else if(event.time < editCursor && event.endTime() > editCursorEnd) {
                event.length = (byte)(editCursor - event.time);
            }
            else if(event.time > editCursorEnd){
                event.time -= editCursorEnd - editCursor;
                doSort = true;
            }
        }
        if(doSort) {
            sortNotes();
        }
    }

    private void sortNotes(){
        notes.sort(Comparator.comparingInt(NoteEvent::startTime));
    }

    private void setEditCursor(int x){
        if(editCursor != editCursorEnd){
            editCursor = x;
        }
        else{
            editCursor = x;
            editCursorEnd = x;
        }
    }

    private void addEditCursor(int x){
        setEditCursor(editCursor + x);
    }

    @Override
    public boolean charTyped(char typedChar, int something) {
        super.charTyped(typedChar, something);

        if (!this.isSigned) {
            if (this.gettingSigned) {
                if (this.noteTitle.length() < 16 && SharedConstants.isAllowedChatCharacter(typedChar)) {
                    this.noteTitle = this.noteTitle + typedChar;
                    this.updateButtons();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scroll){
        if (scroll != 0.d) {
            if(scroll > 0){
                octaveUp.playDownSound(Minecraft.getInstance().getSoundManager());
                octaveUp.onPress();
            }
            else if(scroll < 0){
                octaveDown.playDownSound(Minecraft.getInstance().getSoundManager());
                octaveDown.onPress();
            }
            return true;
        }
        return super.mouseScrolled(x, y, scroll);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void removed() {
        if (dirtyFlag.hasAny()) {
            if(recording){
                stopRecording();
            }
            if(dirtyFlag.hasNotes || dirtyFlag.hasLength) {
                version++;
                dirtyFlag.hasVersion = true;
                MusicManagerClient.setMusicData(id, version, notes);
            }

            try {
                MusicUpdatePacket pack = new MusicUpdatePacket(dirtyFlag, notes, lengthBeats, bps, volume, isSigned,
                        noteTitle, (byte)previewInstrument, prevInsLocked, id, version, highlightInterval);
                XercaMusic.NETWORK_HANDLER.sendToServer(pack);
            } catch (ImportMusicSendPacket.NotesTooLargeException e) {
                int partsCount = (int)Math.ceil((double)notes.size()/(double)MAX_NOTES_IN_PACKET);

                try {
                    MusicUpdatePacket pack = new MusicUpdatePacket(dirtyFlag, null, lengthBeats, bps, volume, isSigned,
                            noteTitle, (byte)previewInstrument, prevInsLocked, id, version, highlightInterval);
                    NotesPartAckFromServerPacketHandler.addCallback(id, ()-> XercaMusic.NETWORK_HANDLER.sendToServer(pack));
                    for(int i=0; i<partsCount; i++) {
                        SendNotesPartToServerPacket partPack = new SendNotesPartToServerPacket(id, partsCount, i, notes.subList(i*MAX_NOTES_IN_PACKET, Math.min((i+1)*MAX_NOTES_IN_PACKET, notes.size())));
                        XercaMusic.NETWORK_HANDLER.sendToServer(partPack);
                    }
                } catch (ImportMusicSendPacket.NotesTooLargeException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if (SoundEvents.CLOSE_SCROLL != null) {
            editingPlayer.playSound(SoundEvents.CLOSE_SCROLL, 1.0f, 0.8f + editingPlayer.level.random.nextFloat()*0.4f);
        }
    }

    private boolean validClick(int x, int y) {
        return x <= noteRegionRight && x >= noteRegionLeft && y <= noteRegionBottom && y >= noteRegionTop;
    }


    public static class ChangeableImageButton extends Button {
        protected final ResourceLocation resourceLocation;
        protected int xTexStart;
        protected int yTexStart;
        protected final int yDiffText;
        protected final int texWidth;
        protected final int texHeight;

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, OnPress onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, 256, 256, onClick);
        }

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, Component.empty());
        }

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick, Component message) {
            super(x, y, width, height, message, onClick);
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

        protected int preRender(){
            RenderSystem.setShaderTexture(0, this.resourceLocation);
            GlStateManager._disableDepthTest();
            int yTexStartNew = this.yTexStart;
            if (this.isHovered && this.active) {
                yTexStartNew += this.yDiffText;
            }
            return yTexStartNew;
        }

        protected void postRender(){
            GlStateManager._enableDepthTest();
        }

        @Override
        public void renderButton(@NotNull PoseStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            int yTexStartNew = preRender();
            blit(matrixStack, this.x, this.y, (float)this.xTexStart, (float)yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
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
        public void renderButton(@NotNull PoseStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            int yTexStartNew = preRender();

            blit(matrixStack, this.x, this.y, (float)this.xTexStart, (float)yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
            if(prevInsLocked){
                blit(matrixStack, this.x, this.y, 0, this.texHeight - this.height, this.width, this.height, this.texWidth, this.texHeight);
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
        private NoteEvent event;
        private final int noteY = 18;
        private final int lengthY = 29;
        private final AbstractWidget[] children = new AbstractWidget[7];
        private NoteSound previewSound;
        private boolean changed = false;

        public NoteEditBox(int x, int y, int w, int h, Component msg) {
            super(x, y, w, h, msg);
            sliderVelocity = new BetterSlider(10, 0, 50, 10, Component.literal("Vol "), Component.empty(), 0, 100, 50, true){
                @Override public void applyValue() {

                    setChanged();
                    event.volume = (byte)Math.round(value * 127.0f);
                }
            };
            buttonNoteDown = new Button(0, 0, 10, 8, Component.translatable("note.leftButton"), (button) -> {
                if(event.note > 0) {
                    setChanged();
                    event.note--;
                    playPrev();
                }
            });
            buttonNoteUp = new Button(0, 0, 10, 8, Component.translatable("note.rightButton"), (button) -> {
                if(event.note < 95) {
                    setChanged();
                    event.note++;
                    playPrev();
                }
            });
            buttonLengthDown = new Button(0, 0, 10, 8, Component.translatable("note.leftButton"), (button) -> {
                if(event.length > 1) {
                    setChanged();
                    event.length--;
                    playPrev();
                }
            });
            buttonLengthUp = new Button(0, 0, 10, 8, Component.translatable("note.rightButton"), (button) -> {
                if(event.length < maxNoteLength) {
                    setChanged();
                    event.length++;
                    playPrev();
                }
            });
            buttonExit = new Button(0, 0, 10, 10, Component.translatable("note.exitButton"), (button) -> {
                this.visible = false;
                this.active = false;
                if(previewSound != null && !previewSound.isStopped()) {
                    previewSound.stopSound();
                }
            });
            buttonPrev = new Button(0, 0, 10, 10, Component.translatable("note.startPreviewButton"), (button) -> playPrev());

            children[0] = sliderVelocity;
            children[1] = buttonNoteDown;
            children[2] = buttonNoteUp;
            children[3] = buttonLengthDown;
            children[4] = buttonLengthUp;
            children[5] = buttonExit;
            children[6] = buttonPrev;
        }

        private void setChanged() {
            if(!changed) {
                changed = true;
                dirtyFlag.hasNotes = true;
                dirtyFlag.hasLength = true;
                pushUndo();
            }
        }

        private void playPrev() {
            if(previewSound != null && !previewSound.isStopped()) {
                previewSound.stopSound();
            }
            previewSound = playSound(event, previewInstrument);
        }

        @Override
        public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
            if (this.visible && event != null) {
                fill(poseStack, x, y, x + width, y + height, 0xFFEEEEEE);
                Minecraft minecraft = Minecraft.getInstance();
                Font font = minecraft.font;
                int noteId = IItemInstrument.noteToId(event.note);
                int octave = noteId / 12;
                font.draw(poseStack, noteNames[noteId % 12] + (noteId % 12 < 3 ? octave : octave + 1) , x + 15, y + noteY, 0xFFD3C200);
                font.draw(poseStack, noteNamesSolfege[noteId % 12], x + 35, y + noteY, 0xFFD3C200);
                font.draw(poseStack, ("" + event.length) + (event.length == 1 ? " Beat" : " Beats"), x + 15, y + lengthY, 0xFF495EE5);

                for(AbstractWidget widget : children) {
                    widget.render(poseStack, mouseX, mouseY, partialTicks);
                }

                if(buttonPrev.isHoveredOrFocused()) {
                    renderTooltip(poseStack, Component.translatable("note.previewNoteTooltip"), mouseX, mouseY);
                }
                else if(buttonExit.isHoveredOrFocused()) {
                    renderTooltip(poseStack, Component.translatable("note.closeNoteTooltip"), mouseX, mouseY);
                }
            }
        }

        public void appear(int x, int y, NoteEvent event) {
            changed = false;
            this.x = x;
            this.y = y;
            this.visible = true;
            this.active = true;
            this.event = event;
            sliderVelocity.x = x + 10;
            int sliderY = 41;
            sliderVelocity.y = y + sliderY;
            sliderVelocity.setValue(event.floatVolume() * 100.0f);
            sliderVelocity.applyValue();

            buttonNoteDown.x = x + 3;
            buttonNoteDown.y = y + noteY;
            buttonNoteUp.x = x + 59;
            buttonNoteUp.y = y + noteY;

            buttonLengthDown.x = x + 3;
            buttonLengthDown.y = y + lengthY;
            buttonLengthUp.x = x + 59;
            buttonLengthUp.y = y + lengthY;

            buttonExit.x = x + 59;
            buttonExit.y = y + 2;

            buttonPrev.x = x + 3;
            buttonPrev.y = y + 2;

            playPrev();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (this.active && this.visible) {
                if(mouseButton == 2){
                    this.visible = false;
                    this.active = false;
                }

                for(AbstractWidget widget : children) {
                    if(mouseX >= widget.x && mouseX < widget.x + widget.getWidth() &&
                            mouseY >= widget.y && mouseY < widget.y + widget.getHeight()){
                        widget.mouseClicked(mouseX, mouseY, mouseButton);
                        return true;
                    }
                }

                boolean flag = this.clicked(mouseX, mouseY);
                if (flag) {
                    this.onClick(mouseX, mouseY);
                }
                else{
                    this.visible = false;
                    this.active = false;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
            if(posX >= sliderVelocity.x && posX < sliderVelocity.x + sliderVelocity.getWidth() &&
                    posY >= sliderVelocity.y && posY < sliderVelocity.y + sliderVelocity.getHeight()){
                sliderVelocity.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            }
            return true;
        }

        @Override
        public boolean mouseReleased(double posX, double posY, int mouseButton) {
            sliderVelocity.onRelease(posX, posY);
            return true;
        }

        @Override
        public void updateNarration(@NotNull NarrationElementOutput output) {
        }

        private final String[] noteNames = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
        private final String[] noteNamesSolfege = {"La", "La#", "Si", "Do", "Do#", "Re", "Re#", "Mi", "Fa", "Fa#", "Sol", "Sol#"};
    }
}
