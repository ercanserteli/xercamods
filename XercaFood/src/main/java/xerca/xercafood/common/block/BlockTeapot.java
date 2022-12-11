package xerca.xercafood.common.block;

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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xerca.xercafood.common.SoundEvents;
import xerca.xercafood.common.item.ItemTeapot;
import xerca.xercafood.common.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockTeapot extends Block {
    public static final IntegerProperty TEA_AMOUNT = IntegerProperty.create("tea", 0, 7);
    private static final VoxelShape centerShape = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape topShape = Block.box(7.0D, 10.0D, 7.0D, 9.0D, 11.0D, 9.0D);
    private static final VoxelShape handleShape = Block.box(7.0D, 3.0D, 12.0D, 9.0D, 9.0D, 14.0D);
    private static final VoxelShape tipShape = Block.box(7.0D, 8.0D, 3.0D, 9.0D, 9.0D, 4.0D);
    private static final VoxelShape shape = Shapes.or(Shapes.or(Shapes.or(centerShape, topShape), handleShape), tipShape);

    public BlockTeapot() {
        super(Properties.of(Material.CLAY).strength(0.0F, 1.0F).sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(TEA_AMOUNT, 0));
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource r) {
        int teaAmount = stateIn.getValue(TEA_AMOUNT);
        if (teaAmount > 0) {
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        if (worldIn.isClientSide) return InteractionResult.SUCCESS;

        if (player instanceof ServerPlayer)
        {
            if(player.getMainHandItem().getItem() == Items.ITEM_TEACUP && state.getValue(TEA_AMOUNT) > 0){
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TEA_POUR, SoundSource.PLAYERS, 1.0F, worldIn.random.nextFloat() * 0.1F + 0.9F);

                player.getMainHandItem().shrink(1);
                player.addItem(new ItemStack(Items.ITEM_FULL_TEACUP_0));

                worldIn.setBlockAndUpdate(pos, state.setValue(TEA_AMOUNT, state.getValue(TEA_AMOUNT) - 1));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        int teaAmount = state.getValue(TEA_AMOUNT);
        ItemStack teapotStack;
        if(teaAmount == 0){
            teapotStack = new ItemStack(Items.ITEM_TEAPOT);
        }else{
            teapotStack = new ItemStack(getItemHotTeapot(teaAmount));
        }
        return Collections.singletonList(teapotStack);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader worldReader, BlockPos blockPos) {
        return worldReader.getBlockState(blockPos.below()).getMaterial().isSolid();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor world, BlockPos blockPos, BlockPos blockPos1) {
        return direction == Direction.DOWN && !state.canSurvive(world, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, world, blockPos, blockPos1);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEA_AMOUNT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public ItemTeapot getItemHotTeapot(int teaAmount){
        return switch (teaAmount) {
            case 1 -> Items.ITEM_HOT_TEAPOT_1;
            case 2 -> Items.ITEM_HOT_TEAPOT_2;
            case 3 -> Items.ITEM_HOT_TEAPOT_3;
            case 4 -> Items.ITEM_HOT_TEAPOT_4;
            case 5 -> Items.ITEM_HOT_TEAPOT_5;
            case 6 -> Items.ITEM_HOT_TEAPOT_6;
            case 7 -> Items.ITEM_HOT_TEAPOT_7;
            default -> Items.ITEM_HOT_TEAPOT_1;
        };
    }
}

