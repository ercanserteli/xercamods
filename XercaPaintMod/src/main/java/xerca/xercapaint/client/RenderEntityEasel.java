package xerca.xercapaint.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
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
public class RenderEntityEasel extends EntityRenderer<EntityEasel, RenderEntityEasel.EaselRenderState> implements RenderLayerParent<RenderEntityEasel.EaselRenderState, EaselModel> {
    protected final EaselModel model;
    protected final List<RenderLayer<EaselRenderState, EaselModel>> layers = Lists.newArrayList();
    public static RenderEntityEasel theInstance;
    private static final Identifier woodTexture = Mod.id("textures/block/birch_long.png");

    public RenderEntityEasel(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new EaselModel(ctx.bakeLayer(ModClient.EASEL_MAIN_LAYER));
        this.layers.add(new EaselCanvasLayer(this));
    }

    @Override
    public @NotNull EaselModel getModel() {
        return this.model;
    }

    @Override
    public @NotNull EaselRenderState createRenderState() {
        return new EaselRenderState();
    }

    @Override
    public void extractRenderState(EntityEasel entity, EaselRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entityYaw = entity.getYRot();
        state.itemStack = entity.getItem();
    }

    @Override
    public void submit(EaselRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
    //public void render(EaselRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        RenderType renderType = this.model.renderType(woodTexture);


        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(-state.entityYaw));

        this.model.setupAnim(state);

        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, 0));
        poseStack.translate(0, -1.5, 0);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
        submitNodeCollector.submitModel(this.model, state, poseStack, renderType, state.lightCoords, overlay, state.outlineColor, null);

        for (RenderLayer<EaselRenderState, EaselModel> layer : layers) {
            layer.submit(poseStack, submitNodeCollector, state.lightCoords, state, 0, 0);
        }
        poseStack.popPose();



        super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel, double distanceSquared) {
        HitResult result = Minecraft.getInstance().hitResult;
        if (result instanceof EntityHitResult entityHitResult) {
            if (Minecraft.renderNames() && entityHitResult.getEntity().getUUID().equals(easel.getUUID()) && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem())) {
                float range = easel.isDiscrete() ? 32.0F : 64.0F;
                return distanceSquared < (double) (range * range);
            }
        }
        return false;
    }

    @Override
    protected void submitNameTag(EaselRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        submitNodeCollector.submitNameTag(poseStack, state.nameTagAttachment, 0, ItemCanvas.getFullLabel(state.itemStack), !state.isDiscrete, state.lightCoords, state.distanceToCameraSq, cameraRenderState);
        poseStack.popPose();
    }

    public static class RenderEntityEaselFactory implements EntityRendererProvider<EntityEasel> {
        @Override
        public @NotNull EntityRenderer<EntityEasel, EaselRenderState> create(EntityRendererProvider.Context ctx) {
            theInstance = new RenderEntityEasel(ctx);
            return theInstance;
        }
    }

    public static class EaselRenderState extends EntityRenderState {
        public ItemStack itemStack;
        public float entityYaw;
    }
}
