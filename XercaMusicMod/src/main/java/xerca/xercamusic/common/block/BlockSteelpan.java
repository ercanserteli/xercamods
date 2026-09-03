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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

public class BlockSteelpan extends BlockInstrument {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    /*
     * The Blockbench model VoxelShapes. The four entries follow Direction's
     * horizontal data-value order: north, south, west, east.
     */
    private static final VoxelShape BASE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 16.0D),
            Block.box(15.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D),
            Block.box(1.0D, 0.0D, 0.0D, 15.0D, 1.0D, 1.0D),
            Block.box(1.0D, 0.0D, 15.0D, 15.0D, 1.0D, 16.0D)
    );
    
    private static final VoxelShape[] SHAPES = {
            Shapes.or(
                    BASE,
                    Block.box(0.0D, 1.0D, 7.0D, 1.0D, 12.0D, 9.0D),
                    Block.box(15.0D, 1.0D, 7.0D, 16.0D, 12.0D, 9.0D),
                    Block.box(1.0D, 10.0D, 2.0D, 15.0D, 16.0D, 16.0D)
            ),
            Shapes.or(
                    BASE,
                    Block.box(0.0D, 1.0D, 7.0D, 1.0D, 12.0D, 9.0D),
                    Block.box(15.0D, 1.0D, 7.0D, 16.0D, 12.0D, 9.0D),
                    Block.box(1.0D, 10.0D, 0.0D, 15.0D, 16.0D, 14.0D)
            ),
            Shapes.or(
                    BASE,
                    Block.box(7.0D, 1.0D, 0.0D, 9.0D, 12.0D, 1.0D),
                    Block.box(7.0D, 1.0D, 15.0D, 9.0D, 12.0D, 16.0D),
                    Block.box(2.0D, 10.0D, 1.0D, 16.0D, 16.0D, 15.0D)
            ),
            Shapes.or(
                    BASE,
                    Block.box(7.0D, 1.0D, 0.0D, 9.0D, 12.0D, 1.0D),
                    Block.box(7.0D, 1.0D, 15.0D, 9.0D, 12.0D, 16.0D),
                    Block.box(0.0D, 10.0D, 1.0D, 14.0D, 16.0D, 15.0D)
            )
    };

    public BlockSteelpan(Properties properties) {
        super(properties
                .mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .strength(2.0f, 6.0f)
                .sound(SoundType.METAL)
                .noOcclusion());

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        // Now the black outline will perfectly hug your complex 3D model!
        return SHAPES[Math.max(0, state.getValue(FACING).get3DDataValue() - 2)];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public IItemInstrument getItemInstrument() {
        return (IItemInstrument) Items.STEELPAN;
    }
}