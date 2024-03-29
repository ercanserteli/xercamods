package xerca.xercamod.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.entity.EntityConfettiBall;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class RenderConfettiBallFactory implements EntityRendererProvider<EntityConfettiBall> {

    @Override
    public @NotNull EntityRenderer<EntityConfettiBall> create(@NotNull Context ctx) {
        return new ThrownItemRenderer<>(ctx);
    }
}
