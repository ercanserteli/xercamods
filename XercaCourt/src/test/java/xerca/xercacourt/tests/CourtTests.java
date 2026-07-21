package xerca.xercacourt.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercacourt.Mod;

@net.neoforged.fml.common.Mod("xercacourt_tests")
public class CourtTests {
    public CourtTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            CourtGameTests tests = new CourtGameTests();
            helper.register(Mod.id("gavel_recipe"), tests::gavelRecipeCraftsFromPlanksAndSticks);
            helper.register(Mod.id("prosecutor_badge_recipe"), tests::prosecutorBadgeAcceptsPoppyAndDandelionVariants);
            helper.register(Mod.id("badge_cooldown"), tests::badgeUseStartsCooldown);
            helper.register(Mod.id("gavel_use_solid"), tests::gavelUseOnSolidBlockSucceeds);
            helper.register(Mod.id("gavel_sound_solid"), tests::gavelOnSolidBlockPlaysSound);
            helper.register(Mod.id("gavel_no_sound_nonsolid"), tests::gavelOnNonSolidBlockPlaysNoSound);
            helper.register(Mod.id("prosecutor_objection_sound"), tests::prosecutorBadgeUsePlaysObjectionSound);
        });
    }
}
