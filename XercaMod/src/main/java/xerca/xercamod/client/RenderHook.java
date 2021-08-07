package xerca.xercamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import xerca.xercamod.common.entity.EntityHook;

import javax.annotation.Nonnull;

class RenderHook extends EntityRenderer<EntityHook> {
    private static final ResourceLocation resourceLocation = new ResourceLocation("xercamod:textures/particle/hook.png");
    private static final RenderType renderType = RenderType.entityCutout(resourceLocation);

    public RenderHook(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
    public void render(EntityHook entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        Player playerentity = entity.getAngler();
        if (playerentity != null) {
            matrixStackIn.pushPose();
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            PoseStack.Pose matrixstack$entry = matrixStackIn.last();
            Matrix4f matrix4f = matrixstack$entry.pose();
            Matrix3f matrix3f = matrixstack$entry.normal();
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);
            vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1);
            vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1);
            vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0);
            vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0);
            matrixStackIn.popPose();
            int i = playerentity.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            ItemStack itemstack = playerentity.getMainHandItem();
            if (!(itemstack.getItem() instanceof net.minecraft.world.item.FishingRodItem)) {
                i = -i;
            }

            float f = playerentity.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * (float)Math.PI);
            float f2 = Mth.lerp(partialTicks, playerentity.yBodyRotO, playerentity.yBodyRot) * ((float)Math.PI / 180F);
            double d0 = (double)Mth.sin(f2);
            double d1 = (double)Mth.cos(f2);
            double d2 = (double)i * 0.35D;
            double d3 = 0.8D;
            double d4;
            double d5;
            double d6;
            float f3;
            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && playerentity == Minecraft.getInstance().player) {
//                double d7 = this.entityRenderDispatcher.options.fov;
//                d7 = d7 / 100.0D;
//                Vec3 vec3d = new Vec3((double)i * -0.36D * d7, -0.045D * d7, 0.4D);
//                vec3d = vec3d.xRot(-Mth.lerp(partialTicks, playerentity.xRotO, playerentity.getXRot()) * ((float)Math.PI / 180F));
//                vec3d = vec3d.yRot(-Mth.lerp(partialTicks, playerentity.yRotO, playerentity.getYRot()) * ((float)Math.PI / 180F));
//                vec3d = vec3d.yRot(f1 * 0.5F);
//                vec3d = vec3d.xRot(-f1 * 0.7F);
//                d4 = Mth.lerp((double)partialTicks, playerentity.xo, playerentity.getX()) + vec3d.x;
//                d5 = Mth.lerp((double)partialTicks, playerentity.yo, playerentity.getY()) + vec3d.y;
//                d6 = Mth.lerp((double)partialTicks, playerentity.zo, playerentity.getZ()) + vec3d.z;
//                f3 = playerentity.getEyeHeight();
                double d7 = 960.0D / this.entityRenderDispatcher.options.fov;
                Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)i * 0.525F, -0.1F);
                vec3 = vec3.scale(d7);
                vec3 = vec3.yRot(f1 * 0.5F);
                vec3 = vec3.xRot(-f1 * 0.7F);
                d4 = Mth.lerp((double)partialTicks, playerentity.xo, playerentity.getX()) + vec3.x;
                d5 = Mth.lerp((double)partialTicks, playerentity.yo, playerentity.getY()) + vec3.y;
                d6 = Mth.lerp((double)partialTicks, playerentity.zo, playerentity.getZ()) + vec3.z;
                f3 = playerentity.getEyeHeight();
            } else {
                d4 = Mth.lerp((double)partialTicks, playerentity.xo, playerentity.getX()) - d1 * d2 - d0 * 0.8D;
                d5 = playerentity.yo + (double)playerentity.getEyeHeight() + (playerentity.getY() - playerentity.yo) * (double)partialTicks - 0.45D;
                d6 = Mth.lerp((double)partialTicks, playerentity.zo, playerentity.getZ()) - d0 * d2 + d1 * 0.8D;
                f3 = playerentity.isCrouching() ? -0.1875F : 0.0F;
            }

            double d9 = Mth.lerp((double)partialTicks, entity.xo, entity.getX());
            double d10 = Mth.lerp((double)partialTicks, entity.yo, entity.getY()) + 0.25D;
            double d8 = Mth.lerp((double)partialTicks, entity.zo, entity.getZ());
            float f4 = (float)(d4 - d9);
            float f5 = (float)(d5 - d10) + f3;
            float f6 = (float)(d6 - d8);
            VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(RenderType.lines());
            PoseStack.Pose posestack$pose1 = matrixStackIn.last();

            for(int k = 0; k <= 16; ++k) {
                stringVertex(f4, f5, f6, ivertexbuilder1, posestack$pose1, floatDivision(k, 16), floatDivision(k + 1, 16));
            }

            matrixStackIn.popPose();
            super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    private static float floatDivision(int a, int b) {
        return (float)a / (float)b;
    }

    private static void vertex(VertexConsumer p_114712_, Matrix4f p_114713_, Matrix3f p_114714_, int p_114715_, float p_114716_, int p_114717_, int p_114718_, int p_114719_) {
        p_114712_.vertex(p_114713_, p_114716_ - 0.5F, (float)p_114717_ - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)p_114718_, (float)p_114719_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114715_).normal(p_114714_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void stringVertex(float p_174119_, float p_174120_, float p_174121_, VertexConsumer p_174122_, PoseStack.Pose p_174123_, float p_174124_, float p_174125_) {
        float f = p_174119_ * p_174124_;
        float f1 = p_174120_ * (p_174124_ * p_174124_ + p_174124_) * 0.5F + 0.25F;
        float f2 = p_174121_ * p_174124_;
        float f3 = p_174119_ * p_174125_ - f;
        float f4 = p_174120_ * (p_174125_ * p_174125_ + p_174125_) * 0.5F + 0.25F - f1;
        float f5 = p_174121_ * p_174125_ - f2;
        float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
        f3 = f3 / f6;
        f4 = f4 / f6;
        f5 = f5 / f6;
        p_174122_.vertex(p_174123_.pose(), f, f1, f2).color(0, 0, 0, 255).normal(p_174123_.normal(), f3, f4, f5).endVertex();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    public ResourceLocation getTextureLocation(@Nonnull EntityHook entity) {
        return resourceLocation;
    }
}
