package xerca.xercafood.common.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.item.ItemTeacup;
import xerca.xercafood.common.item.Items;

public class RecipeTeaSugaring extends CustomRecipe {
    public static Item getTeacup(int sugarAmount) {
        Item res = net.minecraft.world.item.Items.AIR;
        switch (sugarAmount) {
            case 0 -> res = Items.FULL_TEACUP_0;
            case 1 -> res = Items.FULL_TEACUP_1;
            case 2 -> res = Items.FULL_TEACUP_2;
            case 3 -> res = Items.FULL_TEACUP_3;
            case 4 -> res = Items.FULL_TEACUP_4;
            case 5 -> res = Items.FULL_TEACUP_5;
            case 6 -> res = Items.FULL_TEACUP_6;
        }
        return res;
    }

    public RecipeTeaSugaring(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInput inv, Level worldIn) {
        int i = 0;
        ItemStack teacupStack = ItemStack.EMPTY;
        ItemTeacup teacup = null;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemTeacup) {
                    if (!teacupStack.isEmpty()) {
                        return false;
                    }

                    teacupStack = itemstack;
                    teacup = (ItemTeacup) itemstack.getItem();
                } else {
                    if (itemstack.getItem() != net.minecraft.world.item.Items.SUGAR || i >= 6) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !teacupStack.isEmpty() && teacup != null && i > 0 && (teacup.getSugarAmount() + i) <= 6;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        int i = 0;
        ItemStack teacupStack = ItemStack.EMPTY;
        ItemTeacup teacup = null;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemTeacup) {
                    if (!teacupStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    teacupStack = itemstack;
                    teacup = (ItemTeacup) itemstack.getItem();
                } else {
                    if (itemstack.getItem() != net.minecraft.world.item.Items.SUGAR || i >= 6) {
                        return ItemStack.EMPTY;
                    }
                    ++i;
                }
            }
        }

        if (!teacupStack.isEmpty() && teacup != null && i >= 1 && (teacup.getSugarAmount() + i) <= 6) {
            return new ItemStack(getTeacup(teacup.getSugarAmount() + i));
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_SUGARING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
