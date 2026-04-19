package xerca.xercablocks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.menu.Menus;

public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ROPE, RenderType.cutoutMipped());
        MenuScreens.register(Menus.BOOKCASE, BookcaseScreen::new);
    }
}
