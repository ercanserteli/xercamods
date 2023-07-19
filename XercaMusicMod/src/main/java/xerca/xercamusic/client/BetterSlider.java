package xerca.xercamusic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;

public class BetterSlider extends ForgeSlider {
    public BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
    }

    public BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
    }

    protected void renderBg(@NotNull PoseStack stack, @NotNull Minecraft minecraft, int _1, int _2) {
        ScreenUtils.blitWithBorder(stack, WIDGETS_LOCATION, this.getX() + (int)(this.value * (float)(this.width - 8)), this.getY(), 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, 0);
    }

    @Override
    public void render(@NotNull PoseStack stack, int p_93658_, int p_93659_, float p_93660_) {
        if (this.visible) {
            renderBg(stack, Minecraft.getInstance(), p_93658_, p_93659_);
            super.render(stack, p_93658_, p_93659_, p_93660_);
        }
    }

    @Override
    public void applyValue() {}
}
