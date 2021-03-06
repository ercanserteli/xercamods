package xerca.xercapaint.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.SoundEvents;
import xerca.xercapaint.common.XercaPaint;

import static xerca.xercapaint.common.PaletteUtil.emptinessColor;
import static xerca.xercapaint.common.PaletteUtil.readCustomColorArrayFromNBT;

public abstract class BasePalette extends Screen {
    protected static final ResourceLocation paletteTextures = new ResourceLocation(XercaPaint.MODID, "textures/gui/palette.png");
    final static int dyeSpriteX = 240;
    final static int dyeSpriteSize = 16;
    final static int brushSpriteX = 0;
    final static int brushSpriteY = 247;
    final static int brushSpriteSize = 9;
    final static int dropSpriteWidth = 6;
    final static int paletteWidth = 157;
    final static int paletteHeight = 193;
    static final int colorPickerSpriteX = 25;
    static final int colorPickerSpriteY = 242;
    static final int colorPickerPosX = 98;
    static final int colorPickerPosY = 62;
    static final int colorPickerSize = 14;
    static int paletteX;
    static int paletteY;
    final static PaletteUtil.Color waterColor = new PaletteUtil.Color(53, 118, 191);

    final static PaletteUtil.Color[] basicColors = {
            new PaletteUtil.Color(0xFF1D1D21),
            new PaletteUtil.Color(0xFFB02E26),
            new PaletteUtil.Color(0xFF5E7C16),
            new PaletteUtil.Color(0xFF835432),
            new PaletteUtil.Color(0xFF3C44AA),
            new PaletteUtil.Color(0xFF8932B8),
            new PaletteUtil.Color(0xFF169C9C),
            new PaletteUtil.Color(0xFF9D9D97),
            new PaletteUtil.Color(0xFF474F52),
            new PaletteUtil.Color(0xFFF38BAA),
            new PaletteUtil.Color(0xFF80C71F),
            new PaletteUtil.Color(0xFFFED83D),
            new PaletteUtil.Color(0xFF3AB3DA),
            new PaletteUtil.Color(0xFFC74EBD),
            new PaletteUtil.Color(0xFFF9801D),
            new PaletteUtil.Color(0xFFF9FFFE)
    };
    final static Vector2f[] basicColorCenters = {
            new Vector2f(23.5f, 172.5f),
            new Vector2f(18.5f, 145.5f),
            new Vector2f(16.5f, 117.5f),
            new Vector2f(17.5f, 89.5f),
            new Vector2f(23.5f, 62.5f),
            new Vector2f(38.5f, 39.5f),
            new Vector2f(61.5f, 24.5f),
            new Vector2f(87.5f, 17.5f),
            new Vector2f(114.5f, 15.5f),
            new Vector2f(44.5f, 154.5f),
            new Vector2f(41.5f, 127.5f),
            new Vector2f(42.5f, 100.5f),
            new Vector2f(48.5f, 74.5f),
            new Vector2f(64.5f, 52.5f),
            new Vector2f(90.5f, 44.5f),
            new Vector2f(117.5f, 42.5f)
    };
    final static Vector2f[] customColorCenters = {
            new Vector2f(101.5f, 132.0f),
            new Vector2f(113.5f, 118.0f),
            new Vector2f(120.5f, 102.0f),
            new Vector2f(124.5f, 084.0f),
            new Vector2f(126.5f, 066.0f),
            new Vector2f(097.5f, 152.0f),
            new Vector2f(114.5f, 146.0f),
            new Vector2f(127.5f, 133.0f),
            new Vector2f(134.5f, 116.0f),
            new Vector2f(139.5f, 098.0f),
            new Vector2f(142.5f, 080.0f),
            new Vector2f(144.5f, 062.0f),
    };
    final static Vector2f waterCenter = new Vector2f(140.5f, 28.f);
    final static float basicColorRadius = 11.f;
    final static float customColorRadius = 6.5f;

    boolean isPickingColor = false;
    boolean isCarryingColor = false;
    boolean isCarryingWater = false;
    boolean dirty = false;
    PaletteUtil.Color carriedColor;
    int carriedCustomColorId = -1;
    PaletteUtil.Color currentColor = basicColors[0];
    PaletteUtil.CustomColor[] customColors;
    boolean[] basicColorFlags;
    boolean paletteComplete = false;

    BasePalette(ITextComponent titleIn, CompoundNBT paletteTag) {
        super(titleIn);
        this.customColors = new PaletteUtil.CustomColor[12];
        this.basicColorFlags = new boolean[16];

        if (paletteTag != null && !paletteTag.isEmpty()) {
            if(paletteTag.contains("r") && paletteTag.contains("g") && paletteTag.contains("b")
                    && paletteTag.contains("m") && paletteTag.contains("n")){
                readCustomColorArrayFromNBT(paletteTag, this.customColors);

            }else{
                for(int i=0; i < customColors.length; i++){
                    customColors[i] = new PaletteUtil.CustomColor();
                }
            }

            if(paletteTag.contains("basic")){
                paletteComplete = true;
                byte[] basics = paletteTag.getByteArray("basic");
                for(int i=0; i<basics.length; i++){
                    basicColorFlags[i] = basics[i] > 0;
                    paletteComplete &= basicColorFlags[i];
                }
            }
        }else{
            for(int i=0; i < customColors.length; i++){
                customColors[i] = new PaletteUtil.CustomColor();
            }
        }
    }

    protected void superRender(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        Minecraft.getInstance().getTextureManager().bindTexture(paletteTextures);

        // Draw basic colors
        for(int i=0; i<basicColorFlags.length; i++){
            int x = paletteX + (int)basicColorCenters[i].x;
            int y = paletteY + (int)basicColorCenters[i].y;
            int r = (int)basicColorRadius;
            if(basicColorFlags[i]){
                fill(matrixStack, x-r, y-r, x+r+1, y+r+1, basicColors[i].rgbVal());

                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                blit(matrixStack, x - 8, y - 8, dyeSpriteX, i*dyeSpriteSize, dyeSpriteSize, dyeSpriteSize);
            }
            else{
                fill(matrixStack, x-r, y-r, x+r+1, y+r+1, emptinessColor.rgbVal());
            }
        }

        // Draw custom colors
        for(int i=0; i<customColors.length; i++){
            int x = paletteX + (int)customColorCenters[i].x;
            int y = paletteY + (int)customColorCenters[i].y;
            fill(matrixStack, x-6, y-7, x+7, y+6, customColors[i].getColor().rgbVal());
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(matrixStack, paletteX, paletteY, 0, 0, paletteWidth, paletteHeight);

        // Draw color picker
        if(paletteComplete){
            blit(matrixStack, paletteX + colorPickerPosX, paletteY + colorPickerPosY, colorPickerSpriteX, colorPickerSpriteY, colorPickerSize, colorPickerSize);
        }
    }

    protected boolean superMouseClicked(double posX, double posY, int mouseButton){
        return super.mouseClicked(posX, posY, mouseButton);
    }

    protected boolean superMouseReleased(double posX, double posY, int mouseButton){
        return super.mouseReleased(posX, posY, mouseButton);
    }

    protected boolean superMouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY){
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    // Mouse button 0: left, 1: right
    @Override
    public boolean mouseClicked(double posX, double posY, int mouseButton) {
        int mouseX = (int)Math.round(posX);
        int mouseY = (int)Math.round(posY);

        if(paletteClick(mouseX, mouseY)){
            int x = (mouseX - paletteX);
            int y = (mouseY - paletteY);
            Vector2f clickVec = new Vector2f(x, y);
            float sqrBasicRadius = basicColorRadius * basicColorRadius;
            float sqrCustomRadius = customColorRadius * customColorRadius;

            boolean colorFound = false;
            for(int i=0; i<basicColorCenters.length; i++){
                if(basicColorFlags[i] && sqrDist(clickVec, basicColorCenters[i]) <= sqrBasicRadius){
                    if(mouseButton == 0){
                        carriedColor = currentColor = basicColors[i];
                        setCarryingColor();
                        playSound(SoundEvents.MIX, 0.6f);
                    }
                    colorFound = true;
                    break;
                }
            }

            if(!colorFound){
                for(int i=0; i<customColorCenters.length; i++){
                    if(sqrDist(clickVec, customColorCenters[i]) <= sqrCustomRadius){
                        if(mouseButton == 0) {
                            if(customColors[i].getNumberOfColors() > 0){
                                carriedColor = currentColor = customColors[i].getColor();
                                carriedCustomColorId = i;
                                setCarryingColor();
                                playSound(SoundEvents.MIX, 0.3f);
                            }
                        }
                        colorFound = true;
                        break;
                    }
                }
            }

            if(!colorFound) {
                if(sqrDist(clickVec, waterCenter) <= sqrCustomRadius){
                    if(mouseButton == 0) {
                        setCarryingWater();
                        playSound(SoundEvents.WATER);
                    }
                }
            }

            if(paletteComplete && !isCarryingWater && !isCarryingColor){
                if(x >= colorPickerPosX && x < colorPickerPosX + colorPickerSize && y >= colorPickerPosY && y < colorPickerPosY + colorPickerSize){
                    if(mouseButton == 0) {
                        setPickingColor();
                        playSound(SoundEvents.COLOR_PICKER);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void setCarryingWater(){
        isCarryingWater = true;
        isCarryingColor = false;
        isPickingColor = false;
    }

    protected void setCarryingColor(){
        isCarryingWater = false;
        isCarryingColor = true;
        isPickingColor = false;
    }

    protected void setPickingColor(){
        isCarryingWater = false;
        isCarryingColor = false;
        isPickingColor = true;
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        int mouseX = (int)Math.round(posX);
        int mouseY = (int)Math.round(posY);
        if(isCarryingColor || isCarryingWater) {
            if (paletteClick(mouseX, mouseY)) {
                float sqrCustomRadius = customColorRadius * customColorRadius;
                int x = (mouseX - paletteX);
                int y = (mouseY - paletteY);
                Vector2f clickVec = new Vector2f(x, y);
                for (int i = 0; i < customColorCenters.length; i++) {
                    if (sqrDist(clickVec, customColorCenters[i]) <= sqrCustomRadius) {
                        PaletteUtil.CustomColor customColor = customColors[i];
                        if(isCarryingWater){
                            customColor.reset();
                            playSound(SoundEvents.WATER_DROP);
                        }else{
                            if(carriedCustomColorId != i){
                                customColor.mix(carriedColor);
                                currentColor = customColor.getColor();
                                playSound(SoundEvents.MIX);
                            }
                        }
                        dirty = true;
                        break;
                    }
                }
            }
            isCarryingColor = false;
            isCarryingWater = false;
            carriedCustomColorId = -1;
        }
        return super.mouseReleased(posX, posY, mouseButton);
    }

    protected void playSound(ISound sound){
        Minecraft.getInstance().getSoundHandler().play(sound);
    }

    protected void playSound(SoundEvent soundEvent){
        playSound(soundEvent, 1.0f);
    }

    protected void playSound(SoundEvent soundEvent, float volume){
        Minecraft m = Minecraft.getInstance();
        if(m.world != null){
            m.getSoundHandler().play(new SimpleSound(soundEvent, SoundCategory.MASTER, volume,
                    0.8f + m.world.rand.nextFloat()*0.4f, Minecraft.getInstance().player.getPosition()));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    boolean paletteClick(int x, int y) {
        return x <= paletteX + paletteWidth && x >= paletteX && y <= paletteY + paletteHeight && y >= paletteY;
    }

    float sqrDist(Vector2f a, Vector2f b){
        return (a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y);
    }
}
