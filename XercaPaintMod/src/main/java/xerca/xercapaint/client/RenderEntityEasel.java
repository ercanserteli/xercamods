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
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;

import java.util.List;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class RenderEntityEasel extends EntityRenderer<EntityEasel> implements RenderLayerParent<EntityEasel, EaselModel> {
    protected final EaselModel model;
    protected final List<RenderLayer<EntityEasel, EaselModel>> layers = Lists.newArrayList();
    static @Nullable RenderEntityEasel theInstance;
    private static final ResourceLocation WOOD_TEXTURE = Mod.id("textures/block/birch_long.png");

    RenderEntityEasel(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new EaselModel(ctx.bakeLayer(ModClient.EASEL_MAIN_LAYER));
        this.layers.add(new EaselCanvasLayer(this));
    }

    @Override
    public EaselModel getModel() {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityEasel entity) {
        return WOOD_TEXTURE;
    }

    @Override
    public void render(EntityEasel entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-entityYaw));

        this.model.setupAnim(entity, 0, 0, 0, 0, 0);

        matrixStackIn.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, 0));
        matrixStackIn.translate(0, -1.5, 0);

        RenderType rendertype = this.model.renderType(this.getTextureLocation(entity));
        VertexConsumer vertexconsumer = bufferIn.getBuffer(rendertype);

        int i = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
        this.model.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, i);

        this.layers.forEach(renderLayer -> renderLayer.render(matrixStackIn, bufferIn, packedLightIn, entity, 0, 0, 0, 0, 0, 0));

        matrixStackIn.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel) {
        HitResult result = Minecraft.getInstance().hitResult;
        if (result instanceof EntityHitResult entityHitResult && Minecraft.renderNames() && easel.equals(entityHitResult.getEntity()) && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem())) {
            double distanceSquared = this.entityRenderDispatcher.distanceToSqr(easel);
            float range = easel.isDiscrete() ? 32.0F : 64.0F;
            return distanceSquared < range * range;
        }
        return false;
    }

    @Override
    protected void renderNameTag(EntityEasel easel, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        super.renderNameTag(easel, ItemCanvas.getFullLabel(easel.getItem()), poseStack, bufferSource, packedLight, partialTick);
        poseStack.popPose();
    }

    public static class RenderEntityEaselFactory implements EntityRendererProvider<EntityEasel> {
        @Override
        public EntityRenderer<EntityEasel> create(Context ctx) {
            RenderEntityEasel instance = new RenderEntityEasel(ctx);
            theInstance = instance;
            return instance;
        }
    }
}
