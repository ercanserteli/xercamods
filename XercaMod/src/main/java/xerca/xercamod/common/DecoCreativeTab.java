package xerca.xercamod.common;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
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
                Items.ITEM_BOOKCASE.get(),
                Items.CARVING_STATION.get(),
                Items.ITEM_BLOCK_LEATHER.get(),
                Items.ITEM_BLOCK_STRAW.get(),
                Items.ROPE.get(),
                Items.VAT.get(),
                Items.OMNI_CHEST.get(),

                Items.BLACK_CUSHION.get(),
                Items.BLUE_CUSHION.get(),
                Items.BROWN_CUSHION.get(),
                Items.CYAN_CUSHION.get(),
                Items.GRAY_CUSHION.get(),
                Items.GREEN_CUSHION.get(),
                Items.LIGHT_BLUE_CUSHION.get(),
                Items.LIGHT_GRAY_CUSHION.get(),
                Items.LIME_CUSHION.get(),
                Items.MAGENTA_CUSHION.get(),
                Items.ORANGE_CUSHION.get(),
                Items.PINK_CUSHION.get(),
                Items.PURPLE_CUSHION.get(),
                Items.RED_CUSHION.get(),
                Items.WHITE_CUSHION.get(),
                Items.YELLOW_CUSHION.get(),

                Items.CARVED_OAK_1.get(),
                Items.CARVED_OAK_2.get(),
                Items.CARVED_OAK_3.get(),
                Items.CARVED_OAK_4.get(),
                Items.CARVED_OAK_5.get(),
                Items.CARVED_OAK_6.get(),
                Items.CARVED_OAK_7.get(),
                Items.CARVED_OAK_8.get(),
                Items.CARVED_BIRCH_1.get(),
                Items.CARVED_BIRCH_2.get(),
                Items.CARVED_BIRCH_3.get(),
                Items.CARVED_BIRCH_4.get(),
                Items.CARVED_BIRCH_5.get(),
                Items.CARVED_BIRCH_6.get(),
                Items.CARVED_BIRCH_7.get(),
                Items.CARVED_BIRCH_8.get(),
                Items.CARVED_DARK_OAK_1.get(),
                Items.CARVED_DARK_OAK_2.get(),
                Items.CARVED_DARK_OAK_3.get(),
                Items.CARVED_DARK_OAK_4.get(),
                Items.CARVED_DARK_OAK_5.get(),
                Items.CARVED_DARK_OAK_6.get(),
                Items.CARVED_DARK_OAK_7.get(),
                Items.CARVED_DARK_OAK_8.get(),
                Items.CARVED_ACACIA_1.get(),
                Items.CARVED_ACACIA_2.get(),
                Items.CARVED_ACACIA_3.get(),
                Items.CARVED_ACACIA_4.get(),
                Items.CARVED_ACACIA_5.get(),
                Items.CARVED_ACACIA_6.get(),
                Items.CARVED_ACACIA_7.get(),
                Items.CARVED_ACACIA_8.get(),
                Items.CARVED_JUNGLE_1.get(),
                Items.CARVED_JUNGLE_2.get(),
                Items.CARVED_JUNGLE_3.get(),
                Items.CARVED_JUNGLE_4.get(),
                Items.CARVED_JUNGLE_5.get(),
                Items.CARVED_JUNGLE_6.get(),
                Items.CARVED_JUNGLE_7.get(),
                Items.CARVED_JUNGLE_8.get(),
                Items.CARVED_SPRUCE_1.get(),
                Items.CARVED_SPRUCE_2.get(),
                Items.CARVED_SPRUCE_3.get(),
                Items.CARVED_SPRUCE_4.get(),
                Items.CARVED_SPRUCE_5.get(),
                Items.CARVED_SPRUCE_6.get(),
                Items.CARVED_SPRUCE_7.get(),
                Items.CARVED_SPRUCE_8.get(),
                Items.CARVED_CRIMSON_1.get(),
                Items.CARVED_CRIMSON_2.get(),
                Items.CARVED_CRIMSON_3.get(),
                Items.CARVED_CRIMSON_4.get(),
                Items.CARVED_CRIMSON_5.get(),
                Items.CARVED_CRIMSON_6.get(),
                Items.CARVED_CRIMSON_7.get(),
                Items.CARVED_CRIMSON_8.get(),
                Items.CARVED_WARPED_1.get(),
                Items.CARVED_WARPED_2.get(),
                Items.CARVED_WARPED_3.get(),
                Items.CARVED_WARPED_4.get(),
                Items.CARVED_WARPED_5.get(),
                Items.CARVED_WARPED_6.get(),
                Items.CARVED_WARPED_7.get(),
                Items.CARVED_WARPED_8.get(),

                Items.TERRATILE.get(),
                Items.BLACK_TERRATILE.get(),
                Items.BLUE_TERRATILE.get(),
                Items.BROWN_TERRATILE.get(),
                Items.CYAN_TERRATILE.get(),
                Items.GRAY_TERRATILE.get(),
                Items.GREEN_TERRATILE.get(),
                Items.LIGHT_BLUE_TERRATILE.get(),
                Items.LIGHT_GRAY_TERRATILE.get(),
                Items.LIME_TERRATILE.get(),
                Items.MAGENTA_TERRATILE.get(),
                Items.ORANGE_TERRATILE.get(),
                Items.PINK_TERRATILE.get(),
                Items.PURPLE_TERRATILE.get(),
                Items.RED_TERRATILE.get(),
                Items.WHITE_TERRATILE.get(),
                Items.YELLOW_TERRATILE.get(),
                Items.TERRATILE_SLAB.get(),
                Items.BLACK_TERRATILE_SLAB.get(),
                Items.BLUE_TERRATILE_SLAB.get(),
                Items.BROWN_TERRATILE_SLAB.get(),
                Items.CYAN_TERRATILE_SLAB.get(),
                Items.GRAY_TERRATILE_SLAB.get(),
                Items.GREEN_TERRATILE_SLAB.get(),
                Items.LIGHT_BLUE_TERRATILE_SLAB.get(),
                Items.LIGHT_GRAY_TERRATILE_SLAB.get(),
                Items.LIME_TERRATILE_SLAB.get(),
                Items.MAGENTA_TERRATILE_SLAB.get(),
                Items.ORANGE_TERRATILE_SLAB.get(),
                Items.PINK_TERRATILE_SLAB.get(),
                Items.PURPLE_TERRATILE_SLAB.get(),
                Items.RED_TERRATILE_SLAB.get(),
                Items.WHITE_TERRATILE_SLAB.get(),
                Items.YELLOW_TERRATILE_SLAB.get(),
                Items.TERRATILE_STAIRS.get(),
                Items.BLACK_TERRATILE_STAIRS.get(),
                Items.BLUE_TERRATILE_STAIRS.get(),
                Items.BROWN_TERRATILE_STAIRS.get(),
                Items.CYAN_TERRATILE_STAIRS.get(),
                Items.GRAY_TERRATILE_STAIRS.get(),
                Items.GREEN_TERRATILE_STAIRS.get(),
                Items.LIGHT_BLUE_TERRATILE_STAIRS.get(),
                Items.LIGHT_GRAY_TERRATILE_STAIRS.get(),
                Items.LIME_TERRATILE_STAIRS.get(),
                Items.MAGENTA_TERRATILE_STAIRS.get(),
                Items.ORANGE_TERRATILE_STAIRS.get(),
                Items.PINK_TERRATILE_STAIRS.get(),
                Items.PURPLE_TERRATILE_STAIRS.get(),
                Items.RED_TERRATILE_STAIRS.get(),
                Items.WHITE_TERRATILE_STAIRS.get(),
                Items.YELLOW_TERRATILE_STAIRS.get()
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(Items.ITEM_BLOCK_LEATHER.get());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemList(@NotNull NonNullList<ItemStack> items) {
        for(Item item : orderedItems) {
            item.fillItemCategory(this, items);
        }
    }
}
