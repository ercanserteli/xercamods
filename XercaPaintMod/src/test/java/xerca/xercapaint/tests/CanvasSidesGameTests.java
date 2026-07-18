package xerca.xercapaint.tests;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
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

@SuppressWarnings("unused")
public class CanvasSidesGameTests {

    private static final RecipeCanvasCloning CLONING_RECIPE = RecipeCanvasCloning.INSTANCE;

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

    @GameTest
    public void sidesLayoutMatchesDimensions(GameTestHelper helper) {
        for (CanvasType type : CanvasType.values()) {
            int width = CanvasType.getWidth(type);
            int height = CanvasType.getHeight(type);
            TestAsserts.assertTrue(helper, CanvasSides.count(type) == 2 * width + 2 * height,
                    "Side pixel count must cover all four edges for " + type);
            TestAsserts.assertTrue(helper, CanvasSides.bottomOffset(type) == width, "Bottom offset must follow the top row");
            TestAsserts.assertTrue(helper, CanvasSides.leftOffset(type) == 2 * width, "Left offset must follow both rows");
            TestAsserts.assertTrue(helper, CanvasSides.rightOffset(type) == 2 * width + height, "Right offset must follow the left column");
            TestAsserts.assertTrue(helper, CanvasSides.defaultPixels(type, false).length == CanvasSides.count(type),
                    "Default side pixels must be fully populated");
            TestAsserts.assertTrue(helper, CanvasSides.defaultPixels(type, false)[0] == CanvasSides.DEFAULT_COLOR,
                    "Default paper side pixels must be white");
            TestAsserts.assertTrue(helper, CanvasSides.defaultPixels(type, true)[0] == 0,
                    "Default glass side pixels must be transparent");
        }
        helper.succeed();
    }

    @GameTest
    public void canvasUpdatePacketRoundTripsSides(GameTestHelper helper) {
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

        TestAsserts.assertTrue(helper, decoded.sidesActive(), "sidesActive must survive packet round-trip");
        TestAsserts.assertTrue(helper, Arrays.equals(decoded.sidePixels(), sidePixels), "Side pixels must survive packet round-trip");
        TestAsserts.assertTrue(helper, Arrays.equals(decoded.pixels(), pixels), "Front pixels must survive packet round-trip");
        helper.succeed();
    }

    @GameTest
    public void cloningCopiesSidePixels(GameTestHelper helper) {
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

        ItemStack result = CLONING_RECIPE.assemble(createGrid(2, 2, original, new ItemStack(Items.ITEM_CANVAS)));

        TestAsserts.assertTrue(helper, !result.isEmpty(), "Expected a clone result");
        TestAsserts.assertTrue(helper, Boolean.TRUE.equals(result.get(Items.CANVAS_SIDES_ACTIVE)), "Clone must copy sidesActive");
        TestAsserts.assertTrue(helper, sideList.equals(result.get(Items.CANVAS_SIDE_PIXELS)), "Clone must copy side pixels");
        helper.succeed();
    }

    @GameTest
    public void glassFlagSurvivesEntityNbtRoundTrip(GameTestHelper helper) {
        ItemStack glassStack = new ItemStack(Items.ITEM_CANVAS_GLASS);
        ItemCanvas itemCanvas = (ItemCanvas) glassStack.getItem();
        TestAsserts.assertTrue(helper, itemCanvas.isGlass(), "ITEM_CANVAS_GLASS must report glass");
        int area = itemCanvas.getWidth() * itemCanvas.getHeight();
        glassStack.set(Items.CANVAS_ID, "glass_entity");
        glassStack.set(Items.CANVAS_VERSION, 1);
        glassStack.set(Items.CANVAS_PIXELS, new ArrayList<>(Collections.nCopies(area, 0)));

        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        EntityCanvas canvas = new EntityCanvas(helper.getLevel(), glassStack, pos, Direction.NORTH, CanvasType.SMALL, 0);
        TestAsserts.assertTrue(helper, canvas.isGlass(), "Placed glass canvas entity must be glass");

        var output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        canvas.addAdditionalSaveData(output);
        CompoundTag tag = output.buildResult();
        TestAsserts.assertTrue(helper, tag.getBooleanOr("glass", false), "NBT must record the glass flag");

        EntityCanvas reloaded = new EntityCanvas(Entities.CANVAS, helper.getLevel());
        reloaded.readAdditionalSaveData(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));
        TestAsserts.assertTrue(helper, reloaded.isGlass(), "Glass flag must survive an NBT round-trip");
        helper.succeed();
    }
}
