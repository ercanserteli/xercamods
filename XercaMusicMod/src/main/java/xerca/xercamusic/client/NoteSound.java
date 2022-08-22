package xerca.xercamusic.client;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;

public class NoteSound extends AbstractSoundInstance implements TickableSoundInstance {
    private boolean donePlaying = false;
    private int remainingTicks = -1;
    private static final float[] fadeVolumes = {0.0f, 0.02f, 0.12f, 0.3f};
    private final float originalVolume;

    NoteSound(SoundEvent soundEvent, SoundSource category, float x, float y, float z, float volume, float pitch, int lengthTicks) {
        super(soundEvent, category, SoundInstance.createUnseededRandom());
        this.volume = this.originalVolume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.looping = false;
        this.attenuation = Attenuation.LINEAR;
        if(lengthTicks > 0){
            this.remainingTicks = lengthTicks + 3;
        }
    }

    public void stopSound() {
        remainingTicks = 3;
    }

    @Override
    public boolean isStopped() {
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
