package xerca.xercafood.common.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xerca.xercafood.common.item.ItemTeapot;
import xerca.xercafood.common.item.Items;

public class RecipeTeaRefilling extends CustomRecipe {
    private record ParsedInput(ItemStack teapotStack, @Nullable ItemTeapot teapot, int teaCount, boolean valid) {
    }

    public RecipeTeaRefilling(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInput inv, Level worldIn) {
        ParsedInput parsed = parseInput(inv);
        ItemTeapot teapot = parsed.teapot();
        return parsed.valid()
                && !parsed.teapotStack().isEmpty()
                && teapot != null
                && !teapot.isHot()
                && teapot.getTeaAmount() + parsed.teaCount() <= 7
                && parsed.teaCount() > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider provider) {
        ParsedInput parsed = parseInput(inv);
        ItemTeapot teapot = parsed.teapot();
        if (!parsed.valid()
                || parsed.teapotStack().isEmpty()
                || teapot == null
                || parsed.teaCount() <= 0
                || teapot.isHot()
                || teapot.getTeaAmount() + parsed.teaCount() > 7) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(RecipeTeaFilling.getFullTeapot(teapot.getTeaAmount() + parsed.teaCount()));
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_REFILLING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }

    private ParsedInput parseInput(CraftingInput inv) {
        int teaCount = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemTeapot teapot = null;

        for (int slot = 0; slot < inv.size(); ++slot) {
            ItemStack itemStack = inv.getItem(slot);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof ItemTeapot currentTeapot) {
                    if (!teapotStack.isEmpty() || currentTeapot.isHot() || currentTeapot.getTeaAmount() > 6) {
                        return invalid();
                    }
                    teapotStack = itemStack;
                    teapot = currentTeapot;
                } else if (itemStack.getItem() == Items.TEA_DRIED) {
                    ++teaCount;
                } else {
                    return invalid();
                }
            }
        }

        return new ParsedInput(teapotStack, teapot, teaCount, true);
    }

    private static ParsedInput invalid() {
        return new ParsedInput(ItemStack.EMPTY, null, 0, false);
    }
}
