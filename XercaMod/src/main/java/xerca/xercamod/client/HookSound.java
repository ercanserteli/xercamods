package xerca.xercamod.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.entity.EntityHook;

@OnlyIn(Dist.CLIENT)
class HookSound extends AbstractTickableSoundInstance {
    private final EntityHook theHook;
    private boolean repeat = true;
    private final boolean isReturning;
    private int repeatDelay = 0;
    private int age = 0;

    private final float pitch;

    public HookSound(EntityHook hook, boolean isReturning) {
        super(SoundEvents.HOOK_CHAIN, SoundSource.PLAYERS);
        volume = 1.0f;
        pitch = 1.0F;
        theHook = hook;
        this.isReturning = isReturning;
    }

    private void setDonePlaying() {
        this.stop();
        this.repeatDelay = 0;
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

        final Player angler = theHook.getAngler();
        if(angler != null){
            x = (float) angler.getX();
            y = (float) angler.getY();
            z = (float) angler.getZ();
        }
    }

    @Override
    public boolean isLooping() {
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
    public int getDelay() {
        return this.repeatDelay;
    }

    @Override
    public Attenuation getAttenuation() {
        return Attenuation.LINEAR;
    }
}
