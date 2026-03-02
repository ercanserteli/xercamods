package xerca.xercapaint.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.SoundEvents;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

import static xerca.xercapaint.PaletteUtil.emptinessColor;

public abstract class BasePalette extends Screen {
    protected static final ResourceLocation paletteTextures = Mod.id("textures/gui/palette.png");
    static final int DYE_SPRITE_X = 240;
    static final int DYE_SPRITE_SIZE = 16;
    static final int BRUSH_SPRITE_X = 0;
    static final int BRUSH_SPRITE_Y = 247;
    static final int BRUSH_SPRITE_SIZE = 9;
    static final int BRUSH_OPACITY_SPRITE_X = 196;
    static final int BRUSH_OPACITY_SPRITE_Y = 197;
    static final int BRUSH_OPACITY_SPRITE_SIZE = 14;
    static final int DROP_SPRITE_WIDTH = 6;
    static final int PALETTE_WIDTH = 157;
    static final int PALETTE_HEIGHT = 193;
    static final int COLOR_PICKER_SPRITE_X = 25;
    static final int COLOR_PICKER_SPRITE_Y = 242;
    static final int COLOR_PICKER_POS_X = 98;
    static final int COLOR_PICKER_POS_Y = 62;
    static final int COLOR_PICKER_SIZE = 14;

    static final double[] paletteXs = {-1000, -1000, -1000, -1000, -1000};
    static final double[] paletteYs = {-1000, -1000, -1000, -1000, -1000};
    double paletteX;
    double paletteY;
    static final PaletteUtil.Color WATER_COLOR = new PaletteUtil.Color(53, 118, 191);

    static final PaletteUtil.Color[] BASIC_COLORS = {
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
    static final Vec2[] BASIC_COLOR_CENTERS = {
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
    static final Vec2[] CUSTOM_COLOR_CENTERS = {
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
    static final Vec2 WATER_CENTER = new Vec2(140.5f, 28.f);
    static final float BASIC_COLOR_RADIUS = 11.f;
    static final float CUSTOM_COLOR_RADIUS = 6.5f;

    boolean isPickingColor = false;
    boolean isCarryingColor = false;
    boolean isCarryingWater = false;
    boolean canvasDirty = false;
    boolean paletteDirty = false;
    PaletteUtil.Color carriedColor;
    int carriedCustomColorId = -1;
    PaletteUtil.Color currentColor = BASIC_COLORS[0];
    final PaletteUtil.CustomColor[] customColors;
    final boolean[] basicColorFlags;
    boolean paletteComplete = false;
    boolean isCarryingPalette = false;

    BasePalette(Component titleIn, ItemStack paletteStack) {
        super(titleIn);
        this.basicColorFlags = new boolean[16];

        ItemPalette.ComponentCustomColor componentCustomColor = paletteStack.get(Items.PALETTE_CUSTOM_COLORS);
        if (componentCustomColor != null) {
            this.customColors = componentCustomColor.colors;
        } else {
            this.customColors = new PaletteUtil.CustomColor[12];
            for (int i = 0; i < customColors.length; i++) {
                customColors[i] = new PaletteUtil.CustomColor();
            }
        }

        byte[] basics = paletteStack.get(Items.PALETTE_BASIC_COLORS);
        if (basics != null) {
            paletteComplete = true;
            for (int i = 0; i < basics.length; i++) {
                basicColorFlags[i] = basics[i] > 0;
                paletteComplete &= basicColorFlags[i];
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
            int x = (int) paletteX + (int) BASIC_COLOR_CENTERS[i].x;
            int y = (int) paletteY + (int) BASIC_COLOR_CENTERS[i].y;
            int r = (int) BASIC_COLOR_RADIUS;
            if (basicColorFlags[i]) {
                guiGraphics.fill(x - r, y - r, x + r + 1, y + r + 1, BASIC_COLORS[i].rgbVal());

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                guiGraphics.blit(paletteTextures, x - 8, y - 8, DYE_SPRITE_X, i * DYE_SPRITE_SIZE, DYE_SPRITE_SIZE, DYE_SPRITE_SIZE);
            } else {
                guiGraphics.fill(x - r, y - r, x + r + 1, y + r + 1, emptinessColor.rgbVal());
            }
        }

        // Draw custom colors
        for (int i = 0; i < customColors.length; i++) {
            int x = (int) paletteX + (int) CUSTOM_COLOR_CENTERS[i].x;
            int y = (int) paletteY + (int) CUSTOM_COLOR_CENTERS[i].y;
            guiGraphics.fill(x - 6, y - 7, x + 7, y + 6, customColors[i].getColor().rgbVal());
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(paletteTextures, (int) paletteX, (int) paletteY, 0, 0, PALETTE_WIDTH, PALETTE_HEIGHT);

        // Draw color picker
        if (paletteComplete) {
            guiGraphics.blit(paletteTextures, (int) paletteX + COLOR_PICKER_POS_X, (int) paletteY + COLOR_PICKER_POS_Y, COLOR_PICKER_SPRITE_X, COLOR_PICKER_SPRITE_Y, COLOR_PICKER_SIZE, COLOR_PICKER_SIZE);
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
            float sqrBasicRadius = BASIC_COLOR_RADIUS * BASIC_COLOR_RADIUS;
            float sqrCustomRadius = CUSTOM_COLOR_RADIUS * CUSTOM_COLOR_RADIUS;

            boolean didSomething = false;
            for (int i = 0; i < BASIC_COLOR_CENTERS.length; i++) {
                if (basicColorFlags[i] && sqrDist(clickVec, BASIC_COLOR_CENTERS[i]) <= sqrBasicRadius) {
                    if (mouseButton == 0) {
                        carriedColor = currentColor = BASIC_COLORS[i];
                        setCarryingColor();
                        playSound(SoundEvents.MIX, 0.6f);
                    }
                    didSomething = true;
                    break;
                }
            }

            if (!didSomething) {
                for (int i = 0; i < CUSTOM_COLOR_CENTERS.length; i++) {
                    if (sqrDist(clickVec, CUSTOM_COLOR_CENTERS[i]) <= sqrCustomRadius) {
                        if (mouseButton == 0 && customColors[i].getNumberOfColors() > 0) {
                            carriedColor = currentColor = customColors[i].getColor();
                            carriedCustomColorId = i;
                            setCarryingColor();
                            playSound(SoundEvents.MIX, 0.3f);
                        }
                        didSomething = true;
                        break;
                    }
                }
            }

            if (!didSomething && sqrDist(clickVec, WATER_CENTER) <= sqrCustomRadius && mouseButton == 0) {
                setCarryingWater();
                playSound(SoundEvents.WATER);
                didSomething = true;
            }

            if (!didSomething && paletteComplete && !isCarryingWater && !isCarryingColor && inColorPicker(x, y) && mouseButton == 0) {
                setPickingColor();
                playSound(SoundEvents.COLOR_PICKER);
                didSomething = true;
            }

            if (!didSomething) {
                isCarryingPalette = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean inColorPicker(int x, int y) {
        return x >= COLOR_PICKER_POS_X && x < COLOR_PICKER_POS_X + COLOR_PICKER_SIZE && y >= COLOR_PICKER_POS_Y && y < COLOR_PICKER_POS_Y + COLOR_PICKER_SIZE;
    }

    protected boolean inWater(int x, int y) {
        return sqrDist(new Vec2(x, y), WATER_CENTER) <= CUSTOM_COLOR_RADIUS * CUSTOM_COLOR_RADIUS;
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
                float sqrCustomRadius = CUSTOM_COLOR_RADIUS * CUSTOM_COLOR_RADIUS;
                int x = (mouseX - (int) paletteX);
                int y = (mouseY - (int) paletteY);
                Vec2 clickVec = new Vec2(x, y);
                for (int i = 0; i < CUSTOM_COLOR_CENTERS.length; i++) {
                    if (sqrDist(clickVec, CUSTOM_COLOR_CENTERS[i]) <= sqrCustomRadius) {
                        PaletteUtil.CustomColor customColor = customColors[i];
                        if (isCarryingWater) {
                            customColor.reset();
                            playSound(SoundEvents.WATER_DROP);
                        } else {
                            if (carriedCustomColorId != i) {
                                customColor.mix(carriedColor);
                                currentColor = customColor.getColor();
                                playSound(SoundEvents.MIX);
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
        return x <= paletteX + PALETTE_WIDTH && x >= paletteX && y <= paletteY + PALETTE_HEIGHT && y >= paletteY;
    }

    float sqrDist(Vec2 a, Vec2 b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }
}
