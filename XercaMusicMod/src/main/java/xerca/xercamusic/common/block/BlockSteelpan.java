package xerca.xercamusic.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

public class BlockSteelpan extends BlockInstrument {
    // Allows the block to face different directions
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    
    // The collision box (hitbox) for the block. 
    // This example makes it a 14x14 box that is 10 pixels high, centered in the block space.
    // Note: The JSON model you made handles the VISUALS. This handles where the player collides.
    private static final VoxelShape SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 10.0D, 15.0D);

    public BlockSteelpan(Properties properties) {
        // We use METAL properties instead of WOOD for the steelpan
        super(properties.mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .strength(3.f, 6.f)
                .sound(SoundType.METAL)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        // Because a steelpan is usually round/symmetrical, we just return one shape 
        // regardless of which way it is facing.
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        // This links the block to its item form. 
        // We will need to make sure Items.STEELPAN exists!
        return (IItemInstrument) Items.STEELPAN;
    }
}