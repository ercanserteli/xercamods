package xerca.xercapaint.common.item.crafting;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.Items;

@MethodsReturnNonnullByDefault
public class RecipeCanvasCloning extends SpecialRecipe {
    public RecipeCanvasCloning(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
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
    public ItemStack assemble(CraftingInventory inv) {
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
        if (!orgCanvas.isEmpty() && orgCanvas.hasTag() && !freshCanvas.isEmpty() && !freshCanvas.hasTag() && gen < 3 && gen > 0) {
            ItemStack resultStack = new ItemStack(orgCanvas.getItem());
            CompoundNBT nbttagcompound = orgCanvas.getTag().copy();
            nbttagcompound.putInt("generation", gen + 1);
            resultStack.setTag(nbttagcompound);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.hasContainerItem()) {
                nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.getItem() instanceof ItemCanvas && itemstack.hasTag() && WrittenBookItem.getGeneration(itemstack) > 0) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
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