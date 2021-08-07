package xerca.xercamod.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemFlask;
import xerca.xercamod.common.item.Items;

public class RecipeEnderBowFilling extends CustomRecipe {
    public RecipeEnderBowFilling(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer inv, Level worldIn) {
        if(!Config.isEnderFlaskEnabled()){
            return false;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        ItemStack bowStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ENDER_BOW) {
                    if (!bowStack.isEmpty()) {
                        return false;
                    }
                    bowStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotion(bowStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return false;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof ThrowablePotionItem)) {
                        return false;
                    }
                    if(potionType.equals(Potions.EMPTY)){
                        potionType = PotionUtils.getPotion(itemstack);
                    }else if(!PotionUtils.getPotion(itemstack).equals(potionType)){
                        return false;
                    }

                    if(!currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !bowStack.isEmpty() && i > 0 && !potionType.equals(Potions.EMPTY) && (ItemFlask.getCharges(bowStack) + i) <= ItemFlask.getMaxCharges(bowStack);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingContainer inv) {
        if(!Config.isEnderFlaskEnabled()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        boolean isLingering = false;
        ItemStack bowStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ENDER_BOW) {
                    if (!bowStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    bowStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotion(bowStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof ThrowablePotionItem)) {
                        return ItemStack.EMPTY;
                    }
                    if(potionType.equals(Potions.EMPTY)){
                        potionType = PotionUtils.getPotion(itemstack);
                        isLingering = itemstack.getItem() == net.minecraft.world.item.Items.LINGERING_POTION;
                    }else if(!PotionUtils.getPotion(itemstack).equals(potionType)){
                        return ItemStack.EMPTY;
                    }

                    if(!currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        int oldCharges = ItemFlask.getCharges(bowStack);
        if (!bowStack.isEmpty() && i > 0 && !potionType.equals(Potions.EMPTY) && (oldCharges + i) <= ItemFlask.getMaxCharges(bowStack)) {
            ItemStack resultStack = new ItemStack(Items.ENDER_BOW);
            resultStack.setTag(bowStack.getOrCreateTag().copy());
            PotionUtils.setPotion(resultStack, potionType);
            ItemFlask.setCharges(resultStack, oldCharges + i);
            resultStack.getOrCreateTag().putBoolean("isLinger", isLingering);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_ENDER_BOW_FILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}