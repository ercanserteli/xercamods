package xerca.xercapaint;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEvents {
    public final static SoundEvent STROKE_LOOP = new SoundEvent(new ResourceLocation(Mod.modId, "stroke_loop"));
    public final static SoundEvent MIX = new SoundEvent(new ResourceLocation(Mod.modId, "mix"));
    public final static SoundEvent COLOR_PICKER = new SoundEvent(new ResourceLocation(Mod.modId, "color_picker"));
    public final static SoundEvent COLOR_PICKER_SUCK = new SoundEvent(new ResourceLocation(Mod.modId, "color_picker_suck"));
    public final static SoundEvent WATER = new SoundEvent(new ResourceLocation(Mod.modId, "water"));
    public final static SoundEvent WATER_DROP = new SoundEvent(new ResourceLocation(Mod.modId, "water_drop"));

    public static void registerSoundEvents() {
        Registry.register(Registry.SOUND_EVENT, STROKE_LOOP.getLocation(), STROKE_LOOP);
        Registry.register(Registry.SOUND_EVENT, MIX.getLocation(), MIX);
        Registry.register(Registry.SOUND_EVENT, COLOR_PICKER.getLocation(), COLOR_PICKER);
        Registry.register(Registry.SOUND_EVENT, COLOR_PICKER_SUCK.getLocation(), COLOR_PICKER_SUCK);
        Registry.register(Registry.SOUND_EVENT, WATER.getLocation(), WATER);
        Registry.register(Registry.SOUND_EVENT, WATER_DROP.getLocation(), WATER_DROP);
    }
}
