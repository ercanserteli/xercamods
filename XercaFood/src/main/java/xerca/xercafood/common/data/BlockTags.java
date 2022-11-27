package xerca.xercafood.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xerca.xercafood.common.XercaFood;
import xerca.xercafood.common.block.Blocks;

public class BlockTags extends BlockTagsProvider implements DataProvider
{
    public BlockTags(DataGenerator gen, ExistingFileHelper existingFileHelper)
    {
        super(gen, XercaFood.MODID, existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        this.tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE).add(
                Blocks.VAT,
                Blocks.VAT_MILK,
                Blocks.VAT_CHEESE
        );
    }
}