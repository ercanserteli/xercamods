package xerca.xercatools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityHealthOrb;

public class RenderHealthOrb extends EntityRenderer<EntityHealthOrb> {
    private static final ResourceLocation TEXTURE = Mod.id("textures/misc/health_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    public RenderHealthOrb(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    @Override
    protected int getBlockLightLevel(EntityHealthOrb entity, BlockPos pos) {
        return Mth.clamp(super.getBlockLightLevel(entity, pos) + 7, 0, 15);
    }

    @Override
    public void render(EntityHealthOrb orb, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float animTime = (orb.tickCount + partialTicks) / 2.0F;
        int red = (int) ((Mth.sin(animTime) + 1.0F) * 32.0F) + 192;
        int blue = (int) ((Mth.sin(animTime + 4.1887903F) + 1.0F) * 0.1F * 64.0F);
        poseStack.translate(0.0D, 0.1D, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer consumer = buffer.getBuffer(RENDER_TYPE);
        Matrix4f matrix = poseStack.last().pose();
        vertex(consumer, matrix, -0.5F, -0.25F, red, 0, blue, 0, 1, packedLight);
        vertex(consumer, matrix, 0.5F, -0.25F, red, 0, blue, 1, 1, packedLight);
        vertex(consumer, matrix, 0.5F, 0.75F, red, 0, blue, 1, 0, packedLight);
        vertex(consumer, matrix, -0.5F, 0.75F, red, 0, blue, 0, 0, packedLight);
        poseStack.popPose();
        super.render(orb, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        consumer.addVertex(matrix, x, y, 0.0F)
                .setColor(red, green, blue, 220)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityHealthOrb entity) {
        return TEXTURE;
    }
}
