package xerca.xercapaint.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
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
    final static int brushOpacitySpriteX = 196;
    final static int brushOpacitySpriteY = 197;
    final static int brushOpacitySpriteSize = 14;
    final static int dropSpriteWidth = 6;
    final static int paletteWidth = 157;
    final static int paletteHeight = 193;
    static final int colorPickerSpriteX = 25;
    static final int colorPickerSpriteY = 242;
    static final int colorPickerPosX = 98;
    static final int colorPickerPosY = 62;
    static final int colorPickerSize = 14;

    static final double[] paletteXs = {-1000, -1000, -1000, -1000, -1000};
    static final double[] paletteYs = {-1000, -1000, -1000, -1000, -1000};
    double paletteX;
    double paletteY;
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
    final static Vec2[] basicColorCenters = {
            new Vec2(23.5f, 172.5f),
            new Vec2(18.5f, 145.5f),
            new Vec2(16.5f, 117.5f),
            new Vec2(17.5f, 89.5f),
            new Vec2(23.5f, 62.5f),
            new Vec2(38.5f, 39.5f),
            new Vec2(61.5f, 24.5f),
            new Vec2(87.5f, 17.5f),
            new Vec2(114.5f, 15.5f),
            new Vec2(44.5f, 154.5f),
            new Vec2(41.5f, 127.5f),
            new Vec2(42.5f, 100.5f),
            new Vec2(48.5f, 74.5f),
            new Vec2(64.5f, 52.5f),
            new Vec2(90.5f, 44.5f),
            new Vec2(117.5f, 42.5f)
    };
    final static Vec2[] customColorCenters = {
            new Vec2(101.5f, 132.0f),
            new Vec2(113.5f, 118.0f),
            new Vec2(120.5f, 102.0f),
            new Vec2(124.5f, 084.0f),
            new Vec2(126.5f, 066.0f),
            new Vec2(097.5f, 152.0f),
            new Vec2(114.5f, 146.0f),
            new Vec2(127.5f, 133.0f),
            new Vec2(134.5f, 116.0f),
            new Vec2(139.5f, 098.0f),
            new Vec2(142.5f, 080.0f),
            new Vec2(144.5f, 062.0f),
    };
    final static Vec2 waterCenter = new Vec2(140.5f, 28.f);
    final static float basicColorRadius = 11.f;
    final static float customColorRadius = 6.5f;

    boolean isPickingColor = false;
    boolean isCarryingColor = false;
    boolean isCarryingWater = false;
    boolean canvasDirty = false;
    boolean paletteDirty = false;
    PaletteUtil.Color carriedColor;
    int carriedCustomColorId = -1;
    static PaletteUtil.Color currentColor = basicColors[0];
    final PaletteUtil.CustomColor[] customColors;
    final boolean[] basicColorFlags;
    boolean paletteComplete = false;
    boolean isCarryingPalette = false;

    BasePalette(Component titleIn, CompoundTag paletteTag) {
        super(titleIn);
        this.customColors = new PaletteUtil.CustomColor[12];
        this.basicColorFlags = new boolean[16];

        if (paletteTag != null && !paletteTag.isEmpty()) {
            if (paletteTag.contains("r") && paletteTag.contains("g") && paletteTag.contains("b")
                    && paletteTag.contains("m") && paletteTag.contains("n")) {
                readCustomColorArrayFromNBT(paletteTag, this.customColors);

            } else {
                for (int i = 0; i < customColors.length; i++) {
                    customColors[i] = new PaletteUtil.CustomColor();
                }
            }

            if (paletteTag.contains("basic")) {
                paletteComplete = true;
                byte[] basics = paletteTag.getByteArray("basic");
                for (int i = 0; i < basics.length; i++) {
                    basicColorFlags[i] = basics[i] > 0;
                    paletteComplete &= basicColorFlags[i];
                }
            }
        } else {
            for (int i = 0; i < customColors.length; i++) {
                customColors[i] = new PaletteUtil.CustomColor();
            }
        }
    }

    protected void superRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        RenderSystem.setShaderTexture(0, paletteTextures);

        // Draw basic colors
        for (int i = 0; i < basicColorFlags.length; i++) {
            int x = (int) paletteX + (int) basicColorCenters[i].x;
            int y = (int) paletteY + (int) basicColorCenters[i].y;
            int r = (int) basicColorRadius;
            if (basicColorFlags[i]) {
                guiGraphics.fill(x - r, y - r, x + r + 1, y + r + 1, basicColors[i].rgbVal());

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                guiGraphics.blit(paletteTextures, x - 8, y - 8, dyeSpriteX, i * dyeSpriteSize, dyeSpriteSize, dyeSpriteSize);
            } else {
                guiGraphics.fill(x - r, y - r, x + r + 1, y + r + 1, emptinessColor.rgbVal());
            }
        }

        // Draw custom colors
        for (int i = 0; i < customColors.length; i++) {
            int x = (int) paletteX + (int) customColorCenters[i].x;
            int y = (int) paletteY + (int) customColorCenters[i].y;
            guiGraphics.fill(x - 6, y - 7, x + 7, y + 6, customColors[i].getColor().rgbVal());
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(paletteTextures, (int) paletteX, (int) paletteY, 0, 0, paletteWidth, paletteHeight);

        // Draw color picker
        if (paletteComplete) {
            guiGraphics.blit(paletteTextures, (int) paletteX + colorPickerPosX, (int) paletteY + colorPickerPosY, colorPickerSpriteX, colorPickerSpriteY, colorPickerSize, colorPickerSize);
        }
    }

    protected boolean superMouseClicked(double posX, double posY, int mouseButton) {
        return super.mouseClicked(posX, posY, mouseButton);
    }

    protected boolean superMouseReleased(double posX, double posY, int mouseButton) {
        return super.mouseReleased(posX, posY, mouseButton);
    }

    protected boolean superMouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    // Mouse button 0: left, 1: right
    @Override
    public boolean mouseClicked(double posX, double posY, int mouseButton) {
        int mouseX = (int) Math.round(posX);
        int mouseY = (int) Math.round(posY);

        if (paletteClick(mouseX, mouseY)) {
            int x = (mouseX - (int) paletteX);
            int y = (mouseY - (int) paletteY);
            Vec2 clickVec = new Vec2(x, y);
            float sqrBasicRadius = basicColorRadius * basicColorRadius;
            float sqrCustomRadius = customColorRadius * customColorRadius;

            boolean didSomething = false;
            for (int i = 0; i < basicColorCenters.length; i++) {
                if (basicColorFlags[i] && sqrDist(clickVec, basicColorCenters[i]) <= sqrBasicRadius) {
                    if (mouseButton == 0) {
                        carriedColor = currentColor = basicColors[i];
                        setCarryingColor();
                        playSound(SoundEvents.MIX.get(), 0.6f);
                    }
                    didSomething = true;
                    break;
                }
            }

            if (!didSomething) {
                for (int i = 0; i < customColorCenters.length; i++) {
                    if (sqrDist(clickVec, customColorCenters[i]) <= sqrCustomRadius) {
                        if (mouseButton == 0) {
                            if (customColors[i].getNumberOfColors() > 0) {
                                carriedColor = currentColor = customColors[i].getColor();
                                carriedCustomColorId = i;
                                setCarryingColor();
                                playSound(SoundEvents.MIX.get(), 0.3f);
                            }
                        }
                        didSomething = true;
                        break;
                    }
                }
            }

            if (!didSomething) {
                if (sqrDist(clickVec, waterCenter) <= sqrCustomRadius) {
                    if (mouseButton == 0) {
                        setCarryingWater();
                        playSound(SoundEvents.WATER.get());
                        didSomething = true;
                    }
                }
            }

            if (!didSomething && paletteComplete && !isCarryingWater && !isCarryingColor) {
                if (inColorPicker(x, y)) {
                    if (mouseButton == 0) {
                        setPickingColor();
                        playSound(SoundEvents.COLOR_PICKER.get());
                        didSomething = true;
                    }
                }
            }

            if (!didSomething) {
                isCarryingPalette = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean inColorPicker(int x, int y) {
        return x >= colorPickerPosX && x < colorPickerPosX + colorPickerSize && y >= colorPickerPosY && y < colorPickerPosY + colorPickerSize;
    }

    protected boolean inWater(int x, int y) {
        return sqrDist(new Vec2(x, y), waterCenter) <= customColorRadius * customColorRadius;
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    protected void setCarryingWater() {
        isCarryingWater = true;
        isCarryingColor = false;
        isPickingColor = false;
    }

    protected void setCarryingColor() {
        isCarryingWater = false;
        isCarryingColor = true;
        isPickingColor = false;
    }

    protected void setPickingColor() {
        isCarryingWater = false;
        isCarryingColor = false;
        isPickingColor = true;
    }

    @Override
    public boolean mouseReleased(double posX, double posY, int mouseButton) {
        int mouseX = (int) Math.round(posX);
        int mouseY = (int) Math.round(posY);
        if (isCarryingColor || isCarryingWater) {
            if (paletteClick(mouseX, mouseY)) {
                float sqrCustomRadius = customColorRadius * customColorRadius;
                int x = (mouseX - (int) paletteX);
                int y = (mouseY - (int) paletteY);
                Vec2 clickVec = new Vec2(x, y);
                for (int i = 0; i < customColorCenters.length; i++) {
                    if (sqrDist(clickVec, customColorCenters[i]) <= sqrCustomRadius) {
                        PaletteUtil.CustomColor customColor = customColors[i];
                        if (isCarryingWater) {
                            customColor.reset();
                            playSound(SoundEvents.WATER_DROP.get());
                        } else {
                            if (carriedCustomColorId != i) {
                                customColor.mix(carriedColor);
                                currentColor = customColor.getColor();
                                playSound(SoundEvents.MIX.get());
                            }
                        }
                        paletteDirty = true;
                        break;
                    }
                }
            }
            isCarryingColor = false;
            isCarryingWater = false;
            carriedCustomColorId = -1;
        }
        isCarryingPalette = false;
        return super.mouseReleased(posX, posY, mouseButton);
    }

    protected void playSound(SoundInstance sound) {
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    protected void playSound(SoundEvent soundEvent) {
        playSound(soundEvent, 1.0f);
    }

    protected void playSound(SoundEvent soundEvent, float volume) {
        Minecraft m = Minecraft.getInstance();
        if (m.level != null && m.player != null) {
            m.getSoundManager().play(new SimpleSoundInstance(soundEvent, SoundSource.MASTER, volume,
                    0.8f + m.level.random.nextFloat() * 0.4f, m.player.getRandom(), m.player.blockPosition()));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    boolean paletteClick(int x, int y) {
        return x <= paletteX + paletteWidth && x >= paletteX && y <= paletteY + paletteHeight && y >= paletteY;
    }

    float sqrDist(Vec2 a, Vec2 b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }
}
