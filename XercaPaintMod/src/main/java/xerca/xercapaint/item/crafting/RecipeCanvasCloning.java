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
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

@MethodsReturnNonnullByDefault
public class RecipeCanvasCloning extends CustomRecipe {
    public RecipeCanvasCloning(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Two canvases clone together only if they share both size and material
     */
    private static boolean sameKind(ItemCanvas a, ItemCanvas b) {
        return a.getCanvasType() == b.getCanvasType() && a.isGlass() == b.isGlass();
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemCanvas itemCanvas && stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return false;
                    }
                    if (!freshCanvas.isEmpty() && !sameKind((ItemCanvas) freshCanvas.getItem(), itemCanvas)) {
                        return false;
                    }

                    orgCanvas = stack;
                } else if (stack.getItem() instanceof ItemCanvas itemCanvas && stack.get(Items.CANVAS_GENERATION) == null) {
                    if (!freshCanvas.isEmpty()) {
                        return false;
                    }
                    if (!orgCanvas.isEmpty() && !sameKind((ItemCanvas) orgCanvas.getItem(), itemCanvas)) {
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
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemCanvas itemCanvas && stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!freshCanvas.isEmpty() && !sameKind((ItemCanvas) freshCanvas.getItem(), itemCanvas)) {
                        return ItemStack.EMPTY;
                    }

                    orgCanvas = stack;
                } else if (stack.getItem() instanceof ItemCanvas itemCanvas && stack.get(Items.CANVAS_GENERATION) == null) {
                    if (!freshCanvas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!orgCanvas.isEmpty() && !sameKind((ItemCanvas) orgCanvas.getItem(), itemCanvas)) {
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
            if (orgCanvas.get(Items.CANVAS_SIDE_PIXELS) != null) {
                resultStack.set(Items.CANVAS_SIDES_ACTIVE, orgCanvas.getOrDefault(Items.CANVAS_SIDES_ACTIVE, false));
                resultStack.set(Items.CANVAS_SIDE_PIXELS, orgCanvas.get(Items.CANVAS_SIDE_PIXELS));
            }
            ItemCanvas.updateStackSize(resultStack);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.getItem() instanceof ItemCanvas && itemStack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                ItemStack copyStack = itemStack.copy();
                copyStack.setCount(1);
                stacks.set(i, copyStack);
                break;
            }
        }

        return stacks;
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
