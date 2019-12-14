package xerca.xercamod.common.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemFlask;
import xerca.xercamod.common.item.Items;

public class RecipeFlaskFilling extends SpecialRecipe {
    public RecipeFlaskFilling(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(!Config.ENDER_FLASK_ENABLE.get()){
            return false;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        ItemStack flaskStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.FLASK) {
                    if (!flaskStack.isEmpty()) {
                        return false;
                    }
                    flaskStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotionFromItem(flaskStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return false;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof PotionItem)) {
                        return false;
                    }
                    if(potionType.equals(Potions.EMPTY)){
                        potionType = PotionUtils.getPotionFromItem(itemstack);
                    }else if(!PotionUtils.getPotionFromItem(itemstack).equals(potionType)){
                        return false;
                    }

                    if(!currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !flaskStack.isEmpty() && i > 0 && !potionType.equals(Potions.EMPTY) && (ItemFlask.getCharges(flaskStack) + i) <= ItemFlask.maxCharges;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(CraftingInventory inv) {
        if(!Config.ENDER_FLASK_ENABLE.get()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        ItemStack flaskStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.FLASK) {
                    if (!flaskStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    flaskStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotionFromItem(flaskStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof PotionItem)) {
                        return ItemStack.EMPTY;
                    }
                    if(potionType.equals(Potions.EMPTY)){
                        potionType = PotionUtils.getPotionFromItem(itemstack);
                    }else if(!PotionUtils.getPotionFromItem(itemstack).equals(potionType)){
                        return ItemStack.EMPTY;
                    }

                    if(!currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        int oldCharges = ItemFlask.getCharges(flaskStack);
        if (!flaskStack.isEmpty() && i > 0 && !potionType.equals(Potions.EMPTY) && (oldCharges + i) <= ItemFlask.maxCharges) {
            ItemStack resultStack = new ItemStack(Items.FLASK);
            PotionUtils.addPotionToItemStack(resultStack, potionType);
            ItemFlask.setCharges(resultStack, oldCharges + i);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.hasContainerItem()) {
                nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.getItem() instanceof PotionItem) {
                ItemStack bottle = new ItemStack(net.minecraft.item.Items.GLASS_BOTTLE);
                nonnulllist.set(i, bottle);
            }
        }

        return nonnulllist;
    }

    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_FLASK_FILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }
}