package xerca.xercapaint.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    private static final ResourceLocation woodTexture = Mod.id("textures/block/birch_long.png");

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
    public void render(EaselRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(-state.entityYaw));

        this.model.setupAnim(state);

        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.PI, 0, 0));
        poseStack.translate(0, -1.5, 0);

        RenderType renderType = this.model.renderType(woodTexture);
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        int overlay = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, overlay);

        for (RenderLayer<EaselRenderState, EaselModel> layer : layers) {
            layer.render(poseStack, buffer, packedLight, state, 0, 0);
        }

        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel, double distanceSquared) {
        HitResult result = Minecraft.getInstance().hitResult;
        if (result instanceof EntityHitResult entityHitResult) {
            if (Minecraft.renderNames() && entityHitResult.getEntity() == easel && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem())) {
                float range = easel.isDiscrete() ? 32.0F : 64.0F;
                return distanceSquared < (double) (range * range);
            }
        }
        return false;
    }

    @Override
    protected void renderNameTag(EaselRenderState state, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        super.renderNameTag(state, ItemCanvas.getFullLabel(state.itemStack), poseStack, buffer, packedLight);
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
