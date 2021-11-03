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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmlclient.gui.widget.Slider;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.MusicUpdatePacket;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.UUID;

public class GuiMusicSheet extends Screen {
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
    private final static int bpmButX = 250;
    private final static int bpmButY = 12;
    private final static int[] octaveColors = {0xFF5B3200, 0xFFFF0000, 0xFF0AEE00, 0xFF0059FF, 0xFF7B00FF, 0xFFEF00B7, 0xFF00E2DF, 0XFFF4E800};
    private final static int[] octaveColorsTrans = {0x165B3200,0x16FF0000, 0x160AEE00, 0x160059FF, 0x167B00FF, 0x16EF00B7, 0x1600E2DF, 0X16F4E800};
    private static final int maxLengthBeats = 32000;
    private static final byte copyBeginByte = (byte)50;
    private int noteImageX;
    private int noteImageLeftX;
    private int noteImageY;
    private int currentOctave = 2;
    private boolean isSigned;
    private int generation;
    private boolean gettingSigned;
    private boolean previewing;
    private boolean previewStarted;
    private int previewCursor;
    private int previewCursorStart;
    private int editCursor;
    private int editCursorEnd;
    private int selectionStart; // where the first right click happened when selecting
    private int tickCount;
    private String noteTitle = "";
    private Button bpmUp;
    private Button bpmDown;
    private Button octaveUp;
    private Button octaveDown;
    private Button buttonSign;
    private Button buttonCancel;
    private Button buttonFinalize;
    private Slider sliderTime;
    private Slider sliderVolume;
    private NoteEditBox noteEditBox;
    private ChangeableImageButton buttonPreview;
    private ChangeableImageButton buttonHideNeighbors;
    private LockImageButton buttonLockPrevIns;
    private boolean neighborsHidden = false;
    private boolean prevInsLocked = false;
    private boolean selfSigned = false;

    private final UUID id;
    private int version;
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
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

    private final ArrayList<ArrayList<NoteEvent>> neighborNotes = new ArrayList<>();
    private final ArrayList<Integer> neighborPreviewNextNoteIDs = new ArrayList<>();
    private final ArrayList<Integer> neighborPrevInstruments = new ArrayList<>();

    private int getCurrentOffhandInsIndex(){
        Item offhand = editingPlayer.getOffhandItem().getItem();
        if (offhand instanceof ItemInstrument ins) {
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
                            neighborNotes.add((ArrayList<NoteEvent>) data.notes.clone());
                            neighborPrevInstruments.add(ItemMusicSheet.getPrevInstrument(neighbor));
                            neighborPreviewNextNoteIDs.add(-1);
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
            this.buttonSign = this.addRenderableWidget(new Button( noteImageLeftX + 112, noteImageY + noteImageHeight, 98, 20, new TranslatableComponent("note.signButton"), button -> {
                if (!isSigned) {
                    gettingSigned = true;
                    updateButtons();
                }
            }));
            this.buttonFinalize = this.addRenderableWidget(new Button( noteImageLeftX + 112, 145, 98, 20, new TranslatableComponent("note.finalizeButton"), button -> {
                if (!isSigned) {
                    dirtyFlag.hasSigned = true;
                    dirtyFlag.hasTitle = true;
                    isSigned = true;
                    if(minecraft != null){
                        minecraft.setScreen(null);
                    }
                }
            }));
            this.buttonCancel = this.addRenderableWidget(new Button( noteImageLeftX + 112, 170, 98, 20, new TranslatableComponent("gui.cancel"), button -> {
                if (!isSigned) {
                    gettingSigned = false;
                    updateButtons();
                }
            }));
        }
        this.bpmUp = this.addRenderableWidget(new Button(noteImageLeftX + bpmButX, noteImageY + bpmButY, bpmButW, bpmButH, new TranslatableComponent("note.upButton"), button -> {
            if (!isSigned || selfSigned || generation > 1) {
                if(bps < 50){
                    bps ++;
                    bpm = 60*bps;
                    dirtyFlag.hasBps = true;
                    cumMillis *= (float)(bps-1)/(float)bps;
                }
            }
        }));
        this.bpmDown = this.addRenderableWidget(new Button(noteImageLeftX + bpmButX, noteImageY + bpmButY + 1 + bpmButH, bpmButW, bpmButH, new TranslatableComponent("note.downButton"), button -> {
            if (!isSigned || selfSigned || generation > 1) {
                if(bps > 1){
                    bps --;
                    bpm = 60*bps;
                    dirtyFlag.hasBps = true;
                    cumMillis *= (float)(bps+1)/(float)bps;
                }
            }
        }));
        this.buttonPreview = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 50, 16, 16, 16, 224, 0, 16, noteGuiTextures, button -> {
            if (!previewing) {
                startPreview();
            } else {
                stopPreview();
            }
        }));

        this.buttonHideNeighbors = this.addRenderableWidget(new ChangeableImageButton(noteImageLeftX + 70, 16, 16, 16, 192, 0, 16, noteGuiTextures, button -> {
            neighborsHidden = !neighborsHidden;
            if (neighborsHidden) {
                this.buttonHideNeighbors.setTexStarts(208, 0);
            } else {
                this.buttonHideNeighbors.setTexStarts(192, 0);
            }
        }));

        this.buttonLockPrevIns = this.addRenderableWidget(new LockImageButton( noteImageLeftX + 90, 16, 16, 16, previewInstrument*16 + 16, 32*((previewInstrument+1)/16), 16, instrumentTextures, button -> {
            if(!isSigned || selfSigned || generation > 1){
                prevInsLocked = !prevInsLocked;
                dirtyFlag.hasPrevInsLocked = true;
                if(!prevInsLocked){
                    int index = getCurrentOffhandInsIndex();
                    if (index != previewInstrument) {
                        previewInstrument = index;
                        this.buttonLockPrevIns.setTexStarts(previewInstrument*16 + 16, 32*((previewInstrument+1)/16));
                    }
                }
            }
        }));

        this.octaveUp = this.addRenderableWidget(new Button(noteImageLeftX + 15, noteImageY + 30, bpmButW, bpmButH, new TranslatableComponent("note.upButton"), button -> {
            if(currentOctavePos < 4){
                currentOctavePos ++;
            }
        }));

        this.octaveDown = this.addRenderableWidget(new Button(noteImageLeftX + 15, noteImageY + noteRegionBottom, bpmButW, bpmButH, new TranslatableComponent("note.downButton"), button -> {
            if(currentOctavePos > 0){
                currentOctavePos --;
            }
        }));

        this.sliderTime = this.addRenderableWidget(new Slider(noteImageLeftX + noteRegionLeft, noteImageY + noteRegionBottom + 4, noteRegionRight-noteRegionLeft, 10,
                TextComponent.EMPTY, TextComponent.EMPTY, 0, 1, 0, false, false, b -> {}, (slider) -> {
            sliderPosition = (int)(slider.sliderValue * (double)maxSliderPosition);
        }));

        this.sliderVolume = this.addRenderableWidget(new Slider(noteImageLeftX + noteRegionRight - 50, noteImageY + 15, 50, 10,
                new TextComponent("Vol "), TextComponent.EMPTY, 0, 100, volume*100.f, false, true, b -> {}, (slider) -> {
            if(!isSigned || selfSigned || generation > 1) {
                volume = ((float) slider.sliderValue);
                dirtyFlag.hasVolume = true;
            }
        }));

        this.noteEditBox = this.addRenderableWidget(new NoteEditBox(0, 0, 60, 45, TextComponent.EMPTY));

        updateButtons();

        updateLength();
    }

    private void updateButtons() {
        if (!this.isSigned) {
            this.buttonSign.visible = !this.gettingSigned;
            this.buttonCancel.visible = this.gettingSigned;
            this.buttonFinalize.visible = this.gettingSigned;
            this.buttonFinalize.active = !this.noteTitle.trim().isEmpty();
        }
        this.bpmDown.visible = this.bpmUp.visible = !this.gettingSigned && (!this.isSigned || this.selfSigned || this.generation > 1);
        this.buttonPreview.visible = !this.gettingSigned;
        this.buttonLockPrevIns.visible =  !this.gettingSigned;
        this.buttonLockPrevIns.active = !this.isSigned || this.selfSigned || this.generation > 1;
        this.sliderVolume.visible =  !this.gettingSigned;
        this.sliderVolume.active = !this.isSigned || this.selfSigned || this.generation > 1;
        this.noteEditBox.visible = false;
        this.noteEditBox.active = false;
        this.octaveDown.visible =  !this.gettingSigned;
        this.octaveUp.visible =  !this.gettingSigned;
        this.sliderTime.visible =  !this.gettingSigned;

        this.buttonHideNeighbors.visible = (this.neighborNotes.size() > 0) && !this.gettingSigned;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick() {
        super.tick();
        ++this.tickCount;
    }

    private void playSound(NoteEvent event, int previewInstrument){
        if(event.note < ItemInstrument.minNote || event.note > ItemInstrument.maxNote){
            XercaMusic.LOGGER.warn("Note is invalid: " + event.note);
            return;
        }

        ItemInstrument.InsSound insSound;
        if(previewInstrument >= 0 && previewInstrument < Items.instruments.length){
            ItemInstrument ins = Items.instruments[previewInstrument];
            insSound = ins.getSound(event.note);
        }
        else {
            insSound = SoundEvents.fakeHarpIns.getSound(event.note);
        }
        if(insSound == null){
            return;
        }

        NoteSound sound = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                ClientStuff.playNote(insSound.sound, editingPlayer.getX(), editingPlayer.getY(), editingPlayer.getZ(), volume*event.floatVolume(), insSound.pitch, (byte)beatsToTicks(event.length)));
    }

    private int beatsToTicks(int beats){
        return Math.round(((float)beats) * 20.0f / ((float)bps));
    }

    private void playPreviewSound(int curStart, int curEnd) {
        if(previewNextNoteID < notes.size()) {
            NoteEvent event = notes.get(previewNextNoteID);
            while (event.time >= curStart && event.time < curEnd) {
                playSound(event, previewInstrument);
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
                        playSound(n, ins);
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
        this.font.draw(matrixStack, ChatFormatting.DARK_GRAY + authorStr, left + (116 - i1) / 2, top + 42, 0);
        this.font.drawWordWrap(new TranslatableComponent("note.finalizeWarning"), left + 10, top + 60, 116, 0);
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

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (previewing) {
            long currentMillis = System.currentTimeMillis();

            long delta = currentMillis - lastMillis;
            lastMillis = currentMillis;
            cumMillis += delta;
            int currentBeat = (int)(cumMillis*bps)/1000;
            int oldPreviewCursor = previewCursor;
            previewCursor = previewCursorStart + currentBeat;

            if(previewCursor > sliderPosition + beatsInScreen - 12 && lengthBeats > sliderPosition + beatsInScreen){
                setSliderPos(previewCursor - 24);
            }

            if(oldPreviewCursor != previewCursor){
                if (previewCursor > lengthBeats || (editCursorEnd != editCursor && previewCursor > editCursorEnd+1)) {
                    stopPreview();
                }
                else{
                    previewStarted = true;
                    playPreviewSound(oldPreviewCursor, previewCursor);
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
            for(int i = sliderPosition; i<sliderPosition + beatsInScreen; i++){
                int x = (i - sliderPosition)*3 + noteImageLeftX + noteRegionLeft + 1;
                if(i % 12 == 0){
                    fill(matrixStack, x, noteImageY + noteRegionTop - 1, x+1, noteImageY + noteRegionBottom + 3, 0xFF998A7B);
                }
            }

            // Draw measure numbers
            matrixStack.pushPose();
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            for(int i = sliderPosition; i<sliderPosition + beatsInScreen; i++) {
                if (i % 12 == 0) {
                    final int x = (i - sliderPosition) * 3 + noteImageLeftX + noteRegionLeft;
                    final int y = noteImageY + noteRegionTop - 5;
                    final String name = ""+((i/12)+1);
                    final int w = font.width(name);
                    font.draw(matrixStack, name, (x - (w-6.0f)/4.0f)*2.f, y*2, 0xFF444400);
                }
            }
            matrixStack.popPose();

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

        if(buttonHideNeighbors.isHovered()){
            renderTooltip(matrixStack, new TranslatableComponent("note.toggleTooltip"), mouseX, mouseY);
        }
        if(buttonLockPrevIns.isHovered()){
            renderTooltip(matrixStack, new TranslatableComponent("note.lockTooltip"), mouseX, mouseY);
        }
        if(buttonPreview.isHovered()){
            renderTooltip(matrixStack, new TranslatableComponent("note.previewTooltip"), mouseX, mouseY);
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
        return (note - ItemInstrument.minNote) / 12;
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

            int y = noteImageY + noteRegionTop + (47 - event.note + ItemInstrument.minNote)*3 + currentOctavePos*36;
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
    }

    private void setSliderPos(int time){
        time = Math.min(Math.max(time, 0), maxSliderPosition);

        sliderTime.setValue((float)(time)/(float)maxSliderPosition);
        sliderTime.updateSlider();
    }

    private void stopPreview() {
        this.previewing = false;
        this.previewStarted = false;
        this.buttonPreview.setTexStarts(224, 0);
    }

    private void updateLength() {
        lengthBeats = 0;
        if(!notes.isEmpty()){
            for(NoteEvent event : notes){
                lengthBeats = (short)(event.time + event.length) > lengthBeats ? (short)(event.time + event.length) : lengthBeats;
            }

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

    @Override
    public boolean mouseClicked(double dmouseX, double dmouseY, int mouseButton) {
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
                int note = 47 - (nry / 3) + ItemInstrument.minNote + currentOctavePos*12;
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
                        playSound(event, previewInstrument);
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
                        break;
                    }
                }
            }
        }
        return true;
    }

    private void addNote(byte note, short time) {
        int i = findNote(note, time);
        if(i < 0){
            addNote(note, time, (byte)64, (byte)1);
        }
        else{
            notes.remove(i);
        }
        updateLength();
    }

    private void addNote(byte note, short time, byte volume, byte length) {
        NoteEvent newEvent = new NoteEvent(note, time, volume, length);
        currentlyAddedNote = newEvent;
        for(int i=0; i<notes.size(); i++){
            if(notes.get(i).time > time){
                notes.add(i, newEvent);
                return;
            }
        }
        notes.add(newEvent);
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
        for(int i=0; i<notes.size(); i++) {
            NoteEvent event = notes.get(i);
            if(event.time > time){
                break;
            }
            if((event.time + event.length > time) && event.note == note) {
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
                if(currentlyAddedNote.time < time && time - currentlyAddedNote.time <= 60){
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
        sliderTime.dragging = false;
        sliderVolume.dragging = false;
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

    private void decodeFromClipboard(){
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

            // Push back the existing future note events
            for(NoteEvent event : notes){
                if(event.time >= editCursor){
                    event.time += length;
                }
            }

            for(NoteEvent event : toBePasted){
                event.time += editCursor;
                notes.add(event);
            }

            sortNotes();
            updateLength();
            editCursor += length + 1;
            editCursorEnd = editCursor;
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        setFocused(null);
        super.keyPressed(keyCode, scanCode, modifiers);

        // Copying when viewing self signed
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
                    case GLFW.GLFW_KEY_BACKSPACE:
                        if (!this.noteTitle.isEmpty()) {
                            this.noteTitle = this.noteTitle.substring(0, this.noteTitle.length() - 1);
                            this.updateButtons();
                        }
                        break;
                    case GLFW.GLFW_KEY_ENTER:
                        if (!this.noteTitle.isEmpty()) {
                            dirtyFlag.hasSigned = true;
                            dirtyFlag.hasTitle = true;
                            this.isSigned = true;
                            this.minecraft.setScreen(null);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            } else {
                int x = editCursor;
                boolean resetEditCursorEnd = true;
                switch (keyCode) {
                    case GLFW.GLFW_KEY_DELETE:
                        if (lengthBeats == 0 || lengthBeats <= x) break;
                        dirtyFlag.hasNotes = true;
                        dirtyFlag.hasLength = true;
                        if(editCursorEnd == x){
                            delAtCursor(x);
                        }
                        else {
                            deleteSelected();
                            updateLength();
                        }
                        break;
                    case GLFW.GLFW_KEY_BACKSPACE:
                        if(editCursorEnd == x){
                            if(x == 0){
                                break;
                            }
                            if (lengthBeats == 0 || lengthBeats < x){
                                addEditCursor(-1);
                                break;
                            }
                            else{
                                dirtyFlag.hasNotes = true;
                                dirtyFlag.hasLength = true;
                                addEditCursor(-1);
                                delAtCursor(editCursor);
                            }
                        }
                        else {
                            dirtyFlag.hasNotes = true;
                            dirtyFlag.hasLength = true;
                            deleteSelected();
                            updateLength();
                        }
                        break;
                    case GLFW.GLFW_KEY_SPACE:
                        putSpace(x-1);
                        break;
                    case GLFW.GLFW_KEY_RIGHT:
                        addEditCursor(1);
                        if (editCursor > maxLengthBeats - 1) setEditCursor(maxLengthBeats - 1);
                        break;
                    case GLFW.GLFW_KEY_LEFT:
                        addEditCursor(-1);
                        if (editCursor < 0) setEditCursor(0);
                        break;
                    case GLFW.GLFW_KEY_ENTER:
                        if (!previewing)
                            startPreview();
                        else
                            stopPreview();
                        break;
                    case GLFW.GLFW_KEY_C:
                        if((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL){
                            encodeToClipboard();
                        }
                        break;
                    case GLFW.GLFW_KEY_V:
                        if((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL){
                            decodeFromClipboard();
                        }
                        break;
                    case GLFW.GLFW_KEY_A:
                        if((modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL){
                            editCursor = 0;
                            editCursorEnd = lengthBeats - 1;
                            resetEditCursorEnd = false;
                        }
                        else{
                            if(editCursor == editCursorEnd){
                                currentOctave --;
                                if(currentOctave < 0){
                                    currentOctave = 0;
                                }
                            }
                            else{
                                for (NoteEvent event : notes) {
                                    if (event.endTime() >= editCursor && event.time <= editCursorEnd) {
                                        if (ItemInstrument.noteToId(event.note) / 12 > 0) {
                                            event.note -= 12;
                                        }
                                    }
                                }
                                resetEditCursorEnd = false;
                            }
                        }
                        break;
                    case GLFW.GLFW_KEY_S:
                        if(editCursor == editCursorEnd){
                            currentOctave ++;
                            if(currentOctave > 7){
                                currentOctave = 7;
                            }
                        } else{
                            for (NoteEvent event : notes) {
                                if (event.endTime() >= editCursor && event.time <= editCursorEnd) {
                                    if (ItemInstrument.noteToId(event.note) / 12 < 7) {
                                        event.note += 12;
                                    }
                                }
                            }
                            resetEditCursorEnd = false;
                        }
                        break;
                    case GLFW.GLFW_KEY_LEFT_CONTROL:
                    case GLFW.GLFW_KEY_RIGHT_CONTROL:
                        resetEditCursorEnd = false;
                        break;
                    default:
                        if (scanCode >= 16 && scanCode <= 27) {
                            putSpace(x-1);
                            addNote((byte) ((scanCode - 15 + ItemInstrument.minNote) + 12 * currentOctave), (short)(x));
                            finishAddingNote();
                            break;
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
            if(dirtyFlag.hasNotes || dirtyFlag.hasLength) {
                version++;
                dirtyFlag.hasVersion = true;
                MusicManagerClient.setMusicData(id, version, notes);
            }

            MusicUpdatePacket pack = new MusicUpdatePacket(dirtyFlag, notes, lengthBeats, bps, volume, isSigned,
                    noteTitle, (byte)previewInstrument, prevInsLocked, id, version);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);

        }
        editingPlayer.playSound(SoundEvents.CLOSE_SCROLL, 1.0f, 0.8f + editingPlayer.level.random.nextFloat()*0.4f);
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
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, TextComponent.EMPTY);
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
            if (this.isHovered()) {
                yTexStartNew += this.yDiffText;
            }
            return yTexStartNew;
        }

        protected void postRender(){
            GlStateManager._enableDepthTest();
        }

        @Override
        public void renderButton(PoseStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
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
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, TextComponent.EMPTY);
        }

        public LockImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick, Component message) {
            super(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, message);
        }

        @Override
        public void renderButton(PoseStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            int yTexStartNew = preRender();

            blit(matrixStack, this.x, this.y, (float)this.xTexStart, (float)yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
            if(prevInsLocked){
                blit(matrixStack, this.x, this.y, 0, this.texHeight - this.height, this.width, this.height, this.texWidth, this.texHeight);
            }

            postRender();
        }
    }

    static public class NoteEditBox extends AbstractWidget {
        public Slider velocitySlider;
        private NoteEvent event;

        public NoteEditBox(int x, int y, int w, int h, Component msg) {
            super(x, y, w, h, msg);
            velocitySlider = new Slider(0, 0, 50, 10, new TextComponent("Vol "), TextComponent.EMPTY, 0, 100, 50, false, true, (o) -> {
            },
            (slider) -> {
                event.volume = (byte)Math.round(velocitySlider.sliderValue * 127.0f);
            }
            );
        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
            if (this.visible && event != null) {
                fill(poseStack, x, y, x + width, y + height, 0xFFEEEEEE);
                Minecraft minecraft = Minecraft.getInstance();
                Font font = minecraft.font;
                int noteId = ItemInstrument.noteToId(event.note);
                int octave = noteId / 12;
                font.draw(poseStack, noteNames[noteId % 12] + (noteId % 12 < 3 ? octave : octave + 1) , x + 10, y + 5, 0xFFD3C200);
                font.draw(poseStack, noteNamesSolfege[noteId % 12], x + 35, y + 5, 0xFFD3C200);
                font.draw(poseStack, ("" + event.length) + (event.length == 1 ? " Beat" : " Beats"), x + 10, y + 15, 0xFF495EE5);
                velocitySlider.render(poseStack, mouseX, mouseY, partialTicks);
            }
        }

        public void appear(int x, int y, NoteEvent event) {
            this.x = x;
            this.y = y;
            this.visible = true;
            this.active = true;
            this.event = event;
            velocitySlider.x = x + 5;
            velocitySlider.y = y + 30;
            velocitySlider.setValue(event.floatVolume() * 100.0f);
            velocitySlider.updateSlider();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (this.active && this.visible) {
                if(mouseButton == 2){
                    this.visible = false;
                    this.active = false;
                }

                if(mouseX >= velocitySlider.x && mouseX < velocitySlider.x + velocitySlider.getWidth() &&
                   mouseY >= velocitySlider.y && mouseY < velocitySlider.y + velocitySlider.getHeight()){
                    velocitySlider.mouseClicked(mouseX, mouseY, mouseButton);
                    return true;
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
            if(posX >= velocitySlider.x && posX < velocitySlider.x + velocitySlider.getWidth() &&
                    posY >= velocitySlider.y && posY < velocitySlider.y + velocitySlider.getHeight()){
                velocitySlider.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            }
            return true;
        }

        @Override
        public boolean mouseReleased(double posX, double posY, int mouseButton) {
            velocitySlider.onRelease(posX, posY);
            return true;
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
        }

        private final String[] noteNames = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
        private final String[] noteNamesSolfege = {"La", "Li", "Si", "Do", "Di", "Re", "Ri", "Mi", "Fa", "Fi", "Sol", "Le"};
    }
}
