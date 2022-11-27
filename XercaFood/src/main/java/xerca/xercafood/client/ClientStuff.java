package xerca.xercafood.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.entity.Entities;
import xerca.xercafood.common.tile_entity.TileEntities;


public class ClientStuff {
    static class ModBusSubscriber{
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            event.enqueueWork(()->{
                ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_TEA_PLANT, RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_TOMATO_PLANT, RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_RICE_PLANT, RenderType.cutout());

                ItemBlockRenderTypes.setRenderLayer(Blocks.VAT, RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.VAT_CHEESE, RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.VAT_MILK, RenderType.cutoutMipped());

                registerItemModelsProperties();
            });
            mc = Minecraft.getInstance();
        }

        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.TOMATO, new RenderTomatoFactory());
            event.registerBlockEntityRenderer(TileEntities.DONER, DonerTileEntityRenderer::new);
        }

        static private void registerItemModelsProperties(){

        }
    }
}
