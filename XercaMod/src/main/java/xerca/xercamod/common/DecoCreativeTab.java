package xerca.xercamod.common;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.item.Items;

import java.util.Arrays;
import java.util.List;

public class DecoCreativeTab extends CreativeModeTab {
    static List<Item> orderedItems;

    public DecoCreativeTab() {
        super("deco_tab");
    }

    public static void initItemList(){
        orderedItems = Arrays.asList(
                Items.ITEM_BOOKCASE,
                Items.CARVING_STATION,
                Items.ITEM_BLOCK_LEATHER,
                Items.ITEM_BLOCK_STRAW,
                Items.ROPE,

                Items.BLACK_CUSHION,
                Items.BLUE_CUSHION,
                Items.BROWN_CUSHION,
                Items.CYAN_CUSHION,
                Items.GRAY_CUSHION,
                Items.GREEN_CUSHION,
                Items.LIGHT_BLUE_CUSHION,
                Items.LIGHT_GRAY_CUSHION,
                Items.LIME_CUSHION,
                Items.MAGENTA_CUSHION,
                Items.ORANGE_CUSHION,
                Items.PINK_CUSHION,
                Items.PURPLE_CUSHION,
                Items.RED_CUSHION,
                Items.WHITE_CUSHION,
                Items.YELLOW_CUSHION,

                Items.CARVED_OAK_1,
                Items.CARVED_OAK_2,
                Items.CARVED_OAK_3,
                Items.CARVED_OAK_4,
                Items.CARVED_OAK_5,
                Items.CARVED_OAK_6,
                Items.CARVED_OAK_7,
                Items.CARVED_OAK_8,
                Items.CARVED_BIRCH_1,
                Items.CARVED_BIRCH_2,
                Items.CARVED_BIRCH_3,
                Items.CARVED_BIRCH_4,
                Items.CARVED_BIRCH_5,
                Items.CARVED_BIRCH_6,
                Items.CARVED_BIRCH_7,
                Items.CARVED_BIRCH_8,
                Items.CARVED_DARK_OAK_1,
                Items.CARVED_DARK_OAK_2,
                Items.CARVED_DARK_OAK_3,
                Items.CARVED_DARK_OAK_4,
                Items.CARVED_DARK_OAK_5,
                Items.CARVED_DARK_OAK_6,
                Items.CARVED_DARK_OAK_7,
                Items.CARVED_DARK_OAK_8,
                Items.CARVED_ACACIA_1,
                Items.CARVED_ACACIA_2,
                Items.CARVED_ACACIA_3,
                Items.CARVED_ACACIA_4,
                Items.CARVED_ACACIA_5,
                Items.CARVED_ACACIA_6,
                Items.CARVED_ACACIA_7,
                Items.CARVED_ACACIA_8,
                Items.CARVED_JUNGLE_1,
                Items.CARVED_JUNGLE_2,
                Items.CARVED_JUNGLE_3,
                Items.CARVED_JUNGLE_4,
                Items.CARVED_JUNGLE_5,
                Items.CARVED_JUNGLE_6,
                Items.CARVED_JUNGLE_7,
                Items.CARVED_JUNGLE_8,
                Items.CARVED_SPRUCE_1,
                Items.CARVED_SPRUCE_2,
                Items.CARVED_SPRUCE_3,
                Items.CARVED_SPRUCE_4,
                Items.CARVED_SPRUCE_5,
                Items.CARVED_SPRUCE_6,
                Items.CARVED_SPRUCE_7,
                Items.CARVED_SPRUCE_8,

                Items.TERRATILE,
                Items.BLACK_TERRATILE,
                Items.BLUE_TERRATILE,
                Items.BROWN_TERRATILE,
                Items.CYAN_TERRATILE,
                Items.GRAY_TERRATILE,
                Items.GREEN_TERRATILE,
                Items.LIGHT_BLUE_TERRATILE,
                Items.LIGHT_GRAY_TERRATILE,
                Items.LIME_TERRATILE,
                Items.MAGENTA_TERRATILE,
                Items.ORANGE_TERRATILE,
                Items.PINK_TERRATILE,
                Items.PURPLE_TERRATILE,
                Items.RED_TERRATILE,
                Items.WHITE_TERRATILE,
                Items.YELLOW_TERRATILE,
                Items.TERRATILE_SLAB,
                Items.BLACK_TERRATILE_SLAB,
                Items.BLUE_TERRATILE_SLAB,
                Items.BROWN_TERRATILE_SLAB,
                Items.CYAN_TERRATILE_SLAB,
                Items.GRAY_TERRATILE_SLAB,
                Items.GREEN_TERRATILE_SLAB,
                Items.LIGHT_BLUE_TERRATILE_SLAB,
                Items.LIGHT_GRAY_TERRATILE_SLAB,
                Items.LIME_TERRATILE_SLAB,
                Items.MAGENTA_TERRATILE_SLAB,
                Items.ORANGE_TERRATILE_SLAB,
                Items.PINK_TERRATILE_SLAB,
                Items.PURPLE_TERRATILE_SLAB,
                Items.RED_TERRATILE_SLAB,
                Items.WHITE_TERRATILE_SLAB,
                Items.YELLOW_TERRATILE_SLAB,
                Items.TERRATILE_STAIRS,
                Items.BLACK_TERRATILE_STAIRS,
                Items.BLUE_TERRATILE_STAIRS,
                Items.BROWN_TERRATILE_STAIRS,
                Items.CYAN_TERRATILE_STAIRS,
                Items.GRAY_TERRATILE_STAIRS,
                Items.GREEN_TERRATILE_STAIRS,
                Items.LIGHT_BLUE_TERRATILE_STAIRS,
                Items.LIGHT_GRAY_TERRATILE_STAIRS,
                Items.LIME_TERRATILE_STAIRS,
                Items.MAGENTA_TERRATILE_STAIRS,
                Items.ORANGE_TERRATILE_STAIRS,
                Items.PINK_TERRATILE_STAIRS,
                Items.PURPLE_TERRATILE_STAIRS,
                Items.RED_TERRATILE_STAIRS,
                Items.WHITE_TERRATILE_STAIRS,
                Items.YELLOW_TERRATILE_STAIRS
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
        return new ItemStack(Items.ITEM_BLOCK_LEATHER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> items) {
        for(Item item : orderedItems) {
            item.fillItemCategory(this, items);
        }
    }
}
