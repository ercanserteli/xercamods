package xerca.xercapaint.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityEasel extends EntityRenderer<EntityEasel> implements RenderLayerParent<EntityEasel, EaselModel> {
    protected final EaselModel model;
    protected final List<RenderLayer<EntityEasel, EaselModel>> layers = Lists.newArrayList();
    static public RenderEntityEasel theInstance;
    static private final ResourceLocation woodTexture = Mod.id("textures/block/birch_long.png");

    RenderEntityEasel(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new EaselModel(ctx.bakeLayer(ModClient.EASEL_MAIN_LAYER));
        this.layers.add(new EaselCanvasLayer(this));
    }

    @Override
    public @NotNull EaselModel getModel() {
        return this.model;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(EntityEasel entity) {
        return woodTexture;
    }

    @Override
    public void render(EntityEasel entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-entityYaw));

        this.model.setupAnim(entity, 0, 0, 0, 0, 0);

        matrixStackIn.mulPose((new Quaternionf()).rotationXYZ((float) Math.PI, 0, 0));
        matrixStackIn.translate(0, -1.5, 0);

        RenderType rendertype = this.model.renderType(this.getTextureLocation(entity));
        VertexConsumer vertexconsumer = bufferIn.getBuffer(rendertype);

        int i = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
        this.model.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, i);

        this.layers.forEach(renderlayer -> renderlayer.render(matrixStackIn, bufferIn, packedLightIn, entity, 0, 0, 0, 0, 0, 0));

        matrixStackIn.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel) {
        HitResult result = Minecraft.getInstance().hitResult;
        if(result instanceof EntityHitResult entityHitResult){
            if (Minecraft.renderNames() && entityHitResult.getEntity() == easel && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem())) {
                double d0 = this.entityRenderDispatcher.distanceToSqr(easel);
                float f = easel.isDiscrete() ? 32.0F : 64.0F;
                return d0 < (double)(f * f);
            }
        }
        return false;
    }

    @Override
    protected void renderNameTag(EntityEasel easel, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        super.renderNameTag(easel, ItemCanvas.getFullLabel(easel.getItem()), poseStack, buffer, packedLight, partialTick);
        poseStack.popPose();
    }

    public static class RenderEntityEaselFactory implements EntityRendererProvider<EntityEasel> {
        @Override
        public @NotNull EntityRenderer<EntityEasel> create(Context ctx) {
            theInstance = new RenderEntityEasel(ctx);
            return theInstance;
        }
    }
}