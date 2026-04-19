package xerca.xercablocks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.RenderType;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.menu.Menus;

public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CarvedCrimsonModels.register();
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ROPE, RenderType.cutoutMipped());
        Blocks.CARVED_WOODS.values().forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped()));
        MenuScreens.register(Menus.CARVING_STATION, StonecutterScreen::new);
        MenuScreens.register(Menus.BOOKCASE, BookcaseScreen::new);
    }
}
