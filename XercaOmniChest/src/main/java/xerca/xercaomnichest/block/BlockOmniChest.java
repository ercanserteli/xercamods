package xerca.xercaomnichest.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block_entity.BlockEntities;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;
import xerca.xercaomnichest.data.OmniChestInventory;
import xerca.xercaomnichest.data.OmniChestSavedData;

public class BlockOmniChest extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<BlockOmniChest> CODEC = simpleCodec(properties -> new BlockOmniChest());
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final Component CONTAINER_TITLE = Component.translatable("container.xercaomnichest.omni_chest");

    public BlockOmniChest() {
        super(Properties.of()
                .setId(Mod.blockKey("omni_chest"))
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
        boolean waterlogged = fluidState.getType() == Fluids.WATER;
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        return openMenu(level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InteractionResult result = openMenu(level, pos, player);
        return result.consumesAction() ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private InteractionResult openMenu(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        MinecraftServer server = level.getServer();
        if (server == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        BlockPos blockPosAbove = pos.above();
        if (level.getBlockState(blockPosAbove).isRedstoneConductor(level, blockPosAbove)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BlockEntityOmniChest omniChest)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        OmniChestInventory inventory = getContainer(server);
        inventory.setActiveChest(omniChest, player);
        player.openMenu(menuProvider(inventory));
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            PiglinAi.angerNearbyPiglins(serverLevel, player, true);
        }
        return InteractionResult.SUCCESS;
    }

    private MenuProvider menuProvider(OmniChestInventory inventory) {
        return new SimpleMenuProvider((containerId, playerInventory, player) -> createMenu(containerId, playerInventory, inventory), CONTAINER_TITLE);
    }

    private static ChestMenu createMenu(int containerId, Inventory playerInventory, OmniChestInventory inventory) {
        return ChestMenu.threeRows(containerId, playerInventory, inventory);
    }

    public static OmniChestInventory getContainer(MinecraftServer server) {
        OmniChestSavedData data = server.overworld().getDataStorage().computeIfAbsent(OmniChestSavedData.TYPE);
        data.setDirty();
        return data.getInventory();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        boolean waterlogged = state.getValue(WATERLOGGED);
        return waterlogged ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        boolean waterlogged = state.getValue(WATERLOGGED);
        if (waterlogged) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
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
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityOmniChest(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != BlockEntities.OMNI_CHEST) {
            return null;
        }

        if (level.isClientSide()) {
            return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
                if (blockEntity instanceof BlockEntityOmniChest omniChest) {
                    BlockEntityOmniChest.lidAnimateTick(omniChest);
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
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
