package xerca.xercamod.common.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemFlask;
import xerca.xercamod.common.item.Items;

public class RecipeFlaskMilkFilling extends SpecialRecipe {
    public RecipeFlaskMilkFilling(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(!Config.isEnderFlaskEnabled()){
            return false;
        }

        int i = 0;
        ItemStack flaskStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemFlask) {
                    if (!flaskStack.isEmpty()) {
                        return false;
                    }
                    flaskStack = itemstack;
                    if(!PotionUtils.getPotionFromItem(flaskStack).equals(Potions.EMPTY)){
                        return false;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof MilkBucketItem)) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !flaskStack.isEmpty() && i > 0 && (ItemFlask.getCharges(flaskStack) + i) <= ItemFlask.maxCharges;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        if(!Config.isEnderFlaskEnabled()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        ItemStack flaskStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemFlask) {
                    if (!flaskStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    flaskStack = itemstack;
                    if(!PotionUtils.getPotionFromItem(flaskStack).equals(Potions.EMPTY)){
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof MilkBucketItem)) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }
        int oldCharges = ItemFlask.getCharges(flaskStack);
        if (!flaskStack.isEmpty() && i > 0 && (oldCharges + i) <= ItemFlask.maxCharges) {
            ItemStack resultStack = new ItemStack(Items.FLASK_MILK);
            ItemFlask.setCharges(resultStack, oldCharges + i);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_FLASK_MILK_FILLING;
    }


    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }
}