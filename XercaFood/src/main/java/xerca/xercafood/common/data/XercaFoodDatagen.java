package xerca.xercafood.common.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import xerca.xercafood.common.block.Blocks;

public class XercaFoodDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(BlockTags::new);
    }

    private static class BlockTags extends FabricTagProvider<Block> {
        public BlockTags(FabricDataGenerator dataGenerator) {
            super(dataGenerator, Registry.BLOCK);
        }

        @Override
        protected void generateTags() {
            getOrCreateTagBuilder(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(
                    Blocks.VAT,
                    Blocks.VAT_MILK,
                    Blocks.VAT_CHEESE
            );
        }
    }
}
