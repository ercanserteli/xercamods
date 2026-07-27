package xerca.xercaomnichest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block_entity.BlockEntities;
import xerca.xercaomnichest.item.Items;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class ModClient {
    private ModClient() {
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntities.OMNI_CHEST, OmniChestBlockEntityRenderer::new);
    }

    @SubscribeEvent
    static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        OmniChestItemRenderer renderer = new OmniChestItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, Items.OMNI_CHEST);
    }
}
