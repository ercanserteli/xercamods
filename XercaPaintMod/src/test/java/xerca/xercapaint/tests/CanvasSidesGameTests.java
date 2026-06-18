package xerca.xercapaint.tests;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityCanvas;
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
        int[] sides = CanvasSides.defaultPixels(type, false);
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
            helper.assertTrue(CanvasSides.topOffset() == 0, "Top offset must be 0");
            helper.assertTrue(CanvasSides.bottomOffset(type) == width, "Bottom offset must follow the top row");
            helper.assertTrue(CanvasSides.leftOffset(type) == 2 * width, "Left offset must follow both rows");
            helper.assertTrue(CanvasSides.rightOffset(type) == 2 * width + height, "Right offset must follow the left column");
            helper.assertTrue(CanvasSides.defaultPixels(type, false).length == CanvasSides.count(type),
                    "Default side pixels must be fully populated");
            helper.assertTrue(CanvasSides.defaultPixels(type, false)[0] == CanvasSides.DEFAULT_COLOR,
                    "Default paper side pixels must be white");
            helper.assertTrue(CanvasSides.defaultPixels(type, true)[0] == 0,
                    "Default glass side pixels must be transparent");
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

    @GameTest(template = BASIC_TEMPLATE, batch = SIDES_BATCH)
    public static void glassFlagSurvivesEntityNbtRoundTrip(GameTestHelper helper) {
        ItemStack glassStack = new ItemStack(Items.ITEM_CANVAS_GLASS);
        ItemCanvas itemCanvas = (ItemCanvas) glassStack.getItem();
        helper.assertTrue(itemCanvas.isGlass(), "ITEM_CANVAS_GLASS must report glass");
        int area = itemCanvas.getWidth() * itemCanvas.getHeight();
        glassStack.set(Items.CANVAS_ID, "glass_entity");
        glassStack.set(Items.CANVAS_VERSION, 1);
        glassStack.set(Items.CANVAS_PIXELS, new ArrayList<>(Collections.nCopies(area, 0)));

        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        EntityCanvas canvas = new EntityCanvas(helper.getLevel(), glassStack, pos, Direction.NORTH, CanvasType.SMALL, 0);
        helper.assertTrue(canvas.isGlass(), "Placed glass canvas entity must be glass");

        CompoundTag tag = new CompoundTag();
        canvas.addAdditionalSaveData(tag);
        helper.assertTrue(tag.getBoolean("glass"), "NBT must record the glass flag");

        EntityCanvas reloaded = new EntityCanvas(Entities.CANVAS, helper.getLevel());
        reloaded.readAdditionalSaveData(tag);
        helper.assertTrue(reloaded.isGlass(), "Glass flag must survive an NBT round-trip");
        helper.succeed();
    }
}
