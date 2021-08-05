package xerca.xercamusic.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockPiano extends BlockInstrument {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape[] shapes = {
            Shapes.or(
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.box(0.0D, 10.0D, 7.0D, 16.0D, 16.0D, 16.0D)),
            Shapes.or(
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 9.0D)),
            Shapes.or(
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.box(7.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            Shapes.or(
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
                    Block.box(0.0D, 10.0D, 0.0D, 9.0D, 16.0D, 16.0D)),
    };

    public BlockPiano() {
        super(Properties.of(Material.WOOD).strength(2.f, 6.f).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.setRegistryName("piano");
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return shapes[Math.max(0, state.getValue(FACING).get3DDataValue() - 2)];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public ItemInstrument getItemInstrument() {
        return Items.PIANO;
    }
}
