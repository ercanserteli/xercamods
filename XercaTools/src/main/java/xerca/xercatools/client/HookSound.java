package xerca.xercatools.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.entity.EntityGrabHook;

public final class HookSound extends AbstractTickableSoundInstance {
    private final EntityGrabHook hook;

    public HookSound(EntityGrabHook hook) {
        super(SoundEvents.HOOK_CHAIN, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.hook = hook;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.attenuation = Attenuation.LINEAR;
        this.x = hook.getX();
        this.y = hook.getY();
        this.z = hook.getZ();
    }

    @Override
    public void tick() {
        if (this.hook.isRemoved() || !this.hook.isAlive()) {
            this.stop();
            return;
        }

        this.x = this.hook.getX();
        this.y = this.hook.getY();
        this.z = this.hook.getZ();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

}
