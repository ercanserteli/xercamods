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
    private record ParsedInput(ItemStack launcherStack, PotionContents potionType, PotionContents launcherPotion, int potionCount, boolean lingering, boolean valid) {
    }

    private static final class ParseState {
        private ItemStack launcherStack = ItemStack.EMPTY;
        private PotionContents potionType = PotionContents.EMPTY;
        private PotionContents launcherPotion = PotionContents.EMPTY;
        private int potionCount;
        private boolean lingering;
    }

    public RecipeEnderBowFilling(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ParsedInput parsed = parseInput(inv);
        return parsed.valid()
                && !parsed.launcherStack().isEmpty()
                && parsed.potionCount() > 0
                && !parsed.potionType().equals(PotionContents.EMPTY)
                && (ItemFlask.getCharges(parsed.launcherStack()) + parsed.potionCount()) <= ItemFlask.getMaxCharges(parsed.launcherStack(), level);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ParsedInput parsed = parseInput(inv);
        if (!parsed.valid()
                || parsed.launcherStack().isEmpty()
                || parsed.potionCount() <= 0
                || parsed.potionType().equals(PotionContents.EMPTY)) {
            return ItemStack.EMPTY;
        }

        int oldCharges = ItemFlask.getCharges(parsed.launcherStack());
        int newCharges = oldCharges + parsed.potionCount();
        if (newCharges <= ItemFlask.getMaxCharges(parsed.launcherStack(), provider)) {
            ItemStack result = parsed.launcherStack().copy();
            result.setCount(1);
            result.set(DataComponents.POTION_CONTENTS, parsed.potionType());
            ItemFlask.setCharges(result, newCharges);
            ItemPotionLauncher.setLingering(result, parsed.lingering());
            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return Items.CRAFTING_SPECIAL_ENDER_BOW_FILLING;
    }

    private ParsedInput parseInput(CraftingInput inv) {
        ParseState state = new ParseState();

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (!itemStack.isEmpty() && !parseItem(itemStack, state)) {
                return invalid();
            }
        }

        return new ParsedInput(state.launcherStack, state.potionType, state.launcherPotion, state.potionCount, state.lingering, true);
    }

    private static ParsedInput invalid() {
        return new ParsedInput(ItemStack.EMPTY, PotionContents.EMPTY, PotionContents.EMPTY, 0, false, false);
    }

    private boolean parseItem(ItemStack itemStack, ParseState state) {
        if (itemStack.is(Items.ENDER_BOW)) {
            if (!state.launcherStack.isEmpty()) {
                return false;
            }
            state.launcherStack = itemStack;
            state.launcherPotion = ItemFlask.getPotionContents(state.launcherStack);
            return isPotionCompatible(state.potionType, state.launcherPotion);
        }

        if (itemStack.getItem() instanceof ThrowablePotionItem) {
            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (state.potionType.equals(PotionContents.EMPTY)) {
                state.potionType = potionContents;
                state.lingering = itemStack.is(net.minecraft.world.item.Items.LINGERING_POTION);
            } else if (!potionContents.equals(state.potionType)) {
                return false;
            }
            if (!isPotionCompatible(state.potionType, state.launcherPotion)) {
                return false;
            }
            ++state.potionCount;
            return true;
        }

        return false;
    }

    private static boolean isPotionCompatible(PotionContents expected, PotionContents current) {
        return expected.equals(PotionContents.EMPTY) || current.equals(PotionContents.EMPTY) || current.equals(expected);
    }
}
