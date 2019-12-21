package xerca.xercamod.common.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemTeapot;
import xerca.xercamod.common.item.Items;

public class RecipeTeaPouring extends SpecialRecipe {
    public RecipeTeaPouring(ResourceLocation p_i48170_1_) {
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
    public ItemStack getCraftingResult(CraftingInventory inv) {
        if(!Config.isTeaEnabled()){
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

    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        int teacupCount = 0;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ITEM_TEACUP && teacupCount <= 6) {
                    ++teacupCount;
                }
            }
        }

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.hasContainerItem()) {
                nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.getItem() instanceof ItemTeapot) {
                ItemTeapot oldTeapot = (ItemTeapot) itemstack.getItem();
                if(oldTeapot.getTeaAmount() > teacupCount){
                    String str = oldTeapot.getRegistryName().toString();
                    str = str.substring(0, str.length() - 1) + (oldTeapot.getTeaAmount() - teacupCount);
                    ItemStack remainingStack = new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(str)));
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

    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_POURING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }
}