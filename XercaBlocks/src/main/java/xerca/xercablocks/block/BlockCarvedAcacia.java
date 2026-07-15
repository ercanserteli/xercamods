package xerca.xercablocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class BlockCarvedAcacia extends BlockCarvedLog {
    public static final MapCodec<BlockCarvedAcacia> CODEC = simpleCodec(BlockCarvedAcacia::new);

    public BlockCarvedAcacia(Properties properties) {
        super(properties
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .ignitedByLava()
                .sound(SoundType.WOOD)
                .strength(2.0F)
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends BlockCarvedLog> codec() {
        return CODEC;
    }

    // Grate-like behavior (copper grate): cull faces between same blocks and let light through.
    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }
}
