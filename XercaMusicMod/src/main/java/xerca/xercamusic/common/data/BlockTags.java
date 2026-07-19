package xerca.xercamusic.common.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import xerca.xercamusic.common.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class BlockTags implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockTagGenerator::new);
    }

    private static class BlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
        BlockTagGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> future) {
            super(dataOutput, future);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            builder(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).addAll(Stream.of(
                    Blocks.BLOCK_METRONOME,
                    Blocks.MUSIC_BOX,
                    Blocks.DRUM_KIT,
                    Blocks.PIANO
            ).map(b -> BuiltInRegistries.BLOCK.getResourceKey(b).orElseThrow()));
        }
    }
}