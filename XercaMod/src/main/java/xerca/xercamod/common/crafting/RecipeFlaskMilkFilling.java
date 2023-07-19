package xerca.xercamod.common.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
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

public class RecipeFlaskMilkFilling extends CustomRecipe {
    public RecipeFlaskMilkFilling(ResourceLocation pId, CraftingBookCategory pCategory) {
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
        ItemStack flaskStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemFlask) {
                    if (!flaskStack.isEmpty()) {
                        return false;
                    }
                    flaskStack = itemstack;
                    if(!PotionUtils.getPotion(flaskStack).equals(Potions.EMPTY)){
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

        return !flaskStack.isEmpty() && i > 0 && (ItemFlask.getCharges(flaskStack) + i) <= ItemFlask.getMaxCharges(flaskStack);
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
        ItemStack flaskStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof ItemFlask) {
                    if (!flaskStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    flaskStack = itemstack;
                    if(!PotionUtils.getPotion(flaskStack).equals(Potions.EMPTY)){
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
        if (!flaskStack.isEmpty() && i > 0 && (oldCharges + i) <= ItemFlask.getMaxCharges(flaskStack)) {
            ItemStack resultStack = new ItemStack(Items.FLASK_MILK.get());
            resultStack.setTag(flaskStack.getOrCreateTag().copy());
            ItemFlask.setCharges(resultStack, oldCharges + i);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_FLASK_MILK_FILLING.get();
    }


    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}