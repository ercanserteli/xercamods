package xerca.xercacushion.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import xerca.xercacushion.Mod;
import xerca.xercacushion.block.Blocks;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Mod.CUSHION, CushionRenderer::new);
        for (var block : Blocks.all()) {
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
        }
    }
}
