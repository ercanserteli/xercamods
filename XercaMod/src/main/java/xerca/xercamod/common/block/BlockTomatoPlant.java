package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xerca.xercamod.common.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
class BlockTomatoPlant extends CropBlock implements BonemealableBlock {
    public static final IntegerProperty TOMATO_AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};


    BlockTomatoPlant() {
        super(Block.Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.0f).randomTicks().noCollission());
        this.setRegistryName("block_tomato_plant");
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return Items.ITEM_TOMATO_SEEDS;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return TOMATO_AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(3) != 0) {
            super.tick(state, worldIn, pos, rand);
        }
    }

    @Override
    protected int getBonemealAgeIncrease(Level worldIn) {
        return super.getBonemealAgeIncrease(worldIn) / 3;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOMATO_AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE[state.getValue(this.getAgeProperty())];
    }
}
