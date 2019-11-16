package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xerca.xercamod.common.entity.EntityCushion;

public class RenderCushionFactory implements IRenderFactory<EntityCushion> {

    @Override
    public EntityRenderer<EntityCushion> createRenderFor(EntityRendererManager manager) {
        return new RenderCushion(manager);
    }
}
