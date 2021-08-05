package xerca.xercamusic.client;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;

@OnlyIn(Dist.CLIENT)
public class NoteSound extends AbstractSoundInstance implements TickableSoundInstance {
    private boolean donePlaying = false;
    private int fadingTicks = -1;
    private static final float[] fadeVolumes = {0.0f, 0.02f, 0.12f, 0.3f};

    NoteSound(SoundEvent soundEvent, SoundSource category, float x, float y, float z, float volume, float pitch) {
        super(soundEvent, category);
        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.looping = false;
        this.attenuation = Attenuation.LINEAR;
    }

    public void stopSound() {
        fadingTicks = 3;
    }

    @Override
    public boolean isStopped() {
        return this.donePlaying;
    }

    @Override
    public void tick() {
        if(fadingTicks == 0){
            donePlaying = true;
            fadingTicks = -1;
        }
        if(fadingTicks > 0){
            volume = fadeVolumes[fadingTicks];
            fadingTicks--;
        }
    }
}
