package xerca.xercamod.common.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ThrowablePotionItem;
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

public class RecipeEnderBowFilling extends SpecialRecipe {
    public RecipeEnderBowFilling(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(!Config.isEnderFlaskEnabled()){
            return false;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        ItemStack bowStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ENDER_BOW) {
                    if (!bowStack.isEmpty()) {
                        return false;
                    }
                    bowStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotionFromItem(bowStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return false;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof ThrowablePotionItem)) {
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

        return !bowStack.isEmpty() && i > 0 && !potionType.equals(Potions.EMPTY) && (ItemFlask.getCharges(bowStack) + i) <= ItemFlask.maxCharges;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(CraftingInventory inv) {
        if(!Config.isEnderFlaskEnabled()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        boolean isLingering = false;
        ItemStack flaskStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ENDER_BOW) {
                    if (!flaskStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    flaskStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotionFromItem(flaskStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof ThrowablePotionItem)) {
                        return ItemStack.EMPTY;
                    }
                    if(potionType.equals(Potions.EMPTY)){
                        potionType = PotionUtils.getPotionFromItem(itemstack);
                        isLingering = itemstack.getItem() == net.minecraft.item.Items.LINGERING_POTION;
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
            ItemStack resultStack = new ItemStack(Items.ENDER_BOW);
            PotionUtils.addPotionToItemStack(resultStack, potionType);
            ItemFlask.setCharges(resultStack, oldCharges + i);
            resultStack.getOrCreateTag().putBoolean("isLinger", isLingering);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_ENDER_BOW_FILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }
}