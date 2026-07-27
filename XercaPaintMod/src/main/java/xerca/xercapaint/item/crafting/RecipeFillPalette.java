package xerca.xercapaint.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeFillPalette extends CustomRecipe {
    public RecipeFillPalette(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
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
        if (colorId < 0 || colorId >= ItemPalette.BASIC_COLOR_COUNT) {
            return -1;
        }
        return ItemPalette.BASIC_COLOR_COUNT - 1 - colorId;
    }

    static boolean isDye(ItemStack stack) {
        return getBasicColorIndex(stack) >= 0;
    }

    private boolean isPalette(ItemStack stack) {
        return stack.getItem() instanceof ItemPalette;
    }

    private int findPalette(CraftingContainer inv) {
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (isPalette(stack)) {
                return i;
            }
        }
        return -1;
    }

    private List<ItemStack> findDyes(CraftingContainer inv, int paletteId) {
        List<ItemStack> dyes = new ArrayList<>();
        for (int i = 0; i < inv.getContainerSize(); ++i) {
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

    private byte[] loadBasicColors(CompoundTag paletteTag) {
        byte[] source = paletteTag.getByteArray(ItemPalette.TAG_BASIC_COLORS);
        byte[] basicColors = new byte[ItemPalette.BASIC_COLOR_COUNT];
        System.arraycopy(source, 0, basicColors, 0, Math.min(source.length, basicColors.length));
        return basicColors;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        int paletteId = findPalette(inv);
        if (paletteId < 0) {
            return ItemStack.EMPTY;
        }

        List<ItemStack> dyes = findDyes(inv, paletteId);
        if (dyes.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack inputPalette = inv.getItem(paletteId);

        // Keep all existing palette tags and only update basic colors.
        CompoundTag resultTag = inputPalette.getOrCreateTag().copy();
        byte[] basicColors = loadBasicColors(resultTag);

        for (ItemStack dye : dyes) {
            int realColorId = getBasicColorIndex(dye);
            if (realColorId < 0 || basicColors[realColorId] > 0) {
                return ItemStack.EMPTY;
            }
            basicColors[realColorId] = 1;
        }

        resultTag.putByteArray(ItemPalette.TAG_BASIC_COLORS, basicColors);

        ItemStack result = inputPalette.copyWithCount(1);
        result.setTag(resultTag);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
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
