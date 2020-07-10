package xerca.xercamod.common.tile_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.SoundEvents;

import static net.minecraft.block.CampfireBlock.LIT;
import static xerca.xercamod.common.block.BlockDoner.IS_RAW;
import static xerca.xercamod.common.block.BlockDoner.MEAT_AMOUNT;
import static xerca.xercamod.common.tile_entity.XercaTileEntities.DONER;

public class TileEntityDoner extends TileEntity implements ITickableTileEntity {
    private int spinTicks;
    private int cookingTicks;
    private boolean isSpinning;

    public TileEntityDoner() {
        super(DONER);
        spinTicks = 0;
        cookingTicks = 0;
    }

    @Override
    public void tick() {
        if (this.world.isBlockPowered(this.pos)) {
            this.isSpinning = true;
            this.spinTicks++;
            if(getBlockState().get(IS_RAW) && getBlockState().get(MEAT_AMOUNT) == 4 && gettingRoasted()){
                cookingTicks++;
                if(cookingTicks % 20 == 0){
                    world.playSound(null, getPos(), SoundEvents.GAVEL, SoundCategory.BLOCKS, 0.8f, 1.0f);
                }
                if(cookingTicks > 20){ // todo: increase this
                    world.setBlockState(getPos(), getBlockState().with(IS_RAW, false));
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
