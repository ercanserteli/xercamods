package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xerca.xercamod.common.entity.EntityHook;

class RenderHookFactory implements IRenderFactory<EntityHook> {

    @Override
    public EntityRenderer<EntityHook> createRenderFor(EntityRendererManager manager) {
        return new RenderHook(manager);
    }
}
