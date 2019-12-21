package xerca.xercamod.common.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemTeacup;
import xerca.xercamod.common.item.Items;

public class RecipeTeaSugaring extends SpecialRecipe {
    public RecipeTeaSugaring(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(!Config.isTeaEnabled()){
            return false;
        }

        int i = 0;
        ItemStack teacupStack = ItemStack.EMPTY;
        ItemTeacup teacup = null;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemTeacup) {
                    if (!teacupStack.isEmpty()) {
                        return false;
                    }

                    teacupStack = itemstack;
                    teacup = (ItemTeacup) itemstack.getItem();
                } else {
                    if (itemstack.getItem() != net.minecraft.item.Items.SUGAR || i >= 6) {
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
    public ItemStack getCraftingResult(CraftingInventory inv) {
        if(!Config.isTeaEnabled()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        ItemStack teacupStack = ItemStack.EMPTY;
        ItemTeacup teacup = null;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemTeacup) {
                    if (!teacupStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    teacupStack = itemstack;
                    teacup = (ItemTeacup) itemstack.getItem();
                } else {
                    if (itemstack.getItem() != net.minecraft.item.Items.SUGAR || i >= 6) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        if (!teacupStack.isEmpty() && teacup != null && i >= 1 && (teacup.getSugarAmount() + i) <= 6) {
            String str = teacup.getRegistryName().toString();
            str = str.substring(0, str.length() - 1) + (teacup.getSugarAmount() + i);
            return new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(str)));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_SUGARING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }
}