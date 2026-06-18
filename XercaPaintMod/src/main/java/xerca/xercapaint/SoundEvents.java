package xerca.xercapaint;

import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

import static net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT;

public class SoundEvents {
    private SoundEvents() {
    }

    public static final SoundEvent STROKE_LOOP = SoundEvent.createVariableRangeEvent(Mod.id("stroke_loop"));
    public static final SoundEvent MIX = SoundEvent.createVariableRangeEvent(Mod.id("mix"));
    public static final SoundEvent COLOR_PICKER = SoundEvent.createVariableRangeEvent(Mod.id("color_picker"));
    public static final SoundEvent COLOR_PICKER_SUCK = SoundEvent.createVariableRangeEvent(Mod.id("color_picker_suck"));
    public static final SoundEvent WATER = SoundEvent.createVariableRangeEvent(Mod.id("water"));
    public static final SoundEvent WATER_DROP = SoundEvent.createVariableRangeEvent(Mod.id("water_drop"));

    public static void registerSoundEvents() {
        Registry.register(SOUND_EVENT, STROKE_LOOP.location(), STROKE_LOOP);
        Registry.register(SOUND_EVENT, MIX.location(), MIX);
        Registry.register(SOUND_EVENT, COLOR_PICKER.location(), COLOR_PICKER);
        Registry.register(SOUND_EVENT, COLOR_PICKER_SUCK.location(), COLOR_PICKER_SUCK);
        Registry.register(SOUND_EVENT, WATER.location(), WATER);
        Registry.register(SOUND_EVENT, WATER_DROP.location(), WATER_DROP);
    }
}
