package xerca.xercapaint.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityEasel extends EntityRenderer<EntityEasel> implements IEntityRenderer<EntityEasel, EaselModel> {
    protected EaselModel model;
    protected final List<LayerRenderer<EntityEasel, EaselModel>> layers = Lists.newArrayList();
    static public RenderEntityEasel theInstance;
    static private final ResourceLocation woodTexture = new ResourceLocation(XercaPaint.MODID, "textures/block/birch_long.png");

    RenderEntityEasel(EntityRendererManager manager) {
        super(manager);
        this.model = new EaselModel();
        this.layers.add(new EaselCanvasLayer(this));
    }

    @Override
    public EaselModel getModel() {
        return model;
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(EntityEasel entity) {
        return woodTexture;
    }

    @Override
    public void render(EntityEasel entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-entityYaw));

        this.model.setupAnim(entity, 0, 0, 0, 0, 0);

        matrixStackIn.mulPose(fromXYZDegrees(new Vector3f(0, 180, 0)));
        matrixStackIn.translate(0, -1.5, 0);

        RenderType rendertype = this.model.renderType(this.getTextureLocation(entity));
        IVertexBuilder vertexconsumer = bufferIn.getBuffer(rendertype);

        int i = OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
        this.model.renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn, i, 1.0F, 1.0F, 1.0F, 1.0F);

        this.layers.forEach(renderlayer -> renderlayer.render(matrixStackIn, bufferIn, packedLightIn, entity, 0, 0, 0, 0, 0, 0));

        matrixStackIn.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected boolean shouldShowName(EntityEasel easel) {
        RayTraceResult result = Minecraft.getInstance().hitResult;
        if(result instanceof EntityRayTraceResult){
            EntityRayTraceResult entityHitResult = (EntityRayTraceResult) result;
            if (Minecraft.renderNames() && entityHitResult.getEntity() == easel && !easel.getItem().isEmpty() && ItemCanvas.hasTitle(easel.getItem())) {
                double d0 = this.entityRenderDispatcher.distanceToSqr(easel);
                float f = easel.isDiscrete() ? 32.0F : 64.0F;
                return d0 < (double)(f * f);
            }
        }
        return false;
    }

    @Override
    protected void renderNameTag(EntityEasel easel, ITextComponent component, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int p_115087_) {
        poseStack.pushPose();
        poseStack.translate(0, -0.5, 0);
        super.renderNameTag(easel, ItemCanvas.getFullLabel(easel.getItem()), poseStack, bufferSource, p_115087_);
        poseStack.popPose();
    }

    public static class RenderEntityCanvasFactory implements IRenderFactory<EntityEasel> {
        @Override
        public EntityRenderer<? super EntityEasel> createRenderFor(EntityRendererManager manager) {
            theInstance = new RenderEntityEasel(manager);
            return theInstance;
        }
    }

    public static Quaternion fromXYZDegrees(Vector3f p_175226_) {
        return fromXYZ((float)Math.toRadians((double)p_175226_.x()), (float)Math.toRadians((double)p_175226_.y()), (float)Math.toRadians((double)p_175226_.z()));
    }

    private static Quaternion fromXYZ(float p_175219_, float p_175220_, float p_175221_) {
        Quaternion quaternion = Quaternion.ONE.copy();
        quaternion.mul(new Quaternion(0.0F, (float)Math.sin((double)(p_175219_ / 2.0F)), 0.0F, (float)Math.cos((double)(p_175219_ / 2.0F))));
        quaternion.mul(new Quaternion((float)Math.sin((double)(p_175220_ / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(p_175220_ / 2.0F))));
        quaternion.mul(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(p_175221_ / 2.0F)), (float)Math.cos((double)(p_175221_ / 2.0F))));
        return quaternion;
    }
}