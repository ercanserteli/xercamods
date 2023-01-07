package xerca.xercapaint.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, XercaPaint.MODID);
    public final static RegistryObject<SoundEvent> STROKE_LOOP = createSoundEvent("stroke_loop");
    public final static RegistryObject<SoundEvent> MIX = createSoundEvent("mix");
    public final static RegistryObject<SoundEvent> COLOR_PICKER = createSoundEvent("color_picker");
    public final static RegistryObject<SoundEvent> COLOR_PICKER_SUCK = createSoundEvent("color_picker_suck");
    public final static RegistryObject<SoundEvent> WATER = createSoundEvent("water");
    public final static RegistryObject<SoundEvent> WATER_DROP = createSoundEvent("water_drop");

    private static RegistryObject<SoundEvent> createSoundEvent(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(XercaPaint.MODID, soundName);
        return SOUND_EVENTS.register(soundName, () -> SoundEvent.createVariableRangeEvent(soundID));
    }
}
