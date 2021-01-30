package xerca.xercapaint.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(XercaPaint.MODID)
public class SoundEvents {
    public final static SoundEvent STROKE_LOOP = null;
    public final static SoundEvent MIX = null;
    public final static SoundEvent COLOR_PICKER = null;
    public final static SoundEvent COLOR_PICKER_SUCK = null;
    public final static SoundEvent WATER = null;
    public final static SoundEvent WATER_DROP = null;

    private static SoundEvent createSoundEvent(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(XercaPaint.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
            event.getRegistry().registerAll(
                    createSoundEvent("stroke_loop"),
                    createSoundEvent("mix"),
                    createSoundEvent("color_picker"),
                    createSoundEvent("color_picker_suck"),
                    createSoundEvent("water"),
                    createSoundEvent("water_drop")
            );
        }
    }
}
