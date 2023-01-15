package xerca.xercapaint;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import static net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT;

public class SoundEvents {
    public final static SoundEvent STROKE_LOOP = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "stroke_loop"));
    public final static SoundEvent MIX = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "mix"));
    public final static SoundEvent COLOR_PICKER = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "color_picker"));
    public final static SoundEvent COLOR_PICKER_SUCK = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "color_picker_suck"));
    public final static SoundEvent WATER = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "water"));
    public final static SoundEvent WATER_DROP = SoundEvent.createVariableRangeEvent(new ResourceLocation(Mod.modId, "water_drop"));

    public static void registerSoundEvents() {
        Registry.register(SOUND_EVENT, STROKE_LOOP.getLocation(), STROKE_LOOP);
        Registry.register(SOUND_EVENT, MIX.getLocation(), MIX);
        Registry.register(SOUND_EVENT, COLOR_PICKER.getLocation(), COLOR_PICKER);
        Registry.register(SOUND_EVENT, COLOR_PICKER_SUCK.getLocation(), COLOR_PICKER_SUCK);
        Registry.register(SOUND_EVENT, WATER.getLocation(), WATER);
        Registry.register(SOUND_EVENT, WATER_DROP.getLocation(), WATER_DROP);
    }
}
