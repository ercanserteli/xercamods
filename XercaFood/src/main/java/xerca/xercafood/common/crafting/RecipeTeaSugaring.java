package xerca.xercafood.common.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xerca.xercafood.common.item.ItemTeacup;
import xerca.xercafood.common.item.Items;

public class RecipeTeaSugaring extends CustomRecipe {
    private record ParsedInput(ItemStack teacupStack, @Nullable ItemTeacup teacup, int sugarCount, boolean valid) {
    }

    public static Item getTeacup(int sugarAmount) {
        return switch (sugarAmount) {
            case 0 -> Items.FULL_TEACUP_0;
            case 1 -> Items.FULL_TEACUP_1;
            case 2 -> Items.FULL_TEACUP_2;
            case 3 -> Items.FULL_TEACUP_3;
            case 4 -> Items.FULL_TEACUP_4;
            case 5 -> Items.FULL_TEACUP_5;
            case 6 -> Items.FULL_TEACUP_6;
            default -> net.minecraft.world.item.Items.AIR;
        };
    }

    public RecipeTeaSugaring(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInput inv, Level worldIn) {
        ParsedInput parsed = parseInput(inv);
        ItemTeacup teacup = parsed.teacup();
        return parsed.valid()
                && !parsed.teacupStack().isEmpty()
                && teacup != null
                && parsed.sugarCount() > 0
                && (teacup.getSugarAmount() + parsed.sugarCount()) <= 6;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ParsedInput parsed = parseInput(inv);
        ItemTeacup teacup = parsed.teacup();
        if (!parsed.valid()
                || parsed.teacupStack().isEmpty()
                || teacup == null
                || parsed.sugarCount() < 1
                || (teacup.getSugarAmount() + parsed.sugarCount()) > 6) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(getTeacup(teacup.getSugarAmount() + parsed.sugarCount()));
    }

    private ParsedInput parseInput(CraftingInput inv) {
        int sugarCount = 0;
        ItemStack teacupStack = ItemStack.EMPTY;
        ItemTeacup teacup = null;

        for (int slot = 0; slot < inv.size(); ++slot) {
            ItemStack itemStack = inv.getItem(slot);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof ItemTeacup currentTeacup) {
                    if (!teacupStack.isEmpty()) {
                        return invalid();
                    }
                    teacupStack = itemStack;
                    teacup = currentTeacup;
                } else if (itemStack.getItem() == net.minecraft.world.item.Items.SUGAR && sugarCount < 6) {
                    ++sugarCount;
                } else {
                    return invalid();
                }
            }
        }

        return new ParsedInput(teacupStack, teacup, sugarCount, true);
    }

    private static ParsedInput invalid() {
        return new ParsedInput(ItemStack.EMPTY, null, 0, false);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_SUGARING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
