package xerca.xercapaint.tests;

import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;
import xerca.xercapaint.packets.CanvasUpdatePacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CanvasSidesGameTests {
    private static final String BASIC_TEMPLATE = "xercapaint:basic_test";
    private static final String SIDES_BATCH = "canvas_sides";

    private static final RecipeCanvasCloning CLONING_RECIPE = new RecipeCanvasCloning(CraftingBookCategory.MISC);

    private static CraftingInput createGrid(int width, int height, ItemStack... input) {
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(width * height, ItemStack.EMPTY));
        for (int i = 0; i < input.length && i < stacks.size(); i++) {
            stacks.set(i, input[i]);
        }
        return CraftingInput.of(width, height, stacks);
    }

    private static int[] sampleSidePixels(CanvasType type) {
        int[] sides = CanvasSides.defaultPixels(type);
        for (int i = 0; i < sides.length; i++) {
            // Deterministic, distinct-looking colors per side pixel
            sides[i] = 0xFF000000 | (i * 7 + 13) % 0xFFFFFF;
        }
        return sides;
    }

    @GameTest(template = BASIC_TEMPLATE, batch = SIDES_BATCH)
    public static void sidesLayoutMatchesDimensions(GameTestHelper helper) {
        for (CanvasType type : CanvasType.values()) {
            int width = CanvasType.getWidth(type);
            int height = CanvasType.getHeight(type);
            helper.assertTrue(CanvasSides.count(type) == 2 * width + 2 * height,
                    "Side pixel count must cover all four edges for " + type);
            helper.assertTrue(CanvasSides.topOffset(type) == 0, "Top offset must be 0");
            helper.assertTrue(CanvasSides.bottomOffset(type) == width, "Bottom offset must follow the top row");
            helper.assertTrue(CanvasSides.leftOffset(type) == 2 * width, "Left offset must follow both rows");
            helper.assertTrue(CanvasSides.rightOffset(type) == 2 * width + height, "Right offset must follow the left column");
            helper.assertTrue(CanvasSides.defaultPixels(type).length == CanvasSides.count(type),
                    "Default side pixels must be fully populated");
            helper.assertTrue(CanvasSides.defaultPixels(type)[0] == CanvasSides.DEFAULT_COLOR,
                    "Default side pixels must be white");
        }
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = SIDES_BATCH)
    public static void canvasUpdatePacketRoundTripsSides(GameTestHelper helper) {
        CanvasType type = CanvasType.LARGE;
        int area = CanvasType.getWidth(type) * CanvasType.getHeight(type);
        int[] pixels = new int[area];
        Arrays.fill(pixels, 0xFF112233);
        int[] sidePixels = sampleSidePixels(type);

        PaletteUtil.CustomColor[] palette = new PaletteUtil.CustomColor[12];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = new PaletteUtil.CustomColor();
        }

        CanvasUpdatePacket original = new CanvasUpdatePacket(pixels, false, "", "canvas-sides", 3, -1, palette, type, true, sidePixels);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        original.encode(buf);
        CanvasUpdatePacket decoded = CanvasUpdatePacket.decode(buf);

        helper.assertTrue(decoded.sidesActive(), "sidesActive must survive packet round-trip");
        helper.assertTrue(Arrays.equals(decoded.sidePixels(), sidePixels), "Side pixels must survive packet round-trip");
        helper.assertTrue(Arrays.equals(decoded.pixels(), pixels), "Front pixels must survive packet round-trip");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = SIDES_BATCH)
    public static void cloningCopiesSidePixels(GameTestHelper helper) {
        ItemStack original = new ItemStack(Items.ITEM_CANVAS);
        ItemCanvas itemCanvas = (ItemCanvas) original.getItem();
        CanvasType type = itemCanvas.getCanvasType();
        int pixelCount = itemCanvas.getWidth() * itemCanvas.getHeight();
        original.set(Items.CANVAS_ID, "sides_clone");
        original.set(Items.CANVAS_VERSION, 1);
        original.set(Items.CANVAS_PIXELS, new ArrayList<>(Collections.nCopies(pixelCount, 0)));
        original.set(Items.CANVAS_GENERATION, 1);
        original.set(Items.CANVAS_SIDES_ACTIVE, true);
        List<Integer> sideList = Arrays.stream(sampleSidePixels(type)).boxed().toList();
        original.set(Items.CANVAS_SIDE_PIXELS, sideList);

        ItemStack result = CLONING_RECIPE.assemble(createGrid(2, 2, original, new ItemStack(Items.ITEM_CANVAS)),
                helper.getLevel().registryAccess());

        helper.assertTrue(!result.isEmpty(), "Expected a clone result");
        helper.assertTrue(Boolean.TRUE.equals(result.get(Items.CANVAS_SIDES_ACTIVE)), "Clone must copy sidesActive");
        helper.assertTrue(sideList.equals(result.get(Items.CANVAS_SIDE_PIXELS)), "Clone must copy side pixels");
        helper.succeed();
    }
}
