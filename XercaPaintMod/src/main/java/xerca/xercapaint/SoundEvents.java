package xerca.xercapaint;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class SoundEvents {
    private SoundEvents() {
    }

    public static final SoundEvent STROKE_LOOP = SoundEvent.createVariableRangeEvent(Mod.id("stroke_loop"));
    public static final SoundEvent MIX = SoundEvent.createVariableRangeEvent(Mod.id("mix"));
    public static final SoundEvent COLOR_PICKER = SoundEvent.createVariableRangeEvent(Mod.id("color_picker"));
    public static final SoundEvent COLOR_PICKER_SUCK = SoundEvent.createVariableRangeEvent(Mod.id("color_picker_suck"));
    public static final SoundEvent WATER = SoundEvent.createVariableRangeEvent(Mod.id("water"));
    public static final SoundEvent WATER_DROP = SoundEvent.createVariableRangeEvent(Mod.id("water_drop"));

    public static void registerSoundEvents(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        helper.register(STROKE_LOOP.location(), STROKE_LOOP);
        helper.register(MIX.location(), MIX);
        helper.register(COLOR_PICKER.location(), COLOR_PICKER);
        helper.register(COLOR_PICKER_SUCK.location(), COLOR_PICKER_SUCK);
        helper.register(WATER.location(), WATER);
        helper.register(WATER_DROP.location(), WATER_DROP);
    }
}
