package xerca.xercatools.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.entity.EntityGrabHook;

public final class HookSound extends AbstractTickableSoundInstance {
    private final @Nullable Player angler;
    private final EntityGrabHook hook;

    public HookSound(EntityGrabHook hook) {
        super(SoundEvents.HOOK_CHAIN, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.hook = hook;
        this.angler = hook.getAngler();
        if (angler == null) {
            this.stop();
            return;
        }
        this.looping = false;
        this.delay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.attenuation = Attenuation.LINEAR;
        this.x = this.angler.getX();
        this.y = this.angler.getY();
        this.z = this.angler.getZ();
    }

    @Override
    public void tick() {
        if (this.angler == null || this.hook.isRemoved() || !this.hook.isAlive()) {
            this.stop();
            return;
        }

        if (this.hook.isReturning()) {
            this.pitch = 1.1F;
        }

        this.x = this.angler.getX();
        this.y = this.angler.getY();
        this.z = this.angler.getZ();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

}
