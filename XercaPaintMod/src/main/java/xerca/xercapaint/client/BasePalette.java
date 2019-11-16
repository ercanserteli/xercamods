package xerca.xercapaint.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import xerca.xercapaint.common.XercaPaint;

public abstract class BasePalette extends Screen {
    protected static final ResourceLocation noteGuiTextures = new ResourceLocation(XercaPaint.MODID, "textures/gui/palette.png");
    final static int dyeSpriteX = 240;
    final static int dyeSpriteSize = 16;
    final static int brushSpriteX = 0;
    final static int brushSpriteY = 247;
    final static int brushSpriteSize = 9;
    final static int dropSpriteWidth = 6;
    final static int paletteWidth = 157;
    final static int paletteHeight = 193;
    static int paletteX;
    static int paletteY;
    final static GuiCanvasEdit.Color waterColor = new GuiCanvasEdit.Color(53, 118, 191);
    final static GuiCanvasEdit.Color emptinessColor = new GuiCanvasEdit.Color(255, 236, 229);

    final static GuiCanvasEdit.Color[] basicColors = {
            new GuiCanvasEdit.Color(0xFF1D1D21),
            new GuiCanvasEdit.Color(0xFFB02E26),
            new GuiCanvasEdit.Color(0xFF5E7C16),
            new GuiCanvasEdit.Color(0xFF835432),
            new GuiCanvasEdit.Color(0xFF3C44AA),
            new GuiCanvasEdit.Color(0xFF8932B8),
            new GuiCanvasEdit.Color(0xFF169C9C),
            new GuiCanvasEdit.Color(0xFF9D9D97),
            new GuiCanvasEdit.Color(0xFF474F52),
            new GuiCanvasEdit.Color(0xFFF38BAA),
            new GuiCanvasEdit.Color(0xFF80C71F),
            new GuiCanvasEdit.Color(0xFFFED83D),
            new GuiCanvasEdit.Color(0xFF3AB3DA),
            new GuiCanvasEdit.Color(0xFFC74EBD),
            new GuiCanvasEdit.Color(0xFFF9801D),
            new GuiCanvasEdit.Color(0xFFF9FFFE)
    };
    final static Vec2f[] basicColorCenters = {
            new Vec2f(23.5f, 172.5f),
            new Vec2f(18.5f, 145.5f),
            new Vec2f(16.5f, 117.5f),
            new Vec2f(17.5f, 89.5f),
            new Vec2f(23.5f, 62.5f),
            new Vec2f(38.5f, 39.5f),
            new Vec2f(61.5f, 24.5f),
            new Vec2f(87.5f, 17.5f),
            new Vec2f(114.5f, 15.5f),
            new Vec2f(44.5f, 154.5f),
            new Vec2f(41.5f, 127.5f),
            new Vec2f(42.5f, 100.5f),
            new Vec2f(48.5f, 74.5f),
            new Vec2f(64.5f, 52.5f),
            new Vec2f(90.5f, 44.5f),
            new Vec2f(117.5f, 42.5f)
    };
    final static Vec2f[] customColorCenters = {
            new Vec2f(101.5f, 132.0f),
            new Vec2f(113.5f, 118.0f),
            new Vec2f(120.5f, 102.0f),
            new Vec2f(124.5f, 084.0f),
            new Vec2f(126.5f, 066.0f),
            new Vec2f(097.5f, 152.0f),
            new Vec2f(114.5f, 146.0f),
            new Vec2f(127.5f, 133.0f),
            new Vec2f(134.5f, 116.0f),
            new Vec2f(139.5f, 098.0f),
            new Vec2f(142.5f, 080.0f),
            new Vec2f(144.5f, 062.0f),
    };
    final static Vec2f waterCenter = new Vec2f(140.5f, 28.f);
    final static float basicColorRadius = 11.f;
    final static float customColorRadius = 6.5f;

    boolean isCarryingColor = false;
    boolean isCarryingWater = false;
    boolean dirty = false;
    GuiCanvasEdit.Color carriedColor;
    GuiCanvasEdit.Color currentColor = basicColors[0];
    GuiCanvasEdit.CustomColor[] customColors;
    boolean[] basicColorFlags;

    BasePalette(ITextComponent titleIn, CompoundNBT paletteTag) {
        super(titleIn);
        this.customColors = new CustomColor[12];
        this.basicColorFlags = new boolean[16];

        if (paletteTag != null && !paletteTag.isEmpty()) {
            if(paletteTag.contains("r") && paletteTag.contains("g") && paletteTag.contains("b")
                    && paletteTag.contains("m") && paletteTag.contains("n")){
                readCustomColorArrayFromNBT(paletteTag, this.customColors);

            }else{
                for(int i=0; i < customColors.length; i++){
                    customColors[i] = new CustomColor();
                }
            }

            if(paletteTag.contains("basic")){
                byte[] basics = paletteTag.getByteArray("basic");
                for(int i=0; i<basics.length; i++){
                    basicColorFlags[i] = basics[i] > 0;
                }
            }
            else{

            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        Minecraft.getInstance().getTextureManager().bindTexture(noteGuiTextures);

        // Draw basic colors
        for(int i=0; i<basicColorFlags.length; i++){
            int x = paletteX + (int)basicColorCenters[i].x;
            int y = paletteY + (int)basicColorCenters[i].y;
            int r = (int)basicColorRadius;
            if(basicColorFlags[i]){
                fill(x-r, y-r, x+r+1, y+r+1, basicColors[i].rgbVal());

                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                blit(x - 8, y - 8, dyeSpriteX, i*dyeSpriteSize, dyeSpriteSize, dyeSpriteSize);
            }
            else{
                fill(x-r, y-r, x+r+1, y+r+1, emptinessColor.rgbVal());
            }
        }

        // Draw custom colors
        for(int i=0; i<customColors.length; i++){
            int x = paletteX + (int)customColorCenters[i].x;
            int y = paletteY + (int)customColorCenters[i].y;
            fill(x-6, y-7, x+7, y+6, customColors[i].getColor().rgbVal());
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(paletteX, paletteY, 0, 0, paletteWidth, paletteHeight);
    }

    // Mouse button 0: left, 1: right
    @Override
    public boolean mouseClicked(double posX, double posY, int mouseButton) {
        int mouseX = (int)Math.round(posX);
        int mouseY = (int)Math.round(posY);

        if(paletteClick(mouseX, mouseY)){
            int x = (mouseX - paletteX);
            int y = (mouseY - paletteY);
            Vec2f clickVec = new Vec2f(x, y);
            float sqrBasicRadius = basicColorRadius * basicColorRadius;
            float sqrCustomRadius = customColorRadius * customColorRadius;

            boolean colorFound = false;
            for(int i=0; i<basicColorCenters.length; i++){
                if(basicColorFlags[i] && sqrDist(clickVec, basicColorCenters[i]) <= sqrBasicRadius){
                    if(mouseButton == 0){
                        carriedColor = currentColor = basicColors[i];
                        isCarryingColor = true;
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
                                isCarryingColor = true;
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
                        isCarryingWater = true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
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
                Vec2f clickVec = new Vec2f(x, y);
                for (int i = 0; i < customColorCenters.length; i++) {
                    if (sqrDist(clickVec, customColorCenters[i]) <= sqrCustomRadius) {
                        CustomColor customColor = customColors[i];
                        if(isCarryingWater){
                            customColor.reset();
                        }else{
                            customColor.mix(carriedColor);
                            currentColor = customColor.getColor();
                        }
                        dirty = true;
                        break;
                    }
                }
            }
            isCarryingColor = false;
            isCarryingWater = false;
        }
        return super.mouseReleased(posX, posY, mouseButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    boolean paletteClick(int x, int y) {
        return x <= paletteX + paletteWidth && x >= paletteX && y <= paletteY + paletteHeight && y >= paletteY;
    }

    float sqrDist(Vec2f a, Vec2f b){
        return (a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y);
    }

    public static class CustomColor {
        private int totalRed = 0;
        private int totalGreen = 0;
        private int totalBlue = 0;
        private int totalMaximum = 0;

        private int numberOfColors = 0;

        private GuiCanvasEdit.Color result;

        public CustomColor() {
            calculateResult();
        }

        public CustomColor(PacketBuffer buf) {
            readFromBuffer(buf);
        }

        public CustomColor(int totalRed, int totalGreen, int totalBlue, int totalMaximum, int numberOfColors) {
            this.totalRed = totalRed;
            this.totalGreen = totalGreen;
            this.totalBlue = totalBlue;
            this.totalMaximum = totalMaximum;
            this.numberOfColors = numberOfColors;
            calculateResult();
        }

        public void calculateResult(){
            if(numberOfColors == 0){
                this.result = emptinessColor;//new GuiCanvasEdit.Color(200, 200, 200);
                return;
            }
            int averageRed = totalRed / numberOfColors;
            int averageGreen = totalGreen / numberOfColors;
            int averageBlue = totalBlue / numberOfColors;
            int averageMaximum = totalMaximum / numberOfColors;

            int maximumOfAverage = Math.max(Math.max(averageRed, averageGreen), averageBlue);
            int gainFactor = averageMaximum / maximumOfAverage;

            int resultRed = averageRed * gainFactor;
            int resultGreen = averageGreen * gainFactor;
            int resultBlue = averageBlue * gainFactor;

            this.result = new GuiCanvasEdit.Color(resultRed, resultGreen, resultBlue);
        }

        public void mix(GuiCanvasEdit.Color toBeMixed){
            totalRed += toBeMixed.r;
            totalGreen += toBeMixed.g;
            totalBlue += toBeMixed.b;
            totalMaximum += Math.max(Math.max(toBeMixed.r, toBeMixed.g), toBeMixed.b);
            numberOfColors += 1;
            calculateResult();
        }

        public void reset(){
            totalRed = 0;
            totalGreen = 0;
            totalBlue = 0;
            totalMaximum = 0;
            numberOfColors = 0;
            calculateResult();
        }

        public GuiCanvasEdit.Color getColor() {
            return result;
        }

        public int getNumberOfColors() {
            return numberOfColors;
        }

        public void writeToBuffer(PacketBuffer buf){
            buf.writeInt(totalRed);
            buf.writeInt(totalGreen);
            buf.writeInt(totalBlue);
            buf.writeInt(totalMaximum);
            buf.writeInt(numberOfColors);
        }

        public void readFromBuffer(PacketBuffer buf){
            totalRed = buf.readInt();
            totalGreen = buf.readInt();
            totalBlue = buf.readInt();
            totalMaximum = buf.readInt();
            numberOfColors = buf.readInt();
        }
    }

    public static void writeCustomColorArrayToNBT(CompoundNBT tag, GuiCanvasEdit.CustomColor[] customColors){
        int[] totalReds = new int[12];
        int[] totalGreens = new int[12];
        int[] totalBlues = new int[12];
        int[] totalMaximums = new int[12];
        int[] numbersOfColors = new int[12];

        for(int i=0; i<customColors.length; i++){
            totalReds[i] = customColors[i].totalRed;
            totalGreens[i] = customColors[i].totalGreen;
            totalBlues[i] = customColors[i].totalBlue;
            totalMaximums[i] = customColors[i].totalMaximum;
            numbersOfColors[i] = customColors[i].numberOfColors;
        }
        tag.putIntArray("r", totalReds);
        tag.putIntArray("g", totalGreens);
        tag.putIntArray("b", totalBlues);
        tag.putIntArray("m", totalMaximums);
        tag.putIntArray("n", numbersOfColors);
    }

    public static void readCustomColorArrayFromNBT(CompoundNBT tag, GuiCanvasEdit.CustomColor[] customColors){
        int[] totalReds = tag.getIntArray("r");
        int[] totalGreens = tag.getIntArray("g");
        int[] totalBlues = tag.getIntArray("b");
        int[] totalMaximums = tag.getIntArray("m");
        int[] numbersOfColors = tag.getIntArray("n");

        for(int i=0; i<customColors.length; i++){
            customColors[i] = new GuiCanvasEdit.CustomColor(totalReds[i], totalGreens[i], totalBlues[i], totalMaximums[i], numbersOfColors[i]);
        }
    }
}
