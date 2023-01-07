package xerca.xercamod.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.item.ItemKnife;
import xerca.xercamod.common.item.Items;

public class RecipeWoodCarving extends CustomRecipe {
    public RecipeWoodCarving(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
        if(!Config.isCarvedWoodEnabled()){
            return false;
        }

        int invSize = inv.getContainerSize();
        if(invSize != 4 && invSize != 9){
            return false;
        }
        int slotAxisLength = (int) Math.sqrt(invSize);

        int logX = -1, logY = -1, knifeX = -1, knifeY = -1;
        boolean knifeFound = false, logFound = false;

        for(int y = 0; y < slotAxisLength; ++y) {
            for(int x = 0; x < slotAxisLength; ++x) {
                ItemStack itemstack = inv.getItem(y*slotAxisLength + x);
                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() instanceof ItemKnife) {
                        if(knifeFound){
                            return false;
                        }
                        knifeX = x;
                        knifeY = y;
                        knifeFound = true;
                    } else if(itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_BIRCH_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_DARK_OAK_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_JUNGLE_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_SPRUCE_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_CRIMSON_STEM.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_WARPED_STEM.asItem()
                    ) {
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
        if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_OAK_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_OAK_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_OAK_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_OAK_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_OAK_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_OAK_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_OAK_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_OAK_8.get().asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_BIRCH_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_BIRCH_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_BIRCH_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_BIRCH_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_BIRCH_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_BIRCH_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_BIRCH_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_BIRCH_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_BIRCH_8.get().asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_DARK_OAK_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_DARK_OAK_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_DARK_OAK_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_DARK_OAK_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_DARK_OAK_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_DARK_OAK_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_DARK_OAK_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_DARK_OAK_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_DARK_OAK_8.get().asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_ACACIA_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_ACACIA_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_ACACIA_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_ACACIA_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_ACACIA_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_ACACIA_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_ACACIA_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_ACACIA_8.get().asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_JUNGLE_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_JUNGLE_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_JUNGLE_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_JUNGLE_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_JUNGLE_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_JUNGLE_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_JUNGLE_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_JUNGLE_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_JUNGLE_8.get().asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_SPRUCE_LOG.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_SPRUCE_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_SPRUCE_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_SPRUCE_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_SPRUCE_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_SPRUCE_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_SPRUCE_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_SPRUCE_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_SPRUCE_8.get().asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_CRIMSON_STEM.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_CRIMSON_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_CRIMSON_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_CRIMSON_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_CRIMSON_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_CRIMSON_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_CRIMSON_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_CRIMSON_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_CRIMSON_8.get().asItem();
            }
            else{
                return ItemStack.EMPTY;
            }
        }
        else if(logType == net.minecraft.world.level.block.Blocks.STRIPPED_WARPED_STEM.asItem()){
            if(difX == -1 && difY == -1){
                resultItem = Blocks.CARVED_WARPED_1.get().asItem();
            }
            else if(difX == 0 && difY == -1){
                resultItem = Blocks.CARVED_WARPED_2.get().asItem();
            }
            else if(difX == 1 && difY == -1){
                resultItem = Blocks.CARVED_WARPED_3.get().asItem();
            }
            else if(difX == -1 && difY == 0){
                resultItem = Blocks.CARVED_WARPED_4.get().asItem();
            }
            else if(difX == 1 && difY == 0){
                resultItem = Blocks.CARVED_WARPED_5.get().asItem();
            }
            else if(difX == -1 && difY == 1){
                resultItem = Blocks.CARVED_WARPED_6.get().asItem();
            }
            else if(difX == 0 && difY == 1){
                resultItem = Blocks.CARVED_WARPED_7.get().asItem();
            }
            else if(difX == 1 && difY == 1){
                resultItem = Blocks.CARVED_WARPED_8.get().asItem();
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
    public @NotNull ItemStack assemble(CraftingContainer inv) {
        int invSize = inv.getContainerSize();
        if(invSize != 4 && invSize != 9){
            return ItemStack.EMPTY;
        }
        int slotAxisLength = (int) Math.sqrt(invSize);

        int logX = -1, logY = -1, knifeX = -1, knifeY = -1;
        boolean knifeFound = false, logFound = false;
        Item logType = null;

        for(int y = 0; y < slotAxisLength; ++y) {
            for(int x = 0; x < slotAxisLength; ++x) {
                ItemStack itemstack = inv.getItem(y*slotAxisLength + x);
                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() instanceof ItemKnife) {
                        if(knifeFound){
                            return ItemStack.EMPTY;
                        }
                        knifeX = x;
                        knifeY = y;
                        knifeFound = true;
                    } else if(itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_BIRCH_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_DARK_OAK_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_JUNGLE_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_SPRUCE_LOG.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_CRIMSON_STEM.asItem()
                            || itemstack.getItem() == net.minecraft.world.level.block.Blocks.STRIPPED_WARPED_STEM.asItem()
                    ) {
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
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_WOOD_CARVING.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}