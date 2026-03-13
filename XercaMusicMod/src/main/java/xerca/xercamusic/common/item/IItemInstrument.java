package xerca.xercamusic.common.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

import java.util.List;

public interface IItemInstrument {
    int MIN_NOTE = 21;
    int MAX_NOTE = 117;
    int TOTAL_NOTES = 96;

    static int idToNote(int id) {
        return id + MIN_NOTE;
    }

    static int noteToId(int note) {
        return note - MIN_NOTE;
    }

    static void playMusic(Level worldIn, Player playerIn, boolean canStop) {
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesOfClass(EntityMusicSpirit.class, playerIn.getBoundingBox().inflate(3.0), entity -> entity.getBody().is(playerIn));
        if (musicSpirits.isEmpty()) {
            worldIn.addFreshEntity(new EntityMusicSpirit(worldIn, playerIn, (IItemInstrument) playerIn.getMainHandItem().getItem()));
        } else if (canStop) {
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
    }

    int getMinOctave();

    int getMaxOctave();

    int getInstrumentId();

    void setSounds(List<Pair<Integer, SoundEvent>> sounds);

    InsSound getSound(int note);

    record InsSound(SoundEvent sound, float pitch) {
    }

    record Pair<F, S>(F first, S second) {
        public static <F, S> Pair<F, S> of(F first, S second) {
            if (first == null || second == null) {
                throw new IllegalArgumentException("Pair.of requires non null values.");
            }
            return new Pair<>(first, second);
        }
    }

}
