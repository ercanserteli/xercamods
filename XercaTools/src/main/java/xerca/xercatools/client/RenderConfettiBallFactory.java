package xerca.xercatools.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import xerca.xercatools.entity.EntityConfettiBall;

public class RenderConfettiBallFactory implements EntityRendererProvider<EntityConfettiBall> {

    @Override
    public EntityRenderer<EntityConfettiBall, ThrownItemRenderState> create(Context ctx) {
        return new ThrownItemRenderer<>(ctx);
    }
}
