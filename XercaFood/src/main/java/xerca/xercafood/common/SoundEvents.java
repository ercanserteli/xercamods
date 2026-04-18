package xerca.xercafood.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEvents {
    public final static SoundEvent TOMATO_SPLASH = SoundEvent.createVariableRangeEvent(XercaFood.id("tomato_splash"));
    public final static SoundEvent BIG_BURP = SoundEvent.createVariableRangeEvent(XercaFood.id("big_burp"));
    public final static SoundEvent YAHOO = SoundEvent.createVariableRangeEvent(XercaFood.id("yahoo"));
    public final static SoundEvent SCARY = SoundEvent.createVariableRangeEvent(XercaFood.id("scary"));
    public final static SoundEvent TEA_POUR = SoundEvent.createVariableRangeEvent(XercaFood.id("tea_pour"));
    public final static SoundEvent SIZZLE = SoundEvent.createVariableRangeEvent(XercaFood.id("sizzle"));
    public final static SoundEvent BIG_SIZZLE = SoundEvent.createVariableRangeEvent(XercaFood.id("big_sizzle"));
    public final static SoundEvent HOLY = SoundEvent.createVariableRangeEvent(XercaFood.id("holy"));
    public final static SoundEvent SPARKLES = SoundEvent.createVariableRangeEvent(XercaFood.id("sparkles"));
    public final static SoundEvent FIZZY = SoundEvent.createVariableRangeEvent(XercaFood.id("fizzy"));
    public final static SoundEvent SNEAK_HIT = SoundEvent.createVariableRangeEvent(XercaFood.id("sneak_hit"));

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
