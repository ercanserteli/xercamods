package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.ContainerOmniChest;
import xerca.xercamod.common.SavedDataOmniChest;
import xerca.xercamod.common.tile_entity.TileEntities;
import xerca.xercamod.common.tile_entity.TileEntityOmniChest;

import javax.annotation.Nullable;

public class BlockOmniChest extends AbstractChestBlock<TileEntityOmniChest> implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final Component CONTAINER_TITLE = Component.translatable("container.xercamod.omni_chest");

    public BlockOmniChest(BlockBehaviour.Properties p_53121_) {
        super(p_53121_, TileEntities.OMNI_CHEST::get);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public DoubleBlockCombiner.@NotNull NeighborCombineResult<? extends ChestBlockEntity> combine(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, boolean b) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidstate = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        return this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    public static ContainerOmniChest getContainer(MinecraftServer server) {
        SavedDataOmniChest chestData = server.overworld().getDataStorage().computeIfAbsent(SavedDataOmniChest::load, SavedDataOmniChest::new, "omni_chest");
        chestData.setDirty();
        return chestData.getInventory();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if (level.isClientSide || level.getServer() == null) {
            return InteractionResult.SUCCESS;
        }
        ContainerOmniChest chestInventory = getContainer(level.getServer());
        BlockEntity blockentity = level.getBlockEntity(blockPos);
        if (chestInventory != null && blockentity instanceof TileEntityOmniChest) {
            BlockPos blockpos = blockPos.above();
            if (level.getBlockState(blockpos).isRedstoneConductor(level, blockpos)) {
                return InteractionResult.sidedSuccess(false);
            } else {
                TileEntityOmniChest omniChest = (TileEntityOmniChest)blockentity;
                chestInventory.setActiveChest(omniChest, player);
                player.openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> ChestMenu.threeRows(p_53124_, p_53125_, chestInventory), CONTAINER_TITLE));
                PiglinAi.angerNearbyPiglins(player, true);
                return InteractionResult.CONSUME;
            }
        } else {
            return InteractionResult.sidedSuccess(false);
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new TileEntityOmniChest(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? createTickerHelper(blockEntityType, TileEntities.OMNI_CHEST.get(), TileEntityOmniChest::lidAnimateTick) : null;
    }

    @Override
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        for(int i = 0; i < 3; ++i) {
            int j = random.nextInt(2) * 2 - 1;
            int k = random.nextInt(2) * 2 - 1;
            double d0 = (double)blockPos.getX() + 0.5D + 0.25D * (double)j;
            double d1 = (float)blockPos.getY() + random.nextFloat();
            double d2 = (double)blockPos.getZ() + 0.5D + 0.25D * (double)k;
            double d3 = random.nextFloat() * (float)j * 0.125D;
            double d4 = ((double)random.nextFloat() - 0.5D) * 0.125D;
            double d5 = random.nextFloat() * (float)k * 0.125D;
            level.addParticle(ParticleTypes.REVERSE_PORTAL, d0, d1, d2, d3, d4, d5);
        }

    }

    @Override
    public @NotNull BlockState rotate(BlockState p_53157_, Rotation p_53158_) {
        return p_53157_.setValue(FACING, p_53158_.rotate(p_53157_.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState p_53154_, Mirror p_53155_) {
        return p_53154_.rotate(p_53155_.getRotation(p_53154_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53167_) {
        p_53167_.add(FACING, WATERLOGGED);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState p_53177_) {
        return p_53177_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_53177_);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, @NotNull Direction direction, @NotNull BlockState blockState1, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos blockPos, @NotNull BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(blockState, direction, blockState1, levelAccessor, blockPos, blockPos1);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public void tick(@NotNull BlockState blockState, ServerLevel serverLevel, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        BlockEntity blockentity = serverLevel.getBlockEntity(blockPos);
        if (blockentity instanceof TileEntityOmniChest) {
            ((TileEntityOmniChest)blockentity).recheckOpen();
        }
    }
}
