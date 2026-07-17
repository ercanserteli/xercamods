package xerca.xercatools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityGrabHook;

public class RenderGrabHook extends EntityRenderer<EntityGrabHook, GrabHookRenderState> {
    private static final ResourceLocation TEXTURE = Mod.id("textures/particle/hook.png");
    private static final ResourceLocation CHAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/iron_chain.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);
    private static final RenderType CHAIN_RENDER_TYPE = RenderType.entityCutoutNoCull(CHAIN_TEXTURE);
    private static final float HOOK_ATTACHMENT_Y = 0.25F;
    private static final float CHAIN_HALF_WIDTH = 3.0F / 32.0F;
    private static final float CHAIN_SEGMENT_LENGTH = 1.0F;
    private static final float MIN_CHAIN_LENGTH = 1.0E-3F;
    private static final float CHAIN_U0 = 0.0F;
    private static final float CHAIN_U1 = 3.0F / 16.0F;
    private static final float CHAIN_U2 = 6.0F / 16.0F;
    private static final float HOOK_HALF_WIDTH = 0.5F;
    private static final float HOOK_TOP = 0.0F;
    private static final float HOOK_BOTTOM = -1.0F;

    public RenderGrabHook(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public GrabHookRenderState createRenderState() {
        return new GrabHookRenderState();
    }

    @Override
    public void extractRenderState(EntityGrabHook entity, GrabHookRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        Player player = entity.getAngler();
        state.hasPlayer = player != null;

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

            double handX = Mth.lerp(partialTicks, player.xo, player.getX()) - cos * offset - sin * 0.8D;
            double handY = player.yo + player.getEyeHeight() + (player.getY() - player.yo) * partialTicks - 0.45D;
            double handZ = Mth.lerp(partialTicks, player.zo, player.getZ()) - sin * offset + cos * 0.8D;
            float crouchOffset = player.isCrouching() ? -0.1875F : 0.0F;

            double hookX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double hookY = Mth.lerp(partialTicks, entity.yo, entity.getY()) + HOOK_ATTACHMENT_Y;
            double hookZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            state.chainDx = (float) (handX - hookX);
            state.chainDy = (float) (handY - hookY) + crouchOffset;
            state.chainDz = (float) (handZ - hookZ);
        } else {
            Vec3 fallbackDirection = getFallbackDirection(entity, partialTicks);
            state.chainDx = (float) fallbackDirection.x;
            state.chainDy = (float) fallbackDirection.y;
            state.chainDz = (float) fallbackDirection.z;
        }
    }

    @Override
    public void submit(GrabHookRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        submitHook(state.chainDx, state.chainDy, state.chainDz, poseStack, collector, state.lightCoords);
        if (state.hasPlayer) {
            submitChain(state.chainDx, state.chainDy, state.chainDz, poseStack, collector, state.lightCoords);
        }
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraState);
    }

    private static void submitHook(float chainDx, float chainDy, float chainDz, PoseStack poseStack, SubmitNodeCollector collector, int light) {
        float length = Mth.sqrt(chainDx * chainDx + chainDy * chainDy + chainDz * chainDz);
        if (length < MIN_CHAIN_LENGTH) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotationTo(0.0F, 1.0F, 0.0F, chainDx / length, chainDy / length, chainDz / length));
        collector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, consumer) -> {
            hookVertex(consumer, pose, -HOOK_HALF_WIDTH, HOOK_BOTTOM, 0.0F, light, 0.0F, 1.0F);
            hookVertex(consumer, pose, HOOK_HALF_WIDTH, HOOK_BOTTOM, 0.0F, light, 1.0F, 1.0F);
            hookVertex(consumer, pose, HOOK_HALF_WIDTH, HOOK_TOP, 0.0F, light, 1.0F, 0.0F);
            hookVertex(consumer, pose, -HOOK_HALF_WIDTH, HOOK_TOP, 0.0F, light, 0.0F, 0.0F);

            hookVertex(consumer, pose, 0.0F, HOOK_BOTTOM, -HOOK_HALF_WIDTH, light, 0.0F, 1.0F);
            hookVertex(consumer, pose, 0.0F, HOOK_BOTTOM, HOOK_HALF_WIDTH, light, 1.0F, 1.0F);
            hookVertex(consumer, pose, 0.0F, HOOK_TOP, HOOK_HALF_WIDTH, light, 1.0F, 0.0F);
            hookVertex(consumer, pose, 0.0F, HOOK_TOP, -HOOK_HALF_WIDTH, light, 0.0F, 0.0F);
        });
        poseStack.popPose();
    }

    private static void hookVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int light, float u, float v) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    private static void submitChain(float dx, float dy, float dz, PoseStack poseStack, SubmitNodeCollector collector, int light) {
        float length = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < MIN_CHAIN_LENGTH) {
            return;
        }

        float nx = -dx / length;
        float ny = -dy / length;
        float nz = -dz / length;
        poseStack.pushPose();
        poseStack.translate(dx, dy, dz);
        poseStack.mulPose(new Quaternionf().rotationTo(0.0F, 1.0F, 0.0F, nx, ny, nz));
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        collector.submitCustomGeometry(poseStack, CHAIN_RENDER_TYPE, (pose, consumer) -> {
            int fullSegments = Mth.floor(length / CHAIN_SEGMENT_LENGTH);
            float partialSegment = length - fullSegments * CHAIN_SEGMENT_LENGTH;
            float offset = 0.0F;

            if (partialSegment > MIN_CHAIN_LENGTH) {
                renderChainSegment(consumer, pose, offset, partialSegment, light, true);
                offset += partialSegment;
            }

            for (int segment = 0; segment < fullSegments; segment++) {
                renderChainSegment(consumer, pose, offset, CHAIN_SEGMENT_LENGTH, light, false);
                offset += CHAIN_SEGMENT_LENGTH;
            }
        });
        poseStack.popPose();
    }

    private static void renderChainSegment(VertexConsumer consumer, PoseStack.Pose pose, float offset, float segmentLength, int light, boolean startSegment) {
        float end = offset + segmentLength;
        float segmentFraction = segmentLength / CHAIN_SEGMENT_LENGTH;
        float vBottom = startSegment ? segmentFraction : 1.0F;
        float vTop = startSegment ? 0.0F : 1.0F - segmentFraction;

        chainVertex(consumer, pose, -CHAIN_HALF_WIDTH, offset, 0.0F, 0.0F, 1.0F, light, CHAIN_U1, vBottom);
        chainVertex(consumer, pose, CHAIN_HALF_WIDTH, offset, 0.0F, 0.0F, 1.0F, light, CHAIN_U0, vBottom);
        chainVertex(consumer, pose, CHAIN_HALF_WIDTH, end, 0.0F, 0.0F, 1.0F, light, CHAIN_U0, vTop);
        chainVertex(consumer, pose, -CHAIN_HALF_WIDTH, end, 0.0F, 0.0F, 1.0F, light, CHAIN_U1, vTop);

        chainVertex(consumer, pose, 0.0F, offset, -CHAIN_HALF_WIDTH, 1.0F, 0.0F, light, CHAIN_U2, vBottom);
        chainVertex(consumer, pose, 0.0F, offset, CHAIN_HALF_WIDTH, 1.0F, 0.0F, light, CHAIN_U1, vBottom);
        chainVertex(consumer, pose, 0.0F, end, CHAIN_HALF_WIDTH, 1.0F, 0.0F, light, CHAIN_U1, vTop);
        chainVertex(consumer, pose, 0.0F, end, -CHAIN_HALF_WIDTH, 1.0F, 0.0F, light, CHAIN_U2, vTop);
    }

    private static void chainVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float normalX, float normalZ, int light, float u, float v) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normalX, 0.0F, normalZ);
    }

    private static Vec3 getFallbackDirection(EntityGrabHook entity, float partialTicks) {
        double dx = Mth.lerp(partialTicks, entity.xo, entity.getX()) - entity.getX();
        double dy = Mth.lerp(partialTicks, entity.yo, entity.getY()) - entity.getY();
        double dz = Mth.lerp(partialTicks, entity.zo, entity.getZ()) - entity.getZ();
        Vec3 movement = new Vec3(dx, dy, dz);
        if (movement.lengthSqr() > 1.0E-6D) {
            return movement.normalize().scale(-1.0D);
        }
        Vec3 delta = entity.getDeltaMovement();
        if (delta.lengthSqr() > 1.0E-6D) {
            return delta.normalize().scale(-1.0D);
        }
        return new Vec3(0.0D, 1.0D, 0.0D);
    }

    @Override
    public boolean shouldRender(EntityGrabHook entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        if (super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ)) {
            return true;
        }

        Player angler = entity.getAngler();
        return angler != null && frustum.isVisible(createChainBounds(entity, angler));
    }

    private static AABB createChainBounds(EntityGrabHook entity, Player angler) {
        double hookX = entity.getX();
        double hookY = entity.getY() + HOOK_ATTACHMENT_Y;
        double hookZ = entity.getZ();
        double handX = angler.getX();
        double handY = angler.getY() + angler.getEyeHeight();
        double handZ = angler.getZ();
        return new AABB(
                Math.min(hookX, handX),
                Math.min(hookY, handY),
                Math.min(hookZ, handZ),
                Math.max(hookX, handX),
                Math.max(hookY, handY),
                Math.max(hookZ, handZ)
        ).inflate(CHAIN_HALF_WIDTH * 2.0F);
    }
}
