package xerca.xercapaint.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.packets.CanvasUpdatePacket;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;

@OnlyIn(Dist.CLIENT)
public class GuiCanvasEdit extends BasePalette {
    private static final ResourceLocation noteGuiTextures = new ResourceLocation(XercaPaint.MODID, "textures/gui/palette.png");
    private int canvasX = 240;
    private int canvasY = 40;
    private int canvasWidth = 160;
    private int canvasHeight = 160;
    private final int brushMeterX = 420;
    private final int brushMeterY = 120;
    private int canvasPixelScale;
    private int canvasPixelWidth;
    private int canvasPixelHeight;
    private int brushSize = 0;
    private boolean undoUpToDate = true;

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
        paletteX = 40;
        paletteY = 40;
        this.canvasType = canvasType;
        this.canvasPixelScale = canvasType == CanvasType.SMALL ? 10 : 5;
        this.canvasPixelWidth = CanvasType.getWidth(canvasType);
        this.canvasPixelHeight = CanvasType.getHeight(canvasType);
        int canvasPixelArea = canvasPixelHeight*canvasPixelWidth;

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

    private void setPixelAt(int x, int y, Color color){
        if(x >= 0 && y >= 0 && x < canvasPixelWidth && y < canvasPixelHeight){
            this.pixels[y*canvasPixelWidth + x] = color.rgbVal();
        }
    }

    private void setPixelsAt(int mouseX, int mouseY, Color color, int brushSize){
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

                setPixelAt(x-1, y+2, color);
                setPixelAt(x, y+2, color);

                setPixelAt(x-2, y+1, color);
                setPixelAt(x-1, y+1, color);
                setPixelAt(x, y+1, color);
                setPixelAt(x+1, y+1, color);

                setPixelAt(x-2, y, color);
                setPixelAt(x-1, y, color);
                setPixelAt(x, y, color);
                setPixelAt(x+1, y, color);

                setPixelAt(x-1, y-1, color);
                setPixelAt(x, y-1, color);
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
        // Hide mouse cursor
        GLFW.glfwSetInputMode(this.getMinecraft().mainWindow.getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        super.render(mouseX, mouseY, f);

        // Draw the canvas
        for(int i=0; i<canvasPixelHeight; i++){
            for(int j=0; j<canvasPixelWidth; j++){
                int x = canvasX + i* canvasPixelScale;
                int y = canvasY + j* canvasPixelScale;
                fill(x, y, x+ canvasPixelScale, y+ canvasPixelScale, getPixelAt(i, j));
            }
        }

        // Draw brush meter
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if(keyCode == GLFW.GLFW_KEY_Z && (modifiers & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL){
            if(undoStack.size() > 0){
                pixels = undoStack.pop();
            }
            return true;
        }
        else{
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scroll) {
        if (scroll != 0.d) {
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
//        int mouseX = (int)Math.round(posX);
//        int mouseY = (int)Math.round(posY);
        int mouseX = (int)Math.floor(posX);
        int mouseY = (int)Math.floor(posY);

        if(inCanvas(mouseX, mouseY)){
            if(undoStack.size() >= maxUndoLength){
                undoStack.removeLast();
            }
            undoStack.push(pixels.clone());

            setPixelsAt(mouseX, mouseY, currentColor, brushSize);
            dirty = true;
        }

        if(inBrushMeter(mouseX, mouseY)){
            int selectedSize = 3 - (mouseY - brushMeterY)/brushSpriteSize;
            if(selectedSize >= 0){
                brushSize = selectedSize;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        return super.mouseReleased(posX, posY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
//        int mouseX = (int)Math.round(posX);
//        int mouseY = (int)Math.round(posY);
        int mouseX = (int)Math.floor(posX);
        int mouseY = (int)Math.floor(posY);
        if(inCanvas(mouseX, mouseY)){
            setPixelsAt(mouseX, mouseY, currentColor, brushSize);
            dirty = true;
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

    public static class Color {
        public int r, g, b;

        public Color(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public Color(int rgb) {
            this.r = (rgb >> 16) & 0xFF;
            this.g = (rgb >> 8) & 0xFF;
            this.b = rgb & 0xFF;
        }

        public int rgbVal() {
            int val = r;
            val = (val << 8) + g;
            val = (val << 8) + b;
            val += 0xFF000000;
            return val;
        }

        public int bgrVal() {
            int val = b;
            val = (val << 8) + g;
            val = (val << 8) + r;
            val += 0xFF000000;
            return val;
        }

        public void setGLColor(){
            GlStateManager.color4f(((float)r)/255.f, ((float)g)/255.f, ((float)b)/255.f, 1.0f);
        }
    }

}