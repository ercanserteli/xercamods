package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.item.Items;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;

import static xerca.xercapaint.client.PaintClientTests.check;

/**
 * Client gametest for canvas items rendered in the GUI (hotbar). Guards two regressions:
 * a glass canvas must draw its pale-blue tint sheet in the GUI (the {@code gui_tint} special-model
 * variant selected via {@code minecraft:display_context}), and repainting a canvas must refresh the
 * cached GUI item render (the canvas version is part of the extracted special-model argument, which
 * keys the GUI item-atlas cache).
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class CanvasGuiItemClientTest implements FabricClientGameTest {
    private static final int GLASS_SLOT = 4;
    private static final int PAPER_SLOT = 6;
    private static final int TRANSPARENT = 0x00000000;
    private static final int RED = 0xFFE01818;
    private static final int BLUE = 0xFF1830E0;
    // The GUI tint sheet is pale blue at 25% alpha over the dark backdrop: the slot interior must get
    // clearly brighter and shift towards blue relative to the empty slot.
    private static final int MIN_BRIGHTNESS_GAIN = 15;
    private static final int MIN_BLUE_SHIFT = 4;
    // A solid one-colour painting dominates the slot interior; the dominant channel must clearly win.
    private static final int MIN_CHANNEL_DOMINANCE = 40;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            // A uniform dark backdrop behind the hotbar so slot-interior colour averages are stable.
            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamerule doDaylightCycle false");
            server.runCommand("time set 1000");
            server.runCommand("fill -4 110 -3 4 110 8 minecraft:black_concrete");
            server.runCommand("fill -16 111 5 16 141 5 minecraft:black_concrete");
            server.runCommand("tp @p 0.5 111 3 0 0");
            // Earlier tests hide the HUD and restoreDefaultGameOptions does not reset it; the hotbar must show.
            context.runOnClient(client -> {
                if (client.gui.hud.isHidden()) client.gui.hud.toggle();
            });
            context.waitTicks(40);
            singleplayer.getClientLevel().waitForChunksRender();

            int[] glassRect = slotInteriorRect(context, GLASS_SLOT);
            int[] paperRect = slotInteriorRect(context, PAPER_SLOT);
            int[] emptySlot = avgRgb(context.takeScreenshot("canvas_gui_empty_slot"), glassRect);

            // Glass canvas with a fully transparent painting: only the GUI tint sheet can change the
            // slot interior, so the tint must show up as a brighter, blue-shifted interior.
            giveCanvas(context, singleplayer, GLASS_SLOT, Items.ITEM_CANVAS_GLASS, "gui_tint_glass", 1, TRANSPARENT);
            int[] tinted = avgRgb(context.takeScreenshot("canvas_gui_glass_tint"), glassRect);
            check(brightness(tinted) - brightness(emptySlot) >= MIN_BRIGHTNESS_GAIN,
                    "Expected the glass canvas GUI tint to brighten the slot interior, got "
                            + Arrays.toString(emptySlot) + " -> " + Arrays.toString(tinted));
            check((tinted[2] - tinted[0]) - (emptySlot[2] - emptySlot[0]) >= MIN_BLUE_SHIFT,
                    "Expected the glass canvas GUI tint to shift the slot interior towards blue, got "
                            + Arrays.toString(emptySlot) + " -> " + Arrays.toString(tinted));

            // Paper canvas painted solid red, then repainted solid blue with a bumped version: the GUI
            // item render must pick up the new painting instead of the cached red one.
            giveCanvas(context, singleplayer, PAPER_SLOT, Items.ITEM_CANVAS, "gui_version_paper", 1, RED);
            int[] red = avgRgb(context.takeScreenshot("canvas_gui_painting_v1"), paperRect);
            check(red[0] - red[2] >= MIN_CHANNEL_DOMINANCE,
                    "Expected the red painting to dominate the slot interior, got " + Arrays.toString(red));

            giveCanvas(context, singleplayer, PAPER_SLOT, Items.ITEM_CANVAS, "gui_version_paper", 2, BLUE);
            int[] blue = avgRgb(context.takeScreenshot("canvas_gui_painting_v2"), paperRect);
            check(blue[2] - blue[0] >= MIN_CHANNEL_DOMINANCE,
                    "Expected the repainted (blue, v2) canvas to refresh the GUI render, got " + Arrays.toString(blue));
        }
    }

    private static void giveCanvas(ClientGameTestContext context, TestSingleplayerContext singleplayer,
                                   int slot, Item item, String canvasId, int version, int color) {
        singleplayer.getServer().runOnServer(server -> {
            ItemStack stack = new ItemStack(item);
            stack.set(Items.CANVAS_ID, canvasId);
            stack.set(Items.CANVAS_VERSION, version);
            int area = CanvasType.getWidth(CanvasType.SMALL) * CanvasType.getHeight(CanvasType.SMALL);
            stack.set(Items.CANVAS_PIXELS, Arrays.stream(new int[area]).map(p -> color).boxed().toList());
            server.getPlayerList().getPlayers().getFirst().getInventory().setItem(slot, stack);
        });
        context.waitFor(client -> {
            ItemStack stack = client.player.getInventory().getItem(slot);
            return stack.getItem() == item && stack.getOrDefault(Items.CANVAS_VERSION, 0) == version;
        });
        context.waitTicks(10);
    }

    // The 8x8-gui-pixel interior of the given hotbar slot's 16x16 item, in framebuffer pixels.
    private static int[] slotInteriorRect(ClientGameTestContext context, int slot) {
        return context.computeOnClient(client -> {
            int scale = client.getWindow().getGuiScale();
            int x = client.getWindow().getGuiScaledWidth() / 2 - 90 + slot * 20 + 2;
            int y = client.getWindow().getGuiScaledHeight() - 16 - 3;
            return new int[]{(x + 4) * scale, (y + 4) * scale, 8 * scale, 8 * scale};
        });
    }

    private static int[] avgRgb(Path screenshot, int[] rect) {
        BufferedImage image;
        try {
            image = ImageIO.read(screenshot.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        long r = 0;
        long g = 0;
        long b = 0;
        for (int y = rect[1]; y < rect[1] + rect[3]; y++) {
            for (int x = rect[0]; x < rect[0] + rect[2]; x++) {
                int rgb = image.getRGB(x, y);
                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += rgb & 0xFF;
            }
        }
        int area = rect[2] * rect[3];
        return new int[]{(int) (r / area), (int) (g / area), (int) (b / area)};
    }

    private static int brightness(int[] rgb) {
        return (rgb[0] + rgb[1] + rgb[2]) / 3;
    }
}
