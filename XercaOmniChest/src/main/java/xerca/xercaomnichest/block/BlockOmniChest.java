package xerca.xercaomnichest.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xerca.xercaomnichest.block_entity.BlockEntities;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;
import xerca.xercaomnichest.data.OmniChestInventory;
import xerca.xercaomnichest.data.OmniChestSavedData;

public class BlockOmniChest extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<BlockOmniChest> CODEC = BlockBehaviour.simpleCodec(properties -> new BlockOmniChest());
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final Component CONTAINER_TITLE = Component.translatable("container.xercaomnichest.omni_chest");

    public BlockOmniChest() {
        super(Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(22.5F, 600.0F)
                .lightLevel(state -> 14)
                .requiresCorrectToolForDrops());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull BlockHitResult hitResult) {
        return openMenu(level, pos, player);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        ItemInteractionResult result = openMenu(level, pos, player);
        return result.consumesAction() ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private ItemInteractionResult openMenu(Level level, BlockPos pos, Player player) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (level.getServer() == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockPos blockPosAbove = pos.above();
        if (level.getBlockState(blockPosAbove).isRedstoneConductor(level, blockPosAbove)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BlockEntityOmniChest omniChest)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        OmniChestInventory inventory = getContainer(level.getServer());
        inventory.setActiveChest(omniChest, player);
        player.openMenu(menuProvider(inventory));
        PiglinAi.angerNearbyPiglins(player, true);
        return ItemInteractionResult.SUCCESS;
    }

    private MenuProvider menuProvider(OmniChestInventory inventory) {
        return new SimpleMenuProvider((containerId, playerInventory, player) -> createMenu(containerId, playerInventory, inventory), CONTAINER_TITLE);
    }

    private static ChestMenu createMenu(int containerId, Inventory playerInventory, OmniChestInventory inventory) {
        return ChestMenu.threeRows(containerId, playerInventory, inventory);
    }

    public static OmniChestInventory getContainer(MinecraftServer server) {
        OmniChestSavedData data = server.overworld().getDataStorage().computeIfAbsent(
                new net.minecraft.world.level.saveddata.SavedData.Factory<>(OmniChestSavedData::new, OmniChestSavedData::load, DataFixTypes.SAVED_DATA_MAP_DATA),
                "omni_chest"
        );
        data.setDirty();
        return data.getInventory();
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        for (int i = 0; i < 3; ++i) {
            int xSign = random.nextInt(2) * 2 - 1;
            int zSign = random.nextInt(2) * 2 - 1;
            double x = pos.getX() + 0.5D + 0.25D * xSign;
            double y = pos.getY() + random.nextFloat();
            double z = pos.getZ() + 0.5D + 0.25D * zSign;
            double velocityX = random.nextFloat() * xSign * 0.125D;
            double velocityY = (random.nextFloat() - 0.5D) * 0.125D;
            double velocityZ = random.nextFloat() * zSign * 0.125D;
            level.addParticle(ParticleTypes.REVERSE_PORTAL, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BlockEntityOmniChest(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        if (blockEntityType != BlockEntities.OMNI_CHEST) {
            return null;
        }

        if (level.isClientSide) {
            return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
                if (blockEntity instanceof BlockEntityOmniChest omniChest) {
                    BlockEntityOmniChest.lidAnimateTick(tickerLevel, tickerPos, tickerState, omniChest);
                }
            };
        }

        return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
            if (blockEntity instanceof BlockEntityOmniChest omniChest) {
                omniChest.recheckOpen();
            }
        };
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }
}
