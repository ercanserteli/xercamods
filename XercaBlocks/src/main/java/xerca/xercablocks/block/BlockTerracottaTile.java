package xerca.xercablocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BlockTerracottaTile extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<BlockTerracottaTile> CODEC = simpleCodec(properties -> new BlockTerracottaTile());

    public BlockTerracottaTile(DyeColor color) {
        super(Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5f).sound(SoundType.STONE));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.EAST));
    }

    private BlockTerracottaTile() {
        this(DyeColor.LIGHT_GRAY);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite().getClockWise());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
