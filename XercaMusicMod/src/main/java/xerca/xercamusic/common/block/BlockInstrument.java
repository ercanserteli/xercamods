package xerca.xercamusic.common.block;


import net.minecraft.block.Block;
import xerca.xercamusic.common.item.ItemInstrument;

public abstract class BlockInstrument extends Block {
    public BlockInstrument(Properties properties) {
        super(properties);
    }

    public abstract ItemInstrument getItemInstrument();
}
