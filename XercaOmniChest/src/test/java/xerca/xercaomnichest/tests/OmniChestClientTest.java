package xerca.xercaomnichest.tests;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.minecraft.client.CloudStatus;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.world.entity.player.ChatVisiblity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Renders an omni chest placed in the world while the player holds another one in the selected
 * hotbar slot, with the HUD visible. One SSIM screenshot covers the block-entity renderer,
 * the first-person hand model, and the GUI (hotbar slot) item model at once; the hotbar slot is
 * additionally compared on its own so that GUI item model regressions cannot hide inside the
 * full-frame average.
 */
@SuppressWarnings("unused")
public final class OmniChestClientTest implements FabricClientGameTest {
    // SSIM >= this to pass. 1.0 is identical; rendering the same static scene twice sits very close to 1.0.
    private static final double SSIM_THRESHOLD = 0.98;
    private static final String GOLDEN = "omni_chest_world_hand_hotbar";
    // Region covering the single filled hotbar slot (854x480 framebuffer, GUI scale 2).
    private static final int HOTBAR_X = 250;
    private static final int HOTBAR_Y = 442;
    private static final int HOTBAR_WIDTH = 32;
    private static final int HOTBAR_HEIGHT = 32;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();
        // Clouds drift with game time and chat lines would fade mid-run; both break determinism.
        // hideGui is transient (not restored by restoreDefaultGameOptions), so force the HUD visible.
        // MINIMAL suppresses the omni chest's particles, whose random count
        // Short render distance keeps the aliasing-prone distant grass horizon out of frame
        context.runOnClient(client -> {
            client.options.hideGui = false;
            client.options.cloudStatus().set(CloudStatus.OFF);
            client.options.chatVisibility().set(ChatVisiblity.HIDDEN);
            client.options.particles().set(ParticleStatus.MINIMAL);
            client.options.renderDistance().set(4);
        });

        // Consistent settings (default) give a superflat, fixed-seed world with daylight/weather/mob cycles off.
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamerule sendCommandFeedback false");
            server.runCommand("gamerule doDaylightCycle false");
            server.runCommand("time set 6000");
            server.runCommand("gamemode creative @a");
            server.runCommand("setblock 0 -60 0 xercaomnichest:omni_chest");
            server.runCommand("item replace entity @a hotbar.0 with xercaomnichest:omni_chest");
            // x y z yaw pitch — stand on the superflat ground facing north, looking down at the chest.
            server.runCommand("tp @a 0.5 -60 3 180 25");

            // Let the equip animation finish and the held-item name label above the hotbar fade out.
            context.waitTicks(100);
            singleplayer.getClientLevel().waitForChunksRender();

            context.assertScreenshotEquals(TestScreenshotComparisonOptions.of(GOLDEN)
                    .withAlgorithm(SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD))
                    .saveWithFileName(GOLDEN + "_live")
                    .disableCounterPrefix());

            assertHotbarMatchesGolden();
        }
    }

    /**
     * Compares only the hotbar slot region against the golden, since a regression confined to the
     * small GUI item icon could pass the whole-frame mean SSIM unnoticed. Reuses the frame the
     * full-frame assertion saved: a fresh capture drifts sub-pixel from tick interpolation.
     */
    private static void assertHotbarMatchesGolden() {
        BufferedImage live = readImage(Path.of("screenshots", GOLDEN + "_live.png"));
        BufferedImage golden = readTemplate(GOLDEN);
        int[] livePixels = live.getRGB(HOTBAR_X, HOTBAR_Y, HOTBAR_WIDTH, HOTBAR_HEIGHT, null, 0, HOTBAR_WIDTH);
        int[] goldenPixels = golden.getRGB(HOTBAR_X, HOTBAR_Y, HOTBAR_WIDTH, HOTBAR_HEIGHT, null, 0, HOTBAR_WIDTH);
        if (!SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD).matchesEqualSize(livePixels, goldenPixels, HOTBAR_WIDTH, HOTBAR_HEIGHT)) {
            throw new AssertionError("Hotbar region (" + HOTBAR_X + "," + HOTBAR_Y + " " + HOTBAR_WIDTH + "x"
                    + HOTBAR_HEIGHT + ") does not match golden '" + GOLDEN + "'");
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
