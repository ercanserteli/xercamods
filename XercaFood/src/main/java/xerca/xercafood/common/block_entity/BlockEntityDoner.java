package xerca.xercafood.common.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercafood.common.SoundEvents;

import static net.minecraft.world.level.block.CampfireBlock.LIT;
import static xerca.xercafood.common.block.BlockDoner.IS_RAW;
import static xerca.xercafood.common.block.BlockDoner.MEAT_AMOUNT;

public class BlockEntityDoner extends BlockEntity {
    private int spinTicks;
    private int cookingTicks;
    private int sizzleCooldown;
    private boolean isSpinning;

    public BlockEntityDoner(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.DONER, blockPos, blockState);
        spinTicks = 0;
        cookingTicks = 0;
        sizzleCooldown = 0;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntityDoner t) {
        if (level.hasNeighborSignal(t.worldPosition)) {
            RandomSource r = level.random;
            t.isSpinning = true;
            t.spinTicks++;
            if(t.getBlockState().getValue(IS_RAW) && t.getBlockState().getValue(MEAT_AMOUNT) == 4 && t.gettingRoasted()){
                t.cookingTicks++;
                if(t.sizzleCooldown == 0){
                    if(level.isClientSide){
                        level.playLocalSound(t.getBlockPos().getX(), t.getBlockPos().getY(), t.getBlockPos().getZ(), SoundEvents.SIZZLE, SoundSource.BLOCKS, 1.0f, 0.9f + r.nextFloat()*0.1f, false);
                        for(int i = 0; i < r.nextInt(2) + 1; ++i) {
                            level.addParticle(ParticleTypes.SMOKE,
                                    t.worldPosition.getX() + 0.5D, t.worldPosition.getY() + 0.6D + r.nextDouble()*0.5D, t.worldPosition.getZ() + 0.25D,
                                    -0.05D + r.nextDouble()*0.1D, 0.025D, -0.05D + r.nextDouble()*0.1D);

                        }
                    }
                    t.sizzleCooldown = 30 + r.nextInt(30);
                }
                t.sizzleCooldown--;
                if(t.cookingTicks > 500){
                    level.setBlockAndUpdate(t.getBlockPos(), t.getBlockState().setValue(IS_RAW, false));

                    level.playSound(null, t.getBlockPos(), SoundEvents.BIG_SIZZLE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    for(int i = 0; i < 12; ++i) {
                        level.addParticle(ParticleTypes.SMOKE,
                                t.worldPosition.getX() + 0.5D, t.worldPosition.getY() + 0.6D + r.nextDouble()*0.5D, t.worldPosition.getZ() + 0.25D,
                                -0.05D + r.nextDouble()*0.1D, 0.025D, -0.05D + r.nextDouble()*0.1D);

                    }
                }
            }
        } else {
            t.isSpinning = false;
        }
    }

    public float getAnimationProgress(float partialTicks) {
        return this.isSpinning ? (float)this.spinTicks + partialTicks : (float)this.spinTicks;
    }

    private boolean gettingRoasted(){
        return isFire(level.getBlockState(worldPosition.below())) || isFire(level.getBlockState(worldPosition.east())) ||
               isFire(level.getBlockState(worldPosition.west())) || isFire(level.getBlockState(worldPosition.north())) ||
               isFire(level.getBlockState(worldPosition.south())) || isFire(level.getBlockState(worldPosition.offset(0, -1, 1))) ||
               isFire(level.getBlockState(worldPosition.offset(-1, -1, 0))) || isFire(level.getBlockState(worldPosition.offset(1, -1, 0))) ||
               isFire(level.getBlockState(worldPosition.offset(0, -1, -1)));
    }

    static boolean isFire(BlockState bs){
        return bs.getBlock() == Blocks.FIRE || (bs.getBlock() == Blocks.CAMPFIRE && bs.getValue(LIT));
    }
}
