package xerca.xercafood.common.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.item.Items;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class XercaFoodDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockTagGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
    }

    private static class BlockTagGenerator extends FabricTagProvider<Block> {
        public BlockTagGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> future) {
            super(dataOutput, Registries.BLOCK, future);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            getOrCreateTagBuilder(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(
                    Blocks.VAT,
                    Blocks.VAT_MILK,
                    Blocks.VAT_CHEESE
            );
        }
    }

    private static class ItemTagGenerator extends FabricTagProvider<Item> {
        public ItemTagGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> future) {
            super(dataOutput, Registries.ITEM, future);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            getOrCreateTagBuilder(ItemTags.MEAT).add(
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
            );
        }
    }
}
