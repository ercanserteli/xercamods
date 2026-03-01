package xerca.xercapaint.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xerca.xercapaint.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeCraftPalette extends CustomRecipe {
    public RecipeCraftPalette(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private boolean isPlank(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ItemTags.PLANKS);
    }

    private static int getBasicColorIndex(ItemStack stack) {
        if (!(stack.getItem() instanceof DyeItem dyeItem)) {
            return -1;
        }
        DyeColor dyeColor = dyeItem.getDyeColor();
        if (dyeColor == null || stack.getItem() != DyeItem.byColor(dyeColor)) {
            return -1;
        }
        int colorId = dyeColor.getId();
        if (colorId < 0 || colorId >= 16) {
            return -1;
        }
        return 15 - colorId;
    }

    public static boolean isDye(ItemStack stack) {
        return getBasicColorIndex(stack) >= 0;
    }

    @Nullable
    private byte[] parseBasicColors(CraftingContainer inv) {
        int plankRow = -1;
        int plankCount = 0;
        int leftmostPlankColumn = inv.getWidth();
        int rightmostPlankColumn = -1;

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                int id = i * inv.getWidth() + j;
                ItemStack stack = inv.getItem(id);
                if (stack.isEmpty()) {
                    continue;
                }
                if (isPlank(stack)) {
                    if (plankRow < 0) {
                        plankRow = i;
                    } else if (plankRow != i) {
                        return null;
                    }
                    plankCount++;
                    if (j < leftmostPlankColumn) {
                        leftmostPlankColumn = j;
                    }
                    if (j > rightmostPlankColumn) {
                        rightmostPlankColumn = j;
                    }
                } else if (!isDye(stack)) {
                    return null;
                }
            }
        }

        if (plankRow < 0 || plankCount != 3) {
            return null;
        }
        if (rightmostPlankColumn - leftmostPlankColumn != 2) {
            return null;
        }

        for (int j = 0; j < inv.getWidth(); ++j) {
            int id = plankRow * inv.getWidth() + j;
            ItemStack stack = inv.getItem(id);
            if (!stack.isEmpty() && !isPlank(stack)) {
                return null;
            }
        }

        boolean hasDye = false;
        byte[] basicColors = new byte[16];
        for (int i = 0; i < inv.getHeight(); ++i) {
            if (i == plankRow) {
                continue;
            }
            for (int j = 0; j < inv.getWidth(); ++j) {
                int id = i * inv.getWidth() + j;
                ItemStack stack = inv.getItem(id);
                if (stack.isEmpty()) {
                    continue;
                }
                if (!isDye(stack)) {
                    return null;
                }
                int colorIndex = getBasicColorIndex(stack);
                if (colorIndex < 0 || basicColors[colorIndex] != 0) {
                    return null;
                }
                basicColors[colorIndex] = 1;
                hasDye = true;
            }
        }

        return hasDye ? basicColors : null;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return parseBasicColors(inv) != null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        byte[] basicColors = parseBasicColors(inv);
        if (basicColors == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(Items.ITEM_PALETTE);
        CompoundTag tag = result.getOrCreateTag();
        tag.putByteArray("basic", basicColors);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
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
