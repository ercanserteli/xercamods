package xerca.xercamod.common.tile_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.SoundEvents;

import java.util.Random;

import static net.minecraft.block.CampfireBlock.LIT;
import static xerca.xercamod.common.block.BlockDoner.IS_RAW;
import static xerca.xercamod.common.block.BlockDoner.MEAT_AMOUNT;
import static xerca.xercamod.common.tile_entity.XercaTileEntities.DONER;

public class TileEntityDoner extends TileEntity implements ITickableTileEntity {
    private int spinTicks;
    private int cookingTicks;
    private int sizzleCooldown;
    private boolean isSpinning;

    public TileEntityDoner() {
        super(DONER);
        spinTicks = 0;
        cookingTicks = 0;
        sizzleCooldown = 0;
    }

    @Override
    public void tick() {
        if (world.isBlockPowered(this.pos)) {
            Random r = world.rand;
            this.isSpinning = true;
            this.spinTicks++;
            if(getBlockState().get(IS_RAW) && getBlockState().get(MEAT_AMOUNT) == 4 && gettingRoasted()){
                cookingTicks++;
                if(sizzleCooldown == 0){
                    if(world.isRemote){
                        world.playSound(getPos().getX(), getPos().getY(), getPos().getZ(), SoundEvents.SIZZLE, SoundCategory.BLOCKS, 1.0f, 0.9f + r.nextFloat()*0.1f, false);
                        for(int i = 0; i < r.nextInt(2) + 1; ++i) {
                            world.addParticle(ParticleTypes.SMOKE,
                                    pos.getX() + 0.5D, pos.getY() + 0.6D + r.nextDouble()*0.5D, pos.getZ() + 0.25D,
                                    -0.05D + r.nextDouble()*0.1D, 0.025D, -0.05D + r.nextDouble()*0.1D);

                        }
                    }
                    sizzleCooldown = 30 + r.nextInt(30);
                }
                sizzleCooldown--;
                if(cookingTicks > 500){
                    world.setBlockState(getPos(), getBlockState().with(IS_RAW, false));

                    world.playSound(null, getPos(), SoundEvents.BIG_SIZZLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    for(int i = 0; i < 12; ++i) {
                        world.addParticle(ParticleTypes.SMOKE,
                                pos.getX() + 0.5D, pos.getY() + 0.6D + r.nextDouble()*0.5D, pos.getZ() + 0.25D,
                                -0.05D + r.nextDouble()*0.1D, 0.025D, -0.05D + r.nextDouble()*0.1D);

                    }
                }
            }
        } else {
            this.isSpinning = false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getAnimationProgress(float partialTicks) {
        return this.isSpinning ? (float)this.spinTicks + partialTicks : (float)this.spinTicks;
    }

    private boolean gettingRoasted(){
        return isFire(world.getBlockState(pos.down())) || isFire(world.getBlockState(pos.east())) ||
               isFire(world.getBlockState(pos.west())) || isFire(world.getBlockState(pos.north())) ||
               isFire(world.getBlockState(pos.south())) || isFire(world.getBlockState(pos.add(0, -1, 1))) ||
               isFire(world.getBlockState(pos.add(-1, -1, 0))) || isFire(world.getBlockState(pos.add(1, -1, 0))) ||
               isFire(world.getBlockState(pos.add(0, -1, -1)));
    }

    static boolean isFire(BlockState bs){
        return bs.getBlock() == Blocks.FIRE || (bs.getBlock() == Blocks.CAMPFIRE && bs.get(LIT));
    }
}
