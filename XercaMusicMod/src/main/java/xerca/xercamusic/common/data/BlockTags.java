package xerca.xercamusic.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class BlockTags extends BlockTagsProvider implements DataProvider
{
    public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, XercaMusic.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
                Blocks.BLOCK_METRONOME.get(),
                Blocks.MUSIC_BOX.get(),
                Blocks.DRUM_KIT.get(),
                Blocks.PIANO.get()
        );
    }
}