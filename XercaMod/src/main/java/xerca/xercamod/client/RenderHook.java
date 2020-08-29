package xerca.xercamod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import xerca.xercamod.common.entity.EntityHook;

import javax.annotation.Nonnull;

class RenderHook extends EntityRenderer<EntityHook> {
    private static final ResourceLocation resourceLocation = new ResourceLocation("xercamod:textures/particle/hook.png");
    private static final RenderType renderType = RenderType.getEntityCutout(resourceLocation);

    public RenderHook(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
    public void render(EntityHook entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        PlayerEntity playerentity = entity.getAngler();
        if (playerentity != null) {
            matrixStackIn.push();
            matrixStackIn.push();
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            matrixStackIn.rotate(this.renderManager.getCameraOrientation());
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
            MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
            Matrix4f matrix4f = matrixstack$entry.getMatrix();
            Matrix3f matrix3f = matrixstack$entry.getNormal();
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(renderType);
            func_229106_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1);
            func_229106_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1);
            func_229106_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0);
            func_229106_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0);
            matrixStackIn.pop();
            int i = playerentity.getPrimaryHand() == HandSide.RIGHT ? 1 : -1;
            ItemStack itemstack = playerentity.getHeldItemMainhand();
            if (!(itemstack.getItem() instanceof net.minecraft.item.FishingRodItem)) {
                i = -i;
            }

            float f = playerentity.getSwingProgress(partialTicks);
            float f1 = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
            float f2 = MathHelper.lerp(partialTicks, playerentity.prevRenderYawOffset, playerentity.renderYawOffset) * ((float)Math.PI / 180F);
            double d0 = (double)MathHelper.sin(f2);
            double d1 = (double)MathHelper.cos(f2);
            double d2 = (double)i * 0.35D;
            double d3 = 0.8D;
            double d4;
            double d5;
            double d6;
            float f3;
            if ((this.renderManager.options == null || this.renderManager.options.func_243230_g().func_243192_a()) && playerentity == Minecraft.getInstance().player) {
                double d7 = this.renderManager.options.fov;
                d7 = d7 / 100.0D;
                Vector3d vec3d = new Vector3d((double)i * -0.36D * d7, -0.045D * d7, 0.4D);
                vec3d = vec3d.rotatePitch(-MathHelper.lerp(partialTicks, playerentity.prevRotationPitch, playerentity.rotationPitch) * ((float)Math.PI / 180F));
                vec3d = vec3d.rotateYaw(-MathHelper.lerp(partialTicks, playerentity.prevRotationYaw, playerentity.rotationYaw) * ((float)Math.PI / 180F));
                vec3d = vec3d.rotateYaw(f1 * 0.5F);
                vec3d = vec3d.rotatePitch(-f1 * 0.7F);
                d4 = MathHelper.lerp((double)partialTicks, playerentity.prevPosX, playerentity.getPosX()) + vec3d.x;
                d5 = MathHelper.lerp((double)partialTicks, playerentity.prevPosY, playerentity.getPosY()) + vec3d.y;
                d6 = MathHelper.lerp((double)partialTicks, playerentity.prevPosZ, playerentity.getPosZ()) + vec3d.z;
                f3 = playerentity.getEyeHeight();
            } else {
                d4 = MathHelper.lerp((double)partialTicks, playerentity.prevPosX, playerentity.getPosX()) - d1 * d2 - d0 * 0.8D;
                d5 = playerentity.prevPosY + (double)playerentity.getEyeHeight() + (playerentity.getPosY() - playerentity.prevPosY) * (double)partialTicks - 0.45D;
                d6 = MathHelper.lerp((double)partialTicks, playerentity.prevPosZ, playerentity.getPosZ()) - d0 * d2 + d1 * 0.8D;
                f3 = playerentity.isCrouching() ? -0.1875F : 0.0F;
            }

            double d9 = MathHelper.lerp((double)partialTicks, entity.prevPosX, entity.getPosX());
            double d10 = MathHelper.lerp((double)partialTicks, entity.prevPosY, entity.getPosY()) + 0.25D;
            double d8 = MathHelper.lerp((double)partialTicks, entity.prevPosZ, entity.getPosZ());
            float f4 = (float)(d4 - d9);
            float f5 = (float)(d5 - d10) + f3;
            float f6 = (float)(d6 - d8);
            IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.getLines());
            Matrix4f matrix4f1 = matrixStackIn.getLast().getMatrix();
            int j = 16;

            for(int k = 0; k < 16; ++k) {
                func_229104_a_(f4, f5, f6, ivertexbuilder1, matrix4f1, floatDivision(k, 16));
                func_229104_a_(f4, f5, f6, ivertexbuilder1, matrix4f1, floatDivision(k + 1, 16));
            }

            matrixStackIn.pop();
            super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    private static float floatDivision(int a, int b) {
        return (float)a / (float)b;
    }

    private static void func_229106_a_(IVertexBuilder p_229106_0_, Matrix4f p_229106_1_, Matrix3f p_229106_2_, int p_229106_3_, float p_229106_4_, int p_229106_5_, int p_229106_6_, int p_229106_7_) {
        p_229106_0_.pos(p_229106_1_, p_229106_4_ - 0.5F, (float)p_229106_5_ - 0.5F, 0.0F).color(255, 255, 255, 255).tex((float)p_229106_6_, (float)p_229106_7_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229106_3_).normal(p_229106_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void func_229104_a_(float p_229104_0_, float p_229104_1_, float p_229104_2_, IVertexBuilder p_229104_3_, Matrix4f p_229104_4_, float p_229104_5_) {
        p_229104_3_.pos(p_229104_4_, p_229104_0_ * p_229104_5_, p_229104_1_ * (p_229104_5_ * p_229104_5_ + p_229104_5_) * 0.5F + 0.25F, p_229104_2_ * p_229104_5_).color(0, 0, 0, 255).endVertex();
    }
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    public ResourceLocation getEntityTexture(@Nonnull EntityHook entity) {
        return resourceLocation;
    }
}
