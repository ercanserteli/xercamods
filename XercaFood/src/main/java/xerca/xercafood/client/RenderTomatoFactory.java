package xerca.xercafood.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import xerca.xercafood.common.entity.EntityTomato;

class RenderTomatoFactory implements EntityRendererProvider<EntityTomato> {

    @Override
    public EntityRenderer<EntityTomato> create(Context ctx) {
        return new ThrownItemRenderer<>(ctx);
    }
}
