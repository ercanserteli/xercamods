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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;

import java.util.List;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class RenderEntityEasel extends EntityRenderer<EntityEasel, EntityEasel.RenderState> implements RenderLayerParent<EntityEasel.RenderState, EaselModel> {
    protected final EaselModel model;
    protected final List<RenderLayer<EntityEasel.RenderState, EaselModel>> layers = Lists.newArrayList();
    static public RenderEntityEasel theInstance;
    static private final ResourceLocation woodTexture = Mod.id("textures/block/birch_long.png");

    RenderEntityEasel(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new EaselModel(ctx.bakeLayer(ModClient.EASEL_MAIN_LAYER));
        this.layers.add(new EaselCanvasLayer(this));
    }

    @Override
    public EntityEasel.RenderState createRenderState() {
        return new EntityEasel.RenderState();
    }

    @Override
    public void extractRenderState(EntityEasel entity, EntityEasel.RenderState entityRenderState, float f) {
        entityRenderState.setEntityYaw(entity.getXRot());
        entityRenderState.setItem(entity.getItem());
    }

    @Override
    public @NotNull EaselModel getModel() {
        return this.model;
    }

    public @NotNull ResourceLocation getTextureLocation(EntityEasel.RenderState entity) {
        return woodTexture;
    }


    @Override
    public void render(EntityEasel.RenderState entityRenderState, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-entityRenderState.getEntityYaw()));

        this.model.setupAnim(entityRenderState);

        matrixStackIn.mulPose((new Quaternionf()).rotationXYZ((float) Math.PI, 0, 0));
        matrixStackIn.translate(0, -1.5, 0);

        RenderType rendertype = this.model.renderType(this.getTextureLocation(entityRenderState));
        VertexConsumer vertexconsumer = bufferIn.getBuffer(rendertype);

        int i = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
        this.model.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, i);

        this.layers.forEach(renderlayer -> renderlayer.render(matrixStackIn, bufferIn, packedLightIn, entityRenderState, 0, 0));

        matrixStackIn.popPose();
        super.render(entityRenderState, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel, double distance) {
        HitResult result = Minecraft.getInstance().hitResult;
        if(result instanceof EntityHitResult entityHitResult){
            if (Minecraft.renderNames() && entityHitResult.getEntity() == easel && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem())) {
                float f = easel.isDiscrete() ? 32.0F : 64.0F;
                return distance < (double)(f * f);
            }
        }
        return false;
    }

    @Override
    protected void renderNameTag(EntityEasel.RenderState easel, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        super.renderNameTag(easel, ItemCanvas.getFullLabel(easel.getItem()), poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    public static class RenderEntityEaselFactory implements EntityRendererProvider<EntityEasel> {
        @Override
        public @NotNull EntityRenderer<EntityEasel, EntityEasel.RenderState> create(Context ctx) {
            theInstance = new RenderEntityEasel(ctx);
            return theInstance;
        }
    }
}