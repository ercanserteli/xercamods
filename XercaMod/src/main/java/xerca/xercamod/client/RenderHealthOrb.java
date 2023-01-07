//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package xerca.xercamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.entity.EntityHealthOrb;

@OnlyIn(Dist.CLIENT)
public class RenderHealthOrb extends EntityRenderer<EntityHealthOrb> {
    private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation(XercaMod.MODID, "textures/misc/health_orb.png");
    private static final RenderType RENDER_TYPE;

    public RenderHealthOrb(Context pContext) {
        super(pContext);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    protected int getBlockLightLevel(@NotNull EntityHealthOrb pEntity, @NotNull BlockPos pPos) {
        return Mth.clamp(super.getBlockLightLevel(pEntity, pPos) + 7, 0, 15);
    }

    public void render(EntityHealthOrb healthOrb, float pEntityYaw, float pPartialTicks, PoseStack stack, MultiBufferSource bufferSource, int pPackedLight) {
        stack.pushPose();
        float f8 = ((float)healthOrb.tickCount + pPartialTicks) / 2.0F;
        int red = (int)((Mth.sin(f8) + 1.0F) * 32.0F) + 192;
        int blue = (int)((Mth.sin(f8 + 4.1887903F) + 1.0F) * 0.1F * 64.0F) ;
        stack.translate(0.0D, 0.10000000149011612D, 0.0D);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        stack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        Pose pose = stack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, -0.25F, red, 0, blue, 0, 1, pPackedLight);
        vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, -0.25F, red, 0, blue, 1, 1, pPackedLight);
        vertex(vertexconsumer, matrix4f, matrix3f, 0.5F, 0.75F, red, 0, blue, 1, 0, pPackedLight);
        vertex(vertexconsumer, matrix4f, matrix3f, -0.5F, 0.75F, red, 0, blue, 0, 0, pPackedLight);
        stack.popPose();
        super.render(healthOrb, pEntityYaw, pPartialTicks, stack, bufferSource, pPackedLight);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float pX, float pY, int red, int green, int blue, float pTexU, float pTexV, int pPackedLight) {
        vertexConsumer.vertex(matrix4f, pX, pY, 0.0F).color(red, green, blue, 220).uv(pTexU, pTexV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityHealthOrb pEntity) {
        return EXPERIENCE_ORB_LOCATION;
    }

    static {
        RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);
    }
}
