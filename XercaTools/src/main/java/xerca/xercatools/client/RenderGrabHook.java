package xerca.xercatools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityGrabHook;

public class RenderGrabHook extends EntityRenderer<EntityGrabHook> {
    private static final ResourceLocation TEXTURE = Mod.id("textures/particle/hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    public RenderGrabHook(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityGrabHook entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        Player player = entity.getAngler();

        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.scale(1.0F, 1.0F, 1.0F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);
        vertex(vertexConsumer, matrix4f, packedLight, 0.0F, 0, 0, 1);
        vertex(vertexConsumer, matrix4f, packedLight, 1.0F, 0, 1, 1);
        vertex(vertexConsumer, matrix4f, packedLight, 1.0F, 1, 1, 0);
        vertex(vertexConsumer, matrix4f, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();

        if (player != null) {
            int side = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            ItemStack held = player.getMainHandItem();
            if (!(held.getItem() instanceof FishingRodItem)) {
                side = -side;
            }

            float bodyRot = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float) Math.PI / 180F);
            double sin = Mth.sin(bodyRot);
            double cos = Mth.cos(bodyRot);
            double offset = side * 0.35D;
            double handX;
            double handY;
            double handZ;
            float crouchOffset;

            handX = Mth.lerp(partialTicks, player.xo, player.getX()) - cos * offset - sin * 0.8D;
            handY = player.yo + player.getEyeHeight() + (player.getY() - player.yo) * partialTicks - 0.45D;
            handZ = Mth.lerp(partialTicks, player.zo, player.getZ()) - sin * offset + cos * 0.8D;
            crouchOffset = player.isCrouching() ? -0.1875F : 0.0F;

            double hookX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double hookY = Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.25D;
            double hookZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float dx = (float) (handX - hookX);
            float dy = (float) (handY - hookY) + crouchOffset;
            float dz = (float) (handZ - hookZ);
            VertexConsumer lineBuffer = buffer.getBuffer(RenderType.lines());
            PoseStack.Pose linePose = poseStack.last();
            for (int k = 0; k <= 16; ++k) {
                stringVertex(dx, dy, dz, lineBuffer, linePose, k / 16.0F, (k + 1) / 16.0F);
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light, float x, int y, int u, int v) {
        consumer.addVertex(pose, x - 0.5F, y - 0.5F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static void stringVertex(float dx, float dy, float dz, VertexConsumer consumer, PoseStack.Pose pose, float start, float end) {
        float x = dx * start;
        float y = dy * (start * start + start) * 0.5F + 0.25F;
        float z = dz * start;
        float nx = dx * end - x;
        float ny = dy * (end * end + end) * 0.5F + 0.25F - y;
        float nz = dz * end - z;
        float length = Mth.sqrt(nx * nx + ny * ny + nz * nz);
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(0, 0, 0, 255)
                .setNormal(pose, nx / length, ny / length, nz / length);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityGrabHook entity) {
        return TEXTURE;
    }
}
