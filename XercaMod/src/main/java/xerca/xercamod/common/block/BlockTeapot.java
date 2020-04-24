package xerca.xercamod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.item.ItemTeapot;
import xerca.xercamod.common.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockTeapot extends Block {
    public static final IntegerProperty TEA_AMOUNT = IntegerProperty.create("tea", 0, 7);
    private static final VoxelShape centerShape = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape topShape = Block.makeCuboidShape(7.0D, 10.0D, 7.0D, 9.0D, 11.0D, 9.0D);
    private static final VoxelShape handleShape = Block.makeCuboidShape(7.0D, 3.0D, 12.0D, 9.0D, 9.0D, 14.0D);
    private static final VoxelShape tipShape = Block.makeCuboidShape(7.0D, 8.0D, 3.0D, 9.0D, 9.0D, 4.0D);
    private static final VoxelShape shape = VoxelShapes.or(VoxelShapes.or(VoxelShapes.or(centerShape, topShape), handleShape), tipShape);

    public BlockTeapot() {
        super(Properties.create(Material.CLAY).hardnessAndResistance(0.0F, 1.0F).sound(SoundType.STONE));
        this.setDefaultState(this.stateContainer.getBaseState().with(TEA_AMOUNT, 0));
        this.setRegistryName("block_teapot");
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random r) {
        int teaAmount = stateIn.get(TEA_AMOUNT);
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

    // Called when the block is right clicked
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS;

        if (player instanceof ServerPlayerEntity)
        {
            if(player.getHeldItemMainhand().getItem() == Items.ITEM_TEACUP && state.get(TEA_AMOUNT) > 0){
                player.getHeldItemMainhand().shrink(1);
                player.addItemStackToInventory(new ItemStack(Items.ITEM_FULL_TEACUP_0));

                worldIn.setBlockState(pos, state.with(TEA_AMOUNT, state.get(TEA_AMOUNT) - 1));
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    @Deprecated
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        int teaAmount = state.get(TEA_AMOUNT);
        ItemStack teapotStack;
        if(teaAmount == 0){
            teapotStack = new ItemStack(Items.ITEM_TEAPOT);
        }else{
            teapotStack = new ItemStack(getItemHotTeapot(teaAmount));
        }
        return Collections.singletonList(teapotStack);
    }

    @Override
    public boolean isValidPosition(BlockState blockState, IWorldReader worldReader, BlockPos blockPos) {
        return worldReader.getBlockState(blockPos.down()).getMaterial().isSolid();
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState state1, IWorld world, BlockPos blockPos, BlockPos blockPos1) {
        return direction == Direction.DOWN && !state.isValidPosition(world, blockPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(state, direction, state1, world, blockPos, blockPos1);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TEA_AMOUNT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public ItemTeapot getItemHotTeapot(int teaAmount){
        switch (teaAmount){
            case 1:
                return Items.ITEM_HOT_TEAPOT_1;
            case 2:
                return Items.ITEM_HOT_TEAPOT_2;
            case 3:
                return Items.ITEM_HOT_TEAPOT_3;
            case 4:
                return Items.ITEM_HOT_TEAPOT_4;
            case 5:
                return Items.ITEM_HOT_TEAPOT_5;
            case 6:
                return Items.ITEM_HOT_TEAPOT_6;
            case 7:
                return Items.ITEM_HOT_TEAPOT_7;
            default:
                return Items.ITEM_HOT_TEAPOT_1;
        }
    }
}

