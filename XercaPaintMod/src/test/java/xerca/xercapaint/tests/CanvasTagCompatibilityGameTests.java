package xerca.xercapaint.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityCanvas;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class CanvasTagCompatibilityGameTests {

    private static final RecipeCanvasCloning CLONING_RECIPE = RecipeCanvasCloning.INSTANCE;

    private static CraftingInput createGrid(int width, int height, ItemStack... input) {
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(width * height, ItemStack.EMPTY));
        for (int i = 0; i < input.length && i < stacks.size(); i++) {
            stacks.set(i, input[i]);
        }
        return CraftingInput.of(width, height, stacks);
    }

    @GameTest
    public void foreignTagAloneIsNotCanvasData(GameTestHelper helper) {
        ItemStack stack = new ItemStack(Items.ITEM_CANVAS);
        CompoundTag foreign = new CompoundTag();
        foreign.putString("othermod:othertag", "dev");
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(foreign));

        TestAsserts.assertTrue(helper, stack.get(Items.CANVAS_PIXELS) == null, "Expected foreign-only data to not define pixels");
        TestAsserts.assertTrue(helper, stack.get(Items.CANVAS_GENERATION) == null, "Expected foreign-only data to not define generation");
        helper.succeed();
    }

    @GameTest
    public void cloningTreatsForeignTaggedFreshCanvasAsFresh(GameTestHelper helper) {
        ItemStack original = new ItemStack(Items.ITEM_CANVAS);
        ItemCanvas originalItem = (ItemCanvas) original.getItem();
        int pixelCount = originalItem.getWidth() * originalItem.getHeight();
        original.set(Items.CANVAS_ID, "compat_canvas");
        original.set(Items.CANVAS_VERSION, 1);
        original.set(Items.CANVAS_GENERATION, 1);
        original.set(Items.CANVAS_PIXELS, new ArrayList<>(Collections.nCopies(pixelCount, 0)));

        ItemStack freshWithForeignTag = new ItemStack(Items.ITEM_CANVAS);
        CompoundTag foreign = new CompoundTag();
        foreign.putString("othermod:othertag", "dev");
        freshWithForeignTag.set(DataComponents.CUSTOM_DATA, CustomData.of(foreign));

        CraftingInput grid = createGrid(2, 2, original, freshWithForeignTag);

        TestAsserts.assertTrue(helper, CLONING_RECIPE.matches(grid, helper.getLevel()), "Expected recipe to match with foreign-tagged fresh canvas");
        ItemStack result = CLONING_RECIPE.assemble(grid);
        TestAsserts.assertTrue(helper, !result.isEmpty(), "Expected cloning result to be present");
        TestAsserts.assertTrue(helper, result.getOrDefault(Items.CANVAS_GENERATION, 0) == 2, "Expected generation to increment to 2");
        helper.succeed();
    }

    @GameTest
    public void canvasRotationDoesNotClobberVanillaRotationKey(GameTestHelper helper) {
        ItemStack stack = new ItemStack(Items.ITEM_CANVAS);
        stack.set(Items.CANVAS_ID, "rotation_canvas");
        stack.set(Items.CANVAS_VERSION, 1);

        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        EntityCanvas canvas = new EntityCanvas(helper.getLevel(), stack, pos, Direction.NORTH, CanvasType.SMALL, 2);

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        canvas.addAdditionalSaveData(output);
        CompoundTag tag = output.buildResult();
        TestAsserts.assertTrue(helper, !tag.contains("Rotation"), "Mod save data must not write vanilla's Rotation key");
        TestAsserts.assertTrue(helper, tag.getByteOr("CanvasRotation", (byte) -1) == 2, "NBT must record the quarter-turn under CanvasRotation");

        EntityCanvas reloaded = new EntityCanvas(Entities.CANVAS, helper.getLevel());
        reloaded.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));
        TestAsserts.assertTrue(helper, reloaded.getRotation() == 2, "Quarter-turn must survive an NBT round-trip");
        helper.succeed();
    }
}
