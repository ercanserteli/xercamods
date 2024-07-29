package xerca.xercamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
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
    public void render(EntityHook entity, float entityYaw, float partialTicks, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferIn, int packedLightIn)
    {
        Player player = entity.getAngler();
        if (player != null) {
            stack.pushPose();
            stack.pushPose();
            stack.scale(0.5F, 0.5F, 0.5F);
            stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            stack.mulPose(Axis.YP.rotationDegrees(180.0F));
            PoseStack.Pose pose = stack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            VertexConsumer buffer = bufferIn.getBuffer(renderType);
            vertex(buffer, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1);
            vertex(buffer, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1);
            vertex(buffer, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0);
            vertex(buffer, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0);
            stack.popPose();
            int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            ItemStack itemstack = player.getMainHandItem();
            if (!(itemstack.getItem() instanceof net.minecraft.world.item.FishingRodItem)) {
                i = -i;
            }

            float f = player.getAttackAnim(partialTicks);
            float f1 = Mth.sin(Mth.sqrt(f) * (float)Math.PI);
            float f2 = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float)Math.PI / 180F);
            double d0 = Mth.sin(f2);
            double d1 = Mth.cos(f2);
            double d2 = (double)i * 0.35D;
            double d4;
            double d5;
            double d6;
            float f3;
            if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
                double d7 = 960.0D / this.entityRenderDispatcher.options.fov().get();
                Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)i * 0.525F, -0.1F);
                vec3 = vec3.scale(d7);
                vec3 = vec3.yRot(f1 * 0.5F);
                vec3 = vec3.xRot(-f1 * 0.7F);
                d4 = Mth.lerp(partialTicks, player.xo, player.getX()) + vec3.x;
                d5 = Mth.lerp(partialTicks, player.yo, player.getY()) + vec3.y;
                d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) + vec3.z;
                f3 = player.getEyeHeight();
            } else {
                d4 = Mth.lerp(partialTicks, player.xo, player.getX()) - d1 * d2 - d0 * 0.8D;
                d5 = player.yo + (double)player.getEyeHeight() + (player.getY() - player.yo) * (double)partialTicks - 0.45D;
                d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) - d0 * d2 + d1 * 0.8D;
                f3 = player.isCrouching() ? -0.1875F : 0.0F;
            }

            double d9 = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double d10 = Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.25D;
            double d8 = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float f4 = (float)(d4 - d9);
            float f5 = (float)(d5 - d10) + f3;
            float f6 = (float)(d6 - d8);
            VertexConsumer buffer1 = bufferIn.getBuffer(RenderType.lines());
            PoseStack.Pose pose1 = stack.last();

            for(int k = 0; k <= 16; ++k) {
                stringVertex(f4, f5, f6, buffer1, pose1, floatDivision(k, 16), floatDivision(k + 1, 16));
            }

            stack.popPose();
            super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
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
    public @NotNull ResourceLocation getTextureLocation(@Nonnull EntityHook entity) {
        return resourceLocation;
    }
}
