package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.entity.EntityCushion;

public class RenderCushionFactory implements EntityRendererProvider<EntityCushion> {

    @Override
    public @NotNull EntityRenderer<EntityCushion> create(@NotNull Context ctx) {
        return new RenderCushion(ctx);
    }
}
