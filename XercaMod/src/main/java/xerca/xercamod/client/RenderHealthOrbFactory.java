package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.entity.EntityHealthOrb;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

class RenderHealthOrbFactory implements EntityRendererProvider<EntityHealthOrb> {

    @Override
    public @NotNull EntityRenderer<EntityHealthOrb> create(@NotNull Context ctx) {
        return new RenderHealthOrb(ctx);
    }
}
