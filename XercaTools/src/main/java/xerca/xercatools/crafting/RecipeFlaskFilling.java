package xerca.xercatools.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercatools.item.ItemFlask;
import xerca.xercatools.item.Items;

public class RecipeFlaskFilling extends CustomRecipe {
    public RecipeFlaskFilling(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        int potionCount = 0;
        PotionContents potionType = PotionContents.EMPTY;
        ItemStack flaskStack = ItemStack.EMPTY;
        PotionContents currentFlaskPotion = PotionContents.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (itemStack.is(Items.FLASK)) {
                if (!flaskStack.isEmpty()) {
                    return false;
                }

                flaskStack = itemStack;
                currentFlaskPotion = ItemFlask.getPotionContents(flaskStack);
                if (!potionType.equals(PotionContents.EMPTY) && !currentFlaskPotion.equals(PotionContents.EMPTY) && !currentFlaskPotion.equals(potionType)) {
                    return false;
                }
                continue;
            }

            if (!(itemStack.getItem() instanceof PotionItem)) {
                return false;
            }

            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potionType.equals(PotionContents.EMPTY)) {
                potionType = potionContents;
            } else if (!potionContents.equals(potionType)) {
                return false;
            }

            if (!currentFlaskPotion.equals(PotionContents.EMPTY) && !currentFlaskPotion.equals(potionType)) {
                return false;
            }

            ++potionCount;
        }

        return !flaskStack.isEmpty()
                && potionCount > 0
                && !potionType.equals(PotionContents.EMPTY)
                && (ItemFlask.getCharges(flaskStack) + potionCount) <= ItemFlask.getMaxCharges(flaskStack, level);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        int potionCount = 0;
        PotionContents potionType = PotionContents.EMPTY;
        ItemStack flaskStack = ItemStack.EMPTY;
        PotionContents currentFlaskPotion = PotionContents.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (itemStack.is(Items.FLASK)) {
                if (!flaskStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                flaskStack = itemStack;
                currentFlaskPotion = ItemFlask.getPotionContents(flaskStack);
                if (!potionType.equals(PotionContents.EMPTY) && !currentFlaskPotion.equals(PotionContents.EMPTY) && !currentFlaskPotion.equals(potionType)) {
                    return ItemStack.EMPTY;
                }
                continue;
            }

            if (!(itemStack.getItem() instanceof PotionItem)) {
                return ItemStack.EMPTY;
            }

            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potionType.equals(PotionContents.EMPTY)) {
                potionType = potionContents;
            } else if (!potionContents.equals(potionType)) {
                return ItemStack.EMPTY;
            }

            if (!currentFlaskPotion.equals(PotionContents.EMPTY) && !currentFlaskPotion.equals(potionType)) {
                return ItemStack.EMPTY;
            }

            ++potionCount;
        }

        int oldCharges = ItemFlask.getCharges(flaskStack);
        int newCharges = oldCharges + potionCount;
        if (!flaskStack.isEmpty() && potionCount > 0 && !potionType.equals(PotionContents.EMPTY)
                && newCharges <= ItemFlask.getMaxCharges(flaskStack, provider)) {
            ItemStack result = flaskStack.copy();
            result.setCount(1);
            result.set(DataComponents.POTION_CONTENTS, potionType);
            ItemFlask.setCharges(result, newCharges);
            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for (int i = 0; i < remainingItems.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.getItem().hasCraftingRemainingItem()) {
                remainingItems.set(i, itemStack.getItem().getCraftingRemainingItem().getDefaultInstance());
            } else if (itemStack.getItem() instanceof PotionItem) {
                remainingItems.set(i, new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE));
            }
        }

        return remainingItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_FLASK_FILLING;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
