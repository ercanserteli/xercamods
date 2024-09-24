package xerca.xercamusic.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

public class BetterSlider extends AbstractSliderButton {
    protected final Component prefix;
    protected final Component suffix;
    protected final double minValue;
    protected final double maxValue;
    protected final double interval;
    protected final double stepSize;
    protected final boolean drawString;
    private final DecimalFormat format;

    public BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, boolean drawString) {
        super(x, y, width, height, Component.empty(), 0D);
        this.prefix = prefix;
        this.suffix = suffix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.interval = maxValue - minValue;
        this.stepSize = Math.abs(stepSize);
        this.value = this.snapToNearest((currentValue - minValue) / (interval));
        this.drawString = drawString;

        if (Mth.equal(this.stepSize, Math.floor(this.stepSize))) {
            this.format = new DecimalFormat("0");
        }
        else {
            this.format = new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
        }

        this.updateMessage();
    }

    public BetterSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString)
    {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1D, drawString);
    }

    public double getValue()
    {
        return value * (interval) + minValue;
    }

    public void setValue(double newValue)
    {
        value = snapToNearest((newValue - minValue) / (interval));
        updateMessage();
    }

    public String getValueString()
    {
        return format.format(getValue());
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.setValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY)
    {
        super.onDrag(mouseX, mouseY, dragX, dragY);
        this.setValueFromMouse(mouseX);
    }

    private void setValueFromMouse(double mouseX)
    {
        this.setSliderValue((mouseX - (this.getX() + 4)) / (this.width - 8));
    }

    private void setSliderValue(double newValue)
    {
        double oldValue = value;
        value = snapToNearest(newValue);
        if (!Mth.equal(oldValue, value)) {
            applyValue();
        }
        updateMessage();
    }

    private double snapToNearest(double sliderValue)
    {
        if(stepSize <= 0D) {
            return Mth.clamp(sliderValue, 0D, 1D);
        }

        sliderValue = Mth.lerp(Mth.clamp(sliderValue, 0D, 1D), minValue, maxValue);
        sliderValue = (stepSize * Math.round(sliderValue / stepSize));

        if (minValue > maxValue) {
            sliderValue = Mth.clamp(sliderValue, maxValue, minValue);
        }
        else {
            sliderValue = Mth.clamp(sliderValue, minValue, maxValue);
        }

        return Mth.map(sliderValue, minValue, maxValue, 0D, 1D);
    }

    @Override
    protected void updateMessage()
    {
        if (this.drawString) {
            this.setMessage(Component.empty().append(prefix).append(this.getValueString()).append(suffix));
        }
        else {
            this.setMessage(Component.empty());
        }
    }

    @Override
    public void applyValue() {}
}
