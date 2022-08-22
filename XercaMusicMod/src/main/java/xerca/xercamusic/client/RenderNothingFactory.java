package xerca.xercamusic.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

class RenderNothingFactory implements EntityRendererProvider<EntityMusicSpirit> {

    @Override
    public @NotNull EntityRenderer<EntityMusicSpirit> create(@NotNull Context ctx) {
        return new RenderNothing<>(ctx);
    }
}
