package xerca.xercablocks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ItemModels;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.menu.Menus;

@Environment(EnvType.CLIENT)
public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CarvedCrimsonModels.register();
        ItemModels.ID_MAPPER.put(Mod.id("translucent_model"), TranslucentModelWrapper.Unbaked.MAP_CODEC);
        BlockRenderLayerMap.putBlock(Blocks.ROPE, ChunkSectionLayer.CUTOUT);
        Blocks.carvedWoods().values().forEach(block -> BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT));
        MenuScreens.register(Menus.CARVING_STATION, StonecutterScreen::new);
        MenuScreens.register(Menus.BOOKCASE, BookcaseScreen::new);
    }
}
