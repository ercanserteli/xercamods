package xerca.xercapaint.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;

import java.util.List;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class RenderEntityEasel extends EntityRenderer<EntityEasel, EaselRenderState> implements RenderLayerParent<EaselRenderState, EaselModel> {
    protected final EaselModel model;
    protected final List<RenderLayer<EaselRenderState, EaselModel>> layers = Lists.newArrayList();
    static @Nullable RenderEntityEasel theInstance;
    private static final Identifier WOOD_TEXTURE = Mod.id("textures/block/birch_long.png");

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
    public EaselRenderState createRenderState() {
        return new EaselRenderState();
    }

    @Override
    public void extractRenderState(EntityEasel entity, EaselRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.item = entity.getItem();
        state.canvasType = state.item.getItem() instanceof ItemCanvas itemCanvas ? itemCanvas.getCanvasType() : null;
        state.yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
    }

    @Override
    public void submit(EaselRenderState state, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState cameraState) {
        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-state.yRot));

        matrixStackIn.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, 0));
        matrixStackIn.translate(0, -1.5, 0);

        RenderType rendertype = this.model.renderType(WOOD_TEXTURE);
        int i = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
        collector.submitModel(this.model, state, matrixStackIn, rendertype, state.lightCoords, i, state.outlineColor, null);

        this.layers.forEach(renderLayer -> renderLayer.submit(matrixStackIn, collector, state.lightCoords, state, 0, 0));

        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, cameraState);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel, double distanceToCameraSq) {
        HitResult result = Minecraft.getInstance().hitResult;
        if (result instanceof EntityHitResult entityHitResult && Minecraft.renderNames() && easel.equals(entityHitResult.getEntity()) && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem())) {
            float range = easel.isDiscrete() ? 32.0F : 64.0F;
            return distanceToCameraSq < range * range;
        }
        return false;
    }

    @Override
    protected Component getNameTag(EntityEasel easel) {
        return ItemCanvas.getFullLabel(easel.getItem());
    }

    @Override
    protected void submitNameTag(EaselRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        super.submitNameTag(state, poseStack, collector, cameraState);
        poseStack.popPose();
    }

    public static class RenderEntityEaselFactory implements EntityRendererProvider<EntityEasel> {
        @Override
        public EntityRenderer<EntityEasel, EaselRenderState> create(Context ctx) {
            RenderEntityEasel instance = new RenderEntityEasel(ctx);
            theInstance = instance;
            return instance;
        }
    }
}
