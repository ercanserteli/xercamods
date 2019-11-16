package xerca.xercamusic.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
class RenderNothing extends EntityRenderer {

    RenderNothing(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick) {
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}