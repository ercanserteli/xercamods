package xerca.xercamod.common.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemTeapot;
import xerca.xercamod.common.item.Items;

public class RecipeTeaRefilling extends SpecialRecipe {
    public RecipeTeaRefilling(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(!Config.TEA_ENABLE.get()){
            return false;
        }

        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemTeapot teapot = null;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
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
    public ItemStack getCraftingResult(CraftingInventory inv) {
        if(!Config.TEA_ENABLE.get()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemTeapot teapot = null;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
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
            String str = teapot.getRegistryName().toString();
            str = str.substring(0, str.length() - 1) + (teapot.getTeaAmount() + i);
            return new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(str)));
        } else {
            return ItemStack.EMPTY;
        }
    }


    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_REFILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }
}