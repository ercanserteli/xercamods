package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

import java.util.Arrays;
import java.util.function.Predicate;

import static xerca.xercapaint.client.PaintClientTests.check;

/**
 * Client gametest for the palette GUI ({@link GuiPalette}): an incomplete palette hides the colour picker while a full
 * one shows it, dragging basic colours onto a custom slot mixes them, dragging water empties the slot again, and the
 * mixed colours persist to the server so a re-opened palette keeps them.
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class GuiPaletteClientTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        context.restoreDefaultGameOptions();

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null);

            // An incomplete palette (no basic colours) offers no colour picker.
            context.setScreen(() -> new GuiPalette(new ItemStack(Items.ITEM_PALETTE), Component.literal("empty")));
            context.waitForScreen(GuiPalette.class);
            context.waitTicks(2);
            check(!paletteComplete(context), "Expected an incomplete palette to hide the colour picker");
            context.setScreen(() -> null);

            // A full palette both shows the picker and can be given a matching held stack for persistence.
            ItemStack fullPalette = fullPalette();
            context.runOnClient(client -> client.player.setItemSlot(EquipmentSlot.MAINHAND, fullPalette.copy()));
            singleplayer.getServer().runOnServer(server ->
                    server.getPlayerList().getPlayers().getFirst().setItemSlot(EquipmentSlot.MAINHAND, fullPalette.copy()));

            context.setScreen(() -> new GuiPalette(fullPalette.copy(), Component.literal("full")));
            context.waitForScreen(GuiPalette.class);
            context.waitTicks(2);
            check(paletteComplete(context), "Expected a full palette to show the colour picker");

            double[] palettePos = palettePos(context);

            // Drag a basic colour onto the first custom slot: it mixes in one colour.
            dragBasicToCustom(context, palettePos, 0, 0);
            check(customColorCount(context, 0) == 1, "Expected one mixed colour in the custom slot after a drag");
            check(paletteDirty(context), "Expected mixing to mark the palette dirty");

            // A second basic colour into the same slot stacks up the mix.
            dragBasicToCustom(context, palettePos, 1, 0);
            check(customColorCount(context, 0) == 2, "Expected two mixed colours in the custom slot");

            // Dragging water onto the slot empties it.
            dragWaterToCustom(context, palettePos, 0);
            check(customColorCount(context, 0) == 0, "Expected water to reset the custom slot");

            // Re-mix so there is a colour to persist, then close: GuiPalette.removed() sends the update.
            dragBasicToCustom(context, palettePos, 0, 0);
            check(customColorCount(context, 0) == 1, "Expected the custom slot to hold the re-mixed colour");
            context.setScreen(() -> null);

            check(pollServer(context, singleplayer, GuiPaletteClientTest::hasPersistedCustomColor),
                    "Expected the mixed custom colour to persist to the held palette on the server");
        }
    }

    private static void dragBasicToCustom(ClientGameTestContext context, double[] palettePos, int basicIndex, int customIndex) {
        PaintClientTests.mouseDown(context,
                palettePos[0] + BasePalette.BASIC_COLOR_CENTERS[basicIndex].x,
                palettePos[1] + BasePalette.BASIC_COLOR_CENTERS[basicIndex].y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        PaintClientTests.mouseUp(context,
                palettePos[0] + BasePalette.CUSTOM_COLOR_CENTERS[customIndex].x,
                palettePos[1] + BasePalette.CUSTOM_COLOR_CENTERS[customIndex].y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static void dragWaterToCustom(ClientGameTestContext context, double[] palettePos, int customIndex) {
        PaintClientTests.mouseDown(context,
                palettePos[0] + BasePalette.WATER_CENTER.x,
                palettePos[1] + BasePalette.WATER_CENTER.y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        PaintClientTests.mouseUp(context,
                palettePos[0] + BasePalette.CUSTOM_COLOR_CENTERS[customIndex].x,
                palettePos[1] + BasePalette.CUSTOM_COLOR_CENTERS[customIndex].y, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private static ItemStack fullPalette() {
        ItemStack palette = new ItemStack(Items.ITEM_PALETTE);
        byte[] basics = new byte[16];
        Arrays.fill(basics, (byte) 1);
        palette.set(Items.PALETTE_BASIC_COLORS, basics);
        return palette;
    }

    private static boolean hasPersistedCustomColor(ServerPlayer player) {
        ItemStack palette = player.getMainHandItem();
        ItemPalette.ComponentCustomColor comp = palette.get(Items.PALETTE_CUSTOM_COLORS);
        if (comp == null) {
            return false;
        }
        for (PaletteUtil.CustomColor color : comp.colors) {
            if (color.getNumberOfColors() > 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean paletteComplete(ClientGameTestContext context) {
        return context.computeOnClient(client -> ((BasePalette) client.gui.screen()).paletteComplete);
    }

    private static boolean paletteDirty(ClientGameTestContext context) {
        return context.computeOnClient(client -> ((BasePalette) client.gui.screen()).paletteDirty);
    }

    private static int customColorCount(ClientGameTestContext context, int slot) {
        return context.computeOnClient(client -> ((BasePalette) client.gui.screen()).customColors[slot].getNumberOfColors());
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
