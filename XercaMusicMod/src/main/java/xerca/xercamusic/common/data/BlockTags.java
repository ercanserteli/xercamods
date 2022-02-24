package xerca.xercamusic.common.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IDataProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xerca.xercamusic.common.XercaMusic;

public class BlockTags extends BlockTagsProvider implements IDataProvider
{
    public BlockTags(DataGenerator gen, ExistingFileHelper existingFileHelper)
    {
        super(gen, XercaMusic.MODID, existingFileHelper);
    }
}