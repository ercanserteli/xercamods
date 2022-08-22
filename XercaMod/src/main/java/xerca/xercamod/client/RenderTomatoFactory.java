package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.entity.EntityTomato;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

class RenderTomatoFactory implements EntityRendererProvider<EntityTomato> {

    @Override
    public @NotNull EntityRenderer<EntityTomato> create(@NotNull Context ctx) {
        return new ThrownItemRenderer<>(ctx);
    }
}
