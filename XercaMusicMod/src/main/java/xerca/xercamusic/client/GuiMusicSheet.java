package xerca.xercamusic.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.MusicUpdatePacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

@OnlyIn(Dist.CLIENT)
public class GuiMusicSheet extends Screen {
    private static final ResourceLocation noteGuiTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/music_sheet.png");
    private static final ResourceLocation instrumentTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/instruments.png");
    private final PlayerEntity editingPlayer;
    public final static int beatsInTab = 48;
    public final static int tabAmount = 5;
    private final static int totalBeats = beatsInTab * tabAmount;
    private final static int noteImageWidth = 180;
    private final static int noteImageHeight = 241;
    private final static int tabHeight = 39;
    private final static int startingX = 20;
    private final static int startingY = 36;
    private final static int endingX = 163;
    private final static int endingY = 230;
    private final static int paletteX = 20;
    private final static int paletteY = 31;
    private final static int paletteWidth = 5;
    private final static int bpmButW = 10;
    private final static int bpmButH = 10;
    private final static int bpmButX = 155;
    private final static int bpmButY = 16;
    private final static int[] octaveColors = {0xFFFF0000, 0xFF0CFF00, 0xFF0059FF, 0xFF7B00FF};
    private final static int[] octaveColorsTrans = {0x36FF0000, 0x360CFF00, 0x360059FF, 0x367B00FF};
    private int noteImageX = (this.width - noteImageWidth) / 2;
    private int noteImageY = 2;
    private int currentOctave = 1;
    private boolean isSigned;
    private boolean gettingSigned;
    private boolean previewing;
    private boolean previewStarted;
    private int previewCursor;
    private int editCursor;
    private int editCursorEnd;
    private int selectionStart; // where the first right click happened when selecting
    private int updateCount;
    private String noteTitle = "";
    private Button bpmUp;
    private Button bpmDown;
    private Button buttonSign;
    private Button buttonCancel;
    private Button buttonFinalize;
    private ChangeableImageButton buttonPreview;
    private ChangeableImageButton buttonHideNeighbors;
    private LockImageButton buttonLockPrevIns;
    private NoteSound lastPlayed = null;
    private boolean neighborsHidden = false;
    private boolean prevInsLocked = false;
    private boolean selfSigned = false;

    private byte[] music;
    private int length;
    private int bpm;
    private byte pause;
    private boolean dirty = false;
    private int previewInstrument = -1;
    private String authorName = null;

    private ArrayList<byte[]> neighborMusics = new ArrayList<>();
    private ArrayList<Integer> neighborPrevInstruments = new ArrayList<>();
    private ArrayList<NoteSound> neighborLastPlayeds = new ArrayList<>();

    private static class Note {
        private int tab;
        private int time;
        private int pitch;

        Note() {
        }

        Note(int time, int pitch) {
            this.time = time;
            this.pitch = pitch;
            this.tab = time / beatsInTab;
        }

        int getNoteX() {
            return time;
        }

        int getNoteY() {
            return pitch;
        }

        void setNoteCoords(int pixelX, int pixelY) {
            int noteX = (pixelX - startingX) / 3;
            tab = (pixelY - startingY) / tabHeight;
            time = noteX + tab * beatsInTab;
            pitch = 12 - (pixelY - startingY - tab * tabHeight) / 3;
            //System.out.println("time: "+time+" pitch: "+pitch+" tab: "+tab);
        }

        int getPixelX(int imageX) {
            return imageX + (time - tab * beatsInTab) * 3 + startingX;
        }

        int getPixelY(int imageY) {
            return imageY + (12 - pitch) * 3 + startingY + tab * tabHeight;
        }
    }

    private int getCurrentOffhandInsIndex(){
        Item offhand = editingPlayer.getHeldItemOffhand().getItem();
        if (offhand instanceof ItemInstrument) {
            ItemInstrument ins = (ItemInstrument) offhand;
            return ArrayUtils.indexOf(Items.instruments, ins);
        }
        return -1;
    }

    GuiMusicSheet(PlayerEntity player, CompoundNBT noteTag, ITextComponent title) {
        super(title);
        this.editingPlayer = player;
        if (noteTag != null && !noteTag.isEmpty()) {
            byte[] nbtMusic = noteTag.getByteArray("music");
            this.length = noteTag.getInt("length");
            this.pause = noteTag.getByte("pause");
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

            this.music =  Arrays.copyOfRange(nbtMusic, 0, totalBeats);
        } else {
            this.isSigned = false;
        }

        if (this.music == null) {
            this.music = new byte[totalBeats];
            this.length = 0;
            this.pause = 5;
        }
        this.bpm = 1200 / pause;
        this.updateCount = 0;

        if(!prevInsLocked) {
            int index = getCurrentOffhandInsIndex();
            if (index != previewInstrument) {
                previewInstrument = index;
                dirty = true;
            }
        }

        // Neighbor sheets
        int currentSlot = player.inventory.currentItem;
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
            int neighbor_pause = ItemMusicSheet.getPause(neighbor);
            if(neighbor_pause == pause){
                neighborMusics.add(ItemMusicSheet.getMusic(neighbor));
                neighborPrevInstruments.add(ItemMusicSheet.getPrevInstrument(neighbor));
                neighborLastPlayeds.add(null);
                return true;
            }
        }
        return false;
    }

    private ItemStack getStackInSlot(int slot){
        if(slot >= 0 && slot < editingPlayer.inventory.getSizeInventory()) {
            return editingPlayer.inventory.getStackInSlot(slot);
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
            this.bpmUp = this.addButton(new Button(noteImageX + bpmButX, noteImageY + bpmButY, bpmButW, bpmButH, I18n.format("note.upButton"), button -> {
                if (!isSigned) {
                    dirty = true;
                    if (pause == 20) {
                        pause -= 5;
                    } else if (pause == 15) {
                        pause -= 3;
                    } else if (pause >= 8) {
                        pause -= 2;
                    } else if (pause >= 2) {
                        pause--;
                    }
                    bpm = 1200 / pause;
                }
            }));

            this.bpmDown = this.addButton(new Button(noteImageX + bpmButX, noteImageY + bpmButY + 1 + bpmButH, bpmButW, bpmButH, I18n.format("note.downButton"), button -> {
                if (!isSigned) {
                    dirty = true;
                    if (pause <= 5) {
                        pause++;
                    } else if (pause <= 10) {
                        pause += 2;
                    } else if (pause == 12) {
                        pause += 3;
                    } else if (pause == 15) {
                        pause += 5;
                    }
                    bpm = 1200 / pause;
                }
            }));
            this.buttonSign = this.addButton(new Button( noteImageX - 100, 100, 98, 20, I18n.format("note.signButton"), button -> {
                if (!isSigned) {
                    //System.out.println("Sign button pressed!");
                    gettingSigned = true;
                    updateButtons();
                }

            }));
            this.buttonFinalize = this.addButton(new Button( noteImageX - 100, 100, 98, 20, I18n.format("note.finalizeButton"), button -> {
                if (!isSigned) {
                    //this.sendBookToServer(true);
                    dirty = true;
                    isSigned = true;
                    minecraft.displayGuiScreen(null);
                }

            }));
            this.buttonCancel = this.addButton(new Button( noteImageX - 100, 130, 98, 20, I18n.format("gui.cancel"), button -> {
                if (!isSigned) {
                    //this.sendBookToServer(true);
                    gettingSigned = false;
                    updateButtons();
                }

            }));
        }
        this.buttonPreview = this.addButton(new ChangeableImageButton(noteImageX + 67, 23, 16, 16, 224, 0, 16, noteGuiTextures, button -> {
            if (!previewing) {
                startPreview();
            } else {
                stopPreview();
            }
        }));

        this.buttonHideNeighbors = this.addButton(new ChangeableImageButton( noteImageX + 87, 23, 16, 16, 224, 32, 16, noteGuiTextures, button -> {
            neighborsHidden = !neighborsHidden;
            if(neighborsHidden){
                this.buttonHideNeighbors.setTexStarts(240,32);
            }else{
                this.buttonHideNeighbors.setTexStarts(224,32);
            }
        }));

        this.buttonLockPrevIns = this.addButton(new LockImageButton( noteImageX + 107, 23, 16, 16, previewInstrument*16 +16, 0, 16, instrumentTextures, button -> {
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
        updateButtons();
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

        this.buttonHideNeighbors.visible = this.neighborMusics.size() > 0;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void tick() {
        super.tick();
        ++this.updateCount;
        if (previewing) {
            if (updateCount % this.pause == 0) {
                previewStarted = true;
                if (previewCursor >= length ||(editCursorEnd != editCursor && previewCursor > editCursorEnd)) {
                    stopPreview();
                    return;
                }
                playPrevSound();
                previewCursor++;
            }
        }
    }

    private void playSound(int note, int previewInstrument){
        playSound(note, previewInstrument, -1);
    }

    private void playSound(int note, int previewInstrument, int neighborId){
        SoundEvent noteSound;
        if(previewInstrument > 0 && previewInstrument < Items.instruments.length){
            ItemInstrument ins = Items.instruments[previewInstrument];
            noteSound = ins.getSound(note);

            if(ins.shouldCutOff){
                if(neighborId >= 0 && neighborId < neighborLastPlayeds.size() && neighborLastPlayeds.get(neighborId) != null){
                    neighborLastPlayeds.get(neighborId).stopSound();
                }
                else if(lastPlayed != null){
                    lastPlayed.stopSound();
                }
            }
        }else{
            noteSound = SoundEvents.harp_mcs[note];
        }
        NoteSound sound = XercaMusic.proxy.playNote(noteSound, editingPlayer.getPosX(), editingPlayer.getPosY(), editingPlayer.getPosZ());

        if(neighborId >= 0 && neighborId < neighborLastPlayeds.size()){
            neighborLastPlayeds.set(neighborId, sound);
        }else{
            lastPlayed = sound;
        }
    }

    private void playPrevSound() {
        if (music[previewCursor] != 0 && music[previewCursor] <= 48) {
            playSound(music[previewCursor] - 1, previewInstrument);
        }

        // Play neighbors too
        if(!neighborsHidden){
            for(int i = 0; i < neighborMusics.size(); i++){
                byte[] m = neighborMusics.get(i);
                int ins = neighborPrevInstruments.get(i);
                if(previewCursor < m.length && m[previewCursor] != 0 && m[previewCursor] <= 48){
                    playSound(m[previewCursor] - 1, ins, i);
                }
            }
        }
    }

    private void drawSigning() {
        int i = noteImageX;
        int j = noteImageY;

        fill(i + 30, j + 30, i + 150, j + 150, 0xFFFFFFFF);
        String s = this.noteTitle;

        if (!this.isSigned) {
            if (this.updateCount / 6 % 2 == 0) {
                s = s + "" + TextFormatting.BLACK + "_";
            } else {
                s = s + "" + TextFormatting.GRAY + "_";
            }
        }
        String s1 = I18n.format("note.editTitle");
        int k = this.font.getStringWidth(s1);
        this.font.drawString(s1, i + 36 + (116 - k) / 2.0f, j + 16 + 16, 0);
        int l = this.font.getStringWidth(s);
        this.font.drawString(s, i + 36 + (116 - l) / 2.0f, j + 48, 0);
        String s2 = I18n.format("note.byAuthor", this.editingPlayer.getName().getString());
        int i1 = this.font.getStringWidth(s2);
        this.font.drawString(TextFormatting.DARK_GRAY + s2, i + 36 + (116 - i1) / 2, j + 48 + 10, 0);
        String s3 = I18n.format("note.finalizeWarning");
        this.font.drawSplitString(s3, i + 36, j + 80, 116, 0);
    }

    private void drawCursor(int cursorX, int color){
        Note note = new Note(cursorX, 11);
        int x = note.getPixelX(noteImageX);
        int y = note.getPixelY(noteImageY);
        fill(x + 1, y, x + 2, y + 36, color);
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(noteGuiTextures);
        blit(noteImageX, noteImageY, 0, 0, noteImageWidth, noteImageHeight);
        if (this.gettingSigned) {
            drawSigning();
        } else {
            this.font.drawString("Tempo", noteImageX + bpmButX - 30, noteImageY + bpmButY, 0xFF000000);
            this.font.drawString("" + bpm, noteImageX + bpmButX - 30, noteImageY + bpmButY + 10, 0xFF000000);
            if (!this.isSigned) {
                this.font.drawString("Octave", noteImageX + paletteX, noteImageY + paletteY - 8, 0xFF000000);
                int j = currentOctave;
                for (int i = 0; i < 4; i++) {
                    if (i == j) continue;
                    fill(noteImageX + paletteX + (i * paletteWidth), noteImageY + paletteY, noteImageX + paletteX + (i * paletteWidth) + paletteWidth, noteImageY + paletteY + paletteWidth, octaveColors[i]);
                }
                fill(noteImageX + paletteX + (j * paletteWidth) - 1, noteImageY + paletteY - 1, noteImageX + paletteX + (j * paletteWidth) + paletteWidth + 1, noteImageY + paletteY + paletteWidth + 1, 0xFFFFFF00);
                fill(noteImageX + paletteX + (j * paletteWidth), noteImageY + paletteY, noteImageX + paletteX + (j * paletteWidth) + paletteWidth, noteImageY + paletteY + paletteWidth, octaveColors[j]);

                drawCursor(editCursor, 0xFFAA2222);

                if(editCursor != editCursorEnd){
                    drawCursor(editCursorEnd, 0xFFAA2222);

                    // Render selection rectangle
                    int selectionColor = 0x882222AA;
                    Note beginNote = new Note(editCursor, 11);
                    Note endNote = new Note(editCursorEnd, 11);
                    int x1 = beginNote.getPixelX(noteImageX);
                    int y1 = beginNote.getPixelY(noteImageY);
                    int x2 = endNote.getPixelX(noteImageX);
                    int y2 = endNote.getPixelY(noteImageY);

                    if(y1 == y2){
                        fill(x1 + 1, y1, x2 + 2, y1 + 36, selectionColor);
                    }
                    else{
                        int x_end = new Note(beatsInTab-1, 11).getPixelX(noteImageX);
                        int x_begin = new Note(0, 11).getPixelX(noteImageX);
                        fill(x1 + 1, y1, x_end + 2, y1 + 36, selectionColor);
                        fill(x_begin + 1, y2, x2 + 2, y2 + 36, selectionColor);

                        for(int y = y1 + tabHeight; y <= y2 - tabHeight; y += tabHeight){
                            fill(x_begin + 1, y, x_end + 2, y + 36, selectionColor);
                        }
                    }
                }
            } else {
                int k = this.font.getStringWidth(noteTitle);
                this.font.drawString(noteTitle, (noteImageX + (noteImageWidth - k) / 2.0f), noteImageY + 12, 0xFF000000);

                if(this.selfSigned){
                    drawCursor(editCursor, 0xFFAA2222);

                    if(editCursor != editCursorEnd){
                        drawCursor(editCursorEnd, 0xFFAA2222);

                        // Render selection rectangle
                        int selectionColor = 0x882222AA;
                        Note beginNote = new Note(editCursor, 11);
                        Note endNote = new Note(editCursorEnd, 11);
                        int x1 = beginNote.getPixelX(noteImageX);
                        int y1 = beginNote.getPixelY(noteImageY);
                        int x2 = endNote.getPixelX(noteImageX);
                        int y2 = endNote.getPixelY(noteImageY);

                        if(y1 == y2){
                            fill(x1 + 1, y1, x2 + 2, y1 + 36, selectionColor);
                        }
                        else{
                            int x_end = new Note(beatsInTab-1, 11).getPixelX(noteImageX);
                            int x_begin = new Note(0, 11).getPixelX(noteImageX);
                            fill(x1 + 1, y1, x_end + 2, y1 + 36, selectionColor);
                            fill(x_begin + 1, y2, x2 + 2, y2 + 36, selectionColor);

                            for(int y = y1 + tabHeight; y <= y2 - tabHeight; y += tabHeight){
                                fill(x_begin + 1, y, x_end + 2, y + 36, selectionColor);
                            }
                        }
                    }
                }
            }

            // Neighbor notes
            if(!neighborsHidden) {
                for (byte[] m : neighborMusics) {
                    for (int i = 0; i < m.length; i++) {
                        drawNote(i, m, octaveColorsTrans);
                    }
                }
            }

            // The notes
            for (int i = 0; i < length; i++) {
                drawNote(i, music, octaveColors);
            }
        }
        if (previewStarted) {
            int i = previewCursor - 1;

            Note note = new Note(i, 11);
            int x = note.getPixelX(noteImageX);
            int y = note.getPixelY(noteImageY);
            fill(x + 1, y, x + 2, y + 36, 0xFFAA8822);
            if (music[i] != 0) {
                note = new Note(i, (music[i] - 1) % 12);
                x = note.getPixelX(noteImageX);
                y = note.getPixelY(noteImageY);
                fill(x, y, x + 3, y + 3, 0xFF000000);
            }
            //	}
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.render(mouseX, mouseY, f);
        if (!gettingSigned && !isSigned && validClick(mouseX - noteImageX, mouseY - noteImageY)) {
            fill(mouseX - 1, mouseY - 1, mouseX + 2, mouseY + 2, octaveColors[currentOctave]);
        }

        if(buttonHideNeighbors.isHovered()){
            renderTooltip("Toggle neighbor sheets", mouseX, mouseY);
        }
        if(buttonLockPrevIns.isHovered()){
            renderTooltip("Lock preview instrument", mouseX, mouseY);
        }
        if(buttonPreview.isHovered()){
            renderTooltip("Preview music", mouseX, mouseY);
        }
    }

    private void drawNote(int i, byte[] music, int[] octaveColors) {
        if (music[i] != 0) {
            int value = music[i];
            int height = (value - 1) % 12;
            Note note = new Note(i, height);
            int x = note.getPixelX(noteImageX);
            int y = note.getPixelY(noteImageY);
            fill(x, y, x + 3, y + 3, octaveColors[(value - 1) / 12]);
        }
    }

    private void startPreview() {
        this.previewStarted = false;
        this.previewing = true;
        this.previewCursor = editCursor;
        this.updateCount = 0;
        this.buttonPreview.setTexStarts(240, 0);
    }

    private void stopPreview() {
        this.previewing = false;
        this.previewStarted = false;
        this.buttonPreview.setTexStarts(224, 0);
    }

    private void updateLength() {
        for (int i = length - 1; i >= 0; i--) {
            if (this.music[i] != 0) {
                length = i + 1;
                return;
            }
        }
        length = 0;
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    public boolean mouseClicked(double dmouseX, double dmouseY, int mouseButton) {
        //System.out.println("Mouse clicked: "+mouseX+", "+mouseY+" with "+mouseButton);
        int mouseX = (int)Math.round(dmouseX);
        int mouseY = (int)Math.round(dmouseY);

        boolean viewingSelfSigned = isSigned && selfSigned;
        boolean composing = !isSigned && !gettingSigned;

        if(composing || viewingSelfSigned){
            if (mouseButton == 1) {
                int mx = mouseX - noteImageX;
                int my = mouseY - noteImageY;
                if (validClick(mx, my)) {
                    Note note = new Note();
                    note.setNoteCoords(mx, my);
                    selectionStart = editCursorEnd = editCursor = note.getNoteX();
                }
            }
            if (mouseButton == 0 && viewingSelfSigned){
                editCursorEnd = editCursor;
            }
        }
        if (composing) {
            ///1 = right click, 0 = left click
            if (mouseButton == 0) {
                int mx = mouseX - noteImageX;
                int my = mouseY - noteImageY;
                if (validClick(mx, my)) {
                    //System.out.println("length: "+length);
                    Note note = new Note();
                    note.setNoteCoords(mx, my);
                    int x = note.getNoteX();
                    int y = note.getNoteY();
                    if (y == 12) return false;
                    byte value = (byte) (y + 1 + (currentOctave * 12));
                    if (value == this.music[x]) {
                        changeNote(x, (byte) 0);
                    } else {
                        changeNote(x, value);
                    }
                    //dirty=true;

                    editCursorEnd = editCursor;
                }
                if (mouseX >= paletteX + noteImageX && mouseX < paletteX + noteImageX + (4 * paletteWidth) && mouseY >= paletteY + noteImageY && mouseY < paletteY + noteImageY + paletteWidth) {
                    currentOctave = (mouseX - (noteImageX + paletteX)) / paletteWidth;
                    //System.out.println("Current octave set: "+currentOctave);
                    editCursorEnd = editCursor;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        // if right button is pressed
        if(mouseButton == 1){
            // This check is for preventing the annoying selection at start problem
            if(this.updateCount > 10) {
                int mouseX = (int) Math.round(posX);
                int mouseY = (int) Math.round(posY);

                int mx = mouseX - noteImageX;
                int my = mouseY - noteImageY;
                if (validClick(mx, my)) {
                    Note note = new Note();
                    note.setNoteCoords(mx, my);
                    int noteX = note.getNoteX();
                    if (selectionStart > noteX) {
                        editCursor = noteX;
                    } else {
                        editCursorEnd = noteX;
                    }
                }
            }
        }

        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    private void changeNote(int x, byte note) {
        changeNote(x, note, true);
    }

    private void changeNote(int x, byte note, boolean play) {
        if (note >= 0 && note <= 48) {
            music[x] = note;
            if (note != 0) {
                if(play){
                    playSound(note - 1, previewInstrument);
//                    editingPlayer.world.playSound(editingPlayer, editingPlayer.getPosition(), SoundEvents.harp_mcs[note - 1], SoundCategory.PLAYERS, 3.0f, 1.0f);
                }
                if (x > length - 1) {
                    length = x + 1;
                }
            } else if (x == length - 1) {
                updateLength();
            }
            dirty = true;
        }
    }

    private void putSpace(int x) {
        if (x == totalBeats - 1) return;
        addEditCursor(1);
        if (length == 0 || length <= x) {
            return;
        }
        dirty = true;
        boolean full = false;
        int l = length;
        if (l == totalBeats) {
            full = true;
            l--;
        } else {
            length++;
        }
        for (int i = l - 1; i >= x; i--) {
            music[i + 1] = music[i];
        }
        music[x] = 0;
        if (full) {
            updateLength();
        }
    }

    private void encodeToClipboard(){
        String encodeBytes = Base64.getEncoder().encodeToString(Arrays.copyOfRange(music, editCursor, editCursorEnd + 1));
        GLFW.glfwSetClipboardString(Minecraft.getInstance().getMainWindow().getHandle(), encodeBytes);

        editCursorEnd = editCursor;
    }

    private void decodeFromClipboard(){
        String encodedMusic = GLFW.glfwGetClipboardString(Minecraft.getInstance().getMainWindow().getHandle());
        if(encodedMusic != null && !encodedMusic.isEmpty()){
            byte[] musicPiece;
            try { // Try because this can fail with weird clipboard content
                musicPiece = Base64.getDecoder().decode(encodedMusic);
            }
            catch (IllegalArgumentException ex){
                return;
            }

            // Check if all values are valid
            for (byte b : musicPiece) {
                if (b < 0 || b > 48) {
                    XercaMusic.LOGGER.info("User tried to copy invalid data into music: " + b);
                    return;
                }
            }

            // Copy values
            for (byte b : musicPiece) {
                putSpace(editCursor);
                changeNote(editCursor - 1, b, false);
            }
        }
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
                editCursorEnd = length - 1;
            }
        }

        if (!this.isSigned) {
//            System.out.println("keyPressed key code: " + keyCode);
//            System.out.println("keyPressed scanCode: " + scanCode);
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
                            this.minecraft.displayGuiScreen(null);
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
                        if (length == 0 || length <= x) break;
                        dirty = true;
                        if(editCursorEnd == x){
                            for (int i = x; i < length - 1; i++) {
                                music[i] = music[i + 1];
                            }
                            music[length - 1] = 0;
                            length--;
                        }else{
                            deleteSelected();
                        }
                        break;
                    case GLFW.GLFW_KEY_BACKSPACE:
                        if(editCursorEnd == x) {
                            if (x == 0) break;
                            addEditCursor(-1);
                            if (length == 0 || length < x) break;
                            dirty = true;
                            // manual array copy required, don't change
                            for (int i = x - 1; i < length - 1; i++) {
                                music[i] = music[i + 1];
                            }
                            music[length - 1] = 0;
                            length--;
                        }else{
                            if (length == 0 || length < x) break;
                            dirty = true;
                            deleteSelected();
                        }
                        break;
                    case GLFW.GLFW_KEY_SPACE:
                        putSpace(x);
                        break;
                    case GLFW.GLFW_KEY_RIGHT:
                        addEditCursor(1);
                        if (editCursor >= totalBeats) setEditCursor(0);
                        break;
                    case GLFW.GLFW_KEY_LEFT:
                        addEditCursor(-1);
                        if (editCursor < 0) setEditCursor(totalBeats - 1);
                        break;
                    case GLFW.GLFW_KEY_DOWN:
                        addEditCursor(beatsInTab);
                        if (editCursor >= totalBeats) addEditCursor(-totalBeats);
                        break;
                    case GLFW.GLFW_KEY_UP:
                        addEditCursor(-beatsInTab);
                        if (editCursor < 0) addEditCursor(totalBeats);
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
                            editCursorEnd = length - 1;
                            resetEditCursorEnd = false;
                        }
                        break;
                    case GLFW.GLFW_KEY_LEFT_CONTROL:
                    case GLFW.GLFW_KEY_RIGHT_CONTROL:
                        resetEditCursorEnd = false;
                        break;
                    default:
                        if (scanCode >= 16 && scanCode <= 27) {
                            putSpace(x);
                            changeNote(x, (byte) (scanCode - 15 + 12 * currentOctave));
                            break;
                        }
                        if (scanCode >= 30 && scanCode <= 33) {
                            currentOctave = scanCode - 30;
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
        int diff = editCursorEnd - editCursor + 1;
        int deleted = 0;
        for(int i = editCursor; i < length; i++){
            if(i + diff < length){
                music[i] = music[i + diff];
            }
            else{
                music[i] = 0;
                deleted++;
            }
        }
        length -= deleted;
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
                if (this.noteTitle.length() < 16 && SharedConstants.isAllowedCharacter(typedChar)) {
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
            //System.out.println("wheel: "+wheelState);
            currentOctave += scroll > 0 ? 1 : -1;
            if (currentOctave > 3) currentOctave = 0;
            else if (currentOctave < 0) currentOctave = 3;
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
            System.out.println("GUINote Sending update packet");
            MusicUpdatePacket pack = new MusicUpdatePacket(music, length, pause, isSigned, noteTitle, (byte)previewInstrument, prevInsLocked);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);
        }
    }

    private boolean validClick(int x, int y) {
        return x <= endingX && x >= startingX && y <= endingY && y >= startingY;
    }


    public class ChangeableImageButton extends Button {
        protected final ResourceLocation resourceLocation;
        protected int xTexStart;
        protected int yTexStart;
        protected final int yDiffText;
        protected final int texWidth;
        protected final int texHeight;

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, IPressable onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, 256, 256, onClick);
        }

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, IPressable onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, "");
        }

        public ChangeableImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, IPressable onClick, String message) {
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
            Minecraft lvt_4_1_ = Minecraft.getInstance();
            lvt_4_1_.getTextureManager().bindTexture(this.resourceLocation);
            GlStateManager.disableDepthTest();
            int yTexStartNew = this.yTexStart;
            if (this.isHovered()) {
                yTexStartNew += this.yDiffText;
            }
            return yTexStartNew;
        }

        protected void postRender(){
            GlStateManager.enableDepthTest();
        }

        @Override
        public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            int yTexStartNew = preRender();
            blit(this.x, this.y, (float)this.xTexStart, (float)yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
            postRender();
        }
    }

    public class LockImageButton extends ChangeableImageButton {

        public LockImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, IPressable onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, 256, 256, onClick);
        }

        public LockImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, IPressable onClick) {
            this(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, "");
        }

        public LockImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, IPressable onClick, String message) {
            super(x, y, width, height, xTexStart, yTexStart, yDiffText, texture, texWidth, texHeight, onClick, message);
        }

        @Override
        public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            int yTexStartNew = preRender();

            blit(this.x, this.y, (float)this.xTexStart, (float)yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
            if(prevInsLocked){
                blit(this.x, this.y, 0, this.texHeight - this.height, this.width, this.height, this.texWidth, this.texHeight);
            }

            postRender();
        }
    }

}
