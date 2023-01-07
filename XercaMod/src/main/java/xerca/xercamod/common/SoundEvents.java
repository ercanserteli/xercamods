package xerca.xercamod.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, XercaMod.MODID);

    public final static RegistryObject<SoundEvent> TOMATO_SPLASH = createSoundEvent("tomato_splash");
    public final static RegistryObject<SoundEvent> SNEAK_HIT = createSoundEvent("sneak_hit");
    public final static RegistryObject<SoundEvent> GAVEL = createSoundEvent("gavel");
    public final static RegistryObject<SoundEvent> OBJECTION = createSoundEvent("objection");
    public final static RegistryObject<SoundEvent> HAMMER = createSoundEvent("hammer");
    public final static RegistryObject<SoundEvent> HOOK_CHAIN = createSoundEvent("hook_chain");
    public final static RegistryObject<SoundEvent> HOOK_IMPACT = createSoundEvent("hook_impact");
    public final static RegistryObject<SoundEvent> BIG_BURP = createSoundEvent("big_burp");
    public final static RegistryObject<SoundEvent> YAHOO = createSoundEvent("yahoo");
    public final static RegistryObject<SoundEvent> SCARY = createSoundEvent("scary");
    public final static RegistryObject<SoundEvent> CRACK = createSoundEvent("crack");
    public final static RegistryObject<SoundEvent> CONFETTI = createSoundEvent("confetti");
    public final static RegistryObject<SoundEvent> STOMP = createSoundEvent("stomp");
    public final static RegistryObject<SoundEvent> TEA_POUR = createSoundEvent("tea_pour");
    public final static RegistryObject<SoundEvent> SIZZLE = createSoundEvent("sizzle");
    public final static RegistryObject<SoundEvent> BIG_SIZZLE = createSoundEvent("big_sizzle");
    public final static RegistryObject<SoundEvent> BEHEAD = createSoundEvent("behead");
    public final static RegistryObject<SoundEvent> HOLY = createSoundEvent("holy");
    public final static RegistryObject<SoundEvent> SPARKLES = createSoundEvent("sparkles");
    public final static RegistryObject<SoundEvent> ABSORB = createSoundEvent("absorb");
    public final static RegistryObject<SoundEvent> FIZZY = createSoundEvent("fizzy");

    private static RegistryObject<SoundEvent> createSoundEvent(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(XercaMod.MODID, soundName);
        return SOUND_EVENTS.register(soundName, () -> SoundEvent.createVariableRangeEvent(soundID));
    }
}