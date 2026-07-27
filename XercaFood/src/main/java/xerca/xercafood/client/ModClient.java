package xerca.xercafood.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntities;
import xerca.xercafood.common.entity.Entities;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class ModClient {
    private ModClient() {
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.TOMATO, new RenderTomatoFactory());
        event.registerBlockEntityRenderer(BlockEntities.DONER, DonerTileEntityRenderer::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ModClient::initializeRenderLayers);
    }

    private static void initializeRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_RICE_PLANT, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_TEA_PLANT, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_TOMATO_PLANT, RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(Blocks.VAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.VAT_CHEESE, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.VAT_MILK, RenderType.cutoutMipped());

        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_PEPPERONI_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_FISH, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_FISH, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_FISH, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_CHICKEN, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_PEPPERONI, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA, RenderType.cutoutMipped());
    }
}
