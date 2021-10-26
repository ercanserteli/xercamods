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
    private static final ResourceLocation noteGuiTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/music_sheet.png");
    private static final ResourceLocation instrumentTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/instruments.png");
    private final Player editingPlayer;
    public final static int beatsInScreen = 68;
    private final static int noteImageTexX = 0;
    private final static int noteImageTexY = 76 - 30;
    private final static int noteImageWidth = 241;
    private final static int noteImageHeight = 178 + 30;
    private final static int noteRegionLeft = 23;
    private final static int noteRegionTop = 9 + 30;
    private final static int noteRegionRight = 226;
    private final static int noteRegionBottom = 152 + 30;
    private final static int paletteX = 20;
    private final static int paletteY = 31;
    private final static int paletteWidth = 5;
    private final static int bpmButW = 10;
    private final static int bpmButH = 10;
    private final static int bpmButX = 155;
    private final static int bpmButY = 16;
    private final static int[] octaveColors = {0xFF5B3200, 0xFFFF0000, 0xFF0AEE00, 0xFF0059FF, 0xFF7B00FF, 0xFFEF00B7, 0xFF00E2DF, 0XFFF4E800};
    private final static int[] octaveColorsTrans = {0x365B3200, 0x36FF0000, 0x360AEE00, 0x360059FF, 0x367B00FF, 0x36EF00B7, 0x3600E2DF, 0X36F4E800};
    private final static int[] octaveColorsMoreTrans = {0x165B3200,0x16FF0000, 0x160AEE00, 0x160059FF, 0x167B00FF, 0x16EF00B7, 0x1600E2DF, 0X16F4E800};
    private static final int maxLengthBeats = 32000;
    private int noteImageX;
    private int noteImageY;
    private int currentOctave = 2;
    private boolean isSigned;
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
    private short lengthBeats;
    private byte bps;
    private int bpm;
    private boolean dirty = false;
    private int previewInstrument = -1;
    private String authorName = null;
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
        if (offhand instanceof ItemInstrument) {
            ItemInstrument ins = (ItemInstrument) offhand;
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
            this.isSigned = noteTag.getInt("generation") > 0;
            this.noteTitle = noteTag.getString("title");
            this.authorName = noteTag.getString("author");
            this.prevInsLocked = noteTag.getBoolean("piLocked");
            if(noteTag.contains("prevIns")){
                this.previewInstrument = noteTag.getByte("prevIns");
            }

            if(this.authorName.equals(player.getName().getString())){
                this.selfSigned = true;
            }
        } else {
            this.isSigned = false;
            this.id = UUID.randomUUID();
            this.version = 0;
        }

        if(this.notes.isEmpty()){
            this.lengthBeats = 0;
            this.bps = 8;
        }
        this.bpm = bps*60;
        this.tickCount = 0;

        if(!prevInsLocked) {
            int index = getCurrentOffhandInsIndex();
            if (index != previewInstrument) {
                previewInstrument = index;
                dirty = true;
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
                neighborNotes.add(ItemMusicSheet.getNotes(neighbor));
                neighborPrevInstruments.add(ItemMusicSheet.getPrevInstrument(neighbor));
                neighborPreviewNextNoteIDs.add(-1);
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
        noteImageX = (this.width - noteImageWidth) / 2;
        noteImageY = 2;
        if (!this.isSigned) {
            this.bpmUp = this.addRenderableWidget(new Button(noteImageX + bpmButX, noteImageY + bpmButY, bpmButW, bpmButH, new TranslatableComponent("note.upButton"), button -> {
                if (!isSigned) {
                    if(bps < 50){
                        bps ++;
                        bpm = 60*bps;
                        dirty = true;
                        cumMillis *= (float)(bps-1)/(float)bps;
                    }
                }
            }));

            this.bpmDown = this.addRenderableWidget(new Button(noteImageX + bpmButX, noteImageY + bpmButY + 1 + bpmButH, bpmButW, bpmButH, new TranslatableComponent("note.downButton"), button -> {
                if (!isSigned) {
                    if(bps > 1){
                        bps --;
                        bpm = 60*bps;
                        dirty = true;
                        cumMillis *= (float)(bps+1)/(float)bps;
                    }
                }
            }));
            this.buttonSign = this.addRenderableWidget(new Button( noteImageX - 100, 100, 98, 20, new TranslatableComponent("note.signButton"), button -> {
                if (!isSigned) {
                    gettingSigned = true;
                    updateButtons();
                }

            }));
            this.buttonFinalize = this.addRenderableWidget(new Button( noteImageX - 100, 100, 98, 20, new TranslatableComponent("note.finalizeButton"), button -> {
                if (!isSigned) {
                    dirty = true;
                    isSigned = true;
                    minecraft.setScreen(null);
                }

            }));
            this.buttonCancel = this.addRenderableWidget(new Button( noteImageX - 100, 130, 98, 20, new TranslatableComponent("gui.cancel"), button -> {
                if (!isSigned) {
                    gettingSigned = false;
                    updateButtons();
                }

            }));
        }
        this.buttonPreview = this.addRenderableWidget(new ChangeableImageButton(noteImageX + 67, 23, 16, 16, 224, 0, 16, noteGuiTextures, button -> {
            if (!previewing) {
                startPreview();
            } else {
                stopPreview();
            }
        }));

        this.buttonHideNeighbors = this.addRenderableWidget(new ChangeableImageButton(noteImageX + 87, 23, 16, 16, 192, 0, 16, noteGuiTextures, button -> {
            neighborsHidden = !neighborsHidden;
            if (neighborsHidden) {
                this.buttonHideNeighbors.setTexStarts(208, 0);
            } else {
                this.buttonHideNeighbors.setTexStarts(192, 0);
            }
        }));

        this.buttonLockPrevIns = this.addRenderableWidget(new LockImageButton( noteImageX + 107, 23, 16, 16, previewInstrument*16 +16, 0, 16, instrumentTextures, button -> {
            prevInsLocked = !prevInsLocked;
            dirty = true;
            if(!prevInsLocked){
                int index = getCurrentOffhandInsIndex();
                if (index != previewInstrument) {
                    previewInstrument = index;
                    this.buttonLockPrevIns.setTexStarts(previewInstrument*16 +16, 0);
                }
            }
        }));

        this.octaveUp = this.addRenderableWidget(new Button(noteImageX + 8, noteImageY + 30, bpmButW, bpmButH, new TranslatableComponent("note.upButton"), button -> {
                if(currentOctavePos < 4){
                    currentOctavePos ++;
                }
        }));

        this.octaveDown = this.addRenderableWidget(new Button(noteImageX + 8, noteImageY + noteRegionBottom, bpmButW, bpmButH, new TranslatableComponent("note.downButton"), button -> {
                if(currentOctavePos > 0){
                    currentOctavePos --;
                }
        }));

        this.sliderTime = this.addRenderableWidget(new Slider(noteImageX + noteRegionLeft, noteImageY + noteRegionBottom + 4, noteRegionRight-noteRegionLeft, 10,
                TextComponent.EMPTY, TextComponent.EMPTY, 0, 1, 0, false, false, b -> {}, (slider) -> {
            sliderPosition = (int)(slider.sliderValue * (double)maxSliderPosition);
        }));

        this.sliderVolume = this.addRenderableWidget(new Slider(noteImageX + noteRegionRight - 50, noteImageY + 15, 50, 10,
                new TextComponent("Vol "), TextComponent.EMPTY, 0, 100, volume*100.f, false, true, b -> {}, (slider) -> {
            volume = ((float) slider.sliderValue);
            dirty = true;
        }));

        this.noteEditBox = this.addRenderableWidget(new NoteEditBox(0, 0, 60, 45, TextComponent.EMPTY));

        updateButtons();

        updateLength();
    }

    private void updateButtons() {
        if (!this.isSigned) {
            this.bpmDown.visible = this.bpmUp.visible = !this.gettingSigned;
            this.buttonSign.visible = !this.gettingSigned;
            this.buttonCancel.visible = this.gettingSigned;
            this.buttonFinalize.visible = this.gettingSigned;
            this.buttonFinalize.active = !this.noteTitle.trim().isEmpty();
        }
        this.buttonPreview.visible = !this.gettingSigned;
        this.buttonLockPrevIns.visible = true;
        this.buttonLockPrevIns.active = !this.isSigned;
        this.sliderVolume.active = !this.isSigned;
        this.noteEditBox.visible = false;
        this.noteEditBox.active = false;

        this.buttonHideNeighbors.visible = this.neighborNotes.size() > 0;
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
        int i = noteImageX;
        int j = noteImageY;

        fill(matrixStack, i + 30, j + 30, i + 150, j + 150, 0xFFFFFFFF);
        String s = this.noteTitle;

        if (!this.isSigned) {
            if (this.tickCount / 6 % 2 == 0) {
                s = s + "" + ChatFormatting.BLACK + "_";
            } else {
                s = s + "" + ChatFormatting.GRAY + "_";
            }
        }
        String s1 = I18n.get("note.editTitle");
        int k = this.font.width(s1);
        this.font.draw(matrixStack, s1, i + 36 + (116 - k) / 2.0f, j + 16 + 16, 0);
        int l = this.font.width(s);
        this.font.draw(matrixStack, s, i + 36 + (116 - l) / 2.0f, j + 48, 0);
        String s2 = I18n.get("note.byAuthor", this.editingPlayer.getName().getString());
        int i1 = this.font.width(s2);
        this.font.draw(matrixStack, ChatFormatting.DARK_GRAY + s2, i + 36 + (116 - i1) / 2, j + 48 + 10, 0);
        this.font.drawWordWrap(new TranslatableComponent("note.finalizeWarning"), i + 36, j + 80, 116, 0);
    }

    private int noteToPixelX(int noteX) {
        return noteImageX + noteRegionLeft + noteX*3;
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
        RenderSystem.setShaderTexture(0, noteGuiTextures);
        blit(matrixStack, noteImageX, noteImageY, noteImageTexX, noteImageTexY, noteImageWidth, noteImageHeight);
        if (this.gettingSigned) {
            drawSigning(matrixStack);
        } else {
            // Draw octave tints
            int x1 = noteImageX + noteRegionLeft;
            int x2 = noteImageX + noteRegionRight;
            for(int i=0; i<4; i++){
                int y1 = noteImageY + noteRegionTop + (4-i)*12*3;
                int y2 = noteImageY + noteRegionTop + (3-i)*12*3;
                fill(matrixStack, x1, y1, x2, y2, octaveColorsMoreTrans[i + currentOctavePos]);
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
                drawCenteredString(matrixStack, this.font, octaveNames[i + currentOctavePos], x1 - 10, noteImageY + noteRegionBottom - 18 - i*36, octaveColors[i + currentOctavePos]);
            }

            // Draw measure lines
            for(int i = sliderPosition; i<sliderPosition + beatsInScreen; i++){
                int x = (i - sliderPosition)*3 + noteImageX + noteRegionLeft + 1;
                if(i % 12 == 0){
                    fill(matrixStack, x, noteImageY + noteRegionTop - 3, x+1, noteImageY + noteRegionBottom + 3, 0xFF998A7B);
                }
            }

            this.font.draw(matrixStack, "Tempo", noteImageX + bpmButX - 30, noteImageY + bpmButY, 0xFF000000);
            this.font.draw(matrixStack, "" + bpm, noteImageX + bpmButX - 30, noteImageY + bpmButY + 10, 0xFF000000);
            if (!this.isSigned) {
                drawCursor(matrixStack, editCursor, 0xFFAA2222);

                if(editCursor != editCursorEnd){
                    drawCursor(matrixStack, editCursorEnd, 0xFFAA2222);
                    drawSelectionRect(matrixStack);
                }
            } else {
                int k = this.font.width(noteTitle);
                this.font.draw(matrixStack, noteTitle, (noteImageX + (noteImageWidth - k) / 2.0f), noteImageY + 12, 0xFF000000);

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
//        if (!gettingSigned && !isSigned && validClick(mouseX - noteImageX, mouseY - noteImageY)) {
//            fill(matrixStack, mouseX - 1, mouseY - 1, mouseX + 2, mouseY + 2, 0xFF000000);
//        }

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

            int xBegin = noteImageX + noteRegionLeft + timeDrawBeginning*3;
            int xEnd = noteImageX + noteRegionLeft + timeDrawEnd*3;
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

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    public boolean mouseClicked(double dmouseX, double dmouseY, int mouseButton) {
        // mouseButton: 0 = left click, 1 = right click, 2 = middle click

        if(super.mouseClicked(dmouseX, dmouseY, mouseButton)){
            this.setDragging(true);
            dirty = true;
            return true;
        }

        int mouseX = (int)Math.round(dmouseX);
        int mouseY = (int)Math.round(dmouseY);

        boolean viewingSelfSigned = isSigned && selfSigned;
        boolean composing = !isSigned && !gettingSigned;

        if(composing || viewingSelfSigned){
            if (mouseButton == 1) {
                int mx = mouseX - noteImageX;
                int my = mouseY - noteImageY;
                if (validClick(mx, my)) {
                    selectionStart = editCursorEnd = editCursor = ((mx - noteRegionLeft)/3) + sliderPosition;
                }
            }
            if (mouseButton == 0 && viewingSelfSigned){
                editCursorEnd = editCursor;
            }
        }
        if (composing) {
            int mx = mouseX - noteImageX;
            int my = mouseY - noteImageY;
            if (validClick(mx, my)) {
                int nrx = mx - noteRegionLeft;
                int nry = my - noteRegionTop;

                int time = (nrx / 3) + sliderPosition;
                int note = 47 - (nry / 3) + ItemInstrument.minNote + currentOctavePos*12;
                if (mouseButton == 0) {
                    addNote((byte) note, (short) time);
                    dirty = true;

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

        int mx = mouseX - noteImageX;
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
        dirty = true;
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
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.copiedBuffer(byteArray));

            // Read copied time length and note event count
            int length = buffer.readInt();
            int count = buffer.readInt();

            // Read the note events into an array
            ArrayList<NoteEvent> toBePasted = new ArrayList<>();
            for(int i=0; i<count; i++){
                toBePasted.add(NoteEvent.fromBuffer(buffer));
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
                            dirty = true;
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
                        dirty = true;
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
                                dirty = true;
                                addEditCursor(-1);
                                delAtCursor(editCursor);
                            }
                        }
                        else {
                            dirty = true;
                            deleteSelected();
                            updateLength();
                        }
                        break;
                    case GLFW.GLFW_KEY_SPACE:
                        putSpace(x);
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
                            currentOctave --;
                            if(currentOctave < 0){
                                currentOctave = 0;
                            }
                        }
                        break;
                    case GLFW.GLFW_KEY_S:
                        currentOctave ++;
                        if(currentOctave > 7){
                            currentOctave = 7;
                        }
                        break;
                    case GLFW.GLFW_KEY_LEFT_CONTROL:
                    case GLFW.GLFW_KEY_RIGHT_CONTROL:
                        resetEditCursorEnd = false;
                        break;
                    default:
                        if (scanCode >= 16 && scanCode <= 27) {
                            addNote((byte) ((scanCode - 15 + ItemInstrument.minNote) + 12 * currentOctave), (short)x);
                            finishAddingNote();
                            putSpace(x);
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
        if (dirty) {
            version++;

            MusicUpdatePacket pack = new MusicUpdatePacket(notes, lengthBeats, bps, volume, isSigned, noteTitle, (byte)previewInstrument, prevInsLocked, id, version);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);

            MusicManagerClient.setMusicData(id, version, notes);
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
