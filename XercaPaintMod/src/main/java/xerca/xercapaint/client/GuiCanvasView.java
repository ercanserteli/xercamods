package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.Items;

import java.util.List;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class GuiCanvasView extends Screen {
    private int canvasX;
    private int canvasY = 50;
    private final int canvasWidth;
    private final int canvasPixelScale;
    private final int canvasPixelWidth;
    private final int canvasPixelHeight;
    private final CanvasType canvasType;
    private final boolean glass;

    private int @Nullable [] pixels;
    private final boolean sidesActive;
    private int @Nullable [] sidePixels;
    private @Nullable String authorName = "";
    private String canvasTitle = "";
    private int generation;
    private final @Nullable EntityEasel easel;
    private final @Nullable Player player;

    protected GuiCanvasView(ItemStack canvasStack, Component title, CanvasType canvasType, boolean glass, @Nullable EntityEasel easel) {
        super(title);

        this.canvasType = canvasType;
        this.glass = glass;
        this.canvasPixelScale = canvasType == CanvasType.SMALL ? 10 : 5;
        this.canvasPixelWidth = CanvasType.getWidth(canvasType);
        this.canvasPixelHeight = CanvasType.getHeight(canvasType);
        this.canvasWidth = this.canvasPixelWidth * this.canvasPixelScale;
        this.easel = easel;
        this.player = Minecraft.getInstance().player;

        List<Integer> stackPixels = canvasStack.get(Items.CANVAS_PIXELS);
        if (stackPixels != null) {
            this.authorName = canvasStack.get(Items.CANVAS_AUTHOR);
            this.canvasTitle = canvasStack.getOrDefault(Items.CANVAS_TITLE, "");
            this.generation = canvasStack.getOrDefault(Items.CANVAS_GENERATION, 0);

            this.pixels = stackPixels.stream().mapToInt(i -> i).toArray();
        }

        this.sidesActive = canvasStack.getOrDefault(Items.CANVAS_SIDES_ACTIVE, false);
        List<Integer> stackSidePixels = canvasStack.get(Items.CANVAS_SIDE_PIXELS);
        if (stackSidePixels != null && stackSidePixels.size() == CanvasSides.count(canvasType)) {
            this.sidePixels = stackSidePixels.stream().mapToInt(i -> i).toArray();
        }
    }

    private int getSidePixel(int index) {
        if (sidePixels != null && index >= 0 && index < sidePixels.length) {
            return sidePixels[index];
        }
        return CanvasSides.DEFAULT_COLOR;
    }

    @Override
    public void init() {
        canvasX = (this.width - canvasWidth) / 2;
        if (canvasType == CanvasType.LONG) {
            canvasY += 40;
        }
    }

    private int getPixelAt(int x, int y) {
        return (this.pixels == null) ? 0xFFF9FFFE : this.pixels[y * canvasPixelWidth + x];
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final int CHECKER_LIGHT = 0xFFBFBFBF;
    private static final int CHECKER_DARK = 0xFF7F7F7F;

    private void fillChecker(GuiGraphicsExtractor guiGraphics, int x, int y, int parity) {
        guiGraphics.fill(x, y, x + canvasPixelScale, y + canvasPixelScale, (parity & 1) == 0 ? CHECKER_LIGHT : CHECKER_DARK);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float f) {
        if (glass) {
            for (int i = 0; i < canvasPixelHeight; i++) {
                for (int j = 0; j < canvasPixelWidth; j++) {
                    fillChecker(guiGraphics, canvasX + j * canvasPixelScale, canvasY + i * canvasPixelScale, i + j);
                }
            }
        }

        for (int i = 0; i < canvasPixelHeight; i++) {
            for (int j = 0; j < canvasPixelWidth; j++) {
                int x = canvasX + j * canvasPixelScale;
                int y = canvasY + i * canvasPixelScale;
                guiGraphics.fill(x, y, x + canvasPixelScale, y + canvasPixelScale, getPixelAt(j, i));
            }
        }

        if (sidesActive) {
            int scale = canvasPixelScale;
            int canvasHeight = canvasPixelHeight * scale;
            for (int k = 0; k < canvasPixelWidth; k++) {
                int x = canvasX + k * scale;
                if (glass) {
                    fillChecker(guiGraphics, x, canvasY - scale, k - 1);
                    fillChecker(guiGraphics, x, canvasY + canvasHeight, k + canvasPixelHeight);
                }
                guiGraphics.fill(x, canvasY - scale, x + scale, canvasY, getSidePixel(CanvasSides.TOP_OFFSET + k));
                guiGraphics.fill(x, canvasY + canvasHeight, x + scale, canvasY + canvasHeight + scale, getSidePixel(CanvasSides.bottomOffset(canvasType) + k));
            }
            for (int i = 0; i < canvasPixelHeight; i++) {
                int y = canvasY + i * scale;
                if (glass) {
                    fillChecker(guiGraphics, canvasX - scale, y, i - 1);
                    fillChecker(guiGraphics, canvasX + canvasWidth, y, i + canvasPixelWidth);
                }
                guiGraphics.fill(canvasX - scale, y, canvasX, y + scale, getSidePixel(CanvasSides.leftOffset(canvasType) + i));
                guiGraphics.fill(canvasX + canvasWidth, y, canvasX + canvasWidth + scale, y + scale, getSidePixel(CanvasSides.rightOffset(canvasType) + i));
            }
        }

        if (generation > 0 && !canvasTitle.isEmpty()) {
            String title = canvasTitle + " " + I18n.get("canvas.byAuthor", authorName);
            String gen = "(" + I18n.get("canvas.generation." + (generation - 1)) + ")";

            int titleWidth = this.font.width(title);
            int genWidth = this.font.width(gen);

            float titleX = (canvasX + (canvasWidth - titleWidth) / 2.0f);
            float genX = (canvasX + (canvasWidth - genWidth) / 2.0f);
            float minX = Math.min(genX, titleX);
            float maxX = Math.max(genX + genWidth, titleX + titleWidth);

            guiGraphics.fill((int) (minX - 10), canvasY - 40, (int) (maxX + 10), canvasY - 14, 0xFFEEEEEE);

            guiGraphics.text(font, title, (int) titleX, (canvasY - 35), 0xFF111111, false);
            guiGraphics.text(font, gen, (int) genX, canvasY - 24, 0xFF444444, false);
        }
    }

    @Override
    public void tick() {
        if (easel != null && player != null
                && (easel.getItem().isEmpty() || easel.isRemoved() || easel.distanceToSqr(player) > 64)) {
            this.onClose();
        }
        super.tick();
    }
}
