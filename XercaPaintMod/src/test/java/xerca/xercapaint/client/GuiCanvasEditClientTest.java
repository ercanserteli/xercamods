package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.minecraft.client.gui.components.Button;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.tests.SsimComparisonAlgorithm;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static xerca.xercapaint.client.PaintClientTests.check;

/**
 * Client gametest for the canvas editor GUI ({@link GuiCanvasEdit}): every canvas item routes to an editor
 * with the right type, a brush stroke paints cells, ctrl+Z undoes the whole stroke, the brush-size wheel
 * and opacity key change the brush, the colour picker samples a painted cell, and the sign / cancel /
 * finalize flow closes the screen. Finalizing sends the {@code CanvasUpdatePacket}, so the painted,
 * signed canvas is verified end-to-end on the server. Before signing, the editor is rendered over a solid
 * black-concrete backdrop and the frame is SSIM-diffed against a golden, guarding that the palette and the
 * canvas (with its painted pixels) draw correctly.
 */
public final class GuiCanvasEditClientTest implements FabricClientGameTest {
    private static final int BLACK = 0xFF1D1D21; // BasePalette.BASIC_COLORS[0]
    private static final int WHITE = 0xFFF9FFFE; // BasePalette.BASIC_COLORS[15], the paper canvas default fill
    private static final int SMALL_CANVAS_WIDTH = 16;
    private static final int PAINT_CELL_INDEX = cellIndex(2, 2); // probe stroke cell for the functional checks
    // Palette basic-colour indices for the three composition bands, and the cell where they all cross.
    private static final int RED_INDEX = 1;
    private static final int YELLOW_INDEX = 11;
    private static final int BLUE_INDEX = 4;
    private static final int CENTER_INDEX = cellIndex(7, 7);
    private static final String TITLE = "Masterpiece";
    // SSIM >= this to pass. Rendering the same static editor over the same backdrop sits very close to 1.0.
    private static final double SSIM_THRESHOLD = 0.95;
    private static final String GOLDEN = "canvas_editor_painted";

    private record CanvasCase(Item item, CanvasType type, boolean glass) {
    }

    private static final CanvasCase[] CANVAS_CASES = {
            new CanvasCase(Items.ITEM_CANVAS, CanvasType.SMALL, false),
            new CanvasCase(Items.ITEM_CANVAS_LONG, CanvasType.LONG, false),
            new CanvasCase(Items.ITEM_CANVAS_TALL, CanvasType.TALL, false),
            new CanvasCase(Items.ITEM_CANVAS_LARGE, CanvasType.LARGE, false),
            new CanvasCase(Items.ITEM_CANVAS_GLASS, CanvasType.SMALL, true),
            new CanvasCase(Items.ITEM_CANVAS_GLASS_LONG, CanvasType.LONG, true),
            new CanvasCase(Items.ITEM_CANVAS_GLASS_TALL, CanvasType.TALL, true),
            new CanvasCase(Items.ITEM_CANVAS_GLASS_LARGE, CanvasType.LARGE, true),
    };

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            // Every canvas item (each shape, paper and glass) opens an editor of the matching type.
            for (CanvasCase canvasCase : CANVAS_CASES) {
                context.runOnClient(client -> {
                    client.player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(canvasCase.item()));
                    client.player.setItemSlot(EquipmentSlot.OFFHAND, fullPalette());
                    ModClient.showCanvasGui(client.player);
                });
                context.waitForScreen(GuiCanvasEdit.class);
                context.waitTicks(2);
                check(canvasType(context) == canvasCase.type() && glass(context) == canvasCase.glass(),
                        "Expected " + canvasCase.item() + " to open a " + canvasCase.type()
                                + (canvasCase.glass() ? " glass" : "") + " editor");
                context.setScreen(() -> null);
                context.waitTick();
            }

            // Draw on a fresh small paper canvas held in hand (offhand palette) so the update round-trips.
            singleplayer.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_CANVAS));
                player.setItemSlot(EquipmentSlot.OFFHAND, fullPalette());
            });
            context.runOnClient(client -> {
                client.player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_CANVAS));
                client.player.setItemSlot(EquipmentSlot.OFFHAND, fullPalette());
                ModClient.showCanvasGui(client.player);
            });
            context.waitForScreen(GuiCanvasEdit.class);
            context.waitTicks(2);

            double[] geom = geometry(context);
            double[] palettePos = palettePos(context);

            // Select black from the palette, then paint a short horizontal stroke.
            selectBasicColor(context, palettePos, 0);
            paintStroke(context, geom);
            check(pixel(context, PAINT_CELL_INDEX) == BLACK, "Expected the painted cell to take the selected colour");
            check(pixel(context, PAINT_CELL_INDEX + 1) == BLACK && pixel(context, PAINT_CELL_INDEX + 2) == BLACK,
                    "Expected the whole stroke to be painted");

            // Ctrl+Z undoes the entire stroke.
            PaintClientTests.pressKey(context, GLFW.GLFW_KEY_Z, 0, GLFW.GLFW_MOD_CONTROL);
            check(pixel(context, PAINT_CELL_INDEX) == WHITE, "Expected ctrl+Z to undo the stroke");
            paintStroke(context, geom);
            check(pixel(context, PAINT_CELL_INDEX) == BLACK, "Expected the re-painted cell to be black again");

            // The scroll wheel over the canvas changes the brush size (and wraps back).
            double canvasCenterX = geom[0] + 8 * geom[2];
            double canvasCenterY = geom[1] + 8 * geom[2];
            PaintClientTests.scrollVertically(context, canvasCenterX, canvasCenterY, 1);
            check(brushSize(context) == 1, "Expected scrolling up to grow the brush");
            PaintClientTests.scrollVertically(context, canvasCenterX, canvasCenterY, -1);
            check(brushSize(context) == 0, "Expected scrolling down to shrink the brush");

            // The O key cycles the brush opacity.
            PaintClientTests.pressKey(context, GLFW.GLFW_KEY_O, 0, 0);
            check(brushOpacity(context) == 1, "Expected the O key to advance the brush opacity");

            // The colour picker samples the painted cell.
            PaintClientTests.clickAt(context,
                    palettePos[0] + BasePalette.COLOR_PICKER_POS_X + BasePalette.COLOR_PICKER_SIZE / 2.0,
                    palettePos[1] + BasePalette.COLOR_PICKER_POS_Y + BasePalette.COLOR_PICKER_SIZE / 2.0,
                    GLFW.GLFW_MOUSE_BUTTON_LEFT);
            check(pickingColor(context), "Expected clicking the picker to enter colour-picking mode");
            PaintClientTests.clickAt(context, cellX(geom, 2), cellY(geom, 2), GLFW.GLFW_MOUSE_BUTTON_LEFT);
            check(!pickingColor(context), "Expected sampling a cell to leave picking mode");
            check(carriedColorRgb(context) == BLACK, "Expected the picker to sample the painted cell's colour");

            // Clear the probe stroke, then paint the composition to snapshot: switch to the biggest brush at a
            // low opacity and lay down three overlapping colour bands whose intersections blend to new colours.
            PaintClientTests.pressKey(context, GLFW.GLFW_KEY_Z, 0, GLFW.GLFW_MOD_CONTROL);
            for (int i = 0; i < 3; i++) {
                PaintClientTests.scrollVertically(context, canvasCenterX, canvasCenterY, 1);
            }
            check(brushSize(context) == 3, "Expected the wheel to reach the biggest brush");
            PaintClientTests.pressKey(context, GLFW.GLFW_KEY_O, 0, 0);
            check(brushOpacity(context) == 2, "Expected a low (50%) brush opacity");

            selectBasicColor(context, palettePos, RED_INDEX);
            paintBand(context, geom, diagonalBand());
            selectBasicColor(context, palettePos, YELLOW_INDEX);
            paintBand(context, geom, horizontalBand());
            selectBasicColor(context, palettePos, BLUE_INDEX);
            paintBand(context, geom, antiDiagonalBand());

            int redBand = pixel(context, cellIndex(11, 11));
            int yellowBand = pixel(context, cellIndex(3, 7));
            int blueBand = pixel(context, cellIndex(12, 3));
            int centerBlend = pixel(context, CENTER_INDEX);
            check(redBand != WHITE && yellowBand != WHITE && blueBand != WHITE,
                    "Expected all three colour bands to be painted");
            check(redBand != yellowBand && yellowBand != blueBand && redBand != blueBand,
                    "Expected the three bands to be three different colours");
            check(centerBlend != WHITE && centerBlend != redBand && centerBlend != yellowBand && centerBlend != blueBand,
                    "Expected the triple intersection to blend to a distinct colour");

            // Build a solid black-concrete backdrop directly behind the (transparent-background) editor,
            // then snapshot the palette + painted canvas and diff against the golden.
            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamerule doDaylightCycle false");
            server.runCommand("time set 1000");
            server.runCommand("fill -4 110 -3 4 110 8 minecraft:black_concrete"); // floor to stand on
            server.runCommand("fill -16 111 5 16 141 5 minecraft:black_concrete"); // backdrop wall
            server.runCommand("tp @p 0.5 111 3 0 0"); // stand on the floor, face the wall
            context.runOnClient(client -> client.options.hideGui = true);
            context.waitTicks(40);
            singleplayer.getClientLevel().waitForChunksRender();
            context.assertScreenshotEquals(TestScreenshotComparisonOptions.of(GOLDEN)
                    .withAlgorithm(SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD))
                    .save());

            // Sign flow: sign, cancel back out, sign again, type a title, then finalize (closes the screen).
            pressButton(context, "buttonSign");
            check(gettingSigned(context), "Expected the sign button to enter signing mode");
            // Signing overlay: the prompt text must actually render over the box
            // (regression: color-0 text became invisible with the 1.21.6 GUI rework).
            context.waitTicks(2);
            assertSigningTextRendered(context.takeScreenshot("canvas_signing_live"), signingBoxFramebufferRect(context));
            pressButton(context, "buttonCancel");
            check(!gettingSigned(context), "Expected cancel to leave signing mode");
            pressButton(context, "buttonSign");
            PaintClientTests.typeChars(context, TITLE);
            check(TITLE.equals(canvasTitle(context)), "Expected typed characters to build the title");
            pressButton(context, "buttonFinalize");
            context.waitForScreen(null);

            check(pollServer(context, singleplayer, player -> isSavedSignedCanvas(player, centerBlend)),
                    "Expected the painted, signed canvas to be saved to the held stack on the server");
        }
    }

    private static void paintStroke(ClientGameTestContext context, double[] geom) {
        double y = cellY(geom, 2);
        PaintClientTests.mouseDown(context, cellX(geom, 2), y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        PaintClientTests.mouseDragTo(context, cellX(geom, 3), y, GLFW.GLFW_MOUSE_BUTTON_LEFT, geom[2], 0);
        PaintClientTests.mouseDragTo(context, cellX(geom, 4), y, GLFW.GLFW_MOUSE_BUTTON_LEFT, geom[2], 0);
        PaintClientTests.mouseUp(context, cellX(geom, 4), y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static double cellX(double[] geom, int col) {
        return geom[0] + col * geom[2] + geom[2] / 2.0;
    }

    private static double cellY(double[] geom, int row) {
        return geom[1] + row * geom[2] + geom[2] / 2.0;
    }

    private static int cellIndex(int col, int row) {
        return row * SMALL_CANVAS_WIDTH + col;
    }

    private static void selectBasicColor(ClientGameTestContext context, double[] palettePos, int index) {
        PaintClientTests.clickAt(context,
                palettePos[0] + BasePalette.BASIC_COLOR_CENTERS[index].x,
                palettePos[1] + BasePalette.BASIC_COLOR_CENTERS[index].y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    /**
     * Paints one continuous brush stroke through the given canvas cells (one drag segment per step).
     */
    private static void paintBand(ClientGameTestContext context, double[] geom, int[][] cells) {
        PaintClientTests.mouseDown(context, cellX(geom, cells[0][0]), cellY(geom, cells[0][1]), GLFW.GLFW_MOUSE_BUTTON_LEFT);
        for (int i = 1; i < cells.length; i++) {
            PaintClientTests.mouseDragTo(context, cellX(geom, cells[i][0]), cellY(geom, cells[i][1]), GLFW.GLFW_MOUSE_BUTTON_LEFT, geom[2], 0);
        }
        int last = cells.length - 1;
        PaintClientTests.mouseUp(context, cellX(geom, cells[last][0]), cellY(geom, cells[last][1]), GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static int[][] diagonalBand() {
        int[][] cells = new int[10][2];
        for (int i = 0; i < cells.length; i++) {
            cells[i][0] = 3 + i;
            cells[i][1] = 3 + i;
        }
        return cells;
    }

    private static int[][] horizontalBand() {
        int[][] cells = new int[10][2];
        for (int i = 0; i < cells.length; i++) {
            cells[i][0] = 3 + i;
            cells[i][1] = 7;
        }
        return cells;
    }

    private static int[][] antiDiagonalBand() {
        int[][] cells = new int[10][2];
        for (int i = 0; i < cells.length; i++) {
            cells[i][0] = 3 + i;
            cells[i][1] = 12 - i;
        }
        return cells;
    }

    private static ItemStack fullPalette() {
        ItemStack palette = new ItemStack(Items.ITEM_PALETTE);
        byte[] basics = new byte[16];
        Arrays.fill(basics, (byte) 1);
        palette.set(Items.PALETTE_BASIC_COLORS, basics);
        return palette;
    }

    private static boolean isSavedSignedCanvas(ServerPlayer player, int expectedCenter) {
        ItemStack canvas = player.getMainHandItem();
        List<Integer> pixels = canvas.get(Items.CANVAS_PIXELS);
        if (pixels == null || pixels.size() <= CENTER_INDEX) {
            return false;
        }
        return pixels.get(CENTER_INDEX) == expectedCenter
                && TITLE.equals(canvas.get(Items.CANVAS_TITLE))
                && canvas.get(Items.CANVAS_AUTHOR) != null
                && canvas.getOrDefault(Items.CANVAS_GENERATION, 0) == 1;
    }

    // The drawSigning box (canvasX+10, canvasY+10, 140x140) in framebuffer pixels, inset to skip edges.
    private static int[] signingBoxFramebufferRect(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            double cx = PaintClientTests.<Double>readField(client.screen, GuiCanvasEdit.class, "canvasX");
            double cy = PaintClientTests.<Double>readField(client.screen, GuiCanvasEdit.class, "canvasY");
            int scale = client.getWindow().getGuiScale();
            return new int[]{(int) ((cx + 12) * scale), (int) ((cy + 12) * scale), 136 * scale, 136 * scale};
        });
    }

    private static void assertSigningTextRendered(java.nio.file.Path screenshot, int[] rect) {
        java.awt.image.BufferedImage image;
        try {
            image = javax.imageio.ImageIO.read(screenshot.toFile());
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        int dark = 0;
        int light = 0;
        for (int y = rect[1]; y < rect[1] + rect[3]; y++) {
            for (int x = rect[0]; x < rect[0] + rect[2]; x++) {
                int rgb = image.getRGB(x, y);
                int luminance = ((rgb >> 16 & 0xFF) + (rgb >> 8 & 0xFF) + (rgb & 0xFF)) / 3;
                if (luminance < 100) {
                    dark++;
                } else if (luminance > 220) {
                    light++;
                }
            }
        }
        int area = rect[2] * rect[3];
        check(light > area / 2, "Expected the signing box to render as a light panel, got " + light + "/" + area + " light pixels");
        check(dark >= 40, "Expected the signing prompt text to render dark pixels over the box, got " + dark);
    }

    private static void pressButton(ClientGameTestContext context, String fieldName) {
        context.runOnClient(client -> {
            Button button = PaintClientTests.readField(client.screen, GuiCanvasEdit.class, fieldName);
            button.onPress(new net.minecraft.client.input.MouseButtonInfo(0, 0));
        });
        context.waitTick();
    }

    private static double[] geometry(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            double cx = PaintClientTests.<Double>readField(client.screen, GuiCanvasEdit.class, "canvasX");
            double cy = PaintClientTests.<Double>readField(client.screen, GuiCanvasEdit.class, "canvasY");
            int scale = PaintClientTests.<Integer>readField(client.screen, GuiCanvasEdit.class, "canvasPixelScale");
            return new double[]{cx, cy, scale};
        });
    }

    private static double[] palettePos(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            BasePalette palette = (BasePalette) client.screen;
            return new double[]{palette.paletteX, palette.paletteY};
        });
    }

    private static int pixel(ClientGameTestContext context, int index) {
        return context.computeOnClient(client -> {
            int[] pixels = PaintClientTests.readField(client.screen, GuiCanvasEdit.class, "pixels");
            return pixels[index];
        });
    }

    private static CanvasType canvasType(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.readField(client.screen, GuiCanvasEdit.class, "canvasType"));
    }

    private static boolean glass(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.<Boolean>readField(client.screen, GuiCanvasEdit.class, "glass"));
    }

    private static int brushSize(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.<Integer>readField(client.screen, GuiCanvasEdit.class, "brushSize"));
    }

    private static int brushOpacity(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.<Integer>readField(client.screen, GuiCanvasEdit.class, "brushOpacitySetting"));
    }

    private static boolean gettingSigned(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.<Boolean>readField(client.screen, GuiCanvasEdit.class, "gettingSigned"));
    }

    private static String canvasTitle(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.readField(client.screen, GuiCanvasEdit.class, "canvasTitle"));
    }

    private static boolean pickingColor(ClientGameTestContext context) {
        return context.computeOnClient(client -> ((BasePalette) client.screen).isPickingColor);
    }

    private static int carriedColorRgb(ClientGameTestContext context) {
        return context.computeOnClient(client -> ((BasePalette) client.screen).carriedColor.rgbVal());
    }

    private static boolean pollServer(ClientGameTestContext context, TestSingleplayerContext singleplayer,
                                      Predicate<ServerPlayer> predicate) {
        for (int i = 0; i < 20; i++) {
            boolean satisfied = singleplayer.getServer().computeOnServer(server ->
                    predicate.test(server.getPlayerList().getPlayers().getFirst()));
            if (satisfied) {
                return true;
            }
            context.waitTicks(5);
        }
        return false;
    }
}
