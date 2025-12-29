package xerca.xercamusic.common.tile_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.block.BlockMetronome;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;

import java.util.List;

import static xerca.xercamusic.common.Mod.onlyCallOnClient;

public class TileEntityMetronome extends BlockEntity {
    private static final Vec3i HALF_RANGE = new Vec3i(8, 2, 8);

    private int age = 0;
    private boolean oldPoweredState = false;
    private int countDown = 0;

    public TileEntityMetronome(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.METRONOME, blockPos, blockState);
    }

    public static void tick(Level level, TileEntityMetronome metronome) {
        if (level != null) {
            BlockState state = metronome.getBlockState();
            if (state.getValue(BlockMetronome.POWERED)) {
                if (!metronome.oldPoweredState) {
                    metronome.age = 0;
                    metronome.countDown = 0;
                }

                final int bps = Math.max(state.getValue(BlockMetronome.BPS), 1);
                int pause = Math.max(40 / bps, 1);
                if (metronome.age % pause == 0) {
                    if (level.isClientSide) {// note: doesn't work if this function is only called in server
                        try {
                            onlyCallOnClient(() -> () ->
                                    ClientStuff.playNote(SoundEvents.TICK, metronome.worldPosition.getX(), metronome.worldPosition.getY(), metronome.worldPosition.getZ(), SoundSource.BLOCKS, 1.0f, 0.9f + level.random.nextFloat() * 0.1f, (byte) -1));
                        } catch (Exception e) {
                            Mod.LOGGER.error("Error playing metronome note", e);
                        }

                        level.addParticle(ParticleTypes.NOTE, metronome.worldPosition.getX() + 0.5D, metronome.worldPosition.getY() + 1.2D, metronome.worldPosition.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                    } else {
                        // Server side
                        if (metronome.countDown == 3) {
                            List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(metronome.worldPosition.subtract(HALF_RANGE).getCenter(), metronome.worldPosition.offset(HALF_RANGE).getCenter()),
                                    player -> player.getMainHandItem().getItem() instanceof IItemInstrument &&
                                            player.getOffhandItem().getItem() instanceof ItemMusicSheet && (int) player.getOffhandItem().getOrDefault(Items.SHEET_BPS, (byte) 0) == bps);
                            Mod.LOGGER.info("Metronome found {} players", players::size);
                            for (Player player : players) {
                                IItemInstrument.playMusic(level, player, false);
                            }
                        }
                    }

                    metronome.countDown++;
                }

                metronome.oldPoweredState = true;
                metronome.age++;
            } else {
                metronome.oldPoweredState = false;
            }
        }
    }

}
