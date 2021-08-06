package xerca.xercapaint.common;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PaletteUtil {
    final public static PaletteUtil.Color emptinessColor = new PaletteUtil.Color(255, 236, 229);

    public static class Color {
        public static Color WHITE = new Color(0xFFFFFFFF);

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
//            GlStateManager._color4f(((float)r)/255.f, ((float)g)/255.f, ((float)b)/255.f, 1.0f);
            RenderSystem.setShaderColor(((float)r)/255.f, ((float)g)/255.f, ((float)b)/255.f, 1.0f);
        }
    }
    public static class CustomColor {
        private int totalRed = 0;
        private int totalGreen = 0;
        private int totalBlue = 0;
        private int totalMaximum = 0;

        private int numberOfColors = 0;

        private PaletteUtil.Color result;

        public CustomColor() {
            calculateResult();
        }

        public CustomColor(FriendlyByteBuf buf) {
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
                this.result = emptinessColor;//new PaletteUtil.Color(200, 200, 200);
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

            this.result = new PaletteUtil.Color(resultRed, resultGreen, resultBlue);
        }

        public void mix(PaletteUtil.Color toBeMixed){
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

        public PaletteUtil.Color getColor() {
            return result;
        }

        public int getNumberOfColors() {
            return numberOfColors;
        }

        public void writeToBuffer(FriendlyByteBuf buf){
            buf.writeInt(totalRed);
            buf.writeInt(totalGreen);
            buf.writeInt(totalBlue);
            buf.writeInt(totalMaximum);
            buf.writeInt(numberOfColors);
        }

        public void readFromBuffer(FriendlyByteBuf buf){
            totalRed = buf.readInt();
            totalGreen = buf.readInt();
            totalBlue = buf.readInt();
            totalMaximum = buf.readInt();
            numberOfColors = buf.readInt();
        }
    }

    public static void writeCustomColorArrayToNBT(CompoundTag tag, PaletteUtil.CustomColor[] customColors){
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

    public static void readCustomColorArrayFromNBT(CompoundTag tag, PaletteUtil.CustomColor[] customColors){
        int[] totalReds = tag.getIntArray("r");
        int[] totalGreens = tag.getIntArray("g");
        int[] totalBlues = tag.getIntArray("b");
        int[] totalMaximums = tag.getIntArray("m");
        int[] numbersOfColors = tag.getIntArray("n");

        for(int i=0; i<customColors.length; i++){
            customColors[i] = new PaletteUtil.CustomColor(totalReds[i], totalGreens[i], totalBlues[i], totalMaximums[i], numbersOfColors[i]);
        }
    }
}
