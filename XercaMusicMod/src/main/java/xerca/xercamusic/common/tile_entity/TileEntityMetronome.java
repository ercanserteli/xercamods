package xerca.xercamusic.common.tile_entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMetronome;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;

import java.util.List;

public class TileEntityMetronome extends TileEntity implements ITickableTileEntity {
    public final static int[] pauseLevels = {20, 15, 12, 10, 8, 6, 5, 4, 3, 2, 1};
    private final static Vec3i halfRange = new Vec3i(8, 2, 8);

    private int age = 0;
    private boolean oldPoweredState = false;
    private int countDown = 0;

    public TileEntityMetronome(){
        super(TileEntities.METRONOME);
    }

    @Override
    public CompoundNBT write(CompoundNBT parent) {
        super.write(parent);
        return parent;
    }

    @Override
    public void read(CompoundNBT parent) {
		super.read(parent);
    }

    @Override
    public void tick() {
        if (this.world != null) {
            BlockState state = this.getBlockState();
            if (state.get(BlockMetronome.POWERED)) {
                if(!oldPoweredState){
                    age = 0;
                    countDown = 0;
                }

                int bpmLevel = state.get(BlockMetronome.BPM);
                if (age % pauseLevels[bpmLevel] == 0) {
                    if(this.world.isRemote){
                        // Client side
                        XercaMusic.proxy.playNote(SoundEvents.TICK, pos.getX(), pos.getY(), pos.getZ(), SoundCategory.BLOCKS, 1.0f, 0.9f + world.rand.nextFloat()*0.1f);
                        world.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.2D, (double) pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                    }
                    else{
                        // Server side
                        if(countDown >= 3){
                            List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(this.pos.subtract(halfRange), this.pos.add(halfRange)),
                                    player -> player.getHeldItemMainhand().getItem() instanceof ItemInstrument && player.getHeldItemOffhand().getItem() instanceof ItemMusicSheet
                                            && player.getHeldItemOffhand().hasTag() && player.getHeldItemOffhand().getTag().getInt("pause") == pauseLevels[bpmLevel] );

                            for(PlayerEntity player : players){
                                Items.GUITAR.playMusic(world, player, false);
                            }
                        }
                    }

                    countDown++;
                }

                oldPoweredState = true;
                age++;
            }else{
                oldPoweredState = false;
            }
        }
    }

}
