package xerca.xercamusic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;

public class BetterSlider extends ForgeSlider {
    public BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
    }

    public BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, @NotNull Minecraft minecraft, int _1, int _2) {
        if (this.visible)
        {
            GuiUtils.drawContinuousTexturedBox(stack, WIDGETS_LOCATION, this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
        }
    }

    @Override
    public void applyValue() {}
}
