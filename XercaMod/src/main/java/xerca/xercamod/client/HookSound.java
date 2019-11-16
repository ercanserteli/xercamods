package xerca.xercamod.client;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.entity.EntityHook;

@OnlyIn(Dist.CLIENT)
class HookSound extends TickableSound {
    private final EntityHook theHook;
    private boolean repeat = true;
    private final boolean isReturning;
    private int repeatDelay = 0;
    private int age = 0;

    private final float pitch;

    public HookSound(EntityHook hook, boolean isReturning) {
        super(SoundEvents.HOOK_CHAIN, SoundCategory.PLAYERS);
        volume = 0.8f;
        pitch = 1.0F;
        theHook = hook;
        this.isReturning = isReturning;
    }

    private void setDonePlaying() {
        this.repeat = false;
        this.donePlaying = true;
        this.repeatDelay = 0;
    }

    @Override
    public boolean isDonePlaying() {
        return this.donePlaying;
    }

    @Override
    public void tick() {
        age++;
        if (theHook == null || !theHook.isAlive() || age > 100) {
            this.setDonePlaying();
            return;
        }
        if (!isReturning && (theHook.isReturning || theHook.caughtEntity != null)) {
            this.setDonePlaying();
            return;
        }

        x = (float) theHook.posX;
        y = (float) theHook.posY;
        z = (float) theHook.posZ;
    }

    @Override
    public boolean canRepeat() {
        return this.repeat;
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public int getRepeatDelay() {
        return this.repeatDelay;
    }

    @Override
    public AttenuationType getAttenuationType() {
        return AttenuationType.LINEAR;
    }
}
