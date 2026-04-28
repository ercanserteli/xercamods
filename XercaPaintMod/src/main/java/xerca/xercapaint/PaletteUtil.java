package xerca.xercapaint;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.FriendlyByteBuf;

public class PaletteUtil {
    private PaletteUtil() {
    }

    public static final Color EMPTINESS_COLOR = new Color(255, 236, 229);
    private static final float RATIO_FULL = 1.0f;
    private static final float RATIO_EMPTY = 0.0f;

    public static class Color {
        public static final Color WHITE = new Color(0xFFFFFFFF);

        public int r;
        public int g;
        public int b;

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

        public void setGLColor() {
            RenderSystem.setShaderColor(r / 255.f, g / 255.f, b / 255.f, 1.0f);
        }

        public static Color mix(Color a, Color b, float ratio) {
            if (ratio == RATIO_FULL) {
                return a;
            } else if (ratio == RATIO_EMPTY) {
                return b;
            }
            Color res = new Color(
                    (int) (a.r * ratio) + (int) (b.r * (1 - ratio)),
                    (int) (a.g * ratio) + (int) (b.g * (1 - ratio)),
                    (int) (a.b * ratio) + (int) (b.b * (1 - ratio))
            );
            int averageMaximum = (int) (Math.max(Math.max(a.r, a.g), a.b) * ratio) + (int) (Math.max(Math.max(b.r, b.g), b.b) * (1 - ratio));

            int maximumOfAverage = Math.max(Math.max(res.r, res.g), res.b);
            int gainFactor = maximumOfAverage == 0 ? 0 : averageMaximum / maximumOfAverage;

            res.r *= gainFactor;
            res.g *= gainFactor;
            res.b *= gainFactor;
            return res;
        }
    }

    public static final class CustomColor {
        public int totalRed;
        public int totalGreen;
        public int totalBlue;
        public int totalMaximum;
        public int numberOfColors;

        private Color result;

        public CustomColor() {
            calculateResult();
        }

        public CustomColor(FriendlyByteBuf buf) {
            readFromBuffer(buf);
            calculateResult();
        }

        public CustomColor(int totalRed, int totalGreen, int totalBlue, int totalMaximum, int numberOfColors) {
            this.totalRed = totalRed;
            this.totalGreen = totalGreen;
            this.totalBlue = totalBlue;
            this.totalMaximum = totalMaximum;
            this.numberOfColors = numberOfColors;
            calculateResult();
        }

        public void calculateResult() {
            if (numberOfColors == 0) {
                this.result = EMPTINESS_COLOR;
                return;
            }
            int averageRed = totalRed / numberOfColors;
            int averageGreen = totalGreen / numberOfColors;
            int averageBlue = totalBlue / numberOfColors;
            int averageMaximum = totalMaximum / numberOfColors;

            int maximumOfAverage = Math.max(Math.max(averageRed, averageGreen), averageBlue);
            int gainFactor = maximumOfAverage == 0 ? 0 : averageMaximum / maximumOfAverage;

            int resultRed = averageRed * gainFactor;
            int resultGreen = averageGreen * gainFactor;
            int resultBlue = averageBlue * gainFactor;

            this.result = new Color(resultRed, resultGreen, resultBlue);
        }

        public void mix(Color toBeMixed) {
            totalRed += toBeMixed.r;
            totalGreen += toBeMixed.g;
            totalBlue += toBeMixed.b;
            totalMaximum += Math.max(Math.max(toBeMixed.r, toBeMixed.g), toBeMixed.b);
            numberOfColors += 1;
            calculateResult();
        }

        public void reset() {
            totalRed = 0;
            totalGreen = 0;
            totalBlue = 0;
            totalMaximum = 0;
            numberOfColors = 0;
            calculateResult();
        }

        public Color getColor() {
            return new Color(result.rgbVal());
        }

        public int getNumberOfColors() {
            return numberOfColors;
        }

        public void writeToBuffer(FriendlyByteBuf buf) {
            buf.writeInt(totalRed);
            buf.writeInt(totalGreen);
            buf.writeInt(totalBlue);
            buf.writeInt(totalMaximum);
            buf.writeInt(numberOfColors);
        }

        public void readFromBuffer(FriendlyByteBuf buf) {
            totalRed = buf.readInt();
            totalGreen = buf.readInt();
            totalBlue = buf.readInt();
            totalMaximum = buf.readInt();
            numberOfColors = buf.readInt();
        }
    }
}
