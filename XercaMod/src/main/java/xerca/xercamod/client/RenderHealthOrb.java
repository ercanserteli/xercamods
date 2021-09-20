//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package xerca.xercamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.entity.EntityHealthOrb;

@OnlyIn(Dist.CLIENT)
public class RenderHealthOrb extends EntityRenderer<EntityHealthOrb> {
    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation(XercaMod.MODID, "textures/misc/health_orb.png");
    private static final RenderType RENDER_TYPE;

    public RenderHealthOrb(Context p_174110_) {
        super(p_174110_);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    protected int getBlockLightLevel(EntityHealthOrb p_114606_, BlockPos p_114607_) {
        return Mth.clamp(super.getBlockLightLevel(p_114606_, p_114607_) + 7, 0, 15);
    }

    public void render(EntityHealthOrb healthOrb, float p_114600_, float p_114601_, PoseStack stack, MultiBufferSource bufferSource, int p_114604_) {
        stack.pushPose();
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        float f7 = 255.0F;
        float f8 = ((float)healthOrb.tickCount + p_114601_) / 2.0F;
        int red = (int)((Mth.sin(f8) + 1.0F) * 32.0F) + 192;
        int blue = (int)((Mth.sin(f8 + 4.1887903F) + 1.0F) * 0.1F * 64.0F) ;
        stack.translate(0.0D, 0.10000000149011612D, 0.0D);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        float f9 = 0.3F;
        stack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        Pose posestack$pose = stack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, -0.25F, red, 0, blue, 0, 1, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, -0.25F, red, 0, blue, 1, 1, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, 0.75F, red, 0, blue, 1, 0, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, 0.75F, red, 0, blue, 0, 0, p_114604_);
        stack.popPose();
        super.render(healthOrb, p_114600_, p_114601_, stack, bufferSource, p_114604_);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float p_114612_, float p_114613_, int red, int green, int blue, float p_114617_, float p_114618_, int p_114619_) {
        vertexConsumer.vertex(matrix4f, p_114612_, p_114613_, 0.0F).color(red, green, blue, 220).uv(p_114617_, p_114618_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114619_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(EntityHealthOrb p_114597_) {
        return EXPERIENCE_ORB_LOCATION;
    }

    static {
        RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);
    }
}
