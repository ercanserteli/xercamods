package xerca.xercafood.common.crafting;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.item.ItemTeapot;
import xerca.xercafood.common.item.Items;

public class RecipeTeaPouring extends CustomRecipe {
    private record ParsedInput(ItemStack teapotStack, ItemTeapot teapot, int teacupCount, boolean valid) {
    }

    @SuppressFBWarnings(value = "SF", justification = "teaAmount is validated to 1..7 by recipe logic.")
    public static Item getHotTeapot(int teaAmount) {
        return switch (teaAmount) {
            case 1 -> Items.HOT_TEAPOT_1;
            case 2 -> Items.HOT_TEAPOT_2;
            case 3 -> Items.HOT_TEAPOT_3;
            case 4 -> Items.HOT_TEAPOT_4;
            case 5 -> Items.HOT_TEAPOT_5;
            case 6 -> Items.HOT_TEAPOT_6;
            case 7 -> Items.HOT_TEAPOT_7;
            default -> net.minecraft.world.item.Items.AIR;
        };
    }

    public RecipeTeaPouring(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInput inv, Level worldIn) {
        ParsedInput parsed = parseInput(inv);
        return parsed.valid()
                && !parsed.teapotStack().isEmpty()
                && parsed.teapot() != null
                && parsed.teacupCount() > 0
                && parsed.teapot().isHot()
                && (parsed.teapot().getTeaAmount() - parsed.teacupCount()) >= 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ParsedInput parsed = parseInput(inv);
        if (!parsed.valid()
                || parsed.teapotStack().isEmpty()
                || parsed.teapot() == null
                || parsed.teacupCount() < 1
                || (parsed.teapot().getTeaAmount() - parsed.teacupCount()) < 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Items.FULL_TEACUP_0, parsed.teacupCount());
    }

    @Override
    @SuppressFBWarnings(value = "NP", justification = "hasCraftingRemainingItem() is checked before using crafting remainder item.")
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.size(), ItemStack.EMPTY);
        int teacupCount = countTeacups(inv);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            Item item = itemstack.getItem();
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem().getDefaultInstance());
            } else if (itemstack.getItem() instanceof ItemTeapot oldTeapot) {
                nonnulllist.set(i, getTeapotRemainder(oldTeapot, teacupCount));
                break;
            }
        }

        return nonnulllist;
    }

    private ParsedInput parseInput(CraftingInput inv) {
        int teacupCount = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemTeapot teapot = null;

        for (int slot = 0; slot < inv.size(); ++slot) {
            ItemStack itemStack = inv.getItem(slot);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof ItemTeapot currentTeapot) {
                    if (!teapotStack.isEmpty() || !currentTeapot.isHot()) {
                        return invalid();
                    }
                    teapotStack = itemStack;
                    teapot = currentTeapot;
                } else if (itemStack.getItem() == Items.TEACUP && teacupCount <= 6) {
                    ++teacupCount;
                } else {
                    return invalid();
                }
            }
        }

        return new ParsedInput(teapotStack, teapot, teacupCount, true);
    }

    private static ParsedInput invalid() {
        return new ParsedInput(ItemStack.EMPTY, null, 0, false);
    }

    private int countTeacups(CraftingInput inv) {
        int teacupCount = 0;
        for (int slot = 0; slot < inv.size(); ++slot) {
            ItemStack itemStack = inv.getItem(slot);
            if (!itemStack.isEmpty() && itemStack.getItem() == Items.TEACUP && teacupCount <= 6) {
                ++teacupCount;
            }
        }
        return teacupCount;
    }

    private ItemStack getTeapotRemainder(ItemTeapot oldTeapot, int teacupCount) {
        int remainingTea = oldTeapot.getTeaAmount() - teacupCount;
        if (remainingTea <= 0) {
            return new ItemStack(Items.TEAPOT);
        }
        ItemStack remainder = new ItemStack(getHotTeapot(remainingTea));
        return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_POURING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
