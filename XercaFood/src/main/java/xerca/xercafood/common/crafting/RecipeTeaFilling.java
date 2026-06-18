package xerca.xercafood.common.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.item.Items;

public class RecipeTeaFilling extends CustomRecipe {
    private record ParsedInput(ItemStack teapotStack, ItemStack bucketStack, int teaCount, boolean valid) {
    }

    private static final class ParseState {
        private ItemStack teapotStack = ItemStack.EMPTY;
        private ItemStack bucketStack = ItemStack.EMPTY;
        private int teaCount;
    }


    public static Item getFullTeapot(int teaAmount) {
        return switch (teaAmount) {
            case 1 -> Items.FULL_TEAPOT_1;
            case 2 -> Items.FULL_TEAPOT_2;
            case 3 -> Items.FULL_TEAPOT_3;
            case 4 -> Items.FULL_TEAPOT_4;
            case 5 -> Items.FULL_TEAPOT_5;
            case 6 -> Items.FULL_TEAPOT_6;
            case 7 -> Items.FULL_TEAPOT_7;
            default -> net.minecraft.world.item.Items.AIR;
        };
    }

    public RecipeTeaFilling(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInput inv, Level worldIn) {
        ParsedInput parsed = parseInput(inv);
        return parsed.valid()
                && !parsed.teapotStack().isEmpty()
                && !parsed.bucketStack().isEmpty()
                && parsed.teaCount() > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ParsedInput parsed = parseInput(inv);
        if (!parsed.valid()
                || parsed.teapotStack().isEmpty()
                || parsed.bucketStack().isEmpty()
                || parsed.teaCount() < 1
                || parsed.teaCount() > 7) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(getFullTeapot(parsed.teaCount()));
    }

    private ParsedInput parseInput(CraftingInput inv) {
        ParseState state = new ParseState();

        for (int slot = 0; slot < inv.size(); ++slot) {
            ItemStack itemStack = inv.getItem(slot);
            if (!itemStack.isEmpty() && !parseItem(itemStack, state)) {
                return invalid();
            }
        }

        return new ParsedInput(state.teapotStack, state.bucketStack, state.teaCount, true);
    }

    private static ParsedInput invalid() {
        return new ParsedInput(ItemStack.EMPTY, ItemStack.EMPTY, 0, false);
    }

    private boolean parseItem(ItemStack itemStack, ParseState state) {
        if (itemStack.getItem() == Items.TEAPOT) {
            if (!state.teapotStack.isEmpty()) {
                return false;
            }
            state.teapotStack = itemStack;
            return true;
        }

        if (itemStack.getItem() == net.minecraft.world.item.Items.WATER_BUCKET) {
            if (!state.bucketStack.isEmpty()) {
                return false;
            }
            state.bucketStack = itemStack;
            return true;
        }

        if (itemStack.getItem() == Items.TEA_DRIED) {
            ++state.teaCount;
            return true;
        }

        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_FILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}
