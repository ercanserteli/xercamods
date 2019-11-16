package xerca.xercamod.common.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamod.common.item.Items;

public class RecipeTeaFilling extends SpecialRecipe {
    public RecipeTeaFilling(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemStack bucketStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ITEM_TEAPOT) {
                    if (!teapotStack.isEmpty()) {
                        return false;
                    }
                    teapotStack = itemstack;
                }else if (itemstack.getItem() == net.minecraft.item.Items.WATER_BUCKET) {
                    if (!bucketStack.isEmpty()) {
                        return false;
                    }
                    bucketStack = itemstack;
                } else {
                    if (itemstack.getItem() != Items.ITEM_TEA_DRIED) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !teapotStack.isEmpty() && !bucketStack.isEmpty() && i > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemStack bucketStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ITEM_TEAPOT) {
                    if (!teapotStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    teapotStack = itemstack;
                }else if (itemstack.getItem() == net.minecraft.item.Items.WATER_BUCKET) {
                    if (!bucketStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    bucketStack = itemstack;
                } else {
                    if (itemstack.getItem() != Items.ITEM_TEA_DRIED) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        if (!teapotStack.isEmpty() && !bucketStack.isEmpty() && i > 0) {
            String str = Items.ITEM_FULL_TEAPOT_1.getRegistryName().toString();
            str = str.substring(0, str.length() - 1) + i;
            return new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(str)));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_FILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }
}