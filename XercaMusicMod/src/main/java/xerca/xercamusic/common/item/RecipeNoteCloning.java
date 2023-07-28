package xerca.xercamusic.common.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

@MethodsReturnNonnullByDefault
public class RecipeNoteCloning extends CustomRecipe {
    public RecipeNoteCloning(ResourceLocation location, CraftingBookCategory category) {
        super(location, category);
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
                if (item.getItem() == Items.MUSIC_SHEET && item.hasTag() && WrittenBookItem.getGeneration(item) > 0) {
                    if (!orgNote.isEmpty()) {
                        return false;
                    }

                    orgNote = item;
                } else if (item.getItem() == Items.MUSIC_SHEET && !item.hasTag()) {
                    if (!freshNote.isEmpty()) {
                        return false;
                    }

                    freshNote = item;
                }
            }
        }

        return !orgNote.isEmpty() && !freshNote.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingContainer inv, @NotNull RegistryAccess access) {
        ItemStack orgNote = ItemStack.EMPTY;
        ItemStack freshNote = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack item = inv.getItem(j);
            if (!item.isEmpty()) {
                if (item.getItem() == Items.MUSIC_SHEET && item.hasTag() && WrittenBookItem.getGeneration(item) > 0) {
                    if (!orgNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    orgNote = item;
                } else if (item.getItem() == Items.MUSIC_SHEET && !item.hasTag()) {
                    if (!freshNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    freshNote = item;
                }
            }
        }

        int gen = WrittenBookItem.getGeneration(orgNote);
        if (!orgNote.isEmpty() && orgNote.hasTag() && !freshNote.isEmpty() && !freshNote.hasTag() && gen < 3 && gen > 0) {
            ItemStack resultStack = new ItemStack(Items.MUSIC_SHEET);
            @SuppressWarnings("ConstantConditions") CompoundTag compoundTag = orgNote.getTag().copy();
            compoundTag.putInt("generation", gen + 1);
            resultStack.setTag(compoundTag);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < itemStacks.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.getItem() == Items.MUSIC_SHEET && itemstack.hasTag() && WrittenBookItem.getGeneration(itemstack) > 0) {
                ItemStack itemStack = itemstack.copy();
                itemStack.setCount(1);
                itemStacks.set(i, itemStack);
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