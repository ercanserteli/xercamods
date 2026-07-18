package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.entity.EntityCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.tests.SsimComparisonAlgorithm;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Client render gametest for the placed canvas entity ({@link RenderEntityCanvas}). For both paper and
 * glass canvases, a canvas with a painted front renders that painting plus, with sides enabled, its custom
 * painted frame, and the same canvas with sides disabled renders the default frame (wooden birch for paper,
 * a glass pane for glass). All four share the same front pixels, so the goldens verify the front (painting)
 * as well as the frame. Every canvas is captured from the same fixed spectator camera and SSIM-diffed
 * against its golden; within each material the two goldens are additionally asserted to differ (by a pixel
 * count over the painted edges), proving the sides toggle actually changes the rendered frame.
 */
public final class CanvasSidesRenderClientTest implements FabricClientGameTest {
    // Same-scene renders sit ~1.0; the equals threshold catches gross render breakage.
    private static final double EQUALS_THRESHOLD = 0.99;
    // The custom/default goldens are only thin painted edges apart, so an SSIM over the whole frame cannot
    // separate them; a direct pixel count over the edges does. At least this many pixels must differ clearly.
    private static final int MIN_DIFFERING_PIXELS = 300;
    private static final int CHANNEL_DIFF = 48;
    private static final String GOLDEN_DEFAULT = "canvas_sides_default_frame";
    private static final String GOLDEN_CUSTOM = "canvas_sides_custom_frame";
    private static final String GOLDEN_GLASS_DEFAULT = "canvas_sides_glass_default_frame";
    private static final String GOLDEN_GLASS_CUSTOM = "canvas_sides_glass_custom_frame";

    // Front face: two opaque coloured blocks on a transparent background, shared by both canvases so the
    // goldens verify the painting (front) renders — including glass see-through where the background is
    // transparent. (Paper renders solid, so its transparent background still shows the RGB as gray.)
    private static final int FRONT_COLOR = 0x00DDDDDD; // transparent
    private static final int FRONT_BLOCK_A = 0xFFF07818; // orange
    private static final int FRONT_BLOCK_B = 0xFF1898C0; // cyan
    // Vivid, non-tan edge colours so the custom frame stands well apart from the birch (pale tan) default.
    private static final int SIDE_TOP = 0xFFE01818;    // red
    private static final int SIDE_BOTTOM = 0xFF18C018; // green
    private static final int SIDE_LEFT = 0xFFC020C0;   // magenta
    private static final int SIDE_RIGHT = 0xFF2040E0;  // blue
    private static final BlockPos CANVAS_POS = new BlockPos(0, 100, 5);
    // Chosen framing: a fixed spectator camera looking at the canvas from the front-right at a slight angle.
    private static final String CAMERA = "1.5 100.4 4.8 facing 0.5 99.8 5.9";

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        // The differ auto-writes an absent golden, so only run it once all goldens already exist; a
        // regeneration run (goldens deleted) just re-captures them.
        boolean goldensExisted = goldenExists(GOLDEN_CUSTOM) && goldenExists(GOLDEN_DEFAULT)
                && goldenExists(GOLDEN_GLASS_CUSTOM) && goldenExists(GOLDEN_GLASS_DEFAULT);

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            // A stone wall behind the canvas (its support and a solid backdrop), full-bright and deterministic.
            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamemode spectator @a"); // free-floating camera that neither falls nor takes damage
            server.runCommand("effect give @a minecraft:night_vision infinite 1 true");
            server.runCommand("gamerule doDaylightCycle false");
            server.runCommand("time set 6000");
            server.runCommand("weather clear 1000000");
            server.runCommand("fill -16 84 6 16 120 6 minecraft:stone");
            server.runCommand("tp @p " + CAMERA);
            context.runOnClient(client -> client.options.hideGui = true);

            // Paper: custom painted frame, then the default wooden (birch) frame.
            captureCanvas(context, singleplayer, Items.ITEM_CANVAS, "sides_custom_frame", true, GOLDEN_CUSTOM);
            captureCanvas(context, singleplayer, Items.ITEM_CANVAS, "sides_default_frame", false, GOLDEN_DEFAULT);
            // Glass: custom painted frame, then the default glass-pane frame.
            captureCanvas(context, singleplayer, Items.ITEM_CANVAS_GLASS, "sides_glass_custom_frame", true, GOLDEN_GLASS_CUSTOM);
            captureCanvas(context, singleplayer, Items.ITEM_CANVAS_GLASS, "sides_glass_default_frame", false, GOLDEN_GLASS_DEFAULT);

            // Within each material the two goldens must differ over the painted edges: the toggle changes the
            // render. Guarded so a regeneration run (goldens freshly written) does not race the on-disk files.
            if (goldensExisted) {
                assertGoldensDiffer(GOLDEN_CUSTOM, GOLDEN_DEFAULT);
                assertGoldensDiffer(GOLDEN_GLASS_CUSTOM, GOLDEN_GLASS_DEFAULT);
            }
        }
    }

    private static void captureCanvas(ClientGameTestContext context, TestSingleplayerContext singleplayer,
                                      Item item, String canvasId, boolean customSides, String golden) {
        removeCanvases(singleplayer);
        context.waitTicks(5);
        spawnCanvas(singleplayer, item, canvasId, customSides);
        context.waitTicks(40);
        singleplayer.getClientLevel().waitForChunksRender();
        context.assertScreenshotEquals(TestScreenshotComparisonOptions.of(golden)
                .withAlgorithm(SsimComparisonAlgorithm.withThreshold(EQUALS_THRESHOLD))
                .save());
    }

    private static boolean goldenExists(String name) {
        String base = System.getProperty("fabric.client.gametest.testModResourcesPath");
        return base != null && new File(base, "templates/" + name + ".png").isFile();
    }

    private static void spawnCanvas(TestSingleplayerContext singleplayer, Item item, String canvasId, boolean customSides) {
        singleplayer.getServer().runOnServer(server -> {
            ServerLevel level = server.overworld();
            ItemStack stack = new ItemStack(item);
            stack.set(Items.CANVAS_ID, canvasId);
            stack.set(Items.CANVAS_VERSION, 1);
            stack.set(Items.CANVAS_PIXELS, Arrays.stream(frontPixels()).boxed().toList());
            if (customSides) {
                stack.set(Items.CANVAS_SIDES_ACTIVE, true);
                stack.set(Items.CANVAS_SIDE_PIXELS, Arrays.stream(customSides()).boxed().toList());
            }
            EntityCanvas entity = new EntityCanvas(level, stack, CANVAS_POS, Direction.NORTH, CanvasType.SMALL, 0);
            level.addFreshEntity(entity);
        });
    }

    private static void removeCanvases(TestSingleplayerContext singleplayer) {
        singleplayer.getServer().runOnServer(server -> {
            for (EntityCanvas canvas : server.overworld()
                    .getEntitiesOfClass(EntityCanvas.class, new AABB(CANVAS_POS).inflate(3.0))) {
                canvas.discard();
            }
        });
    }

    private static int[] frontPixels() {
        int width = CanvasType.getWidth(CanvasType.SMALL);
        int height = CanvasType.getHeight(CanvasType.SMALL);
        int[] pixels = new int[width * height];
        Arrays.fill(pixels, FRONT_COLOR);
        fillRect(pixels, width, 3, 3, 8, 8, FRONT_BLOCK_A);
        fillRect(pixels, width, 9, 9, 13, 13, FRONT_BLOCK_B);
        return pixels;
    }

    private static void fillRect(int[] pixels, int width, int col0, int row0, int col1, int row1, int color) {
        for (int row = row0; row <= row1; row++) {
            for (int col = col0; col <= col1; col++) {
                pixels[row * width + col] = color;
            }
        }
    }

    private static int[] customSides() {
        CanvasType type = CanvasType.SMALL;
        int width = CanvasType.getWidth(type);
        int height = CanvasType.getHeight(type);
        int[] sides = new int[CanvasSides.count(type)];
        for (int k = 0; k < width; k++) {
            sides[CanvasSides.TOP_OFFSET + k] = SIDE_TOP;
            sides[CanvasSides.bottomOffset(type) + k] = SIDE_BOTTOM;
        }
        for (int i = 0; i < height; i++) {
            sides[CanvasSides.leftOffset(type) + i] = SIDE_LEFT;
            sides[CanvasSides.rightOffset(type) + i] = SIDE_RIGHT;
        }
        return sides;
    }

    /**
     * Fails unless the two goldens differ over a meaningful number of pixels (the painted edges), proving
     * the sides toggle changes the rendered frame.
     */
    private static void assertGoldensDiffer(String customGolden, String defaultGolden) {
        String base = System.getProperty("fabric.client.gametest.testModResourcesPath");
        if (base == null) {
            throw new AssertionError("testModResourcesPath is not set; cannot compare sides goldens");
        }
        try {
            BufferedImage custom = ImageIO.read(new File(base, "templates/" + customGolden + ".png"));
            BufferedImage def = ImageIO.read(new File(base, "templates/" + defaultGolden + ".png"));
            if (custom == null || def == null) {
                throw new AssertionError("Could not read the sides goldens");
            }
            if (custom.getWidth() != def.getWidth() || custom.getHeight() != def.getHeight()) {
                throw new AssertionError("Sides goldens differ in size");
            }
            int differing = 0;
            for (int y = 0; y < custom.getHeight(); y++) {
                for (int x = 0; x < custom.getWidth(); x++) {
                    int c = custom.getRGB(x, y);
                    int d = def.getRGB(x, y);
                    int dr = Math.abs(((c >> 16) & 0xFF) - ((d >> 16) & 0xFF));
                    int dg = Math.abs(((c >> 8) & 0xFF) - ((d >> 8) & 0xFF));
                    int db = Math.abs((c & 0xFF) - (d & 0xFF));
                    if (Math.max(dr, Math.max(dg, db)) > CHANNEL_DIFF) {
                        differing++;
                    }
                }
            }
            if (differing < MIN_DIFFERING_PIXELS) {
                throw new AssertionError("Expected " + customGolden + " and " + defaultGolden + " to differ over the "
                        + "painted edges, but only " + differing + " pixels differed — the sides toggle may not change the render");
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to read the sides goldens", e);
        }
    }
}
