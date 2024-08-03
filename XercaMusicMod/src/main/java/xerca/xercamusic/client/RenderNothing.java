package xerca.xercamusic.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.XercaMusic;

class RenderNothing<T extends Entity> extends EntityRenderer<T> {
    static final ResourceLocation texture = new ResourceLocation(XercaMusic.MODID, "textures/dot.png");
    RenderNothing(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return texture;
    }
}