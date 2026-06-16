package xerca.xercapaint.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

import java.util.ArrayList;
import java.util.List;

public class RecipeFillPalette extends CustomRecipe {
    public RecipeFillPalette(CraftingBookCategory category) {
        super(category);
    }

    static int getBasicColorIndex(ItemStack stack) {
        if (!(stack.getItem() instanceof DyeItem dyeItem)) {
            return -1;
        }
        DyeColor dyeColor = dyeItem.getDyeColor();
        if (!DyeItem.byColor(dyeColor).equals(stack.getItem())) {
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

    private boolean isPalette(ItemStack stack) {
        return stack.getItem() instanceof ItemPalette;
    }

    private int findPalette(CraftingInput inv) {
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (isPalette(stack)) {
                return i;
            }
        }
        return -1;
    }

    private List<ItemStack> findDyes(CraftingInput inv, int paletteId) {
        List<ItemStack> dyes = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            if (i == paletteId) {
                continue;
            }
            ItemStack stack = inv.getItem(i);
            if (isDye(stack)) {
                dyes.add(stack);
            } else if (!stack.isEmpty()) {
                return new ArrayList<>();
            }
        }
        return dyes;
    }

    private byte[] loadBasicColors(ItemStack palette) {
        byte[] source = palette.getOrDefault(Items.PALETTE_BASIC_COLORS, new byte[0]);
        byte[] basicColors = new byte[16];
        System.arraycopy(source, 0, basicColors, 0, Math.min(source.length, basicColors.length));
        return basicColors;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        int paletteId = findPalette(inv);
        if (paletteId < 0) {
            return false;
        }
        List<ItemStack> dyes = findDyes(inv, paletteId);
        return !dyes.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        int paletteId = findPalette(inv);
        if (paletteId < 0) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> dyes = findDyes(inv, paletteId);
        if (dyes.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack inputPalette = inv.getItem(paletteId);
        byte[] basicColors = loadBasicColors(inputPalette);

        for (ItemStack dye : dyes) {
            int realColorId = getBasicColorIndex(dye);
            if (realColorId < 0 || basicColors[realColorId] > 0) {
                return ItemStack.EMPTY;
            }
            basicColors[realColorId] = 1;
        }

        // Keep all existing palette components and only update basic colors.
        ItemStack result = inputPalette.copyWithCount(1);
        result.set(Items.PALETTE_BASIC_COLORS, basicColors);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        return NonNullList.withSize(inv.size(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_PALETTE_FILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}
