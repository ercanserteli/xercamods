package xerca.xercacushion.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import xerca.xercacushion.Mod;
import xerca.xercacushion.block.Blocks;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(Mod.CUSHION, CushionRenderer::new);
        for (var block : Blocks.all()) {
            BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT_MIPPED);
        }
    }
}
