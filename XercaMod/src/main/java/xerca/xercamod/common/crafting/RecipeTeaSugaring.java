package xerca.xercamod.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemTeacup;
import xerca.xercamod.common.item.Items;

public class RecipeTeaSugaring extends CustomRecipe {
    public RecipeTeaSugaring(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer inv, Level worldIn) {
        if(!Config.isTeaEnabled()){
            return false;
        }

        int i = 0;
        ItemStack teacupStack = ItemStack.EMPTY;
        ItemTeacup teacup = null;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
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
    public ItemStack assemble(CraftingContainer inv) {
        if(!Config.isTeaEnabled()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        ItemStack teacupStack = ItemStack.EMPTY;
        ItemTeacup teacup = null;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
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
            String str = teacup.getRegistryName().toString();
            str = str.substring(0, str.length() - 1) + (teacup.getSugarAmount() + i);
            return new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(str)));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_SUGARING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}