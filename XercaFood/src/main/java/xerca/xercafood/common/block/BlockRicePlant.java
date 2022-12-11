package xerca.xercafood.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import xerca.xercafood.common.item.Items;

class BlockRicePlant extends CropBlock implements BonemealableBlock {

    BlockRicePlant() {
        super(Properties.of(Material.PLANT).sound(SoundType.GRASS).strength(0.0f).randomTicks().noCollission());
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return Items.ITEM_RICE_SEEDS;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        if(state.getBlock() == Blocks.FARMLAND && state.getValue(FarmBlock.MOISTURE) > 6){
            int water_count = 0;
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState blockstate = worldIn.getBlockState(pos.relative(direction));
                FluidState fluidstate = worldIn.getFluidState(pos.relative(direction));
                if (fluidstate.is(FluidTags.WATER) || blockstate.getBlock() == net.minecraft.world.level.block.Blocks.FROSTED_ICE) {
                    water_count++;
                    if(water_count >= 2){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return mayPlaceOn(worldIn.getBlockState(pos.below()), worldIn, pos.below()) && (worldIn.getRawBrightness(pos, 0) >= 8 || worldIn.canSeeSky(pos)) && super.canSurvive(state, worldIn, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if(!mayPlaceOn(worldIn.getBlockState(pos.below()), worldIn, pos.below())){
            worldIn.destroyBlock(pos, true);
        }
        super.tick(state, worldIn, pos, rand);
    }
}
