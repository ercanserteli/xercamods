package xerca.xercafood.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockPizza extends Block {
    public enum Ingredient {
        EMPTY, CHICKEN, FISH, MEAT, MUSHROOM, PEPPERONI
    }

    public static final int MAX_BITES = 3;
    public final int hungerPerBite;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, MAX_BITES);
    protected static final VoxelShape[] SHAPE_BY_BITE = {
            box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D),
            Shapes.or(box(1.0D, 0.0D, 8.0D, 15.0D, 2.0D, 15.0D),
                    box(1.0D, 0.0D, 1.0D, 8.0D, 2.0D, 8.0D)),
            box(1.0D, 0.0D, 8.0D, 15.0D, 2.0D, 15.0D),
            box(8.0D, 0.0D, 8.0D, 15.0D, 2.0D, 15.0D)
    };

    public BlockPizza(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3) {
        super(Properties.of().setId(xerca.xercafood.common.Mod.blockKey(blockName(slot1, slot2, slot3))).sound(SoundType.WOOL).strength(0.5F));
        this.hungerPerBite = 1 + (slot1 == Ingredient.EMPTY ? 0 : 1) + (slot2 == Ingredient.EMPTY ? 0 : 1) + (slot3 == Ingredient.EMPTY ? 0 : 1);
    }

    static String blockName(Ingredient slot1, Ingredient slot2, Ingredient slot3) {
        StringBuilder sb = new StringBuilder("pizza");
        for (Ingredient ingredient : new Ingredient[]{slot1, slot2, slot3}) {
            if (ingredient != Ingredient.EMPTY) {
                sb.append('_').append(ingredient.name().toLowerCase());
            }
        }
        return sb.toString();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE_BY_BITE[blockState.getValue(BITES)];
    }

    @Override
    public InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        InteractionResult ate = useWithoutItem(state, worldIn, pos, player, hit);
        return ate.consumesAction() ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (worldIn.isClientSide()) {
            if (eat(worldIn, pos, state, player, hungerPerBite).consumesAction()) {
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL,
                        1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        InteractionResult ate = eat(worldIn, pos, state, player, hungerPerBite);
        if (ate.consumesAction()) {
            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL,
                    1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
        }
        return ate;
    }

    protected static InteractionResult eat(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Player player, int hungerFilled) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.getFoodData().eat(hungerFilled, 1.0F);
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
    protected boolean isPathfindable(BlockState blockState, PathComputationType computationType) {
        return false;
    }

    public static boolean isAllEmpty(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3) {
        return slot1 == Ingredient.EMPTY && slot2 == Ingredient.EMPTY && slot3 == Ingredient.EMPTY;
    }
}
