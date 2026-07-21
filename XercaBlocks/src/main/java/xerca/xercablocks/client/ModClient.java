package xerca.xercablocks.client;

import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import xerca.xercablocks.Mod;
import xerca.xercablocks.menu.Menus;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class ModClient {
    private ModClient() {
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(Menus.CARVING_STATION, StonecutterScreen::new);
        event.register(Menus.BOOKCASE, BookcaseScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterItemModels(RegisterItemModelsEvent event) {
        event.register(Mod.id("translucent_model"), TranslucentModelWrapper.Unbaked.MAP_CODEC);
    }
}
