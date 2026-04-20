package xerca.xercatools;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class SoundEvents {
    public static final SoundEvent SNEAK_HIT = SoundEvent.createVariableRangeEvent(Mod.id("sneak_hit"));
    public static final SoundEvent HAMMER = SoundEvent.createVariableRangeEvent(Mod.id("hammer"));
    public static final SoundEvent STOMP = SoundEvent.createVariableRangeEvent(Mod.id("stomp"));
    public static final SoundEvent HOOK_CHAIN = SoundEvent.createVariableRangeEvent(Mod.id("hook_chain"));
    public static final SoundEvent HOOK_IMPACT = SoundEvent.createVariableRangeEvent(Mod.id("hook_impact"));
    public static final SoundEvent BEHEAD = SoundEvent.createVariableRangeEvent(Mod.id("behead"));
    public static final SoundEvent ABSORB = SoundEvent.createVariableRangeEvent(Mod.id("absorb"));

    private SoundEvents() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, SNEAK_HIT.getLocation(), SNEAK_HIT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HAMMER.getLocation(), HAMMER);
        Registry.register(BuiltInRegistries.SOUND_EVENT, STOMP.getLocation(), STOMP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HOOK_CHAIN.getLocation(), HOOK_CHAIN);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HOOK_IMPACT.getLocation(), HOOK_IMPACT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BEHEAD.getLocation(), BEHEAD);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ABSORB.getLocation(), ABSORB);
    }
}

