package xerca.xercablocks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.item.ItemModels;
import xerca.xercablocks.Mod;
import xerca.xercablocks.menu.Menus;

@Environment(EnvType.CLIENT)
public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CarvedCrimsonModels.register();
        ItemModels.ID_MAPPER.put(Mod.id("translucent_model"), TranslucentModelWrapper.Unbaked.MAP_CODEC);
        MenuScreens.register(Menus.CARVING_STATION, StonecutterScreen::new);
        MenuScreens.register(Menus.BOOKCASE, BookcaseScreen::new);
    }
}
