package xerca.xercapaint.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

@MethodsReturnNonnullByDefault
public class RecipeCanvasCloning extends CustomRecipe {
    public RecipeCanvasCloning(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput inv, @NotNull Level worldIn) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for(int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemCanvas && stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return false;
                    }
                    if (!freshCanvas.isEmpty() && !((ItemCanvas)freshCanvas.getItem()).getCanvasType().equals(((ItemCanvas) stack.getItem()).getCanvasType())){
                        return false;
                    }

                    orgCanvas = stack;
                } else if (stack.getItem() instanceof ItemCanvas && stack.get(Items.CANVAS_GENERATION) == null) {
                    if (!freshCanvas.isEmpty()) {
                        return false;
                    }
                    if (!orgCanvas.isEmpty() && !((ItemCanvas)orgCanvas.getItem()).getCanvasType().equals(((ItemCanvas) stack.getItem()).getCanvasType())){
                        return false;
                    }

                    freshCanvas = stack;
                }
            }
        }

        return !orgCanvas.isEmpty() && !freshCanvas.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingInput inv, @NotNull HolderLookup.Provider provider) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for(int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemCanvas && stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!freshCanvas.isEmpty() && !((ItemCanvas)freshCanvas.getItem()).getCanvasType().equals(((ItemCanvas) stack.getItem()).getCanvasType())){
                        return ItemStack.EMPTY;
                    }

                    orgCanvas = stack;
                } else if (stack.getItem() instanceof ItemCanvas && stack.get(Items.CANVAS_GENERATION) == null) {
                    if (!freshCanvas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!orgCanvas.isEmpty() && !((ItemCanvas)orgCanvas.getItem()).getCanvasType().equals(((ItemCanvas) stack.getItem()).getCanvasType())){
                        return ItemStack.EMPTY;
                    }

                    freshCanvas = stack;
                }
            }
        }

        int gen = orgCanvas.getOrDefault(Items.CANVAS_GENERATION, 0);
        if (!orgCanvas.isEmpty() && !freshCanvas.isEmpty() && gen > 0 && gen < 3) {
            ItemStack resultStack = new ItemStack(orgCanvas.getItem());
            resultStack.set(Items.CANVAS_GENERATION, gen + 1);
            resultStack.set(Items.CANVAS_PIXELS, orgCanvas.get(Items.CANVAS_PIXELS));
            resultStack.set(Items.CANVAS_ID, orgCanvas.get(Items.CANVAS_ID));
            resultStack.set(Items.CANVAS_VERSION, orgCanvas.get(Items.CANVAS_VERSION));
            resultStack.set(Items.CANVAS_TITLE, orgCanvas.get(Items.CANVAS_TITLE));
            resultStack.set(Items.CANVAS_AUTHOR, orgCanvas.get(Items.CANVAS_AUTHOR));
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof ItemCanvas && stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                ItemStack itemstack1 = stack.copy();
                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_CANVAS_CLONING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}