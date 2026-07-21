package xerca.xercatools;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

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

    public static void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        helper.register(SNEAK_HIT.location(), SNEAK_HIT);
        helper.register(HAMMER.location(), HAMMER);
        helper.register(STOMP.location(), STOMP);
        helper.register(HOOK_CHAIN.location(), HOOK_CHAIN);
        helper.register(HOOK_IMPACT.location(), HOOK_IMPACT);
        helper.register(HOOK_CLINK.location(), HOOK_CLINK);
        helper.register(HOOK_RETURN.location(), HOOK_RETURN);
        helper.register(BEHEAD.location(), BEHEAD);
        helper.register(ABSORB.location(), ABSORB);
        helper.register(SOUND_CRACK.location(), SOUND_CRACK);
        helper.register(SOUND_CONFETTI.location(), SOUND_CONFETTI);
        helper.register(SWOOSH.location(), SWOOSH);
    }
}
