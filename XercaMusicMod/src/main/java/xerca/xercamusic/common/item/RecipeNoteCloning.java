package xerca.xercamusic.common.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
public class RecipeNoteCloning extends CustomRecipe {
    public RecipeNoteCloning(CraftingBookCategory category) {
        super(category);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingContainer inv, @NotNull Level worldIn) {
        ItemStack orgNote = ItemStack.EMPTY;
        ItemStack freshNote = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack item = inv.getItem(j);
            if (!item.isEmpty()) {
                if (item.getItem() == Items.MUSIC_SHEET && item.getOrDefault(Items.SHEET_GENERATION, 0) > 0) {
                    if (!orgNote.isEmpty()) {
                        return false;
                    }

                    orgNote = item;
                } else if (item.getItem() == Items.MUSIC_SHEET && ItemMusicSheet.isEmptySheet(item)) {
                    if (!freshNote.isEmpty()) {
                        return false;
                    }

                    freshNote = item;
                }
            }
        }

        return !orgNote.isEmpty() && !freshNote.isEmpty();
    }

    @Override
    public ItemStack assemble(@NotNull CraftingContainer inv, HolderLookup.@NotNull Provider registries) {
        ItemStack orgNote = ItemStack.EMPTY;
        ItemStack freshNote = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack item = inv.getItem(j);
            if (!item.isEmpty()) {
                if (item.getItem() == Items.MUSIC_SHEET && item.getOrDefault(Items.SHEET_GENERATION, 0) > 0) {
                    if (!orgNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    orgNote = item;
                } else if (item.getItem() == Items.MUSIC_SHEET && ItemMusicSheet.isEmptySheet(item)) {
                    if (!freshNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    freshNote = item;
                }
            }
        }

        int gen = orgNote.getOrDefault(Items.SHEET_GENERATION, 0);
        if (!orgNote.isEmpty() && !freshNote.isEmpty() && ItemMusicSheet.isEmptySheet(freshNote) && gen < 3 && gen > 0) {
            ItemStack resultStack = new ItemStack(Items.MUSIC_SHEET);
            resultStack.set(Items.SHEET_GENERATION, gen + 1);
            resultStack.set(Items.SHEET_ID, orgNote.get(Items.SHEET_ID));
            resultStack.set(Items.SHEET_VERSION, orgNote.get(Items.SHEET_VERSION));
            resultStack.set(Items.SHEET_LENGTH, orgNote.get(Items.SHEET_LENGTH));
            resultStack.set(Items.SHEET_BPS, orgNote.get(Items.SHEET_BPS));
            resultStack.set(Items.SHEET_PREV_INSTRUMENT, orgNote.get(Items.SHEET_PREV_INSTRUMENT));
            resultStack.set(Items.SHEET_VOLUME, orgNote.get(Items.SHEET_VOLUME));
            resultStack.set(Items.SHEET_AUTHOR, orgNote.get(Items.SHEET_AUTHOR));
            resultStack.set(Items.SHEET_TITLE, orgNote.get(Items.SHEET_TITLE));
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < itemStacks.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.getItem() == Items.MUSIC_SHEET && itemStack.getOrDefault(Items.SHEET_GENERATION, 0) > 0) {
                ItemStack copy = itemStack.copy();
                copy.setCount(1);
                itemStacks.set(i, copy);
                break;
            }
        }

        return itemStacks;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_NOTECLONING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}