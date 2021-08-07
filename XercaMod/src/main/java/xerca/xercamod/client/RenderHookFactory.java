package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import xerca.xercamod.common.entity.EntityHook;

class RenderHookFactory implements EntityRendererProvider<EntityHook> {

    @Override
    public EntityRenderer<EntityHook> create(Context ctx) {
        return new RenderHook(ctx);
    }
}
