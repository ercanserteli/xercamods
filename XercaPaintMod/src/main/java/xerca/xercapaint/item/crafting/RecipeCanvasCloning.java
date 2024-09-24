package xerca.xercapaint.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
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
    public boolean matches(CraftingContainer inv, @NotNull Level worldIn) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() instanceof ItemCanvas && itemstack1.hasTag() && WrittenBookItem.getGeneration(itemstack1) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return false;
                    }
                    if (!freshCanvas.isEmpty() && !((ItemCanvas)freshCanvas.getItem()).getCanvasType().equals(((ItemCanvas) itemstack1.getItem()).getCanvasType())){
                        return false;
                    }

                    orgCanvas = itemstack1;
                } else if (itemstack1.getItem() instanceof ItemCanvas && !itemstack1.hasTag()) {
                    if (!freshCanvas.isEmpty()) {
                        return false;
                    }
                    if (!orgCanvas.isEmpty() && !((ItemCanvas)orgCanvas.getItem()).getCanvasType().equals(((ItemCanvas) itemstack1.getItem()).getCanvasType())){
                        return false;
                    }

                    freshCanvas = itemstack1;
                }
            }
        }

        return !orgCanvas.isEmpty() && !freshCanvas.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingContainer inv, @NotNull RegistryAccess access) {
        ItemStack orgCanvas = ItemStack.EMPTY;
        ItemStack freshCanvas = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() instanceof ItemCanvas && itemstack1.hasTag() && WrittenBookItem.getGeneration(itemstack1) > 0) {
                    if (!orgCanvas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!freshCanvas.isEmpty() && !((ItemCanvas)freshCanvas.getItem()).getCanvasType().equals(((ItemCanvas) itemstack1.getItem()).getCanvasType())){
                        return ItemStack.EMPTY;
                    }

                    orgCanvas = itemstack1;
                } else if (itemstack1.getItem() instanceof ItemCanvas && !itemstack1.hasTag()) {
                    if (!freshCanvas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!orgCanvas.isEmpty() && !((ItemCanvas)orgCanvas.getItem()).getCanvasType().equals(((ItemCanvas) itemstack1.getItem()).getCanvasType())){
                        return ItemStack.EMPTY;
                    }

                    freshCanvas = itemstack1;
                }
            }
        }

        int gen = WrittenBookItem.getGeneration(orgCanvas);
        if (!orgCanvas.isEmpty() && orgCanvas.hasTag() && orgCanvas.getTag() != null && !freshCanvas.isEmpty() && !freshCanvas.hasTag() && gen < 3 && gen > 0) {
            ItemStack resultStack = new ItemStack(orgCanvas.getItem());
            CompoundTag compoundTag = orgCanvas.getTag().copy();
            compoundTag.putInt("generation", gen + 1);
            resultStack.setTag(compoundTag);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.getItem() instanceof ItemCanvas && itemstack.hasTag() && WrittenBookItem.getGeneration(itemstack) > 0) {
                ItemStack itemstack1 = itemstack.copy();
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