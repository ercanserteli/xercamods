package xerca.xercamusic.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

public class BlockPiano extends BlockInstrument {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape[] shapes = {
            VoxelShapes.or(
                    Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.makeCuboidShape(0.0D, 10.0D, 7.0D, 16.0D, 16.0D, 16.0D)),
            VoxelShapes.or(
                    Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 9.0D)),
            VoxelShapes.or(
                    Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.makeCuboidShape(7.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            VoxelShapes.or(
                    Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 9.0D, 16.0D, 16.0D)),
    };

    public BlockPiano() {
        super(Properties.create(Material.WOOD).hardnessAndResistance(2.f, 6.f).sound(SoundType.WOOD).notSolid());
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
        this.setRegistryName("piano");
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shapes[Math.max(0, state.get(FACING).getIndex() - 2)];
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public ItemInstrument getItemInstrument() {
        return Items.PIANO;
    }
}
