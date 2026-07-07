package xerca.xercaomnichest.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import xerca.xercaomnichest.block_entity.BlockEntities;

@Environment(EnvType.CLIENT)
public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(BlockEntities.OMNI_CHEST, OmniChestBlockEntityRenderer::new);
    }
}
