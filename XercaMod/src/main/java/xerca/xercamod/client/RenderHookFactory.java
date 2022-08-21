package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.entity.EntityHook;

class RenderHookFactory implements EntityRendererProvider<EntityHook> {

    @Override
    public @NotNull EntityRenderer<EntityHook> create(@NotNull Context ctx) {
        return new RenderHook(ctx);
    }
}
