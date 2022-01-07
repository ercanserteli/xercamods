package xerca.xercapaint.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;

public class EaselCanvasLayer extends LayerRenderer<EntityEasel, EaselModel> {
    public EaselCanvasLayer(IEntityRenderer<EntityEasel, EaselModel> p_117183_) {
        super(p_117183_);
    }

    @Override
    public void render(MatrixStack poseStack, IRenderTypeBuffer bufferSource, int i, EntityEasel entity, float v, float v1, float v2, float v3, float v4, float v5) {
        ItemStack itemstack = entity.getItem();
        if (itemstack.getItem() instanceof ItemCanvas) {
            ItemCanvas itemCanvas = (ItemCanvas)itemstack.getItem();
            poseStack.pushPose();

            switch (itemCanvas.getCanvasType()){
                case SMALL:
                    poseStack.scale(1.5F, 1.5f, 1.5f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.5, -1.17, -0.5);
                    break;
                case LARGE:
//                    poseStack.scale(1.5F, 1.5f, 1.5f);
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.45, -1.015, -0.5);
                    break;
                case LONG:
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.45, -0.915, -0.5);
                    break;
                case TALL:
//                    poseStack.scale(1.75F, 1.75f, 1.75f);
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.595, -1.015, -0.5);
                    break;
            }


            itemCanvas.getItemStackTileEntityRenderer().renderByItem(itemstack, ItemCameraTransforms.TransformType.FIXED, poseStack, bufferSource, i, 0);

            poseStack.popPose();
        }
    }
}
