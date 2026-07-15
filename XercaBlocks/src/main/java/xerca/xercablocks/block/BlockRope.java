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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Objects;

public class BlockRope extends PipeBlock {
    public static final MapCodec<BlockRope> CODEC = simpleCodec(BlockRope::new);
    private static final EnumMap<Direction, VoxelShape> CENTER_PROBES = new EnumMap<>(Direction.class);

    static {
        final double lo = 7.0D / 16.0D;
        final double hi = 9.0D / 16.0D;
        final double t = 1.0D / 16.0D;
        CENTER_PROBES.put(Direction.DOWN, Shapes.box(lo, 0.0D, lo, hi, t, hi));
        CENTER_PROBES.put(Direction.UP, Shapes.box(lo, 1.0D - t, lo, hi, 1.0D, hi));
        CENTER_PROBES.put(Direction.NORTH, Shapes.box(lo, lo, 0.0D, hi, hi, t));
        CENTER_PROBES.put(Direction.SOUTH, Shapes.box(lo, lo, 1.0D - t, hi, hi, 1.0D));
        CENTER_PROBES.put(Direction.WEST, Shapes.box(0.0D, lo, lo, t, hi, hi));
        CENTER_PROBES.put(Direction.EAST, Shapes.box(1.0D - t, lo, lo, 1.0D, hi, hi));
    }

    public BlockRope(Properties properties) {
        // PipeBlock's constructor takes the full width in pixels since 1.21.5 (was an apothem in block units).
        super(4.0F, properties.mapColor(MapColor.WOOL).noOcclusion().sound(SoundType.WOOL).pushReaction(PushReaction.NORMAL));
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
        if (neighborState.is(this)) {
            return true;
        }
        Direction face = direction.getOpposite();
        VoxelShape collisionShape = neighborState.getCollisionShape(level, neighborPos);
        return neighborState.is(this) || isFaceFull(collisionShape, face) || isFaceCenterFull(collisionShape, face);
    }

    // True if the neighbor's collision shape fully covers the central 2x2 pixels of the given face
    private static boolean isFaceCenterFull(VoxelShape shape, Direction face) {
        VoxelShape probe = CENTER_PROBES.get(face);
        return !Shapes.joinIsNotEmpty(probe, shape, BooleanOp.ONLY_FIRST);
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
