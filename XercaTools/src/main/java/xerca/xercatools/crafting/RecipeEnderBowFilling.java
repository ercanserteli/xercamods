package xerca.xercatools.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercatools.item.ItemFlask;
import xerca.xercatools.item.ItemPotionLauncher;
import xerca.xercatools.item.Items;

public class RecipeEnderBowFilling extends CustomRecipe {
    public RecipeEnderBowFilling(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        int potionCount = 0;
        PotionContents potionType = PotionContents.EMPTY;
        ItemStack launcherStack = ItemStack.EMPTY;
        PotionContents currentLauncherPotion = PotionContents.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (itemStack.is(Items.ENDER_BOW)) {
                if (!launcherStack.isEmpty()) {
                    return false;
                }

                launcherStack = itemStack;
                currentLauncherPotion = ItemFlask.getPotionContents(launcherStack);
                if (!potionType.equals(PotionContents.EMPTY) && !currentLauncherPotion.equals(PotionContents.EMPTY) && !currentLauncherPotion.equals(potionType)) {
                    return false;
                }
                continue;
            }

            if (!(itemStack.getItem() instanceof ThrowablePotionItem)) {
                return false;
            }

            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potionType.equals(PotionContents.EMPTY)) {
                potionType = potionContents;
            } else if (!potionContents.equals(potionType)) {
                return false;
            }

            if (!currentLauncherPotion.equals(PotionContents.EMPTY) && !currentLauncherPotion.equals(potionType)) {
                return false;
            }

            ++potionCount;
        }

        return !launcherStack.isEmpty()
                && potionCount > 0
                && !potionType.equals(PotionContents.EMPTY)
                && (ItemFlask.getCharges(launcherStack) + potionCount) <= ItemFlask.getMaxCharges(launcherStack, level);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        int potionCount = 0;
        PotionContents potionType = PotionContents.EMPTY;
        boolean isLingering = false;
        ItemStack launcherStack = ItemStack.EMPTY;
        PotionContents currentLauncherPotion = PotionContents.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (itemStack.is(Items.ENDER_BOW)) {
                if (!launcherStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                launcherStack = itemStack;
                currentLauncherPotion = ItemFlask.getPotionContents(launcherStack);
                if (!potionType.equals(PotionContents.EMPTY) && !currentLauncherPotion.equals(PotionContents.EMPTY) && !currentLauncherPotion.equals(potionType)) {
                    return ItemStack.EMPTY;
                }
                continue;
            }

            if (!(itemStack.getItem() instanceof ThrowablePotionItem)) {
                return ItemStack.EMPTY;
            }

            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potionType.equals(PotionContents.EMPTY)) {
                potionType = potionContents;
                isLingering = itemStack.is(net.minecraft.world.item.Items.LINGERING_POTION);
            } else if (!potionContents.equals(potionType)) {
                return ItemStack.EMPTY;
            }

            if (!currentLauncherPotion.equals(PotionContents.EMPTY) && !currentLauncherPotion.equals(potionType)) {
                return ItemStack.EMPTY;
            }

            ++potionCount;
        }

        int oldCharges = ItemFlask.getCharges(launcherStack);
        int newCharges = oldCharges + potionCount;
        if (!launcherStack.isEmpty() && potionCount > 0 && !potionType.equals(PotionContents.EMPTY)
                && newCharges <= ItemFlask.getMaxCharges(launcherStack, provider)) {
            ItemStack result = launcherStack.copy();
            result.setCount(1);
            result.set(DataComponents.POTION_CONTENTS, potionType);
            ItemFlask.setCharges(result, newCharges);
            ItemPotionLauncher.setLingering(result, isLingering);
            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_ENDER_BOW_FILLING;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
