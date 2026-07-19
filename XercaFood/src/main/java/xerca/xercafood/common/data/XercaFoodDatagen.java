package xerca.xercafood.common.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class XercaFoodDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockTagGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
    }

    private static class BlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
        public BlockTagGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> future) {
            super(dataOutput, future);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            builder(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).addAll(Stream.of(
                    (Block) Blocks.VAT,
                    Blocks.VAT_MILK,
                    Blocks.VAT_CHEESE
            ).map(b -> BuiltInRegistries.BLOCK.getResourceKey(b).orElseThrow()));
        }
    }

    private static class ItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {
        public ItemTagGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> future) {
            super(dataOutput, future);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            builder(ItemTags.MEAT).addAll(Stream.of(
                    Items.GLOW_SQUID_INK_PAELLA,
                    Items.SQUID_INK_PAELLA,
                    Items.CHEESEBURGER,
                    Items.CHEESE_SLICE,
                    Items.RAW_SHISH_KEBAB,
                    Items.DONER_WRAP,
                    Items.CHUBBY_DONER,
                    Items.ALEXANDER,
                    Items.DONER_SLICE,
                    Items.SASHIMI,
                    Items.OYAKODON,
                    Items.BEEF_DONBURI,
                    Items.NIGIRI_SUSHI,
                    Items.SUSHI,
                    Items.RAW_PATTY,
                    Items.COOKED_PATTY,
                    Items.RAW_CHICKEN_PATTY,
                    Items.COOKED_CHICKEN_PATTY,
                    Items.HAMBURGER,
                    Items.CHICKEN_BURGER,
                    Items.MUSHROOM_BURGER,
                    Items.ULTIMATE_BOTTOM,
                    Items.ULTIMATE_TOP,
                    Items.ULTIMATE_BURGER,
                    Items.ROTTEN_BURGER,
                    Items.RAW_SAUSAGE,
                    Items.COOKED_SAUSAGE,
                    Items.HOTDOG,
                    Items.FISH_BREAD,
                    Items.CHICKEN_WRAP,
                    Items.RAW_SCHNITZEL,
                    Items.COOKED_SCHNITZEL,
                    Items.FRIED_EGG,
                    Items.SHISH_KEBAB
            ).map(i -> BuiltInRegistries.ITEM.getResourceKey(i).orElseThrow()));
        }
    }
}
