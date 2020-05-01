package xerca.xercamod.common.crafting;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.item.ItemKnife;
import xerca.xercamod.common.item.Items;

@MethodsReturnNonnullByDefault
public class RecipeWoodCarving extends SpecialRecipe {
    public RecipeWoodCarving(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(!Config.isCarvedWoodEnabled()){
            return false;
        }

        int invSize = inv.getSizeInventory();
        if(invSize != 4 && invSize != 9){
            return false;
        }
        int slotAxisLength = (int) Math.sqrt(invSize);

        int logX = -1, logY = -1, knifeX = -1, knifeY = -1;
        boolean knifeFound = false, logFound = false;

        for(int y = 0; y < slotAxisLength; ++y) {
            for(int x = 0; x < slotAxisLength; ++x) {
                ItemStack itemstack = inv.getStackInSlot(y*slotAxisLength + x);
                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() instanceof ItemKnife) {
                        if(knifeFound){
                            return false;
                        }
                        knifeX = x;
                        knifeY = y;
                        knifeFound = true;
                    } else if(itemstack.getItem() == net.minecraft.block.Blocks.STRIPPED_OAK_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.block.Blocks.STRIPPED_BIRCH_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.block.Blocks.STRIPPED_DARK_OAK_LOG.asItem()) {
                        if(logFound){
                            return false;
                        }
                        logX = x;
                        logY = y;
                        logFound = true;
                    }
                }
            }
        }

        return logX != -1 && logY != -1 && knifeX != -1 && knifeY != -1;
    }

    private ItemStack getResultItem(Item logType, int difX, int difY){
        if(!Config.isCarvedWoodEnabled()){
            return ItemStack.EMPTY;
        }

        Item resultItem;
        if(logType == net.minecraft.block.Blocks.STRIPPED_OAK_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_OAK_1.asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_OAK_2.asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_OAK_3.asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_OAK_4.asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_OAK_5.asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_OAK_6.asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_OAK_7.asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_OAK_8.asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.block.Blocks.STRIPPED_BIRCH_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_BIRCH_1.asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_BIRCH_2.asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_BIRCH_3.asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_BIRCH_4.asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_BIRCH_5.asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_BIRCH_6.asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_BIRCH_7.asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_BIRCH_8.asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.block.Blocks.STRIPPED_DARK_OAK_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_DARK_OAK_1.asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_DARK_OAK_2.asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_DARK_OAK_3.asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_DARK_OAK_4.asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_DARK_OAK_5.asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_DARK_OAK_6.asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_DARK_OAK_7.asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_DARK_OAK_8.asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else{
            return ItemStack.EMPTY;
        }

        return new ItemStack(resultItem);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int invSize = inv.getSizeInventory();
        if(invSize != 4 && invSize != 9){
            return ItemStack.EMPTY;
        }
        int slotAxisLength = (int) Math.sqrt(invSize);

        int logX = -1, logY = -1, knifeX = -1, knifeY = -1;
        boolean knifeFound = false, logFound = false;
        Item logType = null;

        for(int y = 0; y < slotAxisLength; ++y) {
            for(int x = 0; x < slotAxisLength; ++x) {
                ItemStack itemstack = inv.getStackInSlot(y*slotAxisLength + x);
                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() instanceof ItemKnife) {
                        if(knifeFound){
                            return ItemStack.EMPTY;
                        }
                        knifeX = x;
                        knifeY = y;
                        knifeFound = true;
                    } else if(itemstack.getItem() == net.minecraft.block.Blocks.STRIPPED_OAK_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.block.Blocks.STRIPPED_BIRCH_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.block.Blocks.STRIPPED_DARK_OAK_LOG.asItem()) {
                        if(logFound){
                            return ItemStack.EMPTY;
                        }
                        logX = x;
                        logY = y;
                        logFound = true;
                        logType = itemstack.getItem();
                    }
                }
            }
        }

        if (logX != -1 && knifeX != -1) {

            int difX = logX - knifeX;
            int difY = logY - knifeY;
            return getResultItem(logType, difX, difY);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_WOOD_CARVING;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }
}