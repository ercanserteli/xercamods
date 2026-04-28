package xerca.xercafood.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class SoundEvents {
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

    public static void registerSoundEvents() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, TOMATO_SPLASH.getLocation(), TOMATO_SPLASH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BIG_BURP.getLocation(), BIG_BURP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, YAHOO.getLocation(), YAHOO);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SCARY.getLocation(), SCARY);
        Registry.register(BuiltInRegistries.SOUND_EVENT, TEA_POUR.getLocation(), TEA_POUR);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SIZZLE.getLocation(), SIZZLE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BIG_SIZZLE.getLocation(), BIG_SIZZLE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HOLY.getLocation(), HOLY);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SPARKLES.getLocation(), SPARKLES);
        Registry.register(BuiltInRegistries.SOUND_EVENT, FIZZY.getLocation(), FIZZY);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SNEAK_HIT.getLocation(), SNEAK_HIT);
    }
}
