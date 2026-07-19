package xerca.xercamusic.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMetronome;

public class BlockMetronome extends BaseEntityBlock {
    public static final IntegerProperty BPS = IntegerProperty.create("bps", 1, 50);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<BlockMetronome> METRONOME_CODEC = simpleCodec(BlockMetronome::new);

    public BlockMetronome(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BPS, 6).setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, @Nullable Orientation orientation, boolean isMoving) {
        boolean flag = worldIn.hasNeighborSignal(pos);
        boolean powered = state.getValue(POWERED);
        if (flag != powered) {
            worldIn.setBlock(pos, state.setValue(POWERED, flag), 3);
        }

    }


    public void setBps(BlockState state, Level worldIn, BlockPos pos, int bps) {
        if (!worldIn.isClientSide() && bps >= 1 && bps <= 50) {
            state = state.setValue(BPS, bps);
            worldIn.setBlock(pos, state, 3); // flags 1 | 2 (cause block update and send to clients)
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.METRONOME_SET, SoundSource.BLOCKS, 1.0f, 1.0f);
            ItemStack note = ItemStack.EMPTY;
            if (player.getMainHandItem().getItem() == Items.MUSIC_SHEET) {
                note = player.getMainHandItem();
            } else if (player.getOffhandItem().getItem() == Items.MUSIC_SHEET) {
                note = player.getOffhandItem();
            }

            int bps = note.getOrDefault(Items.SHEET_BPS, (byte) 0);
            if (!note.isEmpty() && bps > 0) {
                setBps(state, level, pos, bps);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            state = state.cycle(BPS); //cycle
            level.setBlock(pos, state, 3); // flags 1 | 2 (cause block update and send to clients)
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BPS, POWERED, FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityMetronome(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return (level1, blockPos, blockState1, t) -> {
            if (t instanceof TileEntityMetronome tileEntityMetronome) {
                TileEntityMetronome.tick(level1, tileEntityMetronome);
            }
        };
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return METRONOME_CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

}
