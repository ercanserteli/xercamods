package xerca.xercafood.common;

import net.minecraft.sounds.SoundEvent;

public class SoundEvents {
    private SoundEvents() {
    }

    public static final SoundEvent TOMATO_SPLASH = SoundEvent.createVariableRangeEvent(Mod.id("tomato_splash"));
    public static final SoundEvent BIG_BURP = SoundEvent.createVariableRangeEvent(Mod.id("big_burp"));
    public static final SoundEvent YAHOO = SoundEvent.createVariableRangeEvent(Mod.id("yahoo"));
    public static final SoundEvent SCARY = SoundEvent.createVariableRangeEvent(Mod.id("scary"));
    public static final SoundEvent TEA_POUR = SoundEvent.createVariableRangeEvent(Mod.id("tea_pour"));
    public static final SoundEvent SIZZLE = SoundEvent.createVariableRangeEvent(Mod.id("sizzle"));
    public static final SoundEvent BIG_SIZZLE = SoundEvent.createVariableRangeEvent(Mod.id("big_sizzle"));
    public static final SoundEvent HOLY = SoundEvent.createVariableRangeEvent(Mod.id("holy"));
    public static final SoundEvent SPARKLES = SoundEvent.createVariableRangeEvent(Mod.id("sparkles"));
    public static final SoundEvent FIZZY = SoundEvent.createVariableRangeEvent(Mod.id("fizzy"));
    public static final SoundEvent SNEAK_HIT = SoundEvent.createVariableRangeEvent(Mod.id("sneak_hit"));

    public static void registerSoundEvents(net.neoforged.neoforge.registries.RegisterEvent.RegisterHelper<net.minecraft.sounds.SoundEvent> helper) {
        helper.register(TOMATO_SPLASH.location(), TOMATO_SPLASH);
        helper.register(BIG_BURP.location(), BIG_BURP);
        helper.register(YAHOO.location(), YAHOO);
        helper.register(SCARY.location(), SCARY);
        helper.register(TEA_POUR.location(), TEA_POUR);
        helper.register(SIZZLE.location(), SIZZLE);
        helper.register(BIG_SIZZLE.location(), BIG_SIZZLE);
        helper.register(HOLY.location(), HOLY);
        helper.register(SPARKLES.location(), SPARKLES);
        helper.register(FIZZY.location(), FIZZY);
        helper.register(SNEAK_HIT.location(), SNEAK_HIT);
    }
}
