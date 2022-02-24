package xerca.xercamusic.common.tile_entity;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMetronome;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;

public class TileEntityMetronome extends TileEntity implements ITickableTileEntity {
    private final static Vector3i halfRange = new Vector3i(8, 2, 8);

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
    public void read(BlockState state, CompoundNBT parent) {
		super.read(state, parent);
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

                final int bps = Math.max(state.get(BlockMetronome.BPS), 1);
                int pause = Math.max(40 / bps, 1);
                if (age % pause == 0) {
                    if(world.isRemote){// note: doesn't work if this function is only called in server
                        // Client side
                        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                                ClientStuff.playNote(SoundEvents.TICK, pos.getX(), pos.getY(), pos.getZ(), SoundCategory.BLOCKS, 1.0f, 0.9f + world.rand.nextFloat()*0.1f, (byte)-1));

                        world.addParticle(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.2D, (double) pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                    }
                    else{
                        // Server side
                        if(countDown == 3){
                            List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos.subtract(halfRange), pos.add(halfRange)),
                                    player -> player.getHeldItemMainhand().getItem() instanceof ItemInstrument && player.getHeldItemOffhand().getItem() instanceof ItemMusicSheet
                                            && player.getHeldItemOffhand().hasTag() && player.getHeldItemOffhand().getTag().getInt("bps") == bps);
                            XercaMusic.LOGGER.info("Metronome found " + players.size() + " players");
                            for(PlayerEntity player : players){
                                ItemInstrument.playMusic(world, player, false);
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
