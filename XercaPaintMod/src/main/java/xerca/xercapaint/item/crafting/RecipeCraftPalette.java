package xerca.xercapaint.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeCraftPalette extends CustomRecipe {
    private static final ResourceLocation plank = ResourceLocation.fromNamespaceAndPath("minecraft", "planks");

    public RecipeCraftPalette(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    private boolean isPlank(ItemStack stack){
        return stack.getTags().anyMatch((p)-> p.location().equals(plank));
    }

    private boolean isDye(ItemStack stack){
        return stack.getItem() instanceof DyeItem;
    }

    private boolean isPlankRow(CraftingInput inv, int row){
        int plankCount = 0;
        for(int j = 0; j < inv.width(); ++j) {
            int id = row*inv.width() + j;
            ItemStack stack = inv.getItem(id);
            if(isPlank(stack)){
                plankCount++;
            }
        }
        return plankCount == 3;
    }

    private int findPlankRow(CraftingInput inv){
        for(int i = 0; i < inv.height(); ++i) {
            if(isPlankRow(inv, i)){
                return i;
            }
        }
        return -1;
    }

    @Nullable
    private ArrayList<ItemStack> findDyes(CraftingInput inv, int plankRow){
        ArrayList<ItemStack> dyes = new ArrayList<>();
        for(int i = 0; i < inv.height(); ++i) {
            if(i == plankRow){
                continue;
            }
            for(int j = 0; j < inv.width(); ++j) {
                int id = i*inv.width() + j;
                ItemStack stack = inv.getItem(id);
                if(isDye(stack)){
                    dyes.add(stack);
                }
                else if(!stack.isEmpty()){
                    return null;
                }
            }
        }
        return dyes;
    }


    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        int plankRow = findPlankRow(inv);
        if(plankRow < 0){
            return false;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, plankRow);
        return dyes != null && !dyes.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingInput inv, @NotNull HolderLookup.Provider provider) {
        int plankRow = findPlankRow(inv);
        if(plankRow < 0){
            return ItemStack.EMPTY;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, plankRow);
        if(dyes == null || dyes.isEmpty()){
            return ItemStack.EMPTY;
        }

        byte[] basicColors = new byte[16];
        for(ItemStack dye : dyes){
            DyeColor color = ((DyeItem)(dye.getItem())).getDyeColor();
            basicColors[15 - color.getId()] = 1;
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