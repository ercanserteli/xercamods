package xerca.xercamusic.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

public class RenderNothingFactory implements IRenderFactory<EntityMusicSpirit> {

    @Override
    public EntityRenderer<EntityMusicSpirit> createRenderFor(EntityRendererManager manager) {
        return new RenderNothing(manager);
    }
}