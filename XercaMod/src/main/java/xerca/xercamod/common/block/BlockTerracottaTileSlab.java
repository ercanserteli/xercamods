package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class BlockTerracottaTileSlab extends SlabBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected BlockTerracottaTileSlab(DyeColor color) {
        super(Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5f).sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.EAST));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getHorizontalDirection().getOpposite().getClockWise();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = context.getLevel().getBlockState(blockpos);
        if (blockstate.getBlock() == this) {
            return blockstate.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false).setValue(FACING, dir);
        } else {
            FluidState fluidstate = context.getLevel().getFluidState(blockpos);
            BlockState blockState = this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER).setValue(FACING, dir);
            Direction direction = context.getClickedFace();
            return direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double)blockpos.getY() > 0.5D)) ? blockState : blockState.setValue(TYPE, SlabType.TOP);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }
}
