package xerca.xercamusic.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

class RenderNothingFactory implements EntityRendererProvider<EntityMusicSpirit> {

    @Override
    public EntityRenderer<EntityMusicSpirit, EntityRenderState> create(Context ctx) {
        return new RenderNothing<>(ctx);
    }
}
