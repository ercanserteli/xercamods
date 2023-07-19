package xerca.xercapaint.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.SoundEvents;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.packets.CanvasMiniUpdatePacket;
import xerca.xercapaint.common.packets.CanvasUpdatePacket;
import xerca.xercapaint.common.packets.EaselLeftPacket;
import xerca.xercapaint.common.packets.PaletteUpdatePacket;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

@OnlyIn(Dist.CLIENT)
public class GuiCanvasEdit extends BasePalette {
    private double canvasX;
    private double canvasY;
    private static final double[] canvasXs = {-1000, -1000, -1000, -1000};
    private static final double[] canvasYs = {-1000, -1000, -1000, -1000};
    private final int canvasWidth;
    private final int canvasHeight;
    private int brushMeterX;
    private int brushMeterY;
    private int brushOpacityMeterX;
    private int brushOpacityMeterY;
    private final int canvasPixelScale;
    private final int canvasPixelWidth;
    private final int canvasPixelHeight;
    private int brushSize = 0;
    private boolean touchedCanvas = false;
    private boolean undoStarted = false;
    private boolean gettingSigned;
    private boolean isCarryingCanvas;
    private Button buttonSign;
    private Button buttonCancel;
    private Button buttonFinalize;
    private int updateCount;
    private BrushSound brushSound = null;
    private final int canvasHolderHeight = 10;
    private static int brushOpacitySetting = 0;
    private static final float[] brushOpacities = {1.f, 0.75f, 0.5f, 0.25f};
    private static boolean showHelp = false;
    private final Set<Integer> draggedPoints = new HashSet<>();

    private final Player editingPlayer;

    private final CanvasType canvasType;
    private boolean isSigned = false;
    private int[] pixels;
    private String canvasTitle = "";
    private String name = "";
    private int version = 0;
    private final EntityEasel easel;
    private int timeSinceLastUpdate = 0;
    private boolean skippedUpdate = false;

    private static final Vec2[] outlinePoss1 = {
            new Vec2(0.f, 199.0f),
            new Vec2(12.f, 199.0f),
            new Vec2(34.f, 199.0f),
            new Vec2(76.f, 199.0f),
    };

    private static final Vec2[] outlinePoss2 = {
            new Vec2(128.f, 199.0f),
            new Vec2(135.f, 199.0f),
            new Vec2(147.f, 199.0f),
            new Vec2(169.f, 199.0f),
    };

    private static final int maxUndoLength = 16;
    private final Deque<int[]> undoStack = new ArrayDeque<>(maxUndoLength);

    protected GuiCanvasEdit(Player player, CompoundTag canvasTag, CompoundTag paletteTag, Component title, CanvasType canvasType, EntityEasel easel) {
        super(title, paletteTag);
        updateCount = 0;

        this.canvasType = canvasType;
        this.canvasPixelScale = canvasType == CanvasType.SMALL ? 10 : 5;
        this.canvasPixelWidth = CanvasType.getWidth(canvasType);
        this.canvasPixelHeight = CanvasType.getHeight(canvasType);
        int canvasPixelArea = canvasPixelHeight*canvasPixelWidth;
        this.canvasWidth = this.canvasPixelWidth * this.canvasPixelScale;
        this.canvasHeight = this.canvasPixelHeight * this.canvasPixelScale;
        this.easel = easel;

        this.editingPlayer = player;
        if (canvasTag != null && !canvasTag.isEmpty()) {
            int[] nbtPixels = canvasTag.getIntArray("pixels");
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
            this.name = "" + player.getUUID() + "_" + secs;
        }

        if(paletteComplete){
            XercaPaint.LOGGER.warn("Is complete");
        }
    }

    @Override
    public void init() {
        canvasX = canvasXs[canvasType.ordinal()];
        canvasY = canvasYs[canvasType.ordinal()];
        paletteX = paletteXs[canvasType.ordinal()];
        paletteY = paletteYs[canvasType.ordinal()];
        if(canvasX == -1000 || canvasY == -1000 || paletteX == -1000 || paletteY == -1000){
            resetPositions();
        }

        updateCanvasPos(0, 0);
        updatePalettePos(0, 0);
        
        Window window = minecraft.getWindow();

        // Hide mouse cursor
        GLFW.glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

        int x = window.getGuiScaledWidth() - 120;
        int y = window.getGuiScaledHeight() - 30;
        this.buttonSign = this.addRenderableWidget(Button.builder(Component.translatable("canvas.signButton"), button -> {
            if (!isSigned) {
                gettingSigned = true;
                resetPositions();
                updateButtons();

                GLFW.glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            }
        }).bounds(x, y, 98, 20).build());
        this.buttonFinalize = this.addRenderableWidget(Button.builder( Component.translatable("canvas.finalizeButton"), button -> {
            if (!isSigned) {
                canvasDirty = true;
                isSigned = true;
                if(minecraft != null){
                    minecraft.setScreen(null);
                }
            }

        }).bounds((int)canvasX - 100, 100, 98, 20).build());
        this.buttonCancel = this.addRenderableWidget(Button.builder( Component.translatable("gui.cancel"), button -> {
            if (!isSigned) {
                gettingSigned = false;
                updateButtons();

                GLFW.glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            }
        }).bounds((int)canvasX - 100, 130, 98, 20).build());

        x = (int)(window.getGuiScaledWidth()*0.95) - 21;
        y = (int)(window.getGuiScaledHeight()*0.05);
        this.addRenderableWidget(new ToggleHelpButton(x, y, 21, 21, 197, 0, 21,
                paletteTextures, 256, 256, button -> showHelp = !showHelp, Tooltip.create(Component.literal("Toggle help tooltips"))));

        updateButtons();
    }

    private void updateButtons() {
        if (!this.isSigned) {
            this.buttonSign.visible = !this.gettingSigned;
            this.buttonCancel.visible = this.gettingSigned;
            this.buttonFinalize.visible = this.gettingSigned;
            this.buttonFinalize.active = !this.canvasTitle.trim().isEmpty();

            this.buttonFinalize.setX((int)canvasX - 100);
            this.buttonCancel.setX((int)canvasX - 100);
        }
    }

    private int getPixelAt(int x, int y){
        return this.pixels[y*canvasPixelWidth + x];
    }

    private void setPixelAt(int x, int y, PaletteUtil.Color color, float opacity){
        if(x >= 0 && y >= 0 && x < canvasPixelWidth && y < canvasPixelHeight){
            if(!draggedPoints.contains(y*canvasPixelWidth + x)){
                draggedPoints.add(y*canvasPixelWidth + x);
                this.pixels[y*canvasPixelWidth + x] = PaletteUtil.Color.mix(color, new PaletteUtil.Color(this.pixels[y*canvasPixelWidth + x]), opacity).rgbVal();
            }
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void setPixelsAt(int mouseX, int mouseY, PaletteUtil.Color color, int brushSize, float opacity){
        int x, y;
        final int pixelHalf = canvasPixelScale/2;
        switch (brushSize) {
            case 0 -> {
                x = (mouseX - (int) canvasX) / canvasPixelScale;
                y = (mouseY - (int) canvasY) / canvasPixelScale;
                setPixelAt(x, y, color, opacity);
            }
            case 1 -> {
                x = (mouseX - (int) canvasX + pixelHalf) / canvasPixelScale;
                y = (mouseY - (int) canvasY + pixelHalf) / canvasPixelScale;
                setPixelAt(x, y, color, opacity);
                setPixelAt(x - 1, y, color, opacity);
                setPixelAt(x, y - 1, color, opacity);
                setPixelAt(x - 1, y - 1, color, opacity);
            }
            case 2 -> {
                x = (mouseX - (int) canvasX + pixelHalf) / canvasPixelScale;
                y = (mouseY - (int) canvasY + pixelHalf) / canvasPixelScale;
                setPixelAt(x - 1, y + 1, color, opacity);
                setPixelAt(x, y + 1, color, opacity);
                setPixelAt(x - 2, y, color, opacity);
                setPixelAt(x - 1, y, color, opacity);
                setPixelAt(x, y, color, opacity);
                setPixelAt(x + 1, y, color, opacity);
                setPixelAt(x - 2, y - 1, color, opacity);
                setPixelAt(x - 1, y - 1, color, opacity);
                setPixelAt(x, y - 1, color, opacity);
                setPixelAt(x + 1, y - 1, color, opacity);
                setPixelAt(x - 1, y - 2, color, opacity);
                setPixelAt(x, y - 2, color, opacity);
            }
            case 3 -> {
                x = (mouseX - (int) canvasX) / canvasPixelScale;
                y = (mouseY - (int) canvasY) / canvasPixelScale;
                setPixelAt(x - 1, y + 2, color, opacity);
                setPixelAt(x + 0, y + 2, color, opacity);
                setPixelAt(x + 1, y + 2, color, opacity);
                setPixelAt(x - 2, y + 1, color, opacity);
                setPixelAt(x - 1, y + 1, color, opacity);
                setPixelAt(x + 0, y + 1, color, opacity);
                setPixelAt(x + 1, y + 1, color, opacity);
                setPixelAt(x + 2, y + 1, color, opacity);
                setPixelAt(x - 2, y, color, opacity);
                setPixelAt(x - 1, y, color, opacity);
                setPixelAt(x + 0, y, color, opacity);
                setPixelAt(x + 1, y, color, opacity);
                setPixelAt(x + 2, y, color, opacity);
                setPixelAt(x - 2, y - 1, color, opacity);
                setPixelAt(x - 1, y - 1, color, opacity);
                setPixelAt(x + 0, y - 1, color, opacity);
                setPixelAt(x + 1, y - 1, color, opacity);
                setPixelAt(x + 2, y - 1, color, opacity);
                setPixelAt(x - 1, y - 2, color, opacity);
                setPixelAt(x + 0, y - 2, color, opacity);
                setPixelAt(x + 1, y - 2, color, opacity);
            }
        }
    }

    private void resetPositions(){
        final int padding = 40;
        final int paletteCanvasX = (this.width - (paletteWidth + canvasWidth + padding)) / 2;
        canvasX = paletteCanvasX + paletteWidth + padding;
        if(canvasType.equals(CanvasType.LONG)){
            canvasY = 80;
        }
        else{
            canvasY = 40;
        }

        paletteX = paletteCanvasX;
        paletteY = 40;
    }

    @Override
    public void tick() {
        ++this.updateCount;
        ++this.timeSinceLastUpdate;

        if(easel != null){
            if(easel.getItem().isEmpty() || easel.isRemoved() || easel.distanceToSqr(editingPlayer) > 64){
                this.onClose();
            }
            if(skippedUpdate && timeSinceLastUpdate > 20 && canvasDirty){
                updateCanvas(false);
                skippedUpdate = false;
            }
        }

        super.tick();
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float f) {
        if(!gettingSigned) {
            super.render(matrixStack, mouseX, mouseY, f);
        }
        else {
            super.superRender(matrixStack, mouseX, mouseY, f);
        }

        // Draw the canvas holder
        fill(matrixStack, (int)(canvasX + canvasWidth*0.25), (int)canvasY - canvasHolderHeight, (int)(canvasX + canvasWidth*0.75), (int)canvasY, 0xffe1e1e1);

        // Draw the canvas
        for(int i=0; i<canvasPixelHeight; i++){
            for(int j=0; j<canvasPixelWidth; j++){
                int y = (int)canvasY + i* canvasPixelScale;
                int x = (int)canvasX + j* canvasPixelScale;
                fill(matrixStack, x, y, x + canvasPixelScale, y + canvasPixelScale, getPixelAt(j, i));
            }
        }

        if(!gettingSigned){
            // Draw brush meter
            for(int i=0; i<4; i++){
                int y = brushMeterY + i*brushSpriteSize;
                fill(matrixStack, brushMeterX, y, brushMeterX + 3, y + 3, currentColor.rgbVal());
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            blit(matrixStack, brushMeterX, brushMeterY + (3 - brushSize)*brushSpriteSize, 15, 246, 10, 10);
            blit(matrixStack, brushMeterX, brushMeterY, brushSpriteX, brushSpriteY - brushSpriteSize*3, brushSpriteSize, brushSpriteSize*4);

            // Draw opacity meter
            blit(matrixStack, brushOpacityMeterX, brushOpacityMeterY, brushOpacitySpriteX, brushOpacitySpriteY, brushOpacitySpriteSize, brushOpacitySpriteSize*4+3);
            blit(matrixStack, brushOpacityMeterX-1, brushOpacityMeterY-1 + brushOpacitySetting*(brushOpacitySpriteSize+1), 212, 240, 16, 16);

            // Draw brush and outline
            renderCursor(matrixStack, mouseX, mouseY);

            if(showHelp){
                if(inBrushMeter(mouseX, mouseY)){
                    int selectedSize = 3 - (mouseY - brushMeterY)/brushSpriteSize;
                    if(selectedSize <= 3 && selectedSize >= 0){
                        this.renderTooltip(matrixStack, Component.literal("Brush size (" + (selectedSize+1) + ")"), mouseX, mouseY);
                    }
                }
                else if(inBrushOpacityMeter(mouseX, mouseY)){
                    int relativeY = mouseY - brushOpacityMeterY;
                    int selectedOpacity = relativeY/(brushOpacitySpriteSize+1);
                    if(selectedOpacity >= 0 && selectedOpacity <= 3){
                        int percentage = 100 - 25*selectedOpacity;
                        this.renderTooltip(matrixStack, Component.literal("Brush opacity (" + percentage + "%)"), mouseX, mouseY);
                    }
                }
                else if(inColorPicker(mouseX-(int)paletteX, mouseY-(int)paletteY)){
                    this.renderComponentTooltip(matrixStack, Arrays.asList(Component.literal("Color picker"),
                            Component.literal("Select the tool, then pick up a color from the canvas and drag-and-drop it to a custom color slot.").withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
                }
                else if(inWater(mouseX-(int)paletteX, mouseY-(int)paletteY)){
                    this.renderComponentTooltip(matrixStack, Arrays.asList(Component.literal("Color remover"),
                            Component.literal("Pick up some water and drag-and-drop it to a custom color slot to clear it.").withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
                }else if(inCanvasHolder(mouseX, mouseY)){
                    this.renderComponentTooltip(matrixStack, Arrays.asList(Component.literal("Canvas holder"),
                            Component.literal("Pick up the canvas and move it wherever you want. You can move the palette in the same way.").withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
                }
            }
        }
        else{
            drawSigning(matrixStack);
        }
    }

    private void renderCursor(PoseStack matrixStack, int mouseX, int mouseY){
        if(isCarryingColor){
            carriedColor.setGLColor();
            blit(matrixStack, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);

        }else if(isCarryingWater){
            waterColor.setGLColor();
            blit(matrixStack, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);
        }else if(isPickingColor){
            drawOutline(matrixStack, mouseX, mouseY, 0);
            PaletteUtil.Color.WHITE.setGLColor();
            blit(matrixStack, mouseX, mouseY-colorPickerSize, colorPickerSpriteX, colorPickerSpriteY, colorPickerSize, colorPickerSize);
        }
        else{
            drawOutline(matrixStack, mouseX, mouseY, brushSize);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            fill(matrixStack, mouseX, mouseY, mouseX + 3, mouseY + 3, currentColor.rgbVal());

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int trueBrushY = brushSpriteY - brushSpriteSize*brushSize;
            blit(matrixStack, mouseX, mouseY, brushSpriteX, trueBrushY, brushSpriteSize, brushSpriteSize);
        }
    }

    private void drawOutline(PoseStack matrixStack, int mouseX, int mouseY, int brushSize){
        if(inCanvas(mouseX, mouseY)){
            // Render drawing outline
            int x = 0;
            int y = 0;
            int outlineSize = 0;
            int pixelHalf = canvasPixelScale/2;
            if(brushSize == 0){
                x = ((mouseX - (int)canvasX)/ canvasPixelScale)*canvasPixelScale + (int)canvasX - 1;
                y = ((mouseY - (int)canvasY)/ canvasPixelScale)*canvasPixelScale + (int)canvasY - 1;
                outlineSize = canvasPixelScale + 2;
            }
            if(brushSize == 1){
                x = (((mouseX - (int)canvasX + pixelHalf) / canvasPixelScale) - 1)*canvasPixelScale + (int)canvasX - 1;
                y = (((mouseY - (int)canvasY + pixelHalf) / canvasPixelScale) - 1)*canvasPixelScale + (int)canvasY - 1;
                outlineSize = canvasPixelScale*2 + 2;
            }
            if(brushSize == 2){
                x = (((mouseX - (int)canvasX + pixelHalf) / canvasPixelScale) - 2)*canvasPixelScale + (int)canvasX - 1;
                y = (((mouseY - (int)canvasY + pixelHalf) / canvasPixelScale) - 2)*canvasPixelScale + (int)canvasY - 1;
                outlineSize = canvasPixelScale*4 + 2;
            }
            if(brushSize == 3){
                x = (((mouseX - (int)canvasX)/ canvasPixelScale) - 2)*canvasPixelScale + (int)canvasX - 1;
                y = (((mouseY - (int)canvasY)/ canvasPixelScale) - 2)*canvasPixelScale + (int)canvasY - 1;
                outlineSize = canvasPixelScale*5 + 2;
            }

            Vec2 textureVec;
            if(canvasPixelScale == 10){
                textureVec = outlinePoss1[brushSize];
            }
            else{
                textureVec = outlinePoss2[brushSize];
            }

            RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);
            blit(matrixStack, x, y, (int)textureVec.x, (int)textureVec.y, outlineSize, outlineSize);
        }
    }

    private void drawSigning(PoseStack matrixStack) {
        int i = (int)canvasX;
        int j = (int)canvasY;

        fill(matrixStack, i + 10, j + 10, i + 150, j + 150, 0xFFEEEEEE);
        String s = this.canvasTitle;

        if (!this.isSigned) {
            if (this.updateCount / 6 % 2 == 0) {
                s = s + "" + ChatFormatting.BLACK + "_";
            } else {
                s = s + "" + ChatFormatting.GRAY + "_";
            }
        }
        String s1 = I18n.get("canvas.editTitle");
        int k = this.font.width(s1);
        this.font.draw(matrixStack, s1, i + 26 + (116 - k) / 2.0f, j + 16 + 16, 0);
        int l = this.font.width(s);
        this.font.draw(matrixStack, s, i + 26 + (116 - l) / 2.0f, j + 48, 0);
        String s2 = I18n.get("canvas.byAuthor", this.editingPlayer.getName().getString());
        int i1 = this.font.width(s2);
        this.font.draw(matrixStack, ChatFormatting.DARK_GRAY + s2, i + 26 + (116 - i1) / 2.0f, j + 48 + 10, 0);
        this.font.drawWordWrap(matrixStack, Component.translatable("canvas.finalizeWarning"), i + 26, j + 80, 116, 0);
    }

    private void playBrushSound(){
        brushSound = new BrushSound();
        playSound(brushSound);
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
                        canvasDirty = true;
                        this.isSigned = true;
                        if (this.minecraft != null) {
                            this.minecraft.setScreen(null);
                        }
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
                    canvasDirty = true;
                    if(easel != null){
                        updateCanvas(false);
                    }
                }
                return true;
            } else {
                if(keyCode == GLFW_KEY_O){
                    brushOpacitySetting += 1;
                    if(brushOpacitySetting >= 4){
                        brushOpacitySetting = 0;
                    }
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    @Override
    public boolean charTyped(char typedChar, int something) {
        super.charTyped(typedChar, something);

        if (!this.isSigned) {
            if (this.gettingSigned) {
                if (this.canvasTitle.length() < 16 && SharedConstants.isAllowedChatCharacter(typedChar)) {
                    this.canvasTitle = this.canvasTitle + typedChar;
                    this.updateButtons();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double posX, double posY, double scroll) {
        int mouseX = (int)Math.floor(posX);
        int mouseY = (int)Math.floor(posY);
        if (!gettingSigned && scroll != 0.d) {
            if(inBrushOpacityMeter(mouseX, mouseY)){
                final int maxBrushOpacity = 3;
                brushOpacitySetting += scroll < 0 ? 1 : -1;
                if (brushOpacitySetting > maxBrushOpacity) brushOpacitySetting = 0;
                else if (brushOpacitySetting < 0) brushOpacitySetting = maxBrushOpacity;
                return true;
            }
            else{
                final int maxBrushSize = 3;
                brushSize += scroll > 0 ? 1 : -1;
                if (brushSize > maxBrushSize) brushSize = 0;
                else if (brushSize < 0) brushSize = maxBrushSize;
                return true;
            }
        }
        return super.mouseScrolled(posX, posY, scroll);
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
            if(isPickingColor){
                int x = (mouseX - (int)canvasX)/ canvasPixelScale;
                int y = (mouseY - (int)canvasY)/ canvasPixelScale;
                if(x >= 0 && y >= 0 && x < canvasPixelWidth && y < canvasPixelHeight){
                    int color = getPixelAt(x, y);
                    carriedColor = new PaletteUtil.Color(color);
                    setCarryingColor();
                    playSound(SoundEvents.COLOR_PICKER_SUCK.get());
                }
            }
            else{
                clickedCanvas(mouseX, mouseY, mouseButton);
                playBrushSound();
            }
            return super.superMouseClicked(mouseX, mouseY, mouseButton);
        }

        if(inBrushMeter(mouseX, mouseY)){
            int selectedSize = 3 - (mouseY - brushMeterY)/brushSpriteSize;
            if(selectedSize <= 3 && selectedSize >= 0){
                brushSize = selectedSize;
            }
            return super.superMouseClicked(mouseX, mouseY, mouseButton);
        }
        if(inBrushOpacityMeter(mouseX, mouseY)){
            int relativeY = mouseY - brushOpacityMeterY;
            int selectedOpacity = relativeY/(brushOpacitySpriteSize+1);
            if(selectedOpacity >= 0 && selectedOpacity <= 3){
                brushOpacitySetting = selectedOpacity;
            }
            return super.superMouseClicked(mouseX, mouseY, mouseButton);
        }
        if(inCanvasHolder(mouseX, mouseY)){
            isCarryingCanvas = true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void clickedCanvas(int mouseX, int mouseY, int mouseButton){
        touchedCanvas = true;
        if(mouseButton == GLFW_MOUSE_BUTTON_LEFT){
            setPixelsAt(mouseX, mouseY, currentColor, brushSize, brushOpacities[brushOpacitySetting]);
        }else if(mouseButton == GLFW_MOUSE_BUTTON_RIGHT){
            // "Erase" with right click
            setPixelsAt(mouseX, mouseY, PaletteUtil.Color.WHITE, brushSize, 1.0f);
        }
        canvasDirty = true;
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        isCarryingCanvas = false;
        if(gettingSigned){
            return super.superMouseReleased(posX, posY, mouseButton);
        }
        draggedPoints.clear();

        if(undoStarted && !touchedCanvas){
            undoStarted = false;
            undoStack.removeFirst();
        }

        if(brushSound != null){
            brushSound.stopSound();
        }

        if(easel != null){
            updateCanvas(false);
        }

        return super.mouseReleased(posX, posY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        if(gettingSigned){
            return super.superMouseDragged(posX, posY, mouseButton, deltaX, deltaY);
        }
        if(!isCarryingColor && !isCarryingWater && !isPickingColor && !isCarryingPalette && !isCarryingCanvas){
            int mouseX = (int)Math.floor(posX);
            int mouseY = (int)Math.floor(posY);
            if(inCanvas(mouseX, mouseY)){
                clickedCanvas(mouseX, mouseY, mouseButton);
            }

            if(brushSound != null){
                brushSound.refreshFade();
            }
        }
        else if(isCarryingCanvas){
            updateCanvasPos(deltaX, deltaY);
            return super.superMouseDragged(posX, posY, mouseButton, deltaX, deltaY);
        }
        else if(isCarryingPalette){
            boolean ret = super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            updatePalettePos(deltaX, deltaY);
            return ret;
        }
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    private void updateCanvasPos(double deltaX, double deltaY){
        canvasX += deltaX;
        canvasY += deltaY;

        brushMeterX = (int)canvasX + canvasWidth + 2;
        brushMeterY = (int)canvasY + canvasHeight/2 + 30;

        brushOpacityMeterX = (int)canvasX + canvasWidth + 2;
        brushOpacityMeterY = (int)canvasY;

        canvasXs[canvasType.ordinal()] = canvasX;
        canvasYs[canvasType.ordinal()] = canvasY;
    }

    private void updatePalettePos(double deltaX, double deltaY){
        paletteX += deltaX;
        paletteY += deltaY;

        paletteXs[canvasType.ordinal()] = paletteX;
        paletteYs[canvasType.ordinal()] = paletteY;
    }

    private boolean inCanvas(int x, int y) {
        return x < canvasX + canvasWidth && x >= canvasX && y < canvasY + canvasHeight && y >= canvasY;
    }

    private boolean inCanvasHolder(int x, int y) {
        return x < canvasX + ((double)canvasWidth)*0.75 && x >= canvasX + ((double)canvasWidth)*0.25 && y < canvasY && y >= canvasY - canvasHolderHeight;
    }

    private boolean inBrushMeter(int x, int y) {
        return x < brushMeterX + brushSpriteSize && x >= brushMeterX && y < brushMeterY + brushSpriteSize*4 && y >= brushMeterY;
    }

    private boolean inBrushOpacityMeter(int x, int y) {
        return x < brushOpacityMeterX + brushOpacitySpriteSize && x >= brushOpacityMeterX && y < brushOpacityMeterY + brushOpacitySpriteSize*4+3 && y >= brushOpacityMeterY;
    }

    @Override
    public void removed() {
        updateCanvas(true);
    }

    private void updateCanvas(boolean closing){
        if(closing){
            if (canvasDirty) {
                version++;
                CanvasUpdatePacket pack = new CanvasUpdatePacket(pixels, isSigned, canvasTitle, name, version, easel, customColors, canvasType);
                XercaPaint.NETWORK_HANDLER.sendToServer(pack);
            }
            else {
                if(easel != null) {
                    EaselLeftPacket pack = new EaselLeftPacket(easel);
                    XercaPaint.NETWORK_HANDLER.sendToServer(pack);
                }
                if(paletteDirty) {
                    PaletteUpdatePacket pack = new PaletteUpdatePacket(customColors);
                    XercaPaint.NETWORK_HANDLER.sendToServer(pack);
                }
            }
        }
        else{
            if (canvasDirty) {
                if(timeSinceLastUpdate < 10){
                    skippedUpdate = true;
                }
                else{
                    version ++;
                    CanvasMiniUpdatePacket pack = new CanvasMiniUpdatePacket(pixels, name, version, easel, canvasType);
                    XercaPaint.NETWORK_HANDLER.sendToServer(pack);
                    canvasDirty = false;
                    timeSinceLastUpdate = 0;
                }
            }
        }
    }

    public static class ToggleHelpButton extends Button {
        protected final ResourceLocation resourceLocation;
        protected final int xTexStart;
        protected final int yTexStart;
        protected final int yDiffText;
        protected final int texWidth;
        protected final int texHeight;

        public ToggleHelpButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int texWidth, int texHeight, OnPress onClick, Tooltip tooltip) {
            super(x, y, width, height, Component.empty(), onClick, Button.DEFAULT_NARRATION);
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.xTexStart = xTexStart;
            this.yTexStart = yTexStart;
            this.yDiffText = yDiffText;
            this.resourceLocation = texture;
            setTooltip(tooltip);
        }

        protected void postRender(){
            GlStateManager._enableDepthTest();
        }

        @Override
        public void renderWidget(@NotNull PoseStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            RenderSystem.setShaderTexture(0, this.resourceLocation);
            GlStateManager._disableDepthTest();
            int yTexStartNew = this.yTexStart;
            if (this.isHovered) {
                yTexStartNew += this.yDiffText;
            }
            int xTexStartNew = this.xTexStart + (showHelp ? 0 : this.width);
            blit(matrixStack, this.getX(), this.getY(), (float)xTexStartNew, (float)yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
            postRender();
        }
    }
}