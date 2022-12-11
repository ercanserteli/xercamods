package xerca.xercafood.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEvents {
    public final static SoundEvent TOMATO_SPLASH = new SoundEvent(new ResourceLocation(XercaFood.MODID, "tomato_splash"));
    public final static SoundEvent BIG_BURP = new SoundEvent(new ResourceLocation(XercaFood.MODID, "big_burp"));
    public final static SoundEvent YAHOO = new SoundEvent(new ResourceLocation(XercaFood.MODID, "yahoo"));
    public final static SoundEvent SCARY = new SoundEvent(new ResourceLocation(XercaFood.MODID, "scary"));
    public final static SoundEvent TEA_POUR = new SoundEvent(new ResourceLocation(XercaFood.MODID, "tea_pour"));
    public final static SoundEvent SIZZLE = new SoundEvent(new ResourceLocation(XercaFood.MODID, "sizzle"));
    public final static SoundEvent BIG_SIZZLE = new SoundEvent(new ResourceLocation(XercaFood.MODID, "big_sizzle"));
    public final static SoundEvent HOLY = new SoundEvent(new ResourceLocation(XercaFood.MODID, "holy"));
    public final static SoundEvent SPARKLES = new SoundEvent(new ResourceLocation(XercaFood.MODID, "sparkles"));
    public final static SoundEvent FIZZY = new SoundEvent(new ResourceLocation(XercaFood.MODID, "fizzy"));
    public final static SoundEvent SNEAK_HIT = new SoundEvent(new ResourceLocation(XercaFood.MODID, "sneak_hit"));

    public static void registerSoundEvents() {
        Registry.register(Registry.SOUND_EVENT, TOMATO_SPLASH.getLocation(), TOMATO_SPLASH);
        Registry.register(Registry.SOUND_EVENT, BIG_BURP.getLocation(), BIG_BURP);
        Registry.register(Registry.SOUND_EVENT, YAHOO.getLocation(), YAHOO);
        Registry.register(Registry.SOUND_EVENT, SCARY.getLocation(), SCARY);
        Registry.register(Registry.SOUND_EVENT, TEA_POUR.getLocation(), TEA_POUR);
        Registry.register(Registry.SOUND_EVENT, SIZZLE.getLocation(), SIZZLE);
        Registry.register(Registry.SOUND_EVENT, BIG_SIZZLE.getLocation(), BIG_SIZZLE);
        Registry.register(Registry.SOUND_EVENT, HOLY.getLocation(), HOLY);
        Registry.register(Registry.SOUND_EVENT, SPARKLES.getLocation(), SPARKLES);
        Registry.register(Registry.SOUND_EVENT, FIZZY.getLocation(), FIZZY);
        Registry.register(Registry.SOUND_EVENT, SNEAK_HIT.getLocation(), SNEAK_HIT);
    }
}