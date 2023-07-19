package xerca.xercamod.common.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemFlask;
import xerca.xercamod.common.item.Items;

public class RecipeFlaskFilling extends CustomRecipe {
    public RecipeFlaskFilling(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
        if(!Config.isEnderFlaskEnabled()){
            return false;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        ItemStack flaskStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.FLASK.get()) {
                    if (!flaskStack.isEmpty()) {
                        return false;
                    }
                    flaskStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotion(flaskStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return false;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof PotionItem)) {
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

        return !flaskStack.isEmpty() && i > 0 && !potionType.equals(Potions.EMPTY) && (ItemFlask.getCharges(flaskStack) + i) <= ItemFlask.getMaxCharges(flaskStack);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess access) {
        if(!Config.isEnderFlaskEnabled()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        Potion potionType = Potions.EMPTY;
        ItemStack flaskStack = ItemStack.EMPTY;
        Potion currentFlaskPotion = Potions.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.FLASK.get()) {
                    if (!flaskStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    flaskStack = itemstack;
                    currentFlaskPotion = PotionUtils.getPotion(flaskStack);
                    if(potionType != Potions.EMPTY && !currentFlaskPotion.equals(Potions.EMPTY) && !currentFlaskPotion.equals(potionType)){
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!(itemstack.getItem() instanceof PotionItem)) {
                        return ItemStack.EMPTY;
                    }
                    if(potionType.equals(Potions.EMPTY)){
                        potionType = PotionUtils.getPotion(itemstack);
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

        int oldCharges = ItemFlask.getCharges(flaskStack);
        if (!flaskStack.isEmpty() && i > 0 && !potionType.equals(Potions.EMPTY) && (oldCharges + i) <= ItemFlask.getMaxCharges(flaskStack)) {
            ItemStack resultStack = new ItemStack(Items.FLASK.get());
            resultStack.setTag(flaskStack.getOrCreateTag().copy());
            PotionUtils.setPotion(resultStack, potionType);
            ItemFlask.setCharges(resultStack, oldCharges + i);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.hasCraftingRemainingItem()) {
                nonnulllist.set(i, itemstack.getCraftingRemainingItem());
            } else if (itemstack.getItem() instanceof PotionItem) {
                ItemStack bottle = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
                nonnulllist.set(i, bottle);
            }
        }

        return nonnulllist;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_FLASK_FILLING.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}