package xerca.xercamusic.common.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
        super(Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
    public IItemInstrument getItemInstrument() {
        return (IItemInstrument)Items.PIANO.get();
    }
}
