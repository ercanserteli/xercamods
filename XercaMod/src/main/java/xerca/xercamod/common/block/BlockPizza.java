package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockPizza extends Block {
    public enum Ingredient {
        EMPTY, CHICKEN, FISH, MEAT, MUSHROOM, PEPPERONI
    }

    public static final int MAX_BITES = 3;
    public final int hungerPerBite;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, MAX_BITES);
    protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D),
            Shapes.or(Block.box(1.0D, 0.0D, 8.0D, 15.0D, 2.0D, 15.0D),
                      Block.box(1.0D, 0.0D, 1.0D, 8.0D, 2.0D, 8.0D)),
            Block.box(1.0D, 0.0D, 8.0D, 15.0D, 2.0D, 15.0D),
            Block.box(8.0D, 0.0D, 8.0D, 15.0D, 2.0D, 15.0D)
    };

    public BlockPizza(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3) {
        super(Properties.of().pushReaction(PushReaction.DESTROY).mapColor(DyeColor.YELLOW).sound(SoundType.WOOL).strength(0.5F));
        this.hungerPerBite = 1 + (slot1.equals(Ingredient.EMPTY) ? 0 : 1) +  (slot2.equals(Ingredient.EMPTY) ? 0 : 1) + (slot3.equals(Ingredient.EMPTY) ? 0 : 1);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return SHAPE_BY_BITE[blockState.getValue(BITES)];
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit){
        ItemStack heldItem = player.getItemInHand(handIn);
        if (worldIn.isClientSide) {
            if (eat(worldIn, pos, state, player, hungerPerBite).consumesAction()) {
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL,
                        1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
                return InteractionResult.SUCCESS;
            }

            if (heldItem.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        InteractionResult ate = eat(worldIn, pos, state, player, hungerPerBite);
        if(ate.shouldSwing()){
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
    public @NotNull BlockState updateShape(@NotNull BlockState blockState, @NotNull Direction direction, @NotNull BlockState blockState1, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos blockPos, @NotNull BlockPos blockPos1) {
        return direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, blockState1, levelAccessor, blockPos, blockPos1);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.below()).isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        blockStateBuilder.add(BITES);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull PathComputationType computationType) {
        return false;
    }

    public static boolean isAllEmpty(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3){
        return slot1.equals(BlockPizza.Ingredient.EMPTY) && slot2.equals(BlockPizza.Ingredient.EMPTY) && slot3.equals(BlockPizza.Ingredient.EMPTY);
    }

    public static String postfix(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3) {
        return itn(slot1) + itn(slot2) + itn(slot3);
    }

    private static String itn(BlockPizza.Ingredient ingredient) {
        return ingredient.equals(BlockPizza.Ingredient.EMPTY) ? "" : "_" + ingredient.name().toLowerCase();
    }
}
