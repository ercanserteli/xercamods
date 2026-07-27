package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.item.crafting.RecipeCanvasCloning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static xerca.xercapaint.common.XercaPaint.MODID;

@GameTestHolder(MODID)
public class CanvasTagCompatibilityGameTests {
    private static final String BASIC_TEMPLATE = "basic_test";
    private static final String CANVAS_COMPAT_BATCH = "canvas_compat";

    private static final RecipeCanvasCloning CLONING_RECIPE = new RecipeCanvasCloning(
            new ResourceLocation(MODID, "test_canvas_cloning"), CraftingBookCategory.MISC
    );

    private static TestCraftingInput createGrid(int width, int height, ItemStack... input) {
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(width * height, ItemStack.EMPTY));
        for (int i = 0; i < input.length && i < stacks.size(); i++) {
            stacks.set(i, input[i]);
        }
        return TestCraftingInput.of(width, height, stacks);
    }

    @PrefixGameTestTemplate(false)

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_COMPAT_BATCH)
    public static void foreignTagAloneIsNotCanvasData(GameTestHelper helper) {
        ItemStack stack = new ItemStack(Items.ITEM_CANVAS.get());
        CompoundTag foreign = new CompoundTag();
        foreign.putString("othermod:othertag", "dev");
        stack.getOrCreateTag().merge(foreign);

        helper.assertTrue(!stack.getOrCreateTag().contains(ItemCanvas.TAG_PIXELS), "Expected foreign-only data to not define pixels");
        helper.assertTrue(!stack.getOrCreateTag().contains(ItemCanvas.TAG_GENERATION), "Expected foreign-only data to not define generation");
        helper.succeed();
    }

    @PrefixGameTestTemplate(false)

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_COMPAT_BATCH)
    public static void cloningTreatsForeignTaggedFreshCanvasAsFresh(GameTestHelper helper) {
        ItemStack original = new ItemStack(Items.ITEM_CANVAS.get());
        ItemCanvas originalItem = (ItemCanvas) original.getItem();
        int pixelCount = originalItem.getWidth() * originalItem.getHeight();
        CompoundTag originalTag = original.getOrCreateTag();
        originalTag.putString(ItemCanvas.TAG_CANVAS_ID, "compat_canvas");
        originalTag.putInt(ItemCanvas.TAG_VERSION, 1);
        originalTag.putInt(ItemCanvas.TAG_GENERATION, 1);
        originalTag.putIntArray(ItemCanvas.TAG_PIXELS, new int[pixelCount]);

        ItemStack freshWithForeignTag = new ItemStack(Items.ITEM_CANVAS.get());
        CompoundTag foreign = new CompoundTag();
        foreign.putString("othermod:othertag", "dev");
        freshWithForeignTag.getOrCreateTag().merge(foreign);

        TestCraftingInput grid = createGrid(2, 2, original, freshWithForeignTag);

        helper.assertTrue(CLONING_RECIPE.matches(grid, helper.getLevel()), "Expected recipe to match with foreign-tagged fresh canvas");
        ItemStack result = CLONING_RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(!result.isEmpty(), "Expected cloning result to be present");
        helper.assertTrue(ItemCanvas.getGeneration(result) == 2, "Expected generation to increment to 2");
        helper.succeed();
    }
}
