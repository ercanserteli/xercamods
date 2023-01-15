package xerca.xercamusic.common.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import xerca.xercamusic.common.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class BlockTags implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockTagGenerator::new);
    }

    private static class BlockTagGenerator extends FabricTagProvider<Block> {
        public BlockTagGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> future) {
            super(dataOutput, Registries.BLOCK, future);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            getOrCreateTagBuilder(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
                    Blocks.BLOCK_METRONOME,
                    Blocks.MUSIC_BOX,
                    Blocks.DRUM_KIT,
                    Blocks.PIANO
            );
        }
    }
}