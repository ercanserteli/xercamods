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
    public RecipeNoteCloning(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingContainer inv, @NotNull Level worldIn) {
        ItemStack orgNote = ItemStack.EMPTY;
        ItemStack freshNote = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.MUSIC_SHEET.get() && stack.hasTag() && WrittenBookItem.getGeneration(stack) > 0) {
                    if (!orgNote.isEmpty()) {
                        return false;
                    }

                    orgNote = stack;
                } else if (stack.getItem() == Items.MUSIC_SHEET.get() && !stack.hasTag()) {
                    if (!freshNote.isEmpty()) {
                        return false;
                    }

                    freshNote = stack;
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
            ItemStack stack = inv.getItem(j);
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.MUSIC_SHEET.get() && stack.hasTag() && WrittenBookItem.getGeneration(stack) > 0) {
                    if (!orgNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    orgNote = stack;
                } else if (stack.getItem() == Items.MUSIC_SHEET.get() && !stack.hasTag()) {
                    if (!freshNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    freshNote = stack;
                }
            }
        }

        int gen = WrittenBookItem.getGeneration(orgNote);
        if (!orgNote.isEmpty() && orgNote.hasTag() && orgNote.getTag() != null && !freshNote.isEmpty() && !freshNote.hasTag() && gen < 3 && gen > 0) {
            ItemStack resultStack = new ItemStack(Items.MUSIC_SHEET.get());
            CompoundTag compoundTag = orgNote.getTag().copy();
            compoundTag.putInt("generation", gen + 1);
            resultStack.setTag(compoundTag);
            return resultStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < stacks.size(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (itemstack.hasCraftingRemainingItem()) {
                stacks.set(i, itemstack.getCraftingRemainingItem());
            } else if (itemstack.getItem() == Items.MUSIC_SHEET.get() && itemstack.hasTag() && WrittenBookItem.getGeneration(itemstack) > 0) {
                ItemStack stack = itemstack.copy();
                stack.setCount(1);
                stacks.set(i, stack);
                break;
            }
        }

        return stacks;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_NOTECLONING.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }
}