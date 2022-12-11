package xerca.xercafood.common.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.item.ItemTeapot;
import xerca.xercafood.common.item.Items;

public class RecipeTeaPouring extends CustomRecipe {
    public static Item getHotTeapot(int teaAmount) {
        Item res = net.minecraft.world.item.Items.AIR;
        switch (teaAmount) {
            case 1 -> res = Items.ITEM_HOT_TEAPOT_1;
            case 2 -> res = Items.ITEM_HOT_TEAPOT_2;
            case 3 -> res = Items.ITEM_HOT_TEAPOT_3;
            case 4 -> res = Items.ITEM_HOT_TEAPOT_4;
            case 5 -> res = Items.ITEM_HOT_TEAPOT_5;
            case 6 -> res = Items.ITEM_HOT_TEAPOT_6;
            case 7 -> res = Items.ITEM_HOT_TEAPOT_7;
        }
        return res;
    }

    public RecipeTeaPouring(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
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
                    if(!teapot.isHot()){
                        return false;
                    }
                } else {
                    if (itemstack.getItem() != Items.ITEM_TEACUP || i > 6) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !teapotStack.isEmpty() && teapot != null && i > 0 && teapot.isHot() && (teapot.getTeaAmount() - i) >= 0;
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
                    if(!teapot.isHot()){
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (itemstack.getItem() != Items.ITEM_TEACUP || i > 6) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        if (!teapotStack.isEmpty() && i >= 1 && teapot != null && (teapot.getTeaAmount() - i) >= 0) {
            return new ItemStack(Items.ITEM_FULL_TEACUP_0, i);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        int teacupCount = 0;
        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ITEM_TEACUP && teacupCount <= 6) {
                    ++teacupCount;
                }
            }
        }

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            Item item = itemstack.getItem();
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem().getDefaultInstance());
            } else if (itemstack.getItem() instanceof ItemTeapot oldTeapot) {
                if(oldTeapot.getTeaAmount() > teacupCount){
                    ItemStack remainingStack = new ItemStack(getHotTeapot(oldTeapot.getTeaAmount() - teacupCount));
                    if(!remainingStack.isEmpty()){
                        nonnulllist.set(i, remainingStack);
                    }
                }else{
                    nonnulllist.set(i, new ItemStack(Items.ITEM_TEAPOT));
                }
                break;
            }
        }

        return nonnulllist;
    }

    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_POURING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}