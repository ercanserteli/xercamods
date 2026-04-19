package xerca.xercacourt;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class SoundEvents {
    public static final SoundEvent GAVEL = SoundEvent.createVariableRangeEvent(Mod.id("gavel"));
    public static final SoundEvent OBJECTION = SoundEvent.createVariableRangeEvent(Mod.id("objection"));

    private SoundEvents() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, GAVEL.getLocation(), GAVEL);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OBJECTION.getLocation(), OBJECTION);
    }
}
