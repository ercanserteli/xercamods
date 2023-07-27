package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.ItemTeapot;
import xerca.xercamod.common.item.Items;

import java.util.Collections;
import java.util.List;

public class BlockTeapot extends Block {
    public static final IntegerProperty TEA_AMOUNT = IntegerProperty.create("tea", 0, 7);
    private static final VoxelShape centerShape = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape topShape = Block.box(7.0D, 10.0D, 7.0D, 9.0D, 11.0D, 9.0D);
    private static final VoxelShape handleShape = Block.box(7.0D, 3.0D, 12.0D, 9.0D, 9.0D, 14.0D);
    private static final VoxelShape tipShape = Block.box(7.0D, 8.0D, 3.0D, 9.0D, 9.0D, 4.0D);
    private static final VoxelShape shape = Shapes.or(Shapes.or(Shapes.or(centerShape, topShape), handleShape), tipShape);

    public BlockTeapot() {
        super(Properties.of().mapColor(MapColor.CLAY).strength(0.0F, 1.0F).sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(TEA_AMOUNT, 0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull RandomSource r) {
        int teaAmount = stateIn.getValue(TEA_AMOUNT);
        if (teaAmount > 0) {
//            if(worldIn.getGameTime() % (9 - teaAmount) == 0){
            if (r.nextDouble()*5 < ((double)teaAmount)*0.5) {
                for(int i = 0; i < r.nextInt(1) + 1; ++i) {
                    worldIn.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() + 0.5D, pos.getY() + 0.6D + r.nextDouble()*0.5D, pos.getZ() + 0.25D,
                            0.0D, 0.025D, 0.0D);

                }
            }
        }
    }

    // Called when the block is right-clicked
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {
        if (worldIn.isClientSide) return InteractionResult.SUCCESS;

        if (player instanceof ServerPlayer)
        {
            if(player.getMainHandItem().getItem() == Items.ITEM_TEACUP.get() && state.getValue(TEA_AMOUNT) > 0){
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TEA_POUR.get(), SoundSource.PLAYERS, 1.0F, worldIn.random.nextFloat() * 0.1F + 0.9F);

                player.getMainHandItem().shrink(1);
                player.addItem(new ItemStack(Items.ITEM_FULL_TEACUP_0.get()));

                worldIn.setBlockAndUpdate(pos, state.setValue(TEA_AMOUNT, state.getValue(TEA_AMOUNT) - 1));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder builder) {
        int teaAmount = state.getValue(TEA_AMOUNT);
        ItemStack teapotStack;
        if(teaAmount == 0){
            teapotStack = new ItemStack(Items.ITEM_TEAPOT.get());
        }else{
            teapotStack = new ItemStack(getItemHotTeapot(teaAmount));
        }
        return Collections.singletonList(teapotStack);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState blockState, LevelReader worldReader, BlockPos blockPos) {
        return worldReader.getBlockState(blockPos.below()).isSolid();
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState state1, @NotNull LevelAccessor world, @NotNull BlockPos blockPos, @NotNull BlockPos blockPos1) {
        return direction == Direction.DOWN && !state.canSurvive(world, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, world, blockPos, blockPos1);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState p_220053_1_, @NotNull BlockGetter p_220053_2_, @NotNull BlockPos p_220053_3_, @NotNull CollisionContext p_220053_4_) {
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEA_AMOUNT);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public ItemTeapot getItemHotTeapot(int teaAmount){
        return switch (teaAmount) {
            case 2 -> Items.ITEM_HOT_TEAPOT_2.get();
            case 3 -> Items.ITEM_HOT_TEAPOT_3.get();
            case 4 -> Items.ITEM_HOT_TEAPOT_4.get();
            case 5 -> Items.ITEM_HOT_TEAPOT_5.get();
            case 6 -> Items.ITEM_HOT_TEAPOT_6.get();
            case 7 -> Items.ITEM_HOT_TEAPOT_7.get();
            default -> Items.ITEM_HOT_TEAPOT_1.get();
        };
    }
}

