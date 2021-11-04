package xerca.xercamusic.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

public class BlockTags extends BlockTagsProvider implements DataProvider
{
    public BlockTags(DataGenerator gen, ExistingFileHelper existingFileHelper)
    {
        super(gen, XercaMusic.MODID, existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
                Blocks.BLOCK_METRONOME,
                Blocks.MUSIC_BOX,
                Blocks.DRUM_KIT,
                Blocks.PIANO
        );
    }
}