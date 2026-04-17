package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;

import static xerca.xercapaint.Mod.MOD_ID;

public class CanvasTagCompatibilityGameTests {
    private static final RecipeCanvasCloning CLONING_RECIPE = new RecipeCanvasCloning(
            new ResourceLocation(MOD_ID, "canvas_cloning_test"),
            CraftingBookCategory.MISC
    );

    private static final class DummyMenu extends AbstractContainerMenu {
        private DummyMenu() {
            super(null, -1);
        }

        @Override
        public ItemStack quickMoveStack(Player player, int slotId) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }

    private static CraftingContainer createGrid(int width, int height) {
        return new TransientCraftingContainer(new DummyMenu(), width, height);
    }

    @GameTest(template = "xercapaint:basic_test", batch = "canvas_compat")
    public static void foreignTagAloneIsNotCanvasData(GameTestHelper helper) {
        ItemStack stack = new ItemStack(Items.ITEM_CANVAS);
        stack.getOrCreateTag().putString("othermod:othertag", "dev");

        helper.assertTrue(!ItemCanvas.hasCanvasData(stack), "Expected foreign-only NBT to not be treated as canvas data");
        helper.succeed();
    }

    @GameTest(template = "xercapaint:basic_test", batch = "canvas_compat")
    public static void pixelsWithoutNameAreNotCanvasData(GameTestHelper helper) {
        ItemStack stack = new ItemStack(Items.ITEM_CANVAS);
        ItemCanvas canvas = (ItemCanvas) stack.getItem();
        int pixelCount = canvas.getWidth() * canvas.getHeight();
        stack.getOrCreateTag().putIntArray("pixels", new int[pixelCount]);

        helper.assertTrue(!ItemCanvas.hasCanvasData(stack), "Expected missing canvas name to be treated as incomplete canvas data");
        helper.succeed();
    }

    @GameTest(template = "xercapaint:basic_test", batch = "canvas_compat")
    public static void cloningTreatsForeignTaggedFreshCanvasAsFresh(GameTestHelper helper) {
        ItemStack original = new ItemStack(Items.ITEM_CANVAS);
        ItemCanvas originalItem = (ItemCanvas) original.getItem();
        int pixelCount = originalItem.getWidth() * originalItem.getHeight();

        CompoundTag originalTag = original.getOrCreateTag();
        originalTag.putString("name", "compat_canvas");
        originalTag.putInt("v", 1);
        originalTag.putInt("generation", 1);
        originalTag.putIntArray("pixels", new int[pixelCount]);

        ItemStack freshWithForeignTag = new ItemStack(Items.ITEM_CANVAS);
        freshWithForeignTag.getOrCreateTag().putString("othermod:othertag", "dev");

        CraftingContainer grid = createGrid(2, 2);
        grid.setItem(0, original);
        grid.setItem(1, freshWithForeignTag);

        helper.assertTrue(CLONING_RECIPE.matches(grid, helper.getLevel()), "Expected recipe to match with foreign-tagged fresh canvas");
        ItemStack result = CLONING_RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(!result.isEmpty(), "Expected cloning result to be present");
        helper.assertTrue(result.getTag() != null && result.getTag().getInt("generation") == 2, "Expected generation to increment to 2");
        helper.succeed();
    }
}
