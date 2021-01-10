package xerca.xercamod.common.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import xerca.xercamod.common.item.Items;

import java.util.Random;

class BlockRicePlant extends CropsBlock implements IGrowable {

    BlockRicePlant() {
        super(Properties.create(Material.PLANTS).sound(SoundType.PLANT).hardnessAndResistance(0.0f).tickRandomly().doesNotBlockMovement());
        this.setRegistryName("block_rice_plant");
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return Items.ITEM_RICE_SEEDS;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        if(state.getBlock() == Blocks.FARMLAND && state.get(FarmlandBlock.MOISTURE) > 6){
            int water_count = 0;
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
                FluidState fluidstate = worldIn.getFluidState(pos.offset(direction));
                if (fluidstate.isTagged(FluidTags.WATER) || blockstate.getBlock() == net.minecraft.block.Blocks.FROSTED_ICE) {
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
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return isValidGround(worldIn.getBlockState(pos.down()), worldIn, pos.down()) && (worldIn.getLightSubtracted(pos, 0) >= 8 || worldIn.canSeeSky(pos)) && super.isValidPosition(state, worldIn, pos);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if(!isValidGround(worldIn.getBlockState(pos.down()), worldIn, pos.down())){
            worldIn.destroyBlock(pos, true);
        }
        super.tick(state, worldIn, pos, rand);
    }
}
