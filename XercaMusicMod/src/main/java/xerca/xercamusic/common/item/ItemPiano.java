package xerca.xercamusic.common.item;

import net.minecraft.block.Block;

public class ItemPiano extends ItemBlockInstrument{
    ItemPiano(int instrumentId, Block block) {
        super("piano", false, instrumentId, block);
    }
}
