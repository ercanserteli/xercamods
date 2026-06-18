package xerca.xercablocks.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BlockTerracottaTileStairs extends StairBlock {
    public BlockTerracottaTileStairs(BlockState baseState, DyeColor color) {
        super(baseState, Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5f).sound(SoundType.STONE));
    }
}
