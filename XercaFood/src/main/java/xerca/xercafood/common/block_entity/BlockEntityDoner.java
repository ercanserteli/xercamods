package xerca.xercafood.common.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
        if (!level.hasNeighborSignal(t.worldPosition)) {
            t.isSpinning = false;
            return;
        }

        RandomSource random = level.random;
        t.isSpinning = true;
        t.spinTicks++;

        if (t.isReadyToCook()) {
            t.tickCooking(level, random);
        }
    }

    public float getAnimationProgress(float partialTicks) {
        return this.isSpinning ? (float) this.spinTicks + partialTicks : (float) this.spinTicks;
    }

    @SuppressFBWarnings(value = "NP", justification = "gettingRoasted is used during block entity ticking when level is attached.")
    private boolean gettingRoasted() {
        return isFire(level.getBlockState(worldPosition.below())) || isFire(level.getBlockState(worldPosition.east())) ||
                isFire(level.getBlockState(worldPosition.west())) || isFire(level.getBlockState(worldPosition.north())) ||
                isFire(level.getBlockState(worldPosition.south())) || isFire(level.getBlockState(worldPosition.offset(0, -1, 1))) ||
                isFire(level.getBlockState(worldPosition.offset(-1, -1, 0))) || isFire(level.getBlockState(worldPosition.offset(1, -1, 0))) ||
                isFire(level.getBlockState(worldPosition.offset(0, -1, -1)));
    }

    static boolean isFire(BlockState bs) {
        return bs.getBlock() == Blocks.FIRE || (bs.getBlock() == Blocks.CAMPFIRE && bs.getValue(LIT));
    }

    private boolean isReadyToCook() {
        return this.getBlockState().getValue(IS_RAW)
                && this.getBlockState().getValue(MEAT_AMOUNT) == 4
                && this.gettingRoasted();
    }

    private void tickCooking(Level level, RandomSource random) {
        this.cookingTicks++;
        tryPlaySizzle(level, random);
        this.sizzleCooldown--;
        if (this.cookingTicks > 500) {
            finishCooking(level, random);
        }
    }

    private void tryPlaySizzle(Level level, RandomSource random) {
        if (this.sizzleCooldown != 0) {
            return;
        }
        if (level.isClientSide) {
            level.playLocalSound(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), SoundEvents.SIZZLE, SoundSource.BLOCKS, 1.0f, 0.9f + random.nextFloat() * 0.1f, false);
            spawnSmoke(level, random, random.nextInt(2) + 1);
        }
        this.sizzleCooldown = 30 + random.nextInt(30);
    }

    private void finishCooking(Level level, RandomSource random) {
        level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(IS_RAW, false));
        level.playSound(null, this.getBlockPos(), SoundEvents.BIG_SIZZLE, SoundSource.BLOCKS, 1.0f, 1.0f);
        spawnSmoke(level, random, 12);
    }

    private void spawnSmoke(Level level, RandomSource random, int amount) {
        for (int i = 0; i < amount; ++i) {
            level.addParticle(
                    ParticleTypes.SMOKE,
                    this.worldPosition.getX() + 0.5D,
                    this.worldPosition.getY() + 0.6D + random.nextDouble() * 0.5D,
                    this.worldPosition.getZ() + 0.25D,
                    -0.05D + random.nextDouble() * 0.1D,
                    0.025D,
                    -0.05D + random.nextDouble() * 0.1D
            );
        }
    }
}
