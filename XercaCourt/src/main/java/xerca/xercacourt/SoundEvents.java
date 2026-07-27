package xerca.xercacourt;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class SoundEvents {
    public static final SoundEvent GAVEL = SoundEvent.createVariableRangeEvent(Mod.id("gavel"));
    public static final SoundEvent OBJECTION = SoundEvent.createVariableRangeEvent(Mod.id("objection"));

    private SoundEvents() {
    }

    public static void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        helper.register(GAVEL.getLocation(), GAVEL);
        helper.register(OBJECTION.getLocation(), OBJECTION);
    }
}
