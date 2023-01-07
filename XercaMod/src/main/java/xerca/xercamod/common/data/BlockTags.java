package xerca.xercamod.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class BlockTags extends BlockTagsProvider implements DataProvider
{
    public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, XercaMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
                Blocks.BLOCK_BOOKCASE.get(),
                Blocks.CARVING_STATION.get(),
                Blocks.CARVED_OAK_1.get(),
                Blocks.CARVED_OAK_2.get(),
                Blocks.CARVED_OAK_3.get(),
                Blocks.CARVED_OAK_4.get(),
                Blocks.CARVED_OAK_5.get(),
                Blocks.CARVED_OAK_6.get(),
                Blocks.CARVED_OAK_7.get(),
                Blocks.CARVED_OAK_8.get(),
                Blocks.CARVED_BIRCH_1.get(),
                Blocks.CARVED_BIRCH_2.get(),
                Blocks.CARVED_BIRCH_3.get(),
                Blocks.CARVED_BIRCH_4.get(),
                Blocks.CARVED_BIRCH_5.get(),
                Blocks.CARVED_BIRCH_6.get(),
                Blocks.CARVED_BIRCH_7.get(),
                Blocks.CARVED_BIRCH_8.get(),
                Blocks.CARVED_DARK_OAK_1.get(),
                Blocks.CARVED_DARK_OAK_2.get(),
                Blocks.CARVED_DARK_OAK_3.get(),
                Blocks.CARVED_DARK_OAK_4.get(),
                Blocks.CARVED_DARK_OAK_5.get(),
                Blocks.CARVED_DARK_OAK_6.get(),
                Blocks.CARVED_DARK_OAK_7.get(),
                Blocks.CARVED_DARK_OAK_8.get(),
                Blocks.CARVED_ACACIA_1.get(),
                Blocks.CARVED_ACACIA_2.get(),
                Blocks.CARVED_ACACIA_3.get(),
                Blocks.CARVED_ACACIA_4.get(),
                Blocks.CARVED_ACACIA_5.get(),
                Blocks.CARVED_ACACIA_6.get(),
                Blocks.CARVED_ACACIA_7.get(),
                Blocks.CARVED_ACACIA_8.get(),
                Blocks.CARVED_JUNGLE_1.get(),
                Blocks.CARVED_JUNGLE_2.get(),
                Blocks.CARVED_JUNGLE_3.get(),
                Blocks.CARVED_JUNGLE_4.get(),
                Blocks.CARVED_JUNGLE_5.get(),
                Blocks.CARVED_JUNGLE_6.get(),
                Blocks.CARVED_JUNGLE_7.get(),
                Blocks.CARVED_JUNGLE_8.get(),
                Blocks.CARVED_SPRUCE_1.get(),
                Blocks.CARVED_SPRUCE_2.get(),
                Blocks.CARVED_SPRUCE_3.get(),
                Blocks.CARVED_SPRUCE_4.get(),
                Blocks.CARVED_SPRUCE_5.get(),
                Blocks.CARVED_SPRUCE_6.get(),
                Blocks.CARVED_SPRUCE_7.get(),
                Blocks.CARVED_SPRUCE_8.get(),
                Blocks.CARVED_CRIMSON_1.get(),
                Blocks.CARVED_CRIMSON_2.get(),
                Blocks.CARVED_CRIMSON_3.get(),
                Blocks.CARVED_CRIMSON_4.get(),
                Blocks.CARVED_CRIMSON_5.get(),
                Blocks.CARVED_CRIMSON_6.get(),
                Blocks.CARVED_CRIMSON_7.get(),
                Blocks.CARVED_CRIMSON_8.get(),
                Blocks.CARVED_WARPED_1.get(),
                Blocks.CARVED_WARPED_2.get(),
                Blocks.CARVED_WARPED_3.get(),
                Blocks.CARVED_WARPED_4.get(),
                Blocks.CARVED_WARPED_5.get(),
                Blocks.CARVED_WARPED_6.get(),
                Blocks.CARVED_WARPED_7.get(),
                Blocks.CARVED_WARPED_8.get()
        );
        this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(
                Blocks.OMNI_CHEST.get(),

                Blocks.VAT.get(),
                Blocks.VAT_MILK.get(),
                Blocks.VAT_CHEESE.get(),

                Blocks.BLACK_TERRATILE.get(),
                Blocks.BLUE_TERRATILE.get(),
                Blocks.BROWN_TERRATILE.get(),
                Blocks.CYAN_TERRATILE.get(),
                Blocks.GRAY_TERRATILE.get(),
                Blocks.GREEN_TERRATILE.get(),
                Blocks.LIGHT_BLUE_TERRATILE.get(),
                Blocks.LIGHT_GRAY_TERRATILE.get(),
                Blocks.LIME_TERRATILE.get(),
                Blocks.MAGENTA_TERRATILE.get(),
                Blocks.ORANGE_TERRATILE.get(),
                Blocks.PINK_TERRATILE.get(),
                Blocks.PURPLE_TERRATILE.get(),
                Blocks.RED_TERRATILE.get(),
                Blocks.WHITE_TERRATILE.get(),
                Blocks.YELLOW_TERRATILE.get(),
                Blocks.TERRATILE.get(),
                Blocks.BLACK_TERRATILE_SLAB.get(),
                Blocks.BLUE_TERRATILE_SLAB.get(),
                Blocks.BROWN_TERRATILE_SLAB.get(),
                Blocks.CYAN_TERRATILE_SLAB.get(),
                Blocks.GRAY_TERRATILE_SLAB.get(),
                Blocks.GREEN_TERRATILE_SLAB.get(),
                Blocks.LIGHT_BLUE_TERRATILE_SLAB.get(),
                Blocks.LIGHT_GRAY_TERRATILE_SLAB.get(),
                Blocks.LIME_TERRATILE_SLAB.get(),
                Blocks.MAGENTA_TERRATILE_SLAB.get(),
                Blocks.ORANGE_TERRATILE_SLAB.get(),
                Blocks.PINK_TERRATILE_SLAB.get(),
                Blocks.PURPLE_TERRATILE_SLAB.get(),
                Blocks.RED_TERRATILE_SLAB.get(),
                Blocks.WHITE_TERRATILE_SLAB.get(),
                Blocks.YELLOW_TERRATILE_SLAB.get(),
                Blocks.TERRATILE_SLAB.get(),
                Blocks.BLACK_TERRATILE_STAIRS.get(),
                Blocks.BLUE_TERRATILE_STAIRS.get(),
                Blocks.BROWN_TERRATILE_STAIRS.get(),
                Blocks.CYAN_TERRATILE_STAIRS.get(),
                Blocks.GRAY_TERRATILE_STAIRS.get(),
                Blocks.GREEN_TERRATILE_STAIRS.get(),
                Blocks.LIGHT_BLUE_TERRATILE_STAIRS.get(),
                Blocks.LIGHT_GRAY_TERRATILE_STAIRS.get(),
                Blocks.LIME_TERRATILE_STAIRS.get(),
                Blocks.MAGENTA_TERRATILE_STAIRS.get(),
                Blocks.ORANGE_TERRATILE_STAIRS.get(),
                Blocks.PINK_TERRATILE_STAIRS.get(),
                Blocks.PURPLE_TERRATILE_STAIRS.get(),
                Blocks.RED_TERRATILE_STAIRS.get(),
                Blocks.WHITE_TERRATILE_STAIRS.get(),
                Blocks.YELLOW_TERRATILE_STAIRS.get(),
                Blocks.TERRATILE_STAIRS.get()
        );
        this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_HOE).add(
                Blocks.BLOCK_LEATHER.get(),
                Blocks.BLOCK_STRAW.get()
        );
    }
}