package xerca.xercamusic.common.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import xerca.xercamusic.common.block.Blocks;

public class BlockTags implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(BlockTagGenerator::new);
    }

    private static class BlockTagGenerator extends FabricTagProvider<Block> {
        public BlockTagGenerator(FabricDataGenerator dataGenerator) {
            super(dataGenerator, Registry.BLOCK, "mineable_with_axe");
        }

        @Override
        protected void generateTags() {
            getOrCreateTagBuilder(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
                    Blocks.BLOCK_METRONOME,
                    Blocks.MUSIC_BOX,
                    Blocks.DRUM_KIT,
                    Blocks.PIANO
            );
        }
    }
}