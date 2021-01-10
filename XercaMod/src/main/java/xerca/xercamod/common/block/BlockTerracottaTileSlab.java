package xerca.xercamod.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;

public class BlockTerracottaTileSlab extends SlabBlock {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    protected BlockTerracottaTileSlab(MaterialColor color) {
        super(Properties.create(Material.ROCK, color).hardnessAndResistance(1.5f).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.EAST));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction dir = context.getPlacementHorizontalFacing().getOpposite().rotateY();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = context.getWorld().getBlockState(blockpos);
        if (blockstate.getBlock() == this) {
            return blockstate.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false).with(FACING, dir);
        } else {
            FluidState fluidstate = context.getWorld().getFluidState(blockpos);
            BlockState blockstate1 = this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER).with(FACING, dir);
            Direction direction = context.getFace();
            return direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitVec().y - (double)blockpos.getY() > 0.5D)) ? blockstate1 : blockstate1.with(TYPE, SlabType.TOP);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }
}
