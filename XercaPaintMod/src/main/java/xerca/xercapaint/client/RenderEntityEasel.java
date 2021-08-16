package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityEasel extends LivingEntityRenderer<EntityEasel, EaselModel> {
    static public RenderEntityEasel theInstance;
    static private final ResourceLocation woodTexture = new ResourceLocation(XercaPaint.MODID, "textures/block/birch_long.png");

    RenderEntityEasel(EntityRendererProvider.Context ctx) {
        super(ctx, new EaselModel(ctx.bakeLayer(ClientProxy.EASEL_MAIN_LAYER)), 0.0F);
        this.addLayer(new EaselCanvasLayer(this));
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(EntityEasel entity) {
        return woodTexture;
    }

    @Override
    public void render(EntityEasel entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel) {
        if (Minecraft.renderNames() && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem()) && this.entityRenderDispatcher.crosshairPickEntity == easel) {
            double d0 = this.entityRenderDispatcher.distanceToSqr(easel);
            float f = easel.isDiscrete() ? 32.0F : 64.0F;
            return d0 < (double)(f * f);
        } else {
            return false;
        }
    }

    @Override
    protected void renderNameTag(EntityEasel easel, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int p_115087_) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        super.renderNameTag(easel, ItemCanvas.getFullLabel(easel.getItem()), poseStack, bufferSource, p_115087_);
        poseStack.popPose();
    }

    public static class RenderEntityEaselFactory implements EntityRendererProvider<EntityEasel> {
        @Override
        public EntityRenderer<EntityEasel> create(Context ctx) {
            theInstance = new RenderEntityEasel(ctx);
            return theInstance;
        }
    }
}