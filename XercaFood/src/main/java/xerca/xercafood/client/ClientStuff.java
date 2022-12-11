package xerca.xercafood.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntities;
import xerca.xercafood.common.entity.Entities;


@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ClientStuff implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Entities.TOMATO, new RenderTomatoFactory());
        BlockEntityRendererRegistry.register(BlockEntities.DONER, DonerTileEntityRenderer::new);
        initializeRenderLayers();
    }

    private void initializeRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.BLOCK_RICE_PLANT, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.BLOCK_TEA_PLANT, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.BLOCK_TOMATO_PLANT, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.VAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.VAT_CHEESE, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.VAT_MILK, RenderType.cutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_PEPPERONI_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MUSHROOM_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MUSHROOM_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MUSHROOM_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_MEAT_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_MEAT_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_MEAT_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_MEAT_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_MEAT_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_MEAT_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_FISH_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_FISH_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_FISH_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_FISH_FISH, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_MEAT_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_MEAT_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_MEAT_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_FISH_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_FISH_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_FISH_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_FISH_FISH, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_FISH, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_CHICKEN, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_PEPPERONI_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MUSHROOM_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MUSHROOM_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH_FISH, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_FISH, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_PEPPERONI, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MUSHROOM, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_MEAT, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_FISH, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA_CHICKEN, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PIZZA, RenderType.cutoutMipped());
    }
}
