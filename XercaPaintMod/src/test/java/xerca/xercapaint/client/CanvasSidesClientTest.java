package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.item.Items;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static xerca.xercapaint.client.PaintClientTests.check;

/**
 * Client gametest for the paintable canvas sides (frames) in the editor ({@link GuiCanvasEdit}): while
 * sides are disabled they cannot be painted, toggling them on initializes and lets you paint them, a
 * multi-pixel brush straddling the front/side boundary paints both in a single stamp, toggling off then
 * on keeps the painted side pixels, and closing the editor persists {@code CANVAS_SIDES_ACTIVE} plus
 * {@code CANVAS_SIDE_PIXELS} to the held stack on the server.
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class CanvasSidesClientTest implements FabricClientGameTest {
    private static final int BLACK = 0xFF1D1D21; // BasePalette.BASIC_COLORS[0]
    private static final int RED = 0xFFB02E26;   // BasePalette.BASIC_COLORS[1]
    // Side-pixel indices into a SMALL canvas's side array (see CanvasSides.leftOffset).
    private static final int LEFT_OFFSET = CanvasSides.leftOffset(CanvasType.SMALL);
    private static final int SIDE_ROW_5 = LEFT_OFFSET + 5;
    private static final int SIDE_ROW_8 = LEFT_OFFSET + 8;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

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

            // Sides start disabled, and a side cell cannot be painted (nothing is even allocated).
            check(!sidesActive(context), "Sides should start disabled");
            selectBasicColor(context, palettePos, 0);
            paintLeftSide(context, geom, 5);
            check(!hasSidePixels(context), "Expected no side pixels to be created while sides are disabled");

            // Toggling the on-canvas button enables sides and allocates the (default-white) side pixels.
            clickSidesToggle(context);
            check(sidesActive(context), "Expected the toggle to enable sides");
            check(sidePixelsLength(context) == CanvasSides.count(CanvasType.SMALL),
                    "Expected the side pixels to be allocated for every edge");

            // Now a side cell paints.
            paintLeftSide(context, geom, 5);
            check(sidePixel(context, SIDE_ROW_5) == BLACK, "Expected the left-side cell to take the selected colour");

            // A big brush placed on the front edge column paints the front pixel AND the adjacent side pixel
            // with the same stamp, so the frame stays flush with the picture across the boundary.
            for (int i = 0; i < 3; i++) {
                PaintClientTests.scrollVertically(context, geom[0] + 8 * geom[2], geom[1] + 8 * geom[2], 1);
            }
            check(brushSize(context) == 3, "Expected the wheel to reach the biggest brush");
            selectBasicColor(context, palettePos, 1);
            paintFrontCell(context, geom, 0, 8);
            check(frontPixel(context, 0, 8) == RED, "Expected the front edge pixel to be painted");
            check(sidePixel(context, SIDE_ROW_8) == RED,
                    "Expected the same brush stamp to paint the adjacent side pixel across the boundary");

            // Toggling sides off then on must not lose the painted side pixels.
            clickSidesToggle(context);
            check(!sidesActive(context), "Expected the toggle to disable sides");
            clickSidesToggle(context);
            check(sidesActive(context), "Expected the toggle to re-enable sides");
            check(sidePixel(context, SIDE_ROW_5) == BLACK && sidePixel(context, SIDE_ROW_8) == RED,
                    "Expected the side pixels to survive a toggle off/on");

            // Closing the editor persists the sides to the held stack on the server.
            context.setScreen(() -> null);
            context.waitForScreen(null);
            check(pollServer(context, singleplayer, CanvasSidesClientTest::hasSavedSides),
                    "Expected the toggled-on side pixels to persist to the held canvas on the server");
        }
    }

    private static void paintLeftSide(ClientGameTestContext context, double[] geom, int row) {
        // The left side band sits one cell to the left of the canvas (col -1).
        PaintClientTests.clickAt(context, geom[0] - geom[2] / 2.0, geom[1] + row * geom[2] + geom[2] / 2.0,
                GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static void paintFrontCell(ClientGameTestContext context, double[] geom, int col, int row) {
        PaintClientTests.clickAt(context, geom[0] + col * geom[2] + geom[2] / 2.0, geom[1] + row * geom[2] + geom[2] / 2.0,
                GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static void clickSidesToggle(ClientGameTestContext context) {
        double[] toggle = context.computeOnClient(client -> {
            int x = PaintClientTests.<Integer>readField(client.gui.screen(), GuiCanvasEdit.class, "sidesToggleX");
            int y = PaintClientTests.<Integer>readField(client.gui.screen(), GuiCanvasEdit.class, "sidesToggleY");
            return new double[]{x, y};
        });
        // Click the centre of the 8px toggle.
        PaintClientTests.clickAt(context, toggle[0] + 4, toggle[1] + 4, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static void selectBasicColor(ClientGameTestContext context, double[] palettePos, int index) {
        PaintClientTests.clickAt(context,
                palettePos[0] + BasePalette.BASIC_COLOR_CENTERS[index].x,
                palettePos[1] + BasePalette.BASIC_COLOR_CENTERS[index].y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static ItemStack fullPalette() {
        ItemStack palette = new ItemStack(Items.ITEM_PALETTE);
        byte[] basics = new byte[16];
        Arrays.fill(basics, (byte) 1);
        palette.set(Items.PALETTE_BASIC_COLORS, basics);
        return palette;
    }

    private static boolean hasSavedSides(ServerPlayer player) {
        ItemStack canvas = player.getMainHandItem();
        List<Integer> sides = canvas.get(Items.CANVAS_SIDE_PIXELS);
        if (!Boolean.TRUE.equals(canvas.get(Items.CANVAS_SIDES_ACTIVE)) || sides == null || sides.size() <= SIDE_ROW_8) {
            return false;
        }
        return sides.get(SIDE_ROW_5) == BLACK && sides.get(SIDE_ROW_8) == RED;
    }

    private static boolean sidesActive(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.<Boolean>readField(client.gui.screen(), GuiCanvasEdit.class, "sidesActive"));
    }

    private static boolean hasSidePixels(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.<int[]>readField(client.gui.screen(), GuiCanvasEdit.class, "sidePixels") != null);
    }

    private static int sidePixelsLength(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            int[] sidePixels = PaintClientTests.readField(client.gui.screen(), GuiCanvasEdit.class, "sidePixels");
            return sidePixels == null ? -1 : sidePixels.length;
        });
    }

    private static int sidePixel(ClientGameTestContext context, int index) {
        return context.computeOnClient(client -> {
            int[] sidePixels = PaintClientTests.readField(client.gui.screen(), GuiCanvasEdit.class, "sidePixels");
            return sidePixels[index];
        });
    }

    private static int frontPixel(ClientGameTestContext context, int col, int row) {
        return context.computeOnClient(client -> {
            int[] pixels = PaintClientTests.readField(client.gui.screen(), GuiCanvasEdit.class, "pixels");
            return pixels[row * CanvasType.getWidth(CanvasType.SMALL) + col];
        });
    }

    private static int brushSize(ClientGameTestContext context) {
        return context.computeOnClient(client ->
                PaintClientTests.<Integer>readField(client.gui.screen(), GuiCanvasEdit.class, "brushSize"));
    }

    private static double[] geometry(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            double cx = PaintClientTests.<Double>readField(client.gui.screen(), GuiCanvasEdit.class, "canvasX");
            double cy = PaintClientTests.<Double>readField(client.gui.screen(), GuiCanvasEdit.class, "canvasY");
            int scale = PaintClientTests.<Integer>readField(client.gui.screen(), GuiCanvasEdit.class, "canvasPixelScale");
            return new double[]{cx, cy, scale};
        });
    }

    private static double[] palettePos(ClientGameTestContext context) {
        return context.computeOnClient(client -> {
            BasePalette palette = (BasePalette) client.gui.screen();
            return new double[]{palette.paletteX, palette.paletteY};
        });
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
