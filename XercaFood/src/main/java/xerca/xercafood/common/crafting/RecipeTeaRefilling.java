package xerca.xercafood.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.item.ItemTeapot;
import xerca.xercafood.common.item.Items;

public class RecipeTeaRefilling extends CustomRecipe {
    public RecipeTeaRefilling(ResourceLocation location, CraftingBookCategory category) {
        super(location, category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer inv, Level worldIn) {
        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemTeapot teapot = null;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemTeapot) {
                    if (!teapotStack.isEmpty()) {
                        return false;
                    }

                    teapotStack = itemstack;
                    teapot = (ItemTeapot) itemstack.getItem();
                    if(teapot.isHot() || teapot.getTeaAmount() > 6){
                        return false;
                    }
                } else {
                    if (itemstack.getItem() != Items.ITEM_TEA_DRIED) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !teapotStack.isEmpty() && teapot != null && !teapot.isHot() && teapot.getTeaAmount() + i <= 7 && i > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingContainer inv) {
        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemTeapot teapot = null;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemTeapot) {
                    if (!teapotStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    teapotStack = itemstack;
                    teapot = (ItemTeapot) itemstack.getItem();
                    if(teapot.isHot() || teapot.getTeaAmount() > 6){
                        return ItemStack.EMPTY;
                    }
                }else {
                    if (itemstack.getItem() != Items.ITEM_TEA_DRIED) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        if (!teapotStack.isEmpty() && teapot != null && !teapot.isHot() && teapot.getTeaAmount() + i <= 7 && i > 0) {
            return new ItemStack(RecipeTeaFilling.getFullTeapot(teapot.getTeaAmount() + i));
        } else {
            return ItemStack.EMPTY;
        }
    }


    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_REFILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}