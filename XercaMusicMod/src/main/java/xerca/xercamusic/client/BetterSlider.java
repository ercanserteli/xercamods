package xerca.xercamusic.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

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
    private static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");

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

    protected void renderBg(@NotNull PoseStack stack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        blitWithBorder(stack, this.getX() + (int)(this.value * (float)(this.width - 8)), this.getY(), 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, 0);
    }

    @Override
    public void render(@NotNull PoseStack stack, int p_93658_, int p_93659_, float p_93660_) {
        if (this.visible) {
            renderBg(stack);
            super.render(stack, p_93658_, p_93659_, p_93660_);
        }
    }

    @Override
    public void renderWidget(@NotNull PoseStack poseStack, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, SLIDER_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blitNineSliced(poseStack, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        blitNineSliced(poseStack, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), 20, 4, 200, 20, 0, this.getHandleTextureY());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int k = this.active ? 16777215 : 10526880;
        this.renderScrollingString(poseStack, minecraft.font, 2, k | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    private int getTextureY() {
        int i = this.isFocused() ? 1 : 0;
        return i * 20;
    }

    private int getHandleTextureY() {
        int i = !this.isHovered ? 2 : 3;
        return i * 20;
    }

    public static void blitWithBorder(PoseStack poseStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
                                      int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        drawTexturedModalRect(poseStack, x, y, u, v, leftBorder, topBorder, zLevel);
        // Top Right
        drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
        // Bottom Left
        drawTexturedModalRect(poseStack, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
        // Bottom Right
        drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
            // Top Border
            drawTexturedModalRect(poseStack, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, zLevel);
            // Bottom Border
            drawTexturedModalRect(poseStack, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, zLevel);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
                drawTexturedModalRect(poseStack, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            }
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
            // Left Border
            drawTexturedModalRect(poseStack, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            // Right Border
            drawTexturedModalRect(poseStack, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel);
        }
    }

    public static void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height, float zLevel)
    {
        final float uScale = 1f / 0x100;
        final float vScale = 1f / 0x100;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder wr = tesselator.getBuilder();
        wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = poseStack.last().pose();
        wr.vertex(matrix, x, y + height, zLevel).uv( u * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + width, y + height, zLevel).uv((u + width) * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + width, y, zLevel).uv((u + width) * uScale, (v * vScale)).endVertex();
        wr.vertex(matrix, x, y , zLevel).uv(u * uScale, (v * vScale)).endVertex();
        tesselator.end();
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
