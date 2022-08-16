package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.SoundEvents;

import java.util.Objects;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class BrushSound extends AbstractTickableSoundInstance {
    private int age = 0;
    private int fadingTicks = 4;
    private Random random;

    private static final float[] fadeVolumes = {0.0f, 0.3f, 0.7f};

    public BrushSound() {
        super(Objects.requireNonNull(SoundEvents.STROKE_LOOP), SoundSource.MASTER);
        volume = 1.0f;
        pitch = 1.0F;
        looping = true;
        attenuation = Attenuation.NONE;
        if(Minecraft.getInstance().level != null) {
            random = Minecraft.getInstance().level.getRandom();
        }
    }

    public void stopSound() {
        age += 300;
    }

    public void refreshFade(){
        fadingTicks = 3;
        volume = 1.0f;
    }

    @Override
    public void tick() {
        age++;
        if(fadingTicks <= 0 && age > 300){
            this.stop();
        }
        if(fadingTicks >= 0){
            if(fadingTicks < fadeVolumes.length){
                volume = fadeVolumes[fadingTicks];
            }
            fadingTicks--;
        }

        float randomPitchChange = 0.03f - random.nextFloat()*0.06f;
        if(pitch >= 1.2f && randomPitchChange > 0.f){
            randomPitchChange -= 0.03f;
        }
        else if(pitch <= 0.8f && randomPitchChange < 0.f){
            randomPitchChange += 0.03f;
        }
        pitch += randomPitchChange;
    }
}

