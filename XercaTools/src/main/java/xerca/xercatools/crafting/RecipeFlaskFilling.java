package xerca.xercatools.crafting;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
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
    private record ParsedInput(ItemStack flaskStack, PotionContents potionType, PotionContents currentFlaskPotion, int potionCount, boolean valid) {
    }

    private static final class ParseState {
        private ItemStack flaskStack = ItemStack.EMPTY;
        private PotionContents potionType = PotionContents.EMPTY;
        private PotionContents currentFlaskPotion = PotionContents.EMPTY;
        private int potionCount;
    }

    public RecipeFlaskFilling(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ParsedInput parsed = parseInput(inv);
        return parsed.valid()
                && !parsed.flaskStack().isEmpty()
                && parsed.potionCount() > 0
                && !parsed.potionType().equals(PotionContents.EMPTY)
                && (ItemFlask.getCharges(parsed.flaskStack()) + parsed.potionCount()) <= ItemFlask.getMaxCharges(parsed.flaskStack(), level);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ParsedInput parsed = parseInput(inv);
        if (!parsed.valid()
                || parsed.flaskStack().isEmpty()
                || parsed.potionCount() <= 0
                || parsed.potionType().equals(PotionContents.EMPTY)) {
            return ItemStack.EMPTY;
        }

        int oldCharges = ItemFlask.getCharges(parsed.flaskStack());
        int newCharges = oldCharges + parsed.potionCount();
        if (newCharges <= ItemFlask.getMaxCharges(parsed.flaskStack(), provider)) {
            ItemStack result = parsed.flaskStack().copy();
            result.setCount(1);
            result.set(DataComponents.POTION_CONTENTS, parsed.potionType());
            ItemFlask.setCharges(result, newCharges);
            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    @SuppressFBWarnings(value = "NP", justification = "hasCraftingRemainingItem() guarantees non-null crafting remainder in this API usage.")
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for (int i = 0; i < remainingItems.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.getItem().hasCraftingRemainingItem()) {
                Item remainderItem = itemStack.getItem().getCraftingRemainingItem();
                remainingItems.set(i, remainderItem == null ? ItemStack.EMPTY : remainderItem.getDefaultInstance());
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

    private ParsedInput parseInput(CraftingInput inv) {
        ParseState state = new ParseState();

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (!itemStack.isEmpty() && !parseItem(itemStack, state)) {
                return invalid();
            }
        }

        return new ParsedInput(state.flaskStack, state.potionType, state.currentFlaskPotion, state.potionCount, true);
    }

    private static ParsedInput invalid() {
        return new ParsedInput(ItemStack.EMPTY, PotionContents.EMPTY, PotionContents.EMPTY, 0, false);
    }

    private boolean parseItem(ItemStack itemStack, ParseState state) {
        if (itemStack.is(Items.FLASK)) {
            if (!state.flaskStack.isEmpty()) {
                return false;
            }
            state.flaskStack = itemStack;
            state.currentFlaskPotion = ItemFlask.getPotionContents(state.flaskStack);
            return isPotionCompatible(state.potionType, state.currentFlaskPotion);
        }

        if (itemStack.getItem() instanceof PotionItem) {
            PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (state.potionType.equals(PotionContents.EMPTY)) {
                state.potionType = potionContents;
            } else if (!potionContents.equals(state.potionType)) {
                return false;
            }
            if (!isPotionCompatible(state.potionType, state.currentFlaskPotion)) {
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
