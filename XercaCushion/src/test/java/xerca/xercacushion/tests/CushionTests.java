package xerca.xercacushion.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercacushion.Mod;

@net.neoforged.fml.common.Mod("xercacushion_tests")
public class CushionTests {
    public CushionTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            CushionGameTests tests = new CushionGameTests();
            helper.register(Mod.id("black_cushion_recipe"), tests::blackCushionRecipeCraftsFromWoolAndFeather);
            helper.register(Mod.id("all_recipes_load"), tests::allSixteenCushionRecipesLoad);
            helper.register(Mod.id("red_cushion_variant"), tests::redCushionEntityKeepsItsItemVariant);
            helper.register(Mod.id("cushion_mount"), tests::interactingWithCushionMountsPlayer);
            helper.register(Mod.id("cushion_falls"), tests::unsupportedCushionFallsSlowly);
            helper.register(Mod.id("cushion_no_slide"), tests::groundedCushionDoesNotSlideFromHorizontalVelocity);
            helper.register(Mod.id("piston_push"), tests::pistonPushesCushionExactlyOneBlock);
            helper.register(Mod.id("cushion_intersect_drop"), tests::movingCushionDropsWhenItIntersectsAnotherCushion);
        });
    }
}
