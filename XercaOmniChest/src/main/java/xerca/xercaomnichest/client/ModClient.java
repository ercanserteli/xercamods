package xerca.xercaomnichest.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import xerca.xercaomnichest.block_entity.BlockEntities;
import xerca.xercaomnichest.item.Items;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(BlockEntities.OMNI_CHEST, OmniChestBlockEntityRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(
                Items.OMNI_CHEST,
                new OmniChestItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
        );
    }
}
