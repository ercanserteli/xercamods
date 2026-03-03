package xerca.xercapaint.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercapaint.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeCraftPalette extends CustomRecipe {
    private static final byte[] EMPTY_BASIC_COLORS = new byte[0];

    public RecipeCraftPalette(CraftingBookCategory category) {
        super(category);
    }

    private boolean isPlank(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ItemTags.PLANKS);
    }

    static int getBasicColorIndex(ItemStack stack) {
        if (!(stack.getItem() instanceof DyeItem dyeItem)) {
            return -1;
        }
        DyeColor dyeColor = dyeItem.getDyeColor();
        if (dyeColor == null || !DyeItem.byColor(dyeColor).equals(stack.getItem())) {
            return -1;
        }
        int colorId = dyeColor.getId();
        if (colorId < 0 || colorId >= 16) {
            return -1;
        }
        return 15 - colorId;
    }

    static boolean isDye(ItemStack stack) {
        return getBasicColorIndex(stack) >= 0;
    }

    private byte[] parseBasicColors(CraftingInput inv) {
        int plankRow = -1;
        int plankCount = 0;
        int leftmostPlankColumn = inv.width();
        int rightmostPlankColumn = -1;

        for (int i = 0; i < inv.height(); ++i) {
            for (int j = 0; j < inv.width(); ++j) {
                int id = i * inv.width() + j;
                ItemStack stack = inv.getItem(id);
                if (stack.isEmpty()) {
                    continue;
                }
                if (isPlank(stack)) {
                    if (plankRow < 0) {
                        plankRow = i;
                    } else if (plankRow != i) {
                        return EMPTY_BASIC_COLORS;
                    }
                    plankCount++;
                    if (j < leftmostPlankColumn) {
                        leftmostPlankColumn = j;
                    }
                    if (j > rightmostPlankColumn) {
                        rightmostPlankColumn = j;
                    }
                } else if (!isDye(stack)) {
                    return EMPTY_BASIC_COLORS;
                }
            }
        }

        if (plankRow < 0 || plankCount != 3) {
            return EMPTY_BASIC_COLORS;
        }
        if (rightmostPlankColumn - leftmostPlankColumn != 2) {
            return EMPTY_BASIC_COLORS;
        }

        for (int j = 0; j < inv.width(); ++j) {
            int id = plankRow * inv.width() + j;
            ItemStack stack = inv.getItem(id);
            if (!stack.isEmpty() && !isPlank(stack)) {
                return EMPTY_BASIC_COLORS;
            }
        }

        boolean hasDye = false;
        byte[] basicColors = new byte[16];
        for (int i = 0; i < inv.height(); ++i) {
            if (i == plankRow) {
                continue;
            }
            for (int j = 0; j < inv.width(); ++j) {
                int id = i * inv.width() + j;
                ItemStack stack = inv.getItem(id);
                if (stack.isEmpty()) {
                    continue;
                }
                int colorIndex = getBasicColorIndex(stack);
                if (colorIndex < 0 || basicColors[colorIndex] != 0) {
                    return EMPTY_BASIC_COLORS;
                }
                basicColors[colorIndex] = 1;
                hasDye = true;
            }
        }
        return hasDye ? basicColors : EMPTY_BASIC_COLORS;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        return parseBasicColors(inv).length > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        byte[] basicColors = parseBasicColors(inv);
        if (basicColors.length == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(Items.ITEM_PALETTE);
        result.set(Items.PALETTE_BASIC_COLORS, basicColors);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        return NonNullList.withSize(inv.size(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_PALETTE_CRAFTING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}
