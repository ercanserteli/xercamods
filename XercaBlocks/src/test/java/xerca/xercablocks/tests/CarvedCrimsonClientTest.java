package xerca.xercablocks.tests;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Renders a wall of emissive carved crimson blocks at night in a deterministic superflat world.
 * Asserts (via SSIM) that the frame matches the glowing golden, and — as a positive check that the
 * emissive glow is actually rendered — that it does NOT match a "no-glow" reference of the same wall.
 * Guards the emissive-overlay block state model ({@link xerca.xercablocks.client.EmissiveOverlayBlockStateModel}).
 */
@SuppressWarnings("unused")
public final class CarvedCrimsonClientTest implements FabricClientGameTest {
    // SSIM >= this to pass. 1.0 is identical; rendering the same static scene twice sits very close to 1.0.
    private static final double SSIM_THRESHOLD = 0.98;
    private static final String GOLDEN = "carved_crimson_wall_night";
    // Same wall rendered with the emissive overlay disabled; the live frame must differ from this.
    private static final String GOLDEN_NO_GLOW = "carved_crimson_wall_night_noglow";

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();
        context.runOnClient(CarvedCrimsonAnimationTestHelper::freezeAtFirstFrame);

        // Consistent settings (default) give a superflat, fixed-seed world with daylight/weather/mob cycles off.
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamerule doDaylightCycle false");
            server.runCommand("time set 18000"); // midnight: dark faces, glowing emissive carvings
            // A wall large enough to fill the whole viewport from the camera position below.
            server.runCommand("fill -8 92 0 8 108 0 xercablocks:carved_crimson_1");
            server.runCommand("gamemode spectator @a");
            // x y z yaw pitch — face north (-Z) toward the wall from 2 blocks away, centered on it.
            server.runCommand("tp @a 0.5 100.5 3 180 0");

            // Hide the HUD so only the world is captured.
            context.runOnClient(client -> client.options.hideGui = true);

            // Let the client receive the block changes and rebuild the chunk mesh.
            context.waitTicks(40);
            singleplayer.getClientLevel().waitForChunksRender();

            // The live (glowing) frame must match the glowing golden...
            context.assertScreenshotEquals(TestScreenshotComparisonOptions.of(GOLDEN)
                    .withAlgorithm(SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD))
                    .save());

            // ...and must NOT match the same wall with the emissive overlay disabled,
            // proving the glow is actually being rendered.
            assertScreenshotDiffers(context, GOLDEN_NO_GLOW);
        }
    }

    /**
     * Fails if the current frame matches {@code template} at or above the SSIM threshold.
     * Screenshot assertion failures are raised off the test thread since client-gametest 4.2.0,
     * so the mismatch is checked directly on a captured frame instead of catching an AssertionError.
     */
    private static void assertScreenshotDiffers(ClientGameTestContext context, String template) {
        Path screenshotPath = context.takeScreenshot("carved_crimson_wall_night_live");
        BufferedImage live = readImage(screenshotPath);
        BufferedImage reference = readTemplate(template);
        if (live.getWidth() != reference.getWidth() || live.getHeight() != reference.getHeight()) {
            return;
        }

        int width = live.getWidth();
        int height = live.getHeight();
        int[] livePixels = live.getRGB(0, 0, width, height, null, 0, width);
        int[] referencePixels = reference.getRGB(0, 0, width, height, null, 0, width);
        if (SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD).matchesEqualSize(livePixels, referencePixels, width, height)) {
            throw new AssertionError("Frame unexpectedly matched no-glow reference '" + template
                    + "' — the carved crimson emissive glow appears to be missing");
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
        String resource = "/templates/" + template + ".png";
        try (InputStream stream = CarvedCrimsonClientTest.class.getResourceAsStream(resource)) {
            return ImageIO.read(Objects.requireNonNull(stream, "Missing template resource " + resource));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
