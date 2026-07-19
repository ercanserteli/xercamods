package xerca.xercapaint.client;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.Nullable;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.SoundEvents;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.CanvasMiniUpdatePacket;
import xerca.xercapaint.packets.CanvasUpdatePacket;
import xerca.xercapaint.packets.EaselLeftPacket;
import xerca.xercapaint.packets.PaletteUpdatePacket;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class GuiCanvasEdit extends BasePalette {
    private static final int BRUSH_LEVEL_COUNT = 4;
    private static final int SMALL_CANVAS_PIXEL_SCALE = 10;
    private static final int MAX_TITLE_LENGTH = 16;
    private static final int MAX_EASEL_DISTANCE_SQR = 64;
    private static final int MINI_UPDATE_INTERVAL_TICKS = 10;
    private static final int POSITION_UNSET = -1000;

    private double canvasX;
    private double canvasY;
    private static final double[] CANVAS_XS = {-1000, -1000, -1000, -1000};
    private static final double[] CANVAS_YS = {-1000, -1000, -1000, -1000};
    private final int canvasWidth;
    private final int canvasHeight;
    private int brushMeterX;
    private int brushMeterY;
    private int brushOpacityMeterX;
    private int brushOpacityMeterY;
    private final int canvasPixelScale;
    private final int canvasPixelWidth;
    private final int canvasPixelHeight;
    private int brushSize;
    private boolean touchedCanvas;
    private boolean undoStarted;
    private boolean gettingSigned;
    private boolean isCarryingCanvas;
    private @Nullable Button buttonSign;
    private @Nullable Button buttonCancel;
    private @Nullable Button buttonFinalize;
    private int updateCount;
    private @Nullable BrushSound brushSound;
    private static final int CANVAS_HOLDER_HEIGHT = 10;
    private int brushOpacitySetting;
    private static final float[] BRUSH_OPACITIES = {1.f, 0.75f, 0.5f, 0.25f};
    private static boolean showHelp;
    private final Set<Integer> draggedPoints = new HashSet<>();

    private static final int SIDES_TOGGLE_SIZE = 8;
    private boolean sidesActive;
    private int @Nullable [] sidePixels;
    private int sidesToggleX;
    private int sidesToggleY;

    private final Player editingPlayer;

    private final CanvasType canvasType;
    private final boolean glass;
    private boolean isSigned;
    private int[] pixels;
    private String canvasTitle = "";
    private final String canvasId;
    private int version;
    private final @Nullable EntityEasel easel;
    private int timeSinceLastUpdate;
    private boolean skippedUpdate;

    private static final Vec2[] OUTLINE_POSS_1 = {
            new Vec2(0.f, 199.0f),
            new Vec2(12.f, 199.0f),
            new Vec2(34.f, 199.0f),
            new Vec2(76.f, 199.0f),
    };

    private static final Vec2[] OUTLINE_POSS_2 = {
            new Vec2(128.f, 199.0f),
            new Vec2(135.f, 199.0f),
            new Vec2(147.f, 199.0f),
            new Vec2(169.f, 199.0f),
    };

    private static final int MAX_UNDO_LENGTH = 16;
    private final Deque<Snapshot> undoStack = new ArrayDeque<>(MAX_UNDO_LENGTH);

    /**
     * A snapshot of the editable canvas state for both front and side pixels
     */
    private record Snapshot(int[] pixels, int[] sidePixels) {
    }

    protected GuiCanvasEdit(Player player, ItemStack canvasStack, ItemStack paletteStack, Component title, CanvasType canvasType, boolean glass, @Nullable EntityEasel easel) {
        super(title, paletteStack);
        updateCount = 0;

        this.canvasType = canvasType;
        this.glass = glass;
        this.canvasPixelScale = canvasType == CanvasType.SMALL ? 10 : 5;
        this.canvasPixelWidth = CanvasType.getWidth(canvasType);
        this.canvasPixelHeight = CanvasType.getHeight(canvasType);
        int canvasPixelArea = canvasPixelHeight * canvasPixelWidth;
        this.canvasWidth = this.canvasPixelWidth * this.canvasPixelScale;
        this.canvasHeight = this.canvasPixelHeight * this.canvasPixelScale;
        this.easel = easel;

        this.editingPlayer = player;
        List<Integer> stackPixels = canvasStack.get(Items.CANVAS_PIXELS);
        String stackCanvasId = canvasStack.get(Items.CANVAS_ID);
        if (stackPixels != null && stackCanvasId != null) {
            this.pixels = stackPixels.stream().mapToInt(i -> i).toArray();
            this.canvasId = stackCanvasId;
            this.version = canvasStack.getOrDefault(Items.CANVAS_VERSION, 1);

            canvasTitle = canvasStack.getOrDefault(Items.CANVAS_TITLE, "");
            isSigned = !canvasTitle.isEmpty();
        } else {
            this.pixels = new int[canvasPixelArea];
            Arrays.fill(this.pixels, glass ? 0 : BASIC_COLORS[15].rgbVal());

            this.canvasId = ItemCanvas.generateName(player);
        }

        this.sidesActive = canvasStack.getOrDefault(Items.CANVAS_SIDES_ACTIVE, false);
        List<Integer> stackSidePixels = canvasStack.get(Items.CANVAS_SIDE_PIXELS);
        if (stackSidePixels != null && stackSidePixels.size() == CanvasSides.count(canvasType)) {
            this.sidePixels = stackSidePixels.stream().mapToInt(i -> i).toArray();
        }
    }

    private void ensureSidePixels() {
        if (sidePixels == null || sidePixels.length != CanvasSides.count(canvasType)) {
            sidePixels = CanvasSides.defaultPixels(canvasType, glass);
        }
    }

    private int getSidePixel(int index) {
        if (sidePixels != null && index >= 0 && index < sidePixels.length) {
            return sidePixels[index];
        }
        return CanvasSides.DEFAULT_COLOR;
    }

    private int sideMargin() {
        return sidesActive ? canvasPixelScale : 0;
    }

    @Override
    public void init() {
        int typeIndex = canvasType.toByte();
        canvasX = CANVAS_XS[typeIndex];
        canvasY = CANVAS_YS[typeIndex];
        paletteX = PALETTE_XS[typeIndex];
        paletteY = PALETTE_YS[typeIndex];
        if (canvasX == POSITION_UNSET || canvasY == POSITION_UNSET || paletteX == POSITION_UNSET || paletteY == POSITION_UNSET) {
            resetPositions();
        }

        updateCanvasPos(0, 0);
        updatePalettePos(0, 0);

        Window window = minecraft.getWindow();

        // Hide mouse cursor
        glfwSetInputMode(window.handle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

        int x = window.getGuiScaledWidth() - 120;
        int y = window.getGuiScaledHeight() - 30;
        this.buttonSign = this.addRenderableWidget(Button.builder(Component.translatable("canvas.signButton"), button -> {
            if (!isSigned) {
                gettingSigned = true;
                resetPositions();
                updateButtons();

                glfwSetInputMode(window.handle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            }
        }).bounds(x, y, 98, 20).build());
        this.buttonFinalize = this.addRenderableWidget(Button.builder(Component.translatable("canvas.finalizeButton"), button -> {
            if (!isSigned) {
                canvasDirty = true;
                isSigned = true;
                minecraft.setScreen(null);
            }

        }).bounds((int) canvasX - 100, 100, 98, 20).build());
        this.buttonCancel = this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
            if (!isSigned) {
                gettingSigned = false;
                updateButtons();

                glfwSetInputMode(window.handle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            }
        }).bounds((int) canvasX - 100, 130, 98, 20).build());

        x = (int) (window.getGuiScaledWidth() * 0.95) - 21;
        y = (int) (window.getGuiScaledHeight() * 0.05);
        ToggleHelpButton toggleHelpButton = this.addRenderableWidget(new ToggleHelpButton(x, y, 21, 21, 197, 0, 21,
                PALETTE_TEXTURES, 256, 256, button -> showHelp = !showHelp));
        toggleHelpButton.setTooltip(Tooltip.create(Component.translatable("canvas.help.toggleHelp")));

        updateButtons();
    }

    private void updateButtons() {
        if (!this.isSigned) {
            if (this.buttonSign != null) {
                this.buttonSign.visible = !this.gettingSigned;
            }
            if (this.buttonCancel != null) {
                this.buttonCancel.visible = this.gettingSigned;
                this.buttonCancel.setX((int) canvasX - 100);
            }
            if (this.buttonFinalize != null) {
                this.buttonFinalize.visible = this.gettingSigned;
                this.buttonFinalize.active = !this.canvasTitle.trim().isEmpty();
                this.buttonFinalize.setX((int) canvasX - 100);
            }
        }
    }

    private int getPixelAt(int x, int y) {
        return this.pixels[y * canvasPixelWidth + x];
    }

    private static final int[][][] BRUSH_OFFSETS = {
            {{0, 0}},
            {{0, 0}, {-1, 0}, {0, -1}, {-1, -1}},
            {{-1, 1}, {0, 1}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-1, -2}, {0, -2}},
            {{-1, 2}, {0, 2}, {1, 2}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}, {2, 1}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {2, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {2, -1}, {-1, -2}, {0, -2}, {1, -2}}
    };
    /**
     * Whether each brush anchors on the nearest grid corner (true) or the cell under the cursor (false).
     */
    private static final boolean[] BRUSH_CORNER_ANCHOR = {false, true, true, false};
    /**
     * The smallest offset and the span (in cells) of each brush's bounding box, used to draw the outline.
     */
    private static final int[] BRUSH_MIN_OFFSET = {0, -1, -2, -2};
    private static final int[] BRUSH_SPAN = {1, 2, 4, 5};

    private int brushAnchor(int mousePos, int origin, boolean corner) {
        int rel = mousePos - origin + (corner ? canvasPixelScale / 2 : 0);
        return Math.floorDiv(rel, canvasPixelScale);
    }

    /**
     * Maps a grid cell that lies outside the canvas to its side-pixel index, or -1 if it is not a side cell.
     */
    private int sideIndexFor(int col, int row) {
        int w = canvasPixelWidth;
        int h = canvasPixelHeight;
        if (row == -1 && col >= 0 && col < w) {
            return CanvasSides.TOP_OFFSET + col;
        }
        if (row == h && col >= 0 && col < w) {
            return CanvasSides.bottomOffset(canvasType) + col;
        }
        if (col == -1 && row >= 0 && row < h) {
            return CanvasSides.leftOffset(canvasType) + row;
        }
        if (col == w && row >= 0 && row < h) {
            return CanvasSides.rightOffset(canvasType) + row;
        }
        return -1;
    }

    private void paintCell(int col, int row, PaletteUtil.Color color, float opacity, boolean erase) {
        boolean onCanvas = col >= 0 && col < canvasPixelWidth && row >= 0 && row < canvasPixelHeight;
        int sideIndex = -1;
        if (!onCanvas) {
            if (!sidesActive) {
                return;
            }
            sideIndex = sideIndexFor(col, row);
            if (sideIndex < 0) {
                return;
            }
        }
        int key = onCanvas ? row * canvasPixelWidth + col : canvasPixelWidth * canvasPixelHeight + sideIndex;
        if (!draggedPoints.add(key)) {
            return;
        }
        if (onCanvas) {
            int i = row * canvasPixelWidth + col;
            pixels[i] = blendPixel(pixels[i], color, opacity, erase);
        } else {
            ensureSidePixels();
            if (sidePixels == null) {
                return;
            }
            sidePixels[sideIndex] = blendPixel(sidePixels[sideIndex], color, opacity, erase);
        }
    }

    /**
     * Combines a brush stroke with an existing pixel; glass uses binary transparency (erase clears, paint is opaque).
     */
    private int blendPixel(int old, PaletteUtil.Color color, float opacity, boolean erase) {
        if (glass) {
            if (erase) {
                return 0;
            }
            if (((old >> 24) & 0xFF) == 0) {
                return color.rgbVal();
            }
        }
        return PaletteUtil.Color.mix(color, new PaletteUtil.Color(old), opacity).rgbVal();
    }

    private void paintAt(int mouseX, int mouseY, int mouseButton) {
        touchedCanvas = true;
        boolean erase = mouseButton == GLFW_MOUSE_BUTTON_RIGHT;
        PaletteUtil.Color color = erase ? PaletteUtil.Color.WHITE : currentColor;
        float opacity = erase ? 1.0f : BRUSH_OPACITIES[brushOpacitySetting];
        boolean corner = BRUSH_CORNER_ANCHOR[brushSize];
        int anchorCol = brushAnchor(mouseX, (int) canvasX, corner);
        int anchorRow = brushAnchor(mouseY, (int) canvasY, corner);
        for (int[] offset : BRUSH_OFFSETS[brushSize]) {
            paintCell(anchorCol + offset[0], anchorRow + offset[1], color, opacity, erase);
        }
        canvasDirty = true;
    }

    private boolean overSide(int mouseX, int mouseY) {
        if (!sidesActive) {
            return false;
        }
        int col = Math.floorDiv(mouseX - (int) canvasX, canvasPixelScale);
        int row = Math.floorDiv(mouseY - (int) canvasY, canvasPixelScale);
        return sideIndexFor(col, row) >= 0;
    }

    private boolean inPaintable(int mouseX, int mouseY) {
        return inCanvas(mouseX, mouseY) || overSide(mouseX, mouseY);
    }

    /**
     * Returns the color of the canvas/side cell under the cursor, or null if it is not a paintable cell.
     */
    private @Nullable Integer cellColorAt(int mouseX, int mouseY) {
        int col = Math.floorDiv(mouseX - (int) canvasX, canvasPixelScale);
        int row = Math.floorDiv(mouseY - (int) canvasY, canvasPixelScale);
        if (col >= 0 && col < canvasPixelWidth && row >= 0 && row < canvasPixelHeight) {
            return pixels[row * canvasPixelWidth + col];
        }
        if (sidesActive) {
            int sideIndex = sideIndexFor(col, row);
            if (sideIndex >= 0) {
                return getSidePixel(sideIndex);
            }
        }
        return null;
    }

    private void resetPositions() {
        final int padding = 40;
        final int paletteCanvasX = (this.width - (PALETTE_WIDTH + canvasWidth + padding)) / 2;
        canvasX = (double) paletteCanvasX + PALETTE_WIDTH + padding;
        if (canvasType == CanvasType.LONG) {
            canvasY = 80;
        } else {
            canvasY = 40;
        }

        paletteX = paletteCanvasX;
        paletteY = 40;
    }

    @Override
    public void tick() {
        ++this.updateCount;
        ++this.timeSinceLastUpdate;

        if (easel != null) {
            if (easel.getItem().isEmpty() || easel.isRemoved() || easel.distanceToSqr(editingPlayer) > MAX_EASEL_DISTANCE_SQR) {
                this.onClose();
            }
            if (skippedUpdate && timeSinceLastUpdate > 20 && canvasDirty) {
                updateCanvas(false);
                skippedUpdate = false;
            }
        }

        super.tick();
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor guiGraphics) {
        // Skip vanilla's world-blur behind the painting GUI so the scene stays crisp
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float f) {
        if (!gettingSigned) {
            super.extractRenderState(guiGraphics, mouseX, mouseY, f);
        } else {
            super.superRender(guiGraphics, mouseX, mouseY, f);
        }

        // Draw the canvas holder
        int holderMargin = sideMargin();
        guiGraphics.fill((int) (canvasX + canvasWidth * 0.25), (int) canvasY - CANVAS_HOLDER_HEIGHT - holderMargin, (int) (canvasX + canvasWidth * 0.75), (int) canvasY - holderMargin, 0xffe1e1e1);

        // For glass canvases, draw a transparency checkerboard so empty cells are visible
        if (glass) {
            for (int i = 0; i < canvasPixelHeight; i++) {
                for (int j = 0; j < canvasPixelWidth; j++) {
                    fillChecker(guiGraphics, (int) canvasX + j * canvasPixelScale, (int) canvasY + i * canvasPixelScale, i + j);
                }
            }
        }

        // Draw the canvas
        for (int i = 0; i < canvasPixelHeight; i++) {
            for (int j = 0; j < canvasPixelWidth; j++) {
                int y = (int) canvasY + i * canvasPixelScale;
                int x = (int) canvasX + j * canvasPixelScale;
                guiGraphics.fill(x, y, x + canvasPixelScale, y + canvasPixelScale, getPixelAt(j, i));
            }
        }

        if (!gettingSigned) {
            // Draw the paintable sides and the toggle button
            if (sidesActive) {
                drawSideLines(guiGraphics);
            }
            drawSidesToggle(guiGraphics);

            // Draw brush meter
            for (int i = 0; i < 4; i++) {
                int y = brushMeterY + i * BRUSH_SPRITE_SIZE;
                guiGraphics.fill(brushMeterX, y, brushMeterX + 3, y + 3, currentColor.rgbVal());
            }
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, brushMeterX, brushMeterY + (3 - brushSize) * BRUSH_SPRITE_SIZE, 15, 246, 10, 10, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, brushMeterX, brushMeterY, BRUSH_SPRITE_X, BRUSH_SPRITE_Y - BRUSH_SPRITE_SIZE * 3, BRUSH_SPRITE_SIZE, BRUSH_SPRITE_SIZE * 4, 256, 256);

            // Draw opacity meter
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, brushOpacityMeterX, brushOpacityMeterY, BRUSH_OPACITY_SPRITE_X, BRUSH_OPACITY_SPRITE_Y, BRUSH_OPACITY_SPRITE_SIZE, BRUSH_OPACITY_SPRITE_SIZE * 4 + 3, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, brushOpacityMeterX - 1, brushOpacityMeterY - 1 + brushOpacitySetting * (BRUSH_OPACITY_SPRITE_SIZE + 1), 212, 240, 16, 16, 256, 256);

            // Draw brush and outline
            renderCursor(guiGraphics, mouseX, mouseY);

            if (showHelp) {
                if (inBrushMeter(mouseX, mouseY)) {
                    int selectedSize = 3 - (mouseY - brushMeterY) / BRUSH_SPRITE_SIZE;
                    if (selectedSize <= 3 && selectedSize >= 0) {
                        guiGraphics.setTooltipForNextFrame(font, Component.translatable("canvas.help.brushSize", selectedSize + 1), mouseX, mouseY);
                    }
                } else if (inBrushOpacityMeter(mouseX, mouseY)) {
                    int relativeY = mouseY - brushOpacityMeterY;
                    int selectedOpacity = relativeY / (BRUSH_OPACITY_SPRITE_SIZE + 1);
                    if (selectedOpacity >= 0 && selectedOpacity <= 3) {
                        int percentage = 100 - 25 * selectedOpacity;
                        guiGraphics.setTooltipForNextFrame(font, Component.translatable("canvas.help.brushOpacity", percentage), mouseX, mouseY);
                    }
                } else if (inColorPicker(mouseX - (int) paletteX, mouseY - (int) paletteY)) {
                    guiGraphics.setComponentTooltipForNextFrame(font, Arrays.asList(Component.translatable("canvas.help.colorPicker"),
                            Component.translatable("canvas.help.colorPicker.desc").withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
                } else if (inWater(mouseX - (int) paletteX, mouseY - (int) paletteY)) {
                    guiGraphics.setComponentTooltipForNextFrame(font, Arrays.asList(Component.translatable("canvas.help.colorRemover"),
                            Component.translatable("canvas.help.colorRemover.desc").withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
                } else if (inCanvasHolder(mouseX, mouseY)) {
                    guiGraphics.setComponentTooltipForNextFrame(font, Arrays.asList(Component.translatable("canvas.help.canvasHolder"),
                            Component.translatable("canvas.help.canvasHolder.desc").withStyle(ChatFormatting.GRAY)), mouseX, mouseY);
                } else if (inSidesToggle(mouseX, mouseY)) {
                    guiGraphics.setTooltipForNextFrame(font, Component.translatable("canvas.help.toggleSides"), mouseX, mouseY);
                }
            }
        } else {
            drawSigning(guiGraphics);
        }
    }

    private void renderCursor(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (isCarryingColor && carriedColor != null) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, mouseX - BRUSH_SPRITE_SIZE / 2, mouseY - BRUSH_SPRITE_SIZE / 2, BRUSH_SPRITE_X + BRUSH_SPRITE_SIZE, BRUSH_SPRITE_Y, DROP_SPRITE_WIDTH, BRUSH_SPRITE_SIZE, 256, 256, carriedColor.rgbVal());

        } else if (isCarryingWater) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, mouseX - BRUSH_SPRITE_SIZE / 2, mouseY - BRUSH_SPRITE_SIZE / 2, BRUSH_SPRITE_X + BRUSH_SPRITE_SIZE, BRUSH_SPRITE_Y, DROP_SPRITE_WIDTH, BRUSH_SPRITE_SIZE, 256, 256, WATER_COLOR.rgbVal());
        } else if (isPickingColor) {
            drawOutline(guiGraphics, mouseX, mouseY, 0);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, mouseX, mouseY - COLOR_PICKER_SIZE, COLOR_PICKER_SPRITE_X, COLOR_PICKER_SPRITE_Y, COLOR_PICKER_SIZE, COLOR_PICKER_SIZE, 256, 256);
        } else {
            drawOutline(guiGraphics, mouseX, mouseY, brushSize);

            guiGraphics.fill(mouseX, mouseY, mouseX + 3, mouseY + 3, currentColor.rgbVal());

            int trueBrushY = BRUSH_SPRITE_Y - BRUSH_SPRITE_SIZE * brushSize;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, mouseX, mouseY, BRUSH_SPRITE_X, trueBrushY, BRUSH_SPRITE_SIZE, BRUSH_SPRITE_SIZE, 256, 256);
        }
    }

    private void drawOutline(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int brushSize) {
        // The outline is the brush stamp's bounding box, so it follows the cursor onto the sides too.
        if (!inPaintable(mouseX, mouseY)) {
            return;
        }
        boolean corner = BRUSH_CORNER_ANCHOR[brushSize];
        int anchorCol = brushAnchor(mouseX, (int) canvasX, corner);
        int anchorRow = brushAnchor(mouseY, (int) canvasY, corner);
        int minOffset = BRUSH_MIN_OFFSET[brushSize];
        int outlineSize = BRUSH_SPAN[brushSize] * canvasPixelScale + 2;
        int x = (anchorCol + minOffset) * canvasPixelScale + (int) canvasX - 1;
        int y = (anchorRow + minOffset) * canvasPixelScale + (int) canvasY - 1;

        Vec2 textureVec = (canvasPixelScale == SMALL_CANVAS_PIXEL_SCALE) ? OUTLINE_POSS_1[brushSize] : OUTLINE_POSS_2[brushSize];
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, x, y, (int) textureVec.x, (int) textureVec.y, outlineSize, outlineSize, 256, 256, 0xFF4D4D4D);
    }

    private void drawSigning(GuiGraphicsExtractor guiGraphics) {
        int i = (int) canvasX;
        int j = (int) canvasY;

        guiGraphics.fill(i + 10, j + 10, i + 150, j + 150, 0xFFEEEEEE);
        String s = this.canvasTitle;

        if (!this.isSigned) {
            if (this.updateCount / 6 % 2 == 0) {
                s = s + ChatFormatting.BLACK + "_";
            } else {
                s = s + ChatFormatting.GRAY + "_";
            }
        }
        String s1 = I18n.get("canvas.editTitle");
        int k = this.font.width(s1);
        guiGraphics.text(this.font, s1, (int) (i + 26 + (116 - k) / 2.0f), (j + 16 + 16), 0xFF000000, false);
        int l = this.font.width(s);
        guiGraphics.text(this.font, s, (int) (i + 26 + (116 - l) / 2.0f), j + 48, 0xFF000000, false);
        String s2 = I18n.get("canvas.byAuthor", this.editingPlayer.getName().getString());
        int i1 = this.font.width(s2);
        guiGraphics.text(this.font, ChatFormatting.DARK_GRAY + s2, (int) (i + 26 + (116 - i1) / 2.0f), j + 48 + 10, 0xFF000000, false);
        guiGraphics.textWithWordWrap(this.font, Component.translatable("canvas.finalizeWarning"), i + 26, j + 80, 116, 0xFF000000, false);
    }

    private void playBrushSound() {
        brushSound = new BrushSound();
        playSound(brushSound);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        int modifiers = event.modifiers();
        if (this.gettingSigned) {
            switch (Integer.valueOf(keyCode)) {
                case Integer k when k == GLFW_KEY_BACKSPACE && !this.canvasTitle.isEmpty() -> {
                    this.canvasTitle = this.canvasTitle.substring(0, this.canvasTitle.length() - 1);
                    this.updateButtons();
                }
                case Integer k when k == GLFW_KEY_ENTER && !this.canvasTitle.isEmpty() -> {
                    canvasDirty = true;
                    this.isSigned = true;
                    this.minecraft.setScreen(null);
                }
                default -> {
                    // Do nothing
                }
            }
            return true;
        } else {
            if (keyCode == GLFW_KEY_Z && (modifiers & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL) {
                if (!undoStack.isEmpty()) {
                    Snapshot snapshot = undoStack.pop();
                    pixels = snapshot.pixels();
                    sidePixels = snapshot.sidePixels();
                    canvasDirty = true;
                    if (easel != null) {
                        updateCanvas(false);
                    }
                }
                return true;
            } else {
                if (keyCode == GLFW_KEY_O) {
                    brushOpacitySetting += 1;
                    if (brushOpacitySetting >= BRUSH_LEVEL_COUNT) {
                        brushOpacitySetting = 0;
                    }
                }
                return super.keyPressed(event);
            }
        }
    }

    private static boolean isAllowedChatCharacter(char var0) {
        return var0 != 167 && var0 >= ' ' && var0 != 127;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        super.charTyped(event);

        char typedChar = (char) event.codepoint();
        if (!this.isSigned) {
            if (this.gettingSigned && this.canvasTitle.length() < MAX_TITLE_LENGTH && isAllowedChatCharacter(typedChar)) {
                this.canvasTitle = this.canvasTitle + typedChar;
                this.updateButtons();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double posX, double posY, double scrollX, double scrollY) {
        int mouseX = (int) Math.floor(posX);
        int mouseY = (int) Math.floor(posY);
        if (!gettingSigned && scrollY != 0.d) {
            if (inBrushOpacityMeter(mouseX, mouseY)) {
                final int maxBrushOpacity = BRUSH_LEVEL_COUNT - 1;
                brushOpacitySetting += scrollY < 0 ? 1 : -1;
                if (brushOpacitySetting > maxBrushOpacity) brushOpacitySetting = 0;
                else if (brushOpacitySetting < 0) brushOpacitySetting = maxBrushOpacity;
                return true;
            } else {
                final int maxBrushSize = BRUSH_LEVEL_COUNT - 1;
                brushSize += scrollY > 0 ? 1 : -1;
                if (brushSize > maxBrushSize) brushSize = 0;
                else if (brushSize < 0) brushSize = maxBrushSize;
                return true;
            }
        }
        return super.mouseScrolled(posX, posY, scrollX, scrollY);
    }

    // Mouse button 0: left, 1: right
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (gettingSigned) {
            return super.superMouseClicked(event, isDoubleClick);
        }

        int mouseButton = event.button();
        int mouseX = (int) Math.floor(event.x());
        int mouseY = (int) Math.floor(event.y());

        undoStarted = true;
        touchedCanvas = false;
        if (undoStack.size() >= MAX_UNDO_LENGTH) {
            undoStack.removeLast();
        }
        int[] sideSnapshot = sidePixels != null ? sidePixels.clone() : CanvasSides.defaultPixels(canvasType, glass);
        undoStack.push(new Snapshot(pixels.clone(), sideSnapshot));

        if (inSidesToggle(mouseX, mouseY)) {
            toggleSides();
            return super.superMouseClicked(event, isDoubleClick);
        }

        if (inPaintable(mouseX, mouseY)) {
            if (isPickingColor) {
                Integer color = cellColorAt(mouseX, mouseY);
                if (color != null) {
                    carriedColor = new PaletteUtil.Color(color);
                    setCarryingColor();
                    playSound(SoundEvents.COLOR_PICKER_SUCK);
                }
            } else {
                paintAt(mouseX, mouseY, mouseButton);
                playBrushSound();
            }
            return super.superMouseClicked(event, isDoubleClick);
        }

        if (inBrushMeter(mouseX, mouseY)) {
            int selectedSize = 3 - (mouseY - brushMeterY) / BRUSH_SPRITE_SIZE;
            if (selectedSize <= 3 && selectedSize >= 0) {
                brushSize = selectedSize;
            }
            return super.superMouseClicked(event, isDoubleClick);
        }
        if (inBrushOpacityMeter(mouseX, mouseY)) {
            int relativeY = mouseY - brushOpacityMeterY;
            int selectedOpacity = relativeY / (BRUSH_OPACITY_SPRITE_SIZE + 1);
            if (selectedOpacity >= 0 && selectedOpacity <= 3) {
                brushOpacitySetting = selectedOpacity;
            }
            return super.superMouseClicked(event, isDoubleClick);
        }
        if (inCanvasHolder(mouseX, mouseY)) {
            isCarryingCanvas = true;
        }
        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        isCarryingCanvas = false;
        if (gettingSigned) {
            return super.superMouseReleased(event);
        }
        draggedPoints.clear();

        if (undoStarted && !touchedCanvas) {
            undoStarted = false;
            undoStack.removeFirst();
        }

        if (brushSound != null) {
            brushSound.stopSound();
        }

        if (easel != null) {
            updateCanvas(false);
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (gettingSigned) {
            return super.superMouseDragged(event, deltaX, deltaY);
        }
        if (!isCarryingColor && !isCarryingWater && !isPickingColor && !isCarryingPalette && !isCarryingCanvas) {
            int mouseX = (int) Math.floor(event.x());
            int mouseY = (int) Math.floor(event.y());
            if (inPaintable(mouseX, mouseY)) {
                paintAt(mouseX, mouseY, event.button());
            }

            if (brushSound != null) {
                brushSound.refreshFade();
            }
        } else if (isCarryingCanvas) {
            updateCanvasPos(deltaX, deltaY);
            return super.superMouseDragged(event, deltaX, deltaY);
        } else if (isCarryingPalette) {
            boolean ret = super.mouseDragged(event, deltaX, deltaY);
            updatePalettePos(deltaX, deltaY);
            return ret;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    private void updateCanvasPos(double deltaX, double deltaY) {
        canvasX += deltaX;
        canvasY += deltaY;

        int margin = sideMargin();
        brushMeterX = (int) canvasX + canvasWidth + 2 + margin;
        brushMeterY = (int) canvasY + canvasHeight / 2 + 30;

        brushOpacityMeterX = (int) canvasX + canvasWidth + 2 + margin;
        brushOpacityMeterY = (int) canvasY;

        sidesToggleX = (int) (canvasX + canvasWidth * 0.25) - SIDES_TOGGLE_SIZE - 3;
        sidesToggleY = (int) canvasY - SIDES_TOGGLE_SIZE - 1 - margin;

        int typeIndex = canvasType.toByte();
        CANVAS_XS[typeIndex] = canvasX;
        CANVAS_YS[typeIndex] = canvasY;
    }

    private void updatePalettePos(double deltaX, double deltaY) {
        paletteX += deltaX;
        paletteY += deltaY;

        int typeIndex = canvasType.toByte();
        PALETTE_XS[typeIndex] = paletteX;
        PALETTE_YS[typeIndex] = paletteY;
    }

    private boolean inCanvas(int x, int y) {
        return x < canvasX + canvasWidth && x >= canvasX && y < canvasY + canvasHeight && y >= canvasY;
    }

    private boolean inCanvasHolder(int x, int y) {
        int margin = sideMargin();
        return x < canvasX + canvasWidth * 0.75 && x >= canvasX + canvasWidth * 0.25 && y < canvasY - margin && y >= canvasY - CANVAS_HOLDER_HEIGHT - margin;
    }

    private boolean inSidesToggle(int x, int y) {
        return x >= sidesToggleX && x < sidesToggleX + SIDES_TOGGLE_SIZE && y >= sidesToggleY && y < sidesToggleY + SIDES_TOGGLE_SIZE;
    }

    private static final int CHECKER_LIGHT = 0xFFBFBFBF;
    private static final int CHECKER_DARK = 0xFF7F7F7F;

    private void fillChecker(GuiGraphicsExtractor guiGraphics, int x, int y, int parity) {
        guiGraphics.fill(x, y, x + canvasPixelScale, y + canvasPixelScale, (parity & 1) == 0 ? CHECKER_LIGHT : CHECKER_DARK);
    }

    private void drawSideLines(GuiGraphicsExtractor guiGraphics) {
        int scale = canvasPixelScale;
        int cx = (int) canvasX;
        int cy = (int) canvasY;
        for (int k = 0; k < canvasPixelWidth; k++) {
            int x = cx + k * scale;
            if (glass) {
                fillChecker(guiGraphics, x, cy - scale, k - 1);
                fillChecker(guiGraphics, x, cy + canvasHeight, k + canvasPixelHeight);
            }
            guiGraphics.fill(x, cy - scale, x + scale, cy, getSidePixel(CanvasSides.TOP_OFFSET + k));
            guiGraphics.fill(x, cy + canvasHeight, x + scale, cy + canvasHeight + scale, getSidePixel(CanvasSides.bottomOffset(canvasType) + k));
        }
        for (int i = 0; i < canvasPixelHeight; i++) {
            int y = cy + i * scale;
            if (glass) {
                fillChecker(guiGraphics, cx - scale, y, i - 1);
                fillChecker(guiGraphics, cx + canvasWidth, y, i + canvasPixelWidth);
            }
            guiGraphics.fill(cx - scale, y, cx, y + scale, getSidePixel(CanvasSides.leftOffset(canvasType) + i));
            guiGraphics.fill(cx + canvasWidth, y, cx + canvasWidth + scale, y + scale, getSidePixel(CanvasSides.rightOffset(canvasType) + i));
        }
    }

    private void drawSidesToggle(GuiGraphicsExtractor guiGraphics) {
        int x = sidesToggleX;
        int y = sidesToggleY;
        int s = SIDES_TOGGLE_SIZE;
        guiGraphics.fill(x - 1, y - 1, x + s + 1, y + s + 1, 0xFF000000);
        guiGraphics.fill(x, y, x + s, y + s, sidesActive ? 0xFF6699FF : 0xFFB0B0B0);
        int edge = sidesActive ? 0xFFFFFFFF : 0xFF808080;
        guiGraphics.fill(x + 1, y + 1, x + s - 1, y + 2, edge);
        guiGraphics.fill(x + 1, y + s - 2, x + s - 1, y + s - 1, edge);
        guiGraphics.fill(x + 1, y + 2, x + 2, y + s - 2, edge);
        guiGraphics.fill(x + s - 2, y + 2, x + s - 1, y + s - 2, edge);
    }

    private void toggleSides() {
        sidesActive = !sidesActive;
        if (sidesActive) {
            ensureSidePixels();
        }
        canvasDirty = true;
        updateCanvasPos(0, 0);
        playSound(SoundEvents.MIX, 0.5f);
        if (easel != null) {
            updateCanvas(false);
        }
    }

    private boolean inBrushMeter(int x, int y) {
        return x < brushMeterX + BRUSH_SPRITE_SIZE && x >= brushMeterX && y < brushMeterY + BRUSH_SPRITE_SIZE * 4 && y >= brushMeterY;
    }

    private boolean inBrushOpacityMeter(int x, int y) {
        return x < brushOpacityMeterX + BRUSH_OPACITY_SPRITE_SIZE && x >= brushOpacityMeterX && y < brushOpacityMeterY + BRUSH_OPACITY_SPRITE_SIZE * 4 + 3 && y >= brushOpacityMeterY;
    }

    @Override
    public void removed() {
        updateCanvas(true);
    }

    private void updateCanvas(boolean closing) {
        int[] sideData = sidePixels == null ? new int[0] : sidePixels;
        if (closing) {
            if (canvasDirty) {
                version++;
                int easelId = easel == null ? -1 : easel.getId();
                ClientPlayNetworking.send(new CanvasUpdatePacket(pixels, isSigned, canvasTitle, canvasId, version, easelId, customColors, canvasType, sidesActive, sideData));
            } else {
                if (easel != null) {
                    ClientPlayNetworking.send(new EaselLeftPacket(easel.getId()));
                }
                if (paletteDirty) {
                    PaletteUpdatePacket pack = new PaletteUpdatePacket(customColors);
                    ClientPlayNetworking.send(pack);
                }
            }
        } else {
            if (canvasDirty) {
                if (timeSinceLastUpdate < MINI_UPDATE_INTERVAL_TICKS) {
                    skippedUpdate = true;
                } else {
                    version++;
                    if (easel != null) {
                        ClientPlayNetworking.send(new CanvasMiniUpdatePacket(pixels, canvasId, version, easel.getId(), canvasType, sidesActive, sideData));
                    }
                    canvasDirty = false;
                    timeSinceLastUpdate = 0;
                }
            }
        }
    }

    public static class ToggleHelpButton extends Button {
        protected final Identifier resourceLocation;
        protected final int xTexStart;
        protected final int yTexStart;
        protected final int yDiffText;
        protected final int texWidth;
        protected final int texHeight;

        public ToggleHelpButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, Identifier texture, int texWidth, int texHeight, OnPress onClick) {
            super(x, y, width, height, Component.empty(), onClick, DEFAULT_NARRATION);
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.xTexStart = xTexStart;
            this.yTexStart = yTexStart;
            this.yDiffText = yDiffText;
            this.resourceLocation = texture;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
            int yTexStartNew = this.yTexStart;
            if (this.isHovered) {
                yTexStartNew += this.yDiffText;
            }
            int xTexStartNew = this.xTexStart + (showHelp ? 0 : this.width);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, resourceLocation, this.getX(), this.getY(), xTexStartNew, yTexStartNew, this.width, this.height, this.texWidth, this.texHeight);
        }
    }
}
