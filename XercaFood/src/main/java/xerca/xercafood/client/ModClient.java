package xerca.xercafood.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntities;
import xerca.xercafood.common.entity.Entities;


@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(Entities.TOMATO, new RenderTomatoFactory());

        BlockEntityRenderers.register(BlockEntities.DONER, DonerTileEntityRenderer::new);
        initializeRenderLayers();
    }

    private void initializeRenderLayers() {
        BlockRenderLayerMap.putBlock(Blocks.BLOCK_RICE_PLANT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.BLOCK_TEA_PLANT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.BLOCK_TOMATO_PLANT, ChunkSectionLayer.CUTOUT);

        BlockRenderLayerMap.putBlock(Blocks.VAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.VAT_CHEESE, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.VAT_MILK, ChunkSectionLayer.CUTOUT);

        BlockRenderLayerMap.putBlock(Blocks.PIZZA_PEPPERONI_PEPPERONI_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MUSHROOM_PEPPERONI_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MUSHROOM_MUSHROOM_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MUSHROOM_MUSHROOM_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_PEPPERONI_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_MUSHROOM_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_MUSHROOM_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_MEAT_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_MEAT_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_MEAT_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_PEPPERONI_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_MUSHROOM_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_MUSHROOM_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_MEAT_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_MEAT_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_MEAT_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_FISH_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_FISH_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_FISH_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_FISH_FISH, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_PEPPERONI_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_MUSHROOM_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_MUSHROOM_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_MEAT_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_MEAT_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_MEAT_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_FISH_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_FISH_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_FISH_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_FISH_FISH, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_FISH, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN_CHICKEN, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_PEPPERONI_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MUSHROOM_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MUSHROOM_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH_FISH, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_FISH, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN_CHICKEN, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_PEPPERONI, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MUSHROOM, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_MEAT, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_FISH, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA_CHICKEN, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PIZZA, ChunkSectionLayer.CUTOUT);
    }
}
