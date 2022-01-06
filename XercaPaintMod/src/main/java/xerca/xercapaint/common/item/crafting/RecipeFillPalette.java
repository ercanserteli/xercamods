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
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeFillPalette extends SpecialRecipe {
    public RecipeFillPalette(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    private boolean isPalette(ItemStack stack){
        return stack.getItem() instanceof ItemPalette;
    }

    private boolean isDye(ItemStack stack){
        return stack.getItem() instanceof DyeItem;
    }

    @Nullable
    private int findPalette(CraftingInventory inv){
        for(int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if(isPalette(stack)){
                return i;
            }
        }
        return -1;
    }

    @Nullable
    private ArrayList<ItemStack> findDyes(CraftingInventory inv, int paletteId){
        ArrayList<ItemStack> dyes = new ArrayList<>();
        for(int i = 0; i < inv.getContainerSize(); ++i) {
            if(i == paletteId){
                continue;
            }
            ItemStack stack = inv.getItem(i);
            if(isDye(stack)){
                dyes.add(stack);
            }
            else if(!stack.isEmpty()){
                return null;
            }
        }
        return dyes;
    }


    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        int paletteId = findPalette(inv);
        if(paletteId < 0){
            return false;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, paletteId);
        return dyes != null && !dyes.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(CraftingInventory inv) {
        int paletteId = findPalette(inv);
        if(paletteId < 0){
            return ItemStack.EMPTY;
        }
        ArrayList<ItemStack> dyes = findDyes(inv, paletteId);
        if(dyes == null || dyes.isEmpty()){
            return ItemStack.EMPTY;
        }

        byte[] basicColors;
        ItemStack inputPalette = inv.getItem(paletteId);
        CompoundNBT orgTag = inputPalette.getOrCreateTag().copy();
        if(orgTag.contains("basic")){
            basicColors = orgTag.getByteArray("basic");
//            XercaPaint.LOGGER.debug("Basic found. Len: " + basicColors.length);
        }
        else{
            basicColors = new byte[16];
//            XercaPaint.LOGGER.debug("Basic not found. Creating");
        }

        for(ItemStack dye : dyes){
            DyeColor color = ((DyeItem)(dye.getItem())).getDyeColor();
            int realColorId = 15 - color.getId();
            if(basicColors[realColorId] > 0){
//                XercaPaint.LOGGER.debug("Color already exists in palette.");
                return ItemStack.EMPTY;
            }
            basicColors[realColorId] = 1;
        }
        orgTag.putByteArray("basic", basicColors);

        ItemStack result = new ItemStack(Items.ITEM_PALETTE);
        result.setTag(orgTag);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
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