package xerca.xercamusic.common.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import xerca.xercamusic.common.block.Blocks;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class BlockTags implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockTagGenerator::new);
    }

    private static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
        BlockTagGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> future) {
            super(dataOutput, future);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            valueLookupBuilder(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
                    Blocks.BLOCK_METRONOME,
                    Blocks.MUSIC_BOX,
                    Blocks.DRUM_KIT,
                    Blocks.PIANO
            );
            valueLookupBuilder(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(
                    Blocks.STEELPAN
            );
        }
    }
}
