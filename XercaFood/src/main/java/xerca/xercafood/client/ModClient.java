package xerca.xercafood.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block_entity.BlockEntities;
import xerca.xercafood.common.entity.Entities;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public class ModClient {
    private ModClient() {
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.TOMATO, new RenderTomatoFactory());
        event.registerBlockEntityRenderer(BlockEntities.DONER, DonerTileEntityRenderer::new);
    }
}
