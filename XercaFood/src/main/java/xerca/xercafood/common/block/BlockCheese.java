package xerca.xercafood.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xerca.xercafood.common.KnifeCompat;
import xerca.xercafood.common.item.Items;

public class BlockCheese extends Block {
    public static final int MAX_BITES = 3;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, MAX_BITES);
    protected static final VoxelShape[] SHAPE_BY_BITE = {
            box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Shapes.or(box(1.0D, 0.0D, 8.0D, 15.0D, 8.0D, 15.0D),
                    box(1.0D, 0.0D, 1.0D, 8.0D, 8.0D, 8.0D)),
            box(1.0D, 0.0D, 8.0D, 15.0D, 8.0D, 15.0D),
            box(8.0D, 0.0D, 8.0D, 15.0D, 8.0D, 15.0D)
    };

    public BlockCheese() {
        super(Properties.of().setId(xerca.xercafood.common.Mod.blockKey("cheese_wheel")).sound(SoundType.SLIME_BLOCK).strength(0.5F));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE_BY_BITE[blockState.getValue(BITES)];
    }

    @Override
    public InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (KnifeCompat.isKnife(heldItem)) {
            if (!worldIn.isClientSide()) {
                slice(worldIn, pos, state, player, handIn, heldItem);
            }
            worldIn.playSound(player, pos, xerca.xercafood.common.SoundEvents.SNEAK_HIT, SoundSource.BLOCKS, 0.4f, 0.9f + worldIn.random.nextFloat() * 0.1f);
            return InteractionResult.SUCCESS;
        }
        InteractionResult ate = useWithoutItem(state, worldIn, pos, player, hit);
        return ate.consumesAction() ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (worldIn.isClientSide()) {
            if (eat(worldIn, pos, state, player).consumesAction()) {
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL,
                        1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }

        InteractionResult ate = eat(worldIn, pos, state, player);
        if (ate.consumesAction()) {
            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL,
                    1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
        }
        return ate;
    }

    protected static InteractionResult eat(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.getFoodData().eat(2, 0.1F);
            int i = blockState.getValue(BITES);
            levelAccessor.gameEvent(player, GameEvent.EAT, blockPos);
            if (i < MAX_BITES) {
                levelAccessor.setBlock(blockPos, blockState.setValue(BITES, i + 1), 3);
            } else {
                levelAccessor.removeBlock(blockPos, false);
                levelAccessor.gameEvent(player, GameEvent.BLOCK_DESTROY, blockPos);
            }

            return InteractionResult.SUCCESS;
        }
    }

    protected static void slice(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack heldItem) {
        Vec3 playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
        Vec3 boost = playerPos.subtract(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
        boost = boost.normalize().scale(0.15d);

        ItemEntity sliceEntity = new ItemEntity(level, pos.getX() + 0.5f + boost.x * 6, pos.getY() + 0.5f,
                pos.getZ() + 0.5f + boost.x * 6, new ItemStack(Items.CHEESE_SLICE));
        sliceEntity.setDefaultPickUpDelay();
        sliceEntity.push(boost.x, 0, boost.z);
        sliceEntity.hurtMarked = true;
        level.addFreshEntity(sliceEntity);

        heldItem.hurtAndBreak(1, player, hand);

        int i = state.getValue(BITES);
        if (i < MAX_BITES) {
            level.setBlock(pos, state.setValue(BITES, i + 1), 3);
        } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }
    }

    @Override
    protected BlockState updateShape(BlockState blockState, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos blockPos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction == Direction.DOWN && !blockState.canSurvive(levelReader, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, levelReader, tickAccess, blockPos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos supportPos = blockPos.below();
        return levelReader.getBlockState(supportPos).isFaceSturdy(levelReader, supportPos, Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(BITES);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
