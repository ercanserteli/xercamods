package xerca.xercamusic.common.tile_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMetronome;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;

import java.util.List;

public class TileEntityMetronome extends BlockEntity {
    public final static int[] pauseLevels = {20, 15, 12, 10, 8, 6, 5, 4, 3, 2, 1};
    private final static Vec3i halfRange = new Vec3i(8, 2, 8);

    private int age = 0;
    private boolean oldPoweredState = false;
    private int countDown = 0;

    public TileEntityMetronome(BlockPos blockPos, BlockState blockState){
        super(TileEntities.METRONOME, blockPos, blockState);
    }

    @Override
    public CompoundTag save(CompoundTag parent) {
        super.save(parent);
        return parent;
    }

    @Override
//    public void load(BlockState state, CompoundTag parent) {
//		super.load(state, parent);
//    }
    public void load(CompoundTag parent) {
		super.load(parent);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TileEntityMetronome metronome) {
        if (metronome.level != null) {
            BlockState state = metronome.getBlockState();
            if (state.getValue(BlockMetronome.POWERED)) {
                if(!metronome.oldPoweredState){
                    metronome.age = 0;
                    metronome.countDown = 0;
                }

                int bpmLevel = state.getValue(BlockMetronome.BPM);
                if (metronome.age % pauseLevels[bpmLevel] == 0) {
                    if(metronome.level.isClientSide){// note: doesn't work if this function is only called in server
                        // Client side
                        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                                ClientStuff.playNote(SoundEvents.TICK, metronome.worldPosition.getX(), metronome.worldPosition.getY(), metronome.worldPosition.getZ(), SoundSource.BLOCKS, 1.0f, 0.9f + level.random.nextFloat()*0.1f));

                        level.addParticle(ParticleTypes.NOTE, (double) metronome.worldPosition.getX() + 0.5D, (double) metronome.worldPosition.getY() + 1.2D, (double) metronome.worldPosition.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                    }
                    else{
                        // Server side
                        if(metronome.countDown >= 3){
                            List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(metronome.worldPosition.subtract(halfRange), metronome.worldPosition.offset(halfRange)),
                                    player -> player.getMainHandItem().getItem() instanceof ItemInstrument && player.getOffhandItem().getItem() instanceof ItemMusicSheet
                                            && player.getOffhandItem().hasTag() && player.getOffhandItem().getTag().getInt("pause") == pauseLevels[bpmLevel] );
                            XercaMusic.LOGGER.warn("Metronome found " + players.size() + " players");
                            for(Player player : players){
                                ItemInstrument.playMusic(level, player, false);
                            }
                        }
                    }

                    metronome.countDown++;
                }

                metronome.oldPoweredState = true;
                metronome.age++;
            }else{
                metronome.oldPoweredState = false;
            }
        }
    }

}
