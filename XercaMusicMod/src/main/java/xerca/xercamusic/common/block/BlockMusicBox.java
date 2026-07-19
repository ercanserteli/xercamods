package xerca.xercamusic.common.block;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

public class BlockMusicBox extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty POWERING = BooleanProperty.create("powering");
    public static final BooleanProperty HAS_MUSIC = BooleanProperty.create("has_music");
    public static final BooleanProperty HAS_INSTRUMENT = BooleanProperty.create("has_instrument");
    public static final MapCodec<BlockMusicBox> MUSIC_BOX_CODEC = simpleCodec(BlockMusicBox::new);

    public BlockMusicBox(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).
                setValue(HAS_MUSIC, false).setValue(HAS_INSTRUMENT, false).setValue(FACING, Direction.NORTH).setValue(POWERING, false));
    }

    public static void insertMusic(LevelAccessor worldIn, BlockPos pos, BlockState state, ItemStack sheetStack) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityMusicBox tileEntityMusicBox) {
            tileEntityMusicBox.setSheetStack(sheetStack, true);
            worldIn.setBlock(pos, state.setValue(HAS_MUSIC, true), 3);
        }
    }

    public static void insertInstrument(LevelAccessor worldIn, BlockPos pos, BlockState state, Item instrument) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityMusicBox tileEntityMusicBox) {
            tileEntityMusicBox.setInstrument(instrument);
            worldIn.setBlock(pos, state.setValue(HAS_INSTRUMENT, true), 3);
        }
        worldIn.playSound(null, pos, SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return MUSIC_BOX_CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos())).
                setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, @Nullable Orientation orientation, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            boolean powered = worldIn.hasNeighborSignal(pos);
            boolean powering = state.getValue(POWERING);
            boolean poweredState = state.getValue(POWERED);
            if (powered && powering) {
                return;
            }
            if (powered != poweredState) {
                worldIn.setBlock(pos, state.setValue(POWERED, powered), 2);
            }
        }
    }

    private void ejectItem(Level world, BlockPos pos, BlockState state, boolean isMusic, boolean isBreaking) {
        if (world.isClientSide()) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TileEntityMusicBox te)) {
            return;
        }

        ItemStack itemStack = getEjectedStack(te, isMusic);
        if (itemStack.isEmpty()) {
            return;
        }

        if (!isBreaking) {
            updateBlockAndInventory(world, pos, state, te, isMusic);
        }

        ItemEntity itemEntity = createItemEntity(world, pos, state, itemStack, isMusic);
        itemEntity.setDefaultPickUpDelay();
        world.addFreshEntity(itemEntity);
    }

    private ItemStack getEjectedStack(TileEntityMusicBox te, boolean isMusic) {
        if (isMusic) {
            return te.getSheetStack();
        }
        IItemInstrument instrument = te.getInstrument();
        return instrument != null ? new ItemStack((ItemLike) instrument) : ItemStack.EMPTY;
    }

    private void updateBlockAndInventory(Level world, BlockPos pos, BlockState state, TileEntityMusicBox te, boolean isMusic) {
        if (isMusic) {
            te.removeSheetStack();
            world.setBlock(pos, state.setValue(HAS_MUSIC, Boolean.FALSE), 3);
        } else {
            te.removeInstrument();
            world.setBlock(pos, state.setValue(HAS_INSTRUMENT, Boolean.FALSE), 3);
        }
    }

    private ItemEntity createItemEntity(Level world, BlockPos pos, BlockState state, ItemStack itemStack, boolean isMusic) {
        if (isMusic) {
            ItemEntity itemEntity = new ItemEntity(
                    world,
                    pos.getX(),
                    pos.getY() + 1.0D,
                    pos.getZ(),
                    itemStack
            );
            itemEntity.setDeltaMovement(
                    world.getRandom().nextDouble() * 0.2 - 0.1,
                    0.1,
                    world.getRandom().nextDouble() * 0.2 - 0.1
            );
            return itemEntity;
        }

        Direction backFace = state.getValue(FACING).getOpposite();
        int xOffset = backFace.getStepX();
        int zOffset = backFace.getStepZ();

        ItemEntity itemEntity = new ItemEntity(
                world,
                pos.getX() + xOffset * 0.625D,
                pos.getY() + 0.5D,
                pos.getZ() + zOffset * 0.625D,
                itemStack
        );
        double speed = world.getRandom().nextDouble() * 0.1 + 0.2;
        itemEntity.setDeltaMovement(xOffset * speed, 0.1, zOffset * speed);
        return itemEntity;
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasMusic = state.getValue(HAS_MUSIC);
        boolean hasInstrument = state.getValue(HAS_INSTRUMENT);
        Direction facing = state.getValue(FACING);
        if (hitResult.getDirection() == Direction.UP && hasMusic) {
            if (heldItem.getItem() instanceof IItemInstrument && !hasInstrument) {
                return InteractionResult.PASS;
            }
            ejectItem(level, pos, state, true, false);
            return InteractionResult.SUCCESS;
        } else if (hitResult.getDirection() == facing.getOpposite() && hasInstrument) {
            if (heldItem.getItem() == Items.MUSIC_SHEET && !hasMusic) {
                return InteractionResult.PASS;
            }
            level.playSound(player, pos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            ejectItem(level, pos, state, false, false);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void ejectContentsOnRemoval(Level world, BlockPos pos, BlockState state, TileEntityMusicBox te) {
        ejectStackOnRemoval(world, pos, state, te, true);
        ejectStackOnRemoval(world, pos, state, te, false);
    }

    private void ejectStackOnRemoval(Level world, BlockPos pos, BlockState state, TileEntityMusicBox te, boolean isMusic) {
        if (world.isClientSide()) {
            return;
        }
        ItemStack itemStack = getEjectedStack(te, isMusic);
        if (itemStack.isEmpty()) {
            return;
        }
        ItemEntity itemEntity = createItemEntity(world, pos, state, itemStack, isMusic);
        itemEntity.setDefaultPickUpDelay();
        world.addFreshEntity(itemEntity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, HAS_MUSIC, HAS_INSTRUMENT, FACING, POWERING);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getSignal(blockAccess, pos, side);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        boolean powering = blockState.getValue(POWERING);
        if (!powering) {
            return 0;
        }
        return blockState.getValue(FACING).getCounterClockWise() == side ? 15 : 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntityMusicBox(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return (level1, blockPos, blockState1, t) -> {
            if (t instanceof TileEntityMusicBox tileEntityMusicBox) {
                TileEntityMusicBox.tick(level1, blockPos, blockState1, tileEntityMusicBox);
            }
        };
    }
}
