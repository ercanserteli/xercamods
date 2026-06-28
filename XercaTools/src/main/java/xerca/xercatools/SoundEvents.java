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
    public static final SoundEvent HOOK_CLINK = SoundEvent.createVariableRangeEvent(Mod.id("hook_clink"));
    public static final SoundEvent HOOK_RETURN = SoundEvent.createVariableRangeEvent(Mod.id("hook_return"));
    public static final SoundEvent BEHEAD = SoundEvent.createVariableRangeEvent(Mod.id("behead"));
    public static final SoundEvent ABSORB = SoundEvent.createVariableRangeEvent(Mod.id("absorb"));
    public static final SoundEvent SOUND_CRACK = SoundEvent.createVariableRangeEvent(Mod.id("crack"));
    public static final SoundEvent SOUND_CONFETTI = SoundEvent.createVariableRangeEvent(Mod.id("confetti"));
    public static final SoundEvent SWOOSH = SoundEvent.createVariableRangeEvent(Mod.id("swoosh"));

    private SoundEvents() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, SNEAK_HIT.location(), SNEAK_HIT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HAMMER.location(), HAMMER);
        Registry.register(BuiltInRegistries.SOUND_EVENT, STOMP.location(), STOMP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HOOK_CHAIN.location(), HOOK_CHAIN);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HOOK_IMPACT.location(), HOOK_IMPACT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HOOK_CLINK.location(), HOOK_CLINK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HOOK_RETURN.location(), HOOK_RETURN);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BEHEAD.location(), BEHEAD);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ABSORB.location(), ABSORB);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_CRACK.location(), SOUND_CRACK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SOUND_CONFETTI.location(), SOUND_CONFETTI);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SWOOSH.location(), SWOOSH);
    }
}
