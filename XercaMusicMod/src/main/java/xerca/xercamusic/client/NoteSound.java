package xerca.xercamusic.client;

import net.minecraft.client.audio.LocatableSound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteSound extends LocatableSound implements ITickableSound {
    private boolean donePlaying = false;
    private int fadingTicks = -1;
    private static final float[] fadeVolumes = {0.0f, 0.02f, 0.12f, 0.3f};

    NoteSound(SoundEvent soundEvent, SoundCategory category, float x, float y, float z, float volume, float pitch) {
        super(soundEvent, category);
        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.repeat = false;
        this.attenuationType = AttenuationType.LINEAR;
    }

    public void stopSound() {
        fadingTicks = 3;
    }

    @Override
    public boolean isDonePlaying() {
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
