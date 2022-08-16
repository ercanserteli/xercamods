package xerca.xercapaint.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static xerca.xercapaint.common.XercaPaint.Null;

@ObjectHolder(XercaPaint.MODID)
public class SoundEvents {
    public final static SoundEvent STROKE_LOOP = Null();
    public final static SoundEvent MIX = Null();
    public final static SoundEvent COLOR_PICKER = Null();
    public final static SoundEvent COLOR_PICKER_SUCK = Null();
    public final static SoundEvent WATER = Null();
    public final static SoundEvent WATER_DROP = Null();

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
