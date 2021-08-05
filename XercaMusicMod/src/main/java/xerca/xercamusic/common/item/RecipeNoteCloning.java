package xerca.xercamusic.common.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class RecipeNoteCloning extends CustomRecipe {
    public RecipeNoteCloning(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack orgNote = ItemStack.EMPTY;
        ItemStack freshNote = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.MUSIC_SHEET && itemstack1.hasTag() && WrittenBookItem.getGeneration(itemstack1) > 0) {
                    if (!orgNote.isEmpty()) {
                        return false;
                    }

                    orgNote = itemstack1;
                } else if (itemstack1.getItem() == Items.MUSIC_SHEET && !itemstack1.hasTag()) {
                    if (!freshNote.isEmpty()) {
                        return false;
                    }

                    freshNote = itemstack1;
                }
            }
        }

        return !orgNote.isEmpty() && !freshNote.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack orgNote = ItemStack.EMPTY;
        ItemStack freshNote = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack1 = inv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.MUSIC_SHEET && itemstack1.hasTag() && WrittenBookItem.getGeneration(itemstack1) > 0) {
                    if (!orgNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    orgNote = itemstack1;
                } else if (itemstack1.getItem() == Items.MUSIC_SHEET && !itemstack1.hasTag()) {
                    if (!freshNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    freshNote = itemstack1;
                }
            }
        }

        int gen = WrittenBookItem.getGeneration(orgNote);
        if (!orgNote.isEmpty() && orgNote.hasTag() && !freshNote.isEmpty() && !freshNote.hasTag() && gen < 3 && gen > 0) {
            ItemStack resultStack = new ItemStack(Items.MUSIC_SHEET);
            CompoundTag nbttagcompound = orgNote.getTag().copy();
            nbttagcompound.putInt("generation", gen + 1);
            resultStack.setTag(nbttagcompound);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.hasContainerItem()) {
                nonnulllist.set(i, itemstack.getContainerItem());
            } else if (itemstack.getItem() == Items.MUSIC_SHEET && itemstack.hasTag() && WrittenBookItem.getGeneration(itemstack) > 0) {
                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
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