package xerca.xercamusic.common.block;

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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMetronome;

import javax.annotation.Nullable;

public class BlockMetronome extends BaseEntityBlock {
    public static final IntegerProperty BPS = IntegerProperty.create("bps", 1, 50);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BlockMetronome() {
        super(Properties.of(Material.WOOD).strength(2.f, 6.f).sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(BPS, 6).setValue(POWERED, false).setValue(FACING, Direction.NORTH));
        this.setRegistryName("block_metronome");
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag = worldIn.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            if (flag) {
                // unpowered to powered
            }

            worldIn.setBlock(pos, state.setValue(POWERED, flag), 3);
        }

    }


    public void setBps(BlockState state, Level worldIn, BlockPos pos, int bps) {
        if (!worldIn.isClientSide) {
            if (bps >= 1 && bps <= 50) {
                state = state.setValue(BPS, bps);
                worldIn.setBlock(pos, state, 3); // flags 1 | 2 (cause block update and send to clients)
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.METRONOME_SET, SoundSource.BLOCKS, 1.0f, 1.0f);
            ItemStack note = ItemStack.EMPTY;
            if(player.getItemInHand(hand).getItem() == Items.MUSIC_SHEET){
                note = player.getItemInHand(hand);
            } else if(player.getOffhandItem().getItem() == Items.MUSIC_SHEET){
                note = player.getOffhandItem();
            }

            if(!note.isEmpty() && note.getTag() != null && note.getTag().contains("bps")){
                int bps = note.getTag().getInt("bps");
                setBps(state, worldIn, pos, bps);
                return InteractionResult.SUCCESS;
            }
            else{
                state = state.cycle(BPS); //cycle
                worldIn.setBlock(pos, state, 3); // flags 1 | 2 (cause block update and send to clients)
                return InteractionResult.SUCCESS;
            }
        }
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

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
//        if (level.isClientSide()) {
//            return null;
//        }
        return (level1, blockPos, blockState1, t) -> {
            if (t instanceof TileEntityMetronome) {
                TileEntityMetronome.tick(level1, blockPos, blockState1, (TileEntityMetronome) t);
            }
        };
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
