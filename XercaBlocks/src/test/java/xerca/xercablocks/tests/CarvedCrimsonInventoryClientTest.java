package xerca.xercablocks.tests;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.minecraft.client.CloudStatus;
import net.minecraft.world.entity.player.ChatVisiblity;

/**
 * Renders a carved crimson block placed in the world while the player holds another one in the
 * selected hotbar slot, with the HUD visible. One SSIM screenshot covers the world block model,
 * the first-person hand model, and the GUI (hotbar slot) item model at once.
 */
public final class CarvedCrimsonInventoryClientTest implements FabricClientGameTest {
    // SSIM >= this to pass. 1.0 is identical; rendering the same static scene twice sits very close to 1.0.
    private static final double SSIM_THRESHOLD = 0.98;
    private static final String GOLDEN = "carved_crimson_inventory_hand";

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();
        // Clouds drift with game time and chat lines would fade mid-run; both break determinism.
        // hideGui is transient (not restored by restoreDefaultGameOptions) and an earlier test sets it.
        context.runOnClient(client -> {
            client.options.hideGui = false;
            client.options.cloudStatus().set(CloudStatus.OFF);
            client.options.chatVisibility().set(ChatVisiblity.HIDDEN);
        });

        // Consistent settings (default) give a superflat, fixed-seed world with daylight/weather/mob cycles off.
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientWorld().waitForChunksRender();

            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamerule sendCommandFeedback false");
            server.runCommand("gamerule doDaylightCycle false");
            server.runCommand("time set 6000");
            server.runCommand("gamemode creative @a");
            server.runCommand("setblock 0 -60 0 xercablocks:carved_crimson_1");
            server.runCommand("item replace entity @a hotbar.0 with xercablocks:carved_crimson_1");
            // x y z yaw pitch — stand on the superflat ground facing north, looking down at the block.
            server.runCommand("tp @a 0.5 -60 3 180 25");

            // Let the equip animation finish and the held-item name label above the hotbar fade out.
            context.waitTicks(100);
            singleplayer.getClientWorld().waitForChunksRender();

            context.assertScreenshotEquals(TestScreenshotComparisonOptions.of(GOLDEN)
                    .withAlgorithm(SsimComparisonAlgorithm.withThreshold(SSIM_THRESHOLD))
                    .save());
        }
    }
}
