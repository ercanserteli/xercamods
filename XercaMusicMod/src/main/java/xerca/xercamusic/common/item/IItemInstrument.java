package xerca.xercamusic.common.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

import java.util.ArrayList;
import java.util.List;

public interface IItemInstrument {
    int minNote = 21;
    int maxNote = 117;
    int totalNotes = 96;

    int getMinOctave();
    int getMaxOctave();
    int getInstrumentId();
    void setSounds(ArrayList<Pair<Integer, SoundEvent>> sounds);
    InsSound getSound(int note);


    static int idToNote(int id){
        return id + minNote;
    }
    static int noteToId(int note){
        return note - minNote;
    }
    static void playMusic(Level worldIn, Player playerIn, boolean canStop){
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesOfClass(EntityMusicSpirit.class, playerIn.getBoundingBox().inflate(3.0), entity -> entity.getBody().is(playerIn));
        if(musicSpirits.size() == 0){
            worldIn.addFreshEntity(new EntityMusicSpirit(worldIn, playerIn, (IItemInstrument) playerIn.getMainHandItem().getItem()));
        }
        else if(canStop){
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
    }

    class InsSound {
        public SoundEvent sound;
        public float pitch;

        public InsSound(SoundEvent sound, float pitch){
            this.sound = sound;
            this.pitch = pitch;
        }
    }

    record Pair<F, S>(F first, S second) {
        public static <F, S> Pair<F, S> of(F first, S second) {
            if (first == null || second == null) {
                throw new IllegalArgumentException("Pair.of requires non null values.");
            }
            return new Pair<>(first, second);
        }

        @Override
        public int hashCode() {
            return first.hashCode() * 37 + second.hashCode();
        }
    }

}
