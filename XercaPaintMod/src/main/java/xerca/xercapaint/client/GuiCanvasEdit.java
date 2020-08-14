package xerca.xercapaint.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.packets.CanvasUpdatePacket;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static org.lwjgl.glfw.GLFW.*;

@OnlyIn(Dist.CLIENT)
public class GuiCanvasEdit extends BasePalette {
    private static final ResourceLocation noteGuiTextures = new ResourceLocation(XercaPaint.MODID, "textures/gui/palette.png");
    private int canvasX; // = 240;
    private int canvasY = 40;
    private int canvasWidth;
    private int canvasHeight;
    private int brushMeterX = 420;
    private int brushMeterY = 120;
    private int canvasPixelScale;
    private int canvasPixelWidth;
    private int canvasPixelHeight;
    private int brushSize = 0;
    private boolean touchedCanvas = false;
    private boolean undoStarted = false;
    private boolean gettingSigned;
    private Button buttonSign;
    private Button buttonCancel;
    private Button buttonFinalize;
    private int updateCount;

    private final PlayerEntity editingPlayer;

    private CanvasType canvasType;
    private boolean isSigned = false;
    private int[] pixels;
    private String authorName = "";
    private String canvasTitle = "";
    private String name = "";
    private int version = 0;

    private static final Vec2f[] outlinePoss1 = {
            new Vec2f(0.f, 199.0f),
            new Vec2f(12.f, 199.0f),
            new Vec2f(34.f, 199.0f),
            new Vec2f(76.f, 199.0f),
    };

    private static final Vec2f[] outlinePoss2 = {
            new Vec2f(128.f, 199.0f),
            new Vec2f(135.f, 199.0f),
            new Vec2f(147.f, 199.0f),
            new Vec2f(169.f, 199.0f),
    };

    private static final int maxUndoLength = 16;
    private Deque<int[]> undoStack = new ArrayDeque<>(maxUndoLength);

    protected GuiCanvasEdit(PlayerEntity player, CompoundNBT canvasTag, CompoundNBT paletteTag, ITextComponent title, CanvasType canvasType) {
        super(title, paletteTag);
        updateCount = 0;

        this.canvasType = canvasType;
        this.canvasPixelScale = canvasType == CanvasType.SMALL ? 10 : 5;
        this.canvasPixelWidth = CanvasType.getWidth(canvasType);
        this.canvasPixelHeight = CanvasType.getHeight(canvasType);
        int canvasPixelArea = canvasPixelHeight*canvasPixelWidth;
        this.canvasWidth = this.canvasPixelWidth * this.canvasPixelScale;
        this.canvasHeight = this.canvasPixelHeight * this.canvasPixelScale;
        if(canvasType.equals(CanvasType.LONG)){
            this.canvasY += 40;
        }

        this.editingPlayer = player;
        if (canvasTag != null && !canvasTag.isEmpty()) {
            int[] nbtPixels = canvasTag.getIntArray("pixels");
            this.authorName = canvasTag.getString("author");
            this.canvasTitle = canvasTag.getString("title");
            this.name = canvasTag.getString("name");
            this.version = canvasTag.getInt("v");

            this.pixels =  Arrays.copyOfRange(nbtPixels, 0, canvasPixelArea);
        } else {
            this.isSigned = false;
        }

        if (this.pixels == null) {
            this.pixels = new int[canvasPixelArea];
            Arrays.fill(this.pixels, basicColors[15].rgbVal());

            long secs = System.currentTimeMillis()/1000;
            this.name = "" + player.getUniqueID().toString() + "_" + secs;
        }
    }

    private int getPixelAt(int x, int y){
        return this.pixels[y*canvasPixelWidth + x];
    }

    private void setPixelAt(int x, int y, PaletteUtil.Color color){
        if(x >= 0 && y >= 0 && x < canvasPixelWidth && y < canvasPixelHeight){
            this.pixels[y*canvasPixelWidth + x] = color.rgbVal();
        }
    }

    private void setPixelsAt(int mouseX, int mouseY, PaletteUtil.Color color, int brushSize){
        int x, y;
        final int pixelHalf = canvasPixelScale/2;
        switch (brushSize){
            case 0:
                x = (mouseX - canvasX)/ canvasPixelScale;
                y = (mouseY - canvasY)/ canvasPixelScale;

                setPixelAt(x, y, color);
                break;
            case 1:
                x = (mouseX - canvasX + pixelHalf)/ canvasPixelScale;
                y = (mouseY - canvasY + pixelHalf)/ canvasPixelScale;

                setPixelAt(x, y, color);
                setPixelAt(x-1, y, color);
                setPixelAt(x, y-1, color);
                setPixelAt(x-1, y-1, color);
                break;
            case 2:
                x = (mouseX - canvasX + pixelHalf)/ canvasPixelScale;
                y = (mouseY - canvasY + pixelHalf)/ canvasPixelScale;

                setPixelAt(x-1, y+1, color);
                setPixelAt(x, y+1, color);

                setPixelAt(x-2, y, color);
                setPixelAt(x-1, y, color);
                setPixelAt(x, y, color);
                setPixelAt(x+1, y, color);

                setPixelAt(x-2, y-1, color);
                setPixelAt(x-1, y-1, color);
                setPixelAt(x, y-1, color);
                setPixelAt(x+1, y-1, color);

                setPixelAt(x-1, y-2, color);
                setPixelAt(x, y-2, color);
                break;
            case 3:
                x = (mouseX - canvasX)/ canvasPixelScale;
                y = (mouseY - canvasY)/ canvasPixelScale;

                setPixelAt(x-1, y+2, color);
                setPixelAt(x+0, y+2, color);
                setPixelAt(x+1, y+2, color);

                setPixelAt(x-2, y+1, color);
                setPixelAt(x-1, y+1, color);
                setPixelAt(x+0, y+1, color);
                setPixelAt(x+1, y+1, color);
                setPixelAt(x+2, y+1, color);

                setPixelAt(x-2, y, color);
                setPixelAt(x-1, y, color);
                setPixelAt(x+0, y, color);
                setPixelAt(x+1, y, color);
                setPixelAt(x+2, y, color);

                setPixelAt(x-2, y-1, color);
                setPixelAt(x-1, y-1, color);
                setPixelAt(x+0, y-1, color);
                setPixelAt(x+1, y-1, color);
                setPixelAt(x+2, y-1, color);

                setPixelAt(x-1, y-2, color);
                setPixelAt(x+0, y-2, color);
                setPixelAt(x+1, y-2, color);
                break;
        }
    }

    @Override
    public void init() {
        final int padding = 40;
        final int paletteCanvasX = (this.width - (paletteWidth + canvasWidth + padding)) / 2;
        canvasX = paletteCanvasX + paletteWidth + padding;

        paletteX = paletteCanvasX;
        paletteY = 40;

        brushMeterX = canvasX + canvasWidth + 2;

        // Hide mouse cursor
        GLFW.glfwSetInputMode(this.getMinecraft().getMainWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

        int x = canvasX;
        int y = canvasY + canvasHeight + 10;
        this.buttonSign = this.addButton(new Button( x, y, 98, 20, I18n.format("canvas.signButton"), button -> {
            if (!isSigned) {
                gettingSigned = true;
                updateButtons();

                GLFW.glfwSetInputMode(this.getMinecraft().getMainWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            }
        }));
        this.buttonFinalize = this.addButton(new Button( canvasX - 100, 100, 98, 20, I18n.format("canvas.finalizeButton"), button -> {
            if (!isSigned) {
                dirty = true;
                isSigned = true;
                if(minecraft != null){
                    minecraft.displayGuiScreen(null);
                }
            }

        }));
        this.buttonCancel = this.addButton(new Button( canvasX - 100, 130, 98, 20, I18n.format("gui.cancel"), button -> {
            if (!isSigned) {
                gettingSigned = false;
                updateButtons();

                GLFW.glfwSetInputMode(this.getMinecraft().getMainWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            }
        }));

        updateButtons();
    }

    private void updateButtons() {
        if (!this.isSigned) {
            this.buttonSign.visible = !this.gettingSigned;
            this.buttonCancel.visible = this.gettingSigned;
            this.buttonFinalize.visible = this.gettingSigned;
            this.buttonFinalize.active = !this.canvasTitle.trim().isEmpty();
        }
    }

    @Override
    public void tick() {
        ++this.updateCount;
        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        if(!gettingSigned) {
            super.render(mouseX, mouseY, f);
        }
        else {
            super.superRender(mouseX, mouseY, f);
        }

        // Draw the canvas
        for(int i=0; i<canvasPixelHeight; i++){
            for(int j=0; j<canvasPixelWidth; j++){
                int y = canvasY + i* canvasPixelScale;
                int x = canvasX + j* canvasPixelScale;
                fill(x, y, x + canvasPixelScale, y + canvasPixelScale, getPixelAt(j, i));
            }
        }

        // Draw brush meter
        if(!gettingSigned){
            for(int i=0; i<4; i++){
                int y = brushMeterY + i*brushSpriteSize;
                fill(brushMeterX, y, brushMeterX + 3, y + 3, currentColor.rgbVal());
            }
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blit(brushMeterX, brushMeterY + (3 - brushSize)*brushSpriteSize, 15, 246, 10, 10);
            blit(brushMeterX, brushMeterY, brushSpriteX, brushSpriteY - brushSpriteSize*3, brushSpriteSize, brushSpriteSize*4);

            // Draw brush and outline
            renderCursor(mouseX, mouseY);
        }
        else{
            drawSigning();
        }
    }

    private void renderCursor(int mouseX, int mouseY){
        if(isCarryingColor){
            carriedColor.setGLColor();
            blit(mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);

        }else if(isCarryingWater){
            waterColor.setGLColor();
            blit(mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);
        }else{
            if(inCanvas(mouseX, mouseY)){
                // Render drawing outline
                int x = 0;
                int y = 0;
                int outlineSize = 0;
                int pixelHalf = canvasPixelScale/2;
                if(brushSize == 0){
                    x = ((mouseX - canvasX)/ canvasPixelScale)*canvasPixelScale + canvasX - 1;
                    y = ((mouseY - canvasY)/ canvasPixelScale)*canvasPixelScale + canvasY - 1;
                    outlineSize = canvasPixelScale + 2;
                }
                if(brushSize == 1){
                    x = (((mouseX - canvasX + pixelHalf) / canvasPixelScale) - 1)*canvasPixelScale + canvasX - 1;
                    y = (((mouseY - canvasY + pixelHalf) / canvasPixelScale) - 1)*canvasPixelScale + canvasY - 1;
                    outlineSize = canvasPixelScale*2 + 2;
                }
                if(brushSize == 2){
                    x = (((mouseX - canvasX + pixelHalf) / canvasPixelScale) - 2)*canvasPixelScale + canvasX - 1;
                    y = (((mouseY - canvasY + pixelHalf) / canvasPixelScale) - 2)*canvasPixelScale + canvasY - 1;
                    outlineSize = canvasPixelScale*4 + 2;
                }
                if(brushSize == 3){
                    x = (((mouseX - canvasX)/ canvasPixelScale) - 2)*canvasPixelScale + canvasX - 1;
                    y = (((mouseY - canvasY)/ canvasPixelScale) - 2)*canvasPixelScale + canvasY - 1;
                    outlineSize = canvasPixelScale*5 + 2;
                }

                Vec2f textureVec;
                if(canvasPixelScale == 10){
                    textureVec = outlinePoss1[brushSize];
                }
                else{
                    textureVec = outlinePoss2[brushSize];
                }

                GlStateManager.color4f(0.3F, 0.3F, 0.3F, 1.0F);
                blit(x, y, (int)textureVec.x, (int)textureVec.y, outlineSize, outlineSize);

            }

            fill(mouseX, mouseY, mouseX + 3, mouseY + 3, currentColor.rgbVal());

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int trueBrushY = brushSpriteY - brushSpriteSize*brushSize;
            blit(mouseX, mouseY, brushSpriteX, trueBrushY, brushSpriteSize, brushSpriteSize);
        }
    }

    private void drawSigning() {
        int i = canvasX;
        int j = canvasY;

        fill(i + 10, j + 10, i + 150, j + 150, 0xFFEEEEEE);
        String s = this.canvasTitle;

        if (!this.isSigned) {
            if (this.updateCount / 6 % 2 == 0) {
                s = s + "" + TextFormatting.BLACK + "_";
            } else {
                s = s + "" + TextFormatting.GRAY + "_";
            }
        }
        String s1 = I18n.format("canvas.editTitle");
        int k = this.font.getStringWidth(s1);
        this.font.drawString(s1, i + 26 + (116 - k) / 2.0f, j + 16 + 16, 0);
        int l = this.font.getStringWidth(s);
        this.font.drawString(s, i + 26 + (116 - l) / 2.0f, j + 48, 0);
        String s2 = I18n.format("canvas.byAuthor", this.editingPlayer.getName().getString());
        int i1 = this.font.getStringWidth(s2);
        this.font.drawString(TextFormatting.DARK_GRAY + s2, i + 26 + (116 - i1) / 2, j + 48 + 10, 0);
        String s3 = I18n.format("canvas.finalizeWarning");
        this.font.drawSplitString(s3, i + 26, j + 80, 116, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if (this.gettingSigned) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE:
                    if (!this.canvasTitle.isEmpty()) {
                        this.canvasTitle = this.canvasTitle.substring(0, this.canvasTitle.length() - 1);
                        this.updateButtons();
                    }
                    break;
                case GLFW.GLFW_KEY_ENTER:
                    if (!this.canvasTitle.isEmpty()) {
                        dirty = true;
                        this.isSigned = true;
                        this.minecraft.displayGuiScreen(null);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        else {
            if (keyCode == GLFW.GLFW_KEY_Z && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                if (undoStack.size() > 0) {
                    pixels = undoStack.pop();
                }
                return true;
            } else {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    @Override
    public boolean charTyped(char typedChar, int something) {
        super.charTyped(typedChar, something);

        if (!this.isSigned) {
            if (this.gettingSigned) {
                if (this.canvasTitle.length() < 16 && SharedConstants.isAllowedCharacter(typedChar)) {
                    this.canvasTitle = this.canvasTitle + typedChar;
                    this.updateButtons();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scroll) {
        if (!gettingSigned && scroll != 0.d) {
            //System.out.println("wheel: "+wheelState);
            final int maxBrushSize = 3;
            brushSize += scroll > 0 ? 1 : -1;
            if (brushSize > maxBrushSize) brushSize = 0;
            else if (brushSize < 0) brushSize = maxBrushSize;
            return true;
        }
        return super.mouseScrolled(x, y, scroll);
    }

    // Mouse button 0: left, 1: right
    @Override
    public boolean mouseClicked(double posX, double posY, int mouseButton) {
        if(gettingSigned){
            return super.superMouseClicked(posX, posY, mouseButton);
        }

        int mouseX = (int)Math.floor(posX);
        int mouseY = (int)Math.floor(posY);

        undoStarted = true;
        touchedCanvas = false;
        if(undoStack.size() >= maxUndoLength){
            undoStack.removeLast();
        }
        undoStack.push(pixels.clone());

        if(inCanvas(mouseX, mouseY)){
            clickedCanvas(mouseX, mouseY, mouseButton);
        }

        if(inBrushMeter(mouseX, mouseY)){
            int selectedSize = 3 - (mouseY - brushMeterY)/brushSpriteSize;
            if(selectedSize >= 0){
                brushSize = selectedSize;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void clickedCanvas(int mouseX, int mouseY, int mouseButton){
        touchedCanvas = true;
        if(mouseButton == GLFW_MOUSE_BUTTON_LEFT){
            setPixelsAt(mouseX, mouseY, currentColor, brushSize);
        }else if(mouseButton == GLFW_MOUSE_BUTTON_RIGHT){
            // "Erase" with right click
            setPixelsAt(mouseX, mouseY, PaletteUtil.Color.WHITE, brushSize);
        }
        dirty = true;
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        if(gettingSigned){
            return super.superMouseReleased(posX, posY, mouseButton);
        }

        if(undoStarted && !touchedCanvas){
            undoStarted = false;
            undoStack.removeFirst();
        }
        return super.mouseReleased(posX, posY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        if(gettingSigned){
            return super.superMouseDragged(posX, posY, mouseButton, deltaX, deltaY);
        }

        int mouseX = (int)Math.floor(posX);
        int mouseY = (int)Math.floor(posY);
        if(inCanvas(mouseX, mouseY)){
            clickedCanvas(mouseX, mouseY, mouseButton);
        }
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    private boolean inCanvas(int x, int y) {
        return x < canvasX + canvasWidth && x >= canvasX && y < canvasY + canvasHeight && y >= canvasY;
    }

    private boolean inBrushMeter(int x, int y) {
        return x < brushMeterX + brushSpriteSize && x >= brushMeterX && y < brushMeterY + brushSpriteSize*4 && y >= brushMeterY;
    }

    @Override
    public void removed() {
        if (dirty) {
            version ++;
            CanvasUpdatePacket pack = new CanvasUpdatePacket(pixels, isSigned, canvasTitle, name, version, customColors, canvasType);
            XercaPaint.NETWORK_HANDLER.sendToServer(pack);
        }
    }


}