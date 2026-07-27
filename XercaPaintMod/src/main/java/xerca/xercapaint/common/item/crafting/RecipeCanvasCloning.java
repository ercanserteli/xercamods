package xerca.xercapaint.common.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.Items;

@MethodsReturnNonnullByDefault
public class RecipeCanvasCloning extends CustomRecipe {
    private static final int MAX_GENERATION = 3;

    public RecipeCanvasCloning(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    /**
     * Two canvases clone together only if they share both size and material
     */
    private static boolean sameKind(ItemCanvas a, ItemCanvas b) {
        return a.getCanvasType() == b.getCanvasType() && a.isGlass() == b.isGlass();
    }

    private static boolean isFresh(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null || !tag.contains(ItemCanvas.TAG_GENERATION);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemCanvas itemCanvas && ItemCanvas.getGeneration(stack) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return false;
                    }
                    if (!freshCanvas.isEmpty() && !sameKind((ItemCanvas) freshCanvas.getItem(), itemCanvas)) {
                        return false;
                    }

                    orgCanvas = stack;
                } else if (stack.getItem() instanceof ItemCanvas itemCanvas && isFresh(stack)) {
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemCanvas itemCanvas && ItemCanvas.getGeneration(stack) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!freshCanvas.isEmpty() && !sameKind((ItemCanvas) freshCanvas.getItem(), itemCanvas)) {
                        return ItemStack.EMPTY;
                    }

                    orgCanvas = stack;
                } else if (stack.getItem() instanceof ItemCanvas itemCanvas && isFresh(stack)) {
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

        int gen = ItemCanvas.getGeneration(orgCanvas);
        CompoundTag orgTag = orgCanvas.getTag();
        if (!orgCanvas.isEmpty() && orgTag != null && !freshCanvas.isEmpty() && gen > 0 && gen < MAX_GENERATION) {
            ItemStack resultStack = new ItemStack(orgCanvas.getItem());
            CompoundTag compoundTag = orgTag.copy();
            compoundTag.putInt(ItemCanvas.TAG_GENERATION, gen + 1);
            resultStack.setTag(compoundTag);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.getItem() instanceof ItemCanvas && ItemCanvas.getGeneration(itemStack) > 0) {
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
        return Items.CRAFTING_SPECIAL_CANVAS_CLONING.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}
