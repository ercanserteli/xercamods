package xerca.xercablocks.tests;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.minecraft.client.CloudStatus;
import net.minecraft.world.entity.player.ChatVisiblity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Renders a carved crimson block placed in the world while the player holds another one in the
 * selected hotbar slot, with all 8 variants filling the first hotbar slots and the HUD visible.
 * One SSIM screenshot covers the world block model, the first-person hand model, and the GUI
 * (hotbar slot) item models at once; the hand and hotbar regions are additionally compared on
 * their own so localized item model regressions cannot hide inside the full-frame average.
 */
@SuppressWarnings("unused")
public final class CarvedCrimsonInventoryClientTest implements FabricClientGameTest {
    // SSIM >= this to pass. 1.0 is identical; rendering the same static scene twice sits very close to 1.0.
    private static final double SSIM_THRESHOLD = 0.98;
    private static final String GOLDEN = "carved_crimson_inventory_hand";
    // Region covering the 8 filled hotbar slots (854x480 framebuffer, GUI scale 2).
    private static final int HOTBAR_X = 250;
    private static final int HOTBAR_Y = 442;
    private static final int HOTBAR_WIDTH = 312;
    private static final int HOTBAR_HEIGHT = 32;
    private static final int HAND_X = 558;
    private static final int HAND_Y = 350;
    private static final int HAND_WIDTH = 296;
    private static final int HAND_HEIGHT = 130;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();
        // Clouds drift with game time and chat lines would fade mid-run; both break determinism.
        // hideGui is transient (not restored by restoreDefaultGameOptions) and an earlier test sets it.
        // Short render distance keeps the aliasing-prone distant grass horizon out of frame
        context.runOnClient(client -> {
            client.options.hideGui = false;
            client.options.cloudStatus().set(CloudStatus.OFF);
            client.options.chatVisibility().set(ChatVisiblity.HIDDEN);
            client.options.renderDistance().set(2);
            CarvedCrimsonAnimationTestHelper.freezeAtFirstFrame(client);
        });

        // Consistent settings (default) give a superflat, fixed-seed world with daylight/weather/mob cycles off.
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamerule sendCommandFeedback false");
            server.runCommand("gamerule doDaylightCycle false");
            server.runCommand("time set 6000");
            server.runCommand("gamemode creative @a");
            server.runCommand("setblock 0 -60 0 xercablocks:carved_crimson_1");
            for (int i = 0; i < 8; i++) {
                server.runCommand("item replace entity @a hotbar." + i + " with xercablocks:carved_crimson_" + (i + 1));
            }
            // x y z yaw pitch — stand on the superflat ground facing north, looking down at the block.
            server.runCommand("tp @a 0.5 -60 3 180 25");

            // Let the equip animation finish and the held-item name label above the hotbar fade out.
            context.waitTicks(100);
            singleplayer.getClientLevel().waitForChunksRender();

            context.assertScreenshotEquals(TestScreenshotComparisonOptions.of(GOLDEN)
                    .withAlgorithm(SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD))
                    .saveWithFileName(GOLDEN + "_live")
                    .disableCounterPrefix());

            assertItemRegionsMatchGolden();
        }
    }

    /**
     * Compares the held item and hotbar regions against the golden so localized regressions cannot
     * pass the whole-frame mean SSIM. Reuses the saved frame so every assertion inspects one render.
     */
    private static void assertItemRegionsMatchGolden() {
        BufferedImage live = readImage(Path.of("screenshots", GOLDEN + "_live.png"));
        BufferedImage golden = readTemplate(GOLDEN);
        assertRegionMatchesGolden("Held-item", live, golden, HAND_X, HAND_Y, HAND_WIDTH, HAND_HEIGHT);
        assertRegionMatchesGolden("Hotbar", live, golden, HOTBAR_X, HOTBAR_Y, HOTBAR_WIDTH, HOTBAR_HEIGHT);
    }

    private static void assertRegionMatchesGolden(String name, BufferedImage live, BufferedImage golden,
                                                  int x, int y, int width, int height) {
        int[] livePixels = live.getRGB(x, y, width, height, null, 0, width);
        int[] goldenPixels = golden.getRGB(x, y, width, height, null, 0, width);
        if (!SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD).matchesEqualSize(livePixels, goldenPixels, width, height)) {
            throw new AssertionError(name + " region (" + x + "," + y + " " + width + "x" + height
                    + ") does not match golden '" + GOLDEN + "'");
        }
    }

    private static BufferedImage readImage(Path path) {
        try {
            return ImageIO.read(path.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static BufferedImage readTemplate(String template) {
        // Read from the test resources directory, not the classpath: a golden written earlier in
        // this run (first-run auto-generation) exists on disk but not in the classpath copy.
        String resourcesPath = Objects.requireNonNull(System.getProperty("fabric.client.gametest.testModResourcesPath"),
                "fabric.client.gametest.testModResourcesPath is not set");
        return readImage(Path.of(resourcesPath, "templates", template + ".png"));
    }
}
