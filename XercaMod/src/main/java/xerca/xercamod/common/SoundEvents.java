package xerca.xercamod.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(XercaMod.MODID)
public class SoundEvents {
    public final static SoundEvent TOMATO_SPLASH = null;
    public final static SoundEvent SNEAK_HIT = null;
    public final static SoundEvent GAVEL = null;
    public final static SoundEvent OBJECTION = null;
    public final static SoundEvent HAMMER = null;
    public final static SoundEvent HOOK_CHAIN = null;
    public final static SoundEvent HOOK_IMPACT = null;
    public final static SoundEvent BIG_BURP = null;
    public final static SoundEvent YAHOO = null;
    public final static SoundEvent SCARY = null;
    public final static SoundEvent CRACK = null;
    public final static SoundEvent CONFETTI = null;
    public final static SoundEvent STOMP = null;
    public final static SoundEvent TEA_POUR = null;
    public final static SoundEvent SIZZLE = null;
    public final static SoundEvent BIG_SIZZLE = null;
    public final static SoundEvent BEHEAD = null;
    public final static SoundEvent HOLY = null;
    public final static SoundEvent SPARKLES = null;

    private static SoundEvent createSoundEvent(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(XercaMod.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
//            System.out.println("REGISTER SOUND EVENTS CALLED");
            event.getRegistry().registerAll(
                    createSoundEvent("tomato_splash"),
                    createSoundEvent("sneak_hit"),
                    createSoundEvent("gavel"),
                    createSoundEvent("objection"),
                    createSoundEvent("hammer"),
                    createSoundEvent("hook_chain"),
                    createSoundEvent("hook_impact"),
                    createSoundEvent("big_burp"),
                    createSoundEvent("yahoo"),
                    createSoundEvent("scary"),
                    createSoundEvent("crack"),
                    createSoundEvent("confetti"),
                    createSoundEvent("stomp"),
                    createSoundEvent("tea_pour"),
                    createSoundEvent("sizzle"),
                    createSoundEvent("big_sizzle"),
                    createSoundEvent("behead"),
                    createSoundEvent("holy"),
                    createSoundEvent("sparkles")
            );
        }
    }
}