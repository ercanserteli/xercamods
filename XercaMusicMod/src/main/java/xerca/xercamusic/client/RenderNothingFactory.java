package xerca.xercamusic.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

class RenderNothingFactory implements EntityRendererProvider<EntityMusicSpirit> {

    @Override
    public EntityRenderer<EntityMusicSpirit> create(Context ctx) {
        return new RenderNothing(ctx);
    }
}
