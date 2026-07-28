package xerca.xercamusic.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import xerca.xercamusic.mixin.AbstractSliderButtonAccessor;

import java.text.DecimalFormat;

public abstract class BetterSlider extends AbstractSliderButton {
    private static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");

    protected final Component prefix;
    protected final Component suffix;
    protected final double minValue;
    protected final double maxValue;
    protected final double interval;
    protected final double stepSize;
    protected final boolean drawString;
    private final DecimalFormat format;

    protected BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, boolean drawString) {
        super(x, y, width, height, Component.empty(), 0D);
        this.prefix = prefix;
        this.suffix = suffix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.interval = maxValue - minValue;
        this.stepSize = Math.abs(stepSize);
        this.value = this.snapToNearest((currentValue - minValue) / interval);
        this.drawString = drawString;

        if (Mth.equal(this.stepSize, Math.floor(this.stepSize))) {
            this.format = new DecimalFormat("0");
        } else {
            this.format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
        }

        this.updateMessage();
    }

    protected BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1D, drawString);
    }

    public double getValue() {
        return value * interval + minValue;
    }

    public void setSliderValue(double newValue) {
        value = snapToNearest((newValue - minValue) / interval);
        updateMessage();
    }

    public String getValueString() {
        return format.format(getValue());
    }

    @Override
    public abstract void applyValue();

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.updateFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.updateFromMouse(mouseX);
    }

    private void updateFromMouse(double mouseX) {
        double newValue = (mouseX - (this.getX() + 4)) / (this.width - 8);
        double oldValue = value;
        value = snapToNearest(newValue);
        if (!Mth.equal(oldValue, value)) {
            applyValue();
        }
        updateMessage();
    }

    private double snapToNearest(double sliderValue) {
        if (stepSize <= 0D) {
            return Mth.clamp(sliderValue, 0D, 1D);
        }

        sliderValue = Mth.lerp(Mth.clamp(sliderValue, 0D, 1D), minValue, maxValue);
        sliderValue = (stepSize * Math.round(sliderValue / stepSize));

        if (minValue > maxValue) {
            sliderValue = Mth.clamp(sliderValue, maxValue, minValue);
        } else {
            sliderValue = Mth.clamp(sliderValue, minValue, maxValue);
        }

        return Mth.map(sliderValue, minValue, maxValue, 0D, 1D);
    }

    /**
     * Copy of the vanilla 1.20.1 render, except that the handle is drawn with the widget's own height.
     * Vanilla hardcodes a height of 20 there, which makes the handle overhang on shorter sliders.
     */
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        boolean canChangeValue = ((AbstractSliderButtonAccessor) this).xercamusic$canChangeValue();
        int textureY = (this.isFocused() && !canChangeValue) ? 20 : 0;
        int handleTextureY = (this.isHovered || canChangeValue) ? 60 : 40;
        guiGraphics.blitNineSliced(SLIDER_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                20, 4, 200, 20, 0, textureY);
        guiGraphics.blitNineSliced(SLIDER_LOCATION, this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.getHeight(),
                20, 4, 200, 20, 0, handleTextureY);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int color = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.renderScrollingString(guiGraphics, minecraft.font, 2, color | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    protected void updateMessage() {
        if (this.drawString) {
            this.setMessage(Component.empty().append(prefix).append(this.getValueString()).append(suffix));
        } else {
            this.setMessage(Component.empty());
        }
    }
}
