package xerca.xercamusic.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import javax.annotation.Nullable;

public class BlockMusicBox extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty POWERING = BooleanProperty.create("powering");
    public static final BooleanProperty HAS_MUSIC = BooleanProperty.create("has_music");
    public static final BooleanProperty HAS_INSTRUMENT = BooleanProperty.create("has_instrument");

    public BlockMusicBox() {
        super(Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD).isRedstoneConductor((blockState, blockGetter, blockPos)->false));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).
                setValue(HAS_MUSIC, false).setValue(HAS_INSTRUMENT, false).setValue(FACING, Direction.NORTH).setValue(POWERING, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos())).
                setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        if(!worldIn.isClientSide){
            boolean powered = worldIn.hasNeighborSignal(pos);
            if(powered && state.getValue(POWERING)){
                return;
            }
            if (powered != state.getValue(POWERED)) {
                worldIn.setBlock(pos, state.setValue(POWERED, powered), 2);
            }
        }
    }

    private void ejectItem(Level world, BlockPos pos, BlockState state, boolean isMusic, boolean isBreaking) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TileEntityMusicBox te) {
                ItemStack itemstack;
                if (isMusic) {
                    itemstack = te.getNoteStack();
                } else {
                    IItemInstrument instrument = te.getInstrument();
                    itemstack = instrument != null ? new ItemStack((ItemLike) instrument) : ItemStack.EMPTY;
                }
                if (!itemstack.isEmpty()) {
                    if (!isBreaking) {
                        if (isMusic) {
                            te.removeNoteStack();
                            world.setBlock(pos, state.setValue(HAS_MUSIC, Boolean.FALSE), 3);
                        }
                        else {
                            te.removeInstrument();
                            world.setBlock(pos, state.setValue(HAS_INSTRUMENT, Boolean.FALSE), 3);
                        }
                    }

                    ItemEntity itemEntity;
                    if(isMusic){
                        itemEntity = new ItemEntity(world, pos.getX(), (double)pos.getY() + 1, pos.getZ(), itemstack);

                        itemEntity.setDeltaMovement(world.random.nextDouble() * 0.2 - 0.1, 0.1, world.random.nextDouble() * 0.2 - 0.1);
                    }
                    else{
                        Direction backFace = state.getValue(FACING).getOpposite();
                        int xOffset = backFace.getStepX();
                        int zOffset = backFace.getStepZ();

                        itemEntity = new ItemEntity(world, pos.getX() + xOffset*0.625, pos.getY() + 0.5D, pos.getZ() + zOffset*0.625, itemstack);
                        double speed = world.random.nextDouble() * 0.1 + 0.2;
                        itemEntity.setDeltaMovement(xOffset * speed, 0.1, zOffset * speed);
                    }

                    itemEntity.setDefaultPickUpDelay();
                    world.addFreshEntity(itemEntity);
                }
            }
        }
    }

    public static void insertMusic(LevelAccessor worldIn, BlockPos pos, BlockState state, ItemStack noteStack) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityMusicBox) {
            ((TileEntityMusicBox) blockEntity).setNoteStack(noteStack, true);
            worldIn.setBlock(pos, state.setValue(HAS_MUSIC, Boolean.TRUE), 3);
        }
    }

    public static void insertInstrument(LevelAccessor worldIn, BlockPos pos, BlockState state, Item instrument) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityMusicBox) {
            ((TileEntityMusicBox) blockEntity).setInstrument(instrument);
            worldIn.setBlock(pos, state.setValue(HAS_INSTRUMENT, Boolean.TRUE), 3);
        }
        worldIn.playSound(null, pos, SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (hit.getDirection() == Direction.UP && state.getValue(HAS_MUSIC)) {
            if(heldItem.getItem() instanceof IItemInstrument && !state.getValue(HAS_INSTRUMENT)){
                return InteractionResult.PASS;
            }
            ejectItem(worldIn, pos, state, true, false);
            return InteractionResult.SUCCESS;
        } else if (hit.getDirection() == state.getValue(FACING).getOpposite() && state.getValue(HAS_INSTRUMENT)) {
            if(heldItem.getItem() == Items.MUSIC_SHEET && !state.getValue(HAS_MUSIC)){
                return InteractionResult.PASS;
            }
            worldIn.playSound(player, pos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
            ejectItem(worldIn, pos, state, false, false);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (state.getBlock() != Blocks.MUSIC_BOX || newState.getBlock() != Blocks.MUSIC_BOX) {
            ejectItem(worldIn, pos, state, true, true);
            ejectItem(worldIn, pos, state, false, true);

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, HAS_MUSIC, HAS_INSTRUMENT, FACING, POWERING);
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        return blockState.getSignal(blockAccess, pos, side);
    }

    @Override
    public int getSignal(BlockState blockState, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        if (!blockState.getValue(POWERING)) {
            return 0;
        } else {
            return blockState.getValue(FACING).getCounterClockWise() == side ? 15 : 0;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new TileEntityMusicBox(blockPos, blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return (level1, blockPos, blockState1, t) -> {
            if (t instanceof TileEntityMusicBox) {
                TileEntityMusicBox.tick(level1, blockPos, blockState1, (TileEntityMusicBox) t);
            }
        };
    }
}
