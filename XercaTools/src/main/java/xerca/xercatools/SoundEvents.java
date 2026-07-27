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
        helper.register(SNEAK_HIT.getLocation(), SNEAK_HIT);
        helper.register(HAMMER.getLocation(), HAMMER);
        helper.register(STOMP.getLocation(), STOMP);
        helper.register(HOOK_CHAIN.getLocation(), HOOK_CHAIN);
        helper.register(HOOK_IMPACT.getLocation(), HOOK_IMPACT);
        helper.register(HOOK_CLINK.getLocation(), HOOK_CLINK);
        helper.register(HOOK_RETURN.getLocation(), HOOK_RETURN);
        helper.register(BEHEAD.getLocation(), BEHEAD);
        helper.register(ABSORB.getLocation(), ABSORB);
        helper.register(SOUND_CRACK.getLocation(), SOUND_CRACK);
        helper.register(SOUND_CONFETTI.getLocation(), SOUND_CONFETTI);
        helper.register(SWOOSH.getLocation(), SWOOSH);
    }
}
