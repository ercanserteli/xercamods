package xerca.xercapaint.common.item.crafting;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
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
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeFillPalette extends CustomRecipe {
    public RecipeFillPalette(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    private boolean isPalette(ItemStack stack){
        return stack.getItem() instanceof ItemPalette;
    }

    private boolean isDye(ItemStack stack){
        return stack.getItem() instanceof DyeItem;
    }

    private int findPalette(CraftingContainer inv){
        for(int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if(isPalette(stack)){
                return i;
            }
        }
        return -1;
    }

    @Nullable
    private ArrayList<ItemStack> findDyes(CraftingContainer inv, int paletteId){
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
    public boolean matches(CraftingContainer inv, Level worldIn) {
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
    public ItemStack assemble(CraftingContainer inv) {
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
        CompoundTag orgTag = inputPalette.getOrCreateTag().copy();
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

        ItemStack result = new ItemStack(Items.ITEM_PALETTE.get());
        result.setTag(orgTag);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_PALETTE_FILLING.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}