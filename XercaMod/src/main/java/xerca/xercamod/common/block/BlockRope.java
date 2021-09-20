package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockRope extends PipeBlock {
    protected BlockRope() {
        super(0.125F, Properties.of(Material.WOOL).noOcclusion().sound(SoundType.WOOL));
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, false).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.makeConnections(context.getLevel(), context.getClickedPos());
    }

    boolean isConnectable(BlockGetter blockReader, BlockPos pos, Direction dir){
        BlockState bs = blockReader.getBlockState(pos.relative(dir));
//        if(dir == Direction.DOWN && bs.getBlock() == Blocks.LANTERN){
//            return true;
//        }
        return bs.getBlock() == this || Block.canSupportCenter((LevelReader) blockReader, pos.relative(dir), dir.getOpposite());
    }

    public BlockState makeConnections(BlockGetter blockReader, BlockPos pos) {
        return this.defaultBlockState().
                setValue(DOWN, isConnectable(blockReader, pos, Direction.DOWN)).
                setValue(UP, isConnectable(blockReader, pos, Direction.UP)).
                setValue(NORTH, isConnectable(blockReader, pos, Direction.NORTH)).
                setValue(EAST, isConnectable(blockReader, pos, Direction.EAST)).
                setValue(SOUTH, isConnectable(blockReader, pos, Direction.SOUTH)).
                setValue(WEST, isConnectable(blockReader, pos, Direction.WEST));
    }

//    public boolean isStickyBlock(BlockState state)
//    {
//        return true;
//    }

//    public boolean canStickTo(BlockState state, BlockState other)
//    {
//        return state.;
//    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.canSurvive(worldIn, currentPos)) {
            worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        } else {
            return stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), isConnectable(worldIn, currentPos, facing));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }


    @Override
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }
}
