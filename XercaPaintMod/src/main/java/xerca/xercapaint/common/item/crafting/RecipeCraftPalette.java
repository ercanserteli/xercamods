package xerca.xercapaint.common.item.crafting;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeCraftPalette extends SpecialRecipe {
    public RecipeCraftPalette(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    private boolean isPlank(ItemStack stack){
        return stack.getItem().getTags().contains(new ResourceLocation("minecraft:planks"));
    }

    private boolean isDye(ItemStack stack){
        return stack.getItem() instanceof DyeItem;
    }

    private boolean isPlankRow(CraftingInventory inv, int row){
        int plankCount = 0;
        for(int j = 0; j < inv.getWidth(); ++j) {
            int id = row*inv.getWidth() + j;
            ItemStack stack = inv.getStackInSlot(id);
            if(isPlank(stack)){
                plankCount++;
            }
        }
        return plankCount == 3;
    }

    private int findPlankRow(CraftingInventory inv){
        for(int i = 0; i < inv.getHeight(); ++i) {
            if(isPlankRow(inv, i)){
                return i;
            }
        }
        return -1;
    }

    @Nullable
    private ArrayList<ItemStack> findDyes(CraftingInventory inv, int plankRow){
        ArrayList<ItemStack> dyes = new ArrayList<>();
        for(int i = 0; i < inv.getHeight(); ++i) {
            if(i == plankRow){
                continue;
            }
            for(int j = 0; j < inv.getWidth(); ++j) {
                int id = i*inv.getWidth() + j;
                ItemStack stack = inv.getStackInSlot(id);
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
    public boolean matches(CraftingInventory inv, World worldIn) {
        int plankRow = findPlankRow(inv);
        if(plankRow < 0){
            return false;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, plankRow);
        if(dyes == null || dyes.isEmpty()){
            return false;
        }

        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
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
        CompoundNBT tag = result.getOrCreateTag();
        tag.putByteArray("basic", basicColors);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        return list;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_PALETTE_CRAFTING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }
}