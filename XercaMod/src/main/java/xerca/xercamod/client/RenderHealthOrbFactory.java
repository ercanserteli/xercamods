package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import xerca.xercamod.common.entity.EntityHealthOrb;
import xerca.xercamod.common.entity.EntityHook;

class RenderHealthOrbFactory implements EntityRendererProvider<EntityHealthOrb> {

    @Override
    public EntityRenderer<EntityHealthOrb> create(Context ctx) {
        return new RenderHealthOrb(ctx);
    }
}
