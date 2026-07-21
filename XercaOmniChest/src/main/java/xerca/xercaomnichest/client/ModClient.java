package xerca.xercaomnichest.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block_entity.BlockEntities;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class ModClient {
    private ModClient() {
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntities.OMNI_CHEST, OmniChestBlockEntityRenderer::new);
    }
}
