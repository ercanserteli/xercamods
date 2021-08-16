package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.RenderProp;

public class EaselCanvasLayer extends RenderLayer<EntityEasel, EaselModel> {
    public EaselCanvasLayer(RenderLayerParent<EntityEasel, EaselModel> p_117183_) {
        super(p_117183_);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, EntityEasel entity, float v, float v1, float v2, float v3, float v4, float v5) {
        ItemStack itemstack = entity.getItem();
        if (itemstack.getItem() instanceof ItemCanvas itemCanvas) {
            poseStack.pushPose();

            switch (itemCanvas.getCanvasType()){
                case SMALL -> {
                    poseStack.scale(1.5F, 1.5f, 1.5f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.5, -1.17, -0.5);
                }
                case LARGE -> {
//                    poseStack.scale(1.5F, 1.5f, 1.5f);
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.45, -1.015, -0.5);
                }
                case LONG -> {
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.45, -0.915, -0.5);
                }
                case TALL -> {
//                    poseStack.scale(1.75F, 1.75f, 1.75f);
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.595, -1.015, -0.5);
                }
            }

            RenderProp.INSTANCE.getItemStackRenderer().renderByItem(itemstack, ItemTransforms.TransformType.FIXED, poseStack, bufferSource, i, 0);

            poseStack.popPose();
        }
    }
}
