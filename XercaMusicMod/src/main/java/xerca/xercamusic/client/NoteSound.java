package xerca.xercamusic.client;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class NoteSound extends LocatableSound implements ITickableSound {
    private boolean donePlaying = false;
    private int remainingTicks = -1;
    private static final float[] fadeVolumes = {0.0f, 0.02f, 0.12f, 0.3f};
    private final float originalVolume;

    NoteSound(SoundEvent soundEvent, SoundCategory category, float x, float y, float z, float volume, float pitch) {
        this(soundEvent, category, x, y, z, volume, pitch, -1);
    }

    NoteSound(SoundEvent soundEvent, SoundCategory category, float x, float y, float z, float volume, float pitch, int lengthTicks) {
        super(soundEvent, category);
        this.volume = this.originalVolume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.repeat = false;
        this.attenuationType = AttenuationType.LINEAR;
        if(lengthTicks > 0){
            this.remainingTicks = lengthTicks + 3;
        }
    }

    public void stopSound() {
        remainingTicks = 3;
    }

    @Override
    public boolean isDonePlaying() {
        return this.donePlaying;
    }

    @Override
    public void tick() {
        if(remainingTicks == 0){
            donePlaying = true;
            remainingTicks = -1;
        }
        if(remainingTicks > 0){
            volume = originalVolume * (remainingTicks >= fadeVolumes.length ? 1 : fadeVolumes[remainingTicks]);
            remainingTicks--;
        }
    }
}
