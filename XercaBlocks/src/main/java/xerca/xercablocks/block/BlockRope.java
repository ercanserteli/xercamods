package xerca.xercablocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.util.Objects;

public class BlockRope extends PipeBlock {
    public static final MapCodec<BlockRope> CODEC = simpleCodec(BlockRope::new);

    public BlockRope(Properties properties) {
        super(0.125F, properties.mapColor(MapColor.WOOL).noOcclusion().sound(SoundType.WOOL).pushReaction(PushReaction.NORMAL));
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return makeConnections(context.getLevel(), context.getClickedPos());
    }

    private boolean isConnectable(BlockGetter level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        return neighborState.is(this) || isFaceFull(neighborState.getCollisionShape(level, neighborPos), direction.getOpposite());
    }

    private BlockState makeConnections(BlockGetter level, BlockPos pos) {
        return defaultBlockState()
                .setValue(DOWN, isConnectable(level, pos, Direction.DOWN))
                .setValue(UP, isConnectable(level, pos, Direction.UP))
                .setValue(NORTH, isConnectable(level, pos, Direction.NORTH))
                .setValue(EAST, isConnectable(level, pos, Direction.EAST))
                .setValue(SOUTH, isConnectable(level, pos, Direction.SOUTH))
                .setValue(WEST, isConnectable(level, pos, Direction.WEST));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos currentPos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        boolean connected = neighborState.is(this)
                || isFaceFull(neighborState.getCollisionShape(level, neighborPos), direction.getOpposite());
        return state.setValue(Objects.requireNonNull(PROPERTY_BY_DIRECTION.get(direction)), connected);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
