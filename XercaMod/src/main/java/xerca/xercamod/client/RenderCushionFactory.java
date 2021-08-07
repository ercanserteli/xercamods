package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import xerca.xercamod.common.entity.EntityCushion;

public class RenderCushionFactory implements EntityRendererProvider<EntityCushion> {

    @Override
    public EntityRenderer<EntityCushion> create(Context ctx) {
        return new RenderCushion(ctx);
    }
}
