package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;

public class EaselCanvasLayer extends RenderLayer<EntityEasel.RenderState, EaselModel> {
    public EaselCanvasLayer(RenderLayerParent<EntityEasel.RenderState, EaselModel> p_117183_) {
        super(p_117183_);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, EntityEasel.RenderState entityRenderState, float f, float g) {
        ItemStack itemstack = entityRenderState.getItem();
        if (itemstack.getItem() instanceof ItemCanvas itemCanvas) {
            poseStack.pushPose();

            switch (itemCanvas.getCanvasType()){
                case SMALL -> {
                    poseStack.scale(1.5F, 1.5f, 1.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.5, -1.17, -0.5);
                }
                case LARGE -> {
//                    poseStack.scale(1.5F, 1.5f, 1.5f);
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.45, -1.015, -0.5);
                }
                case LONG -> {
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.45, -0.915, -0.5);
                }
                case TALL -> {
//                    poseStack.scale(1.75F, 1.75f, 1.75f);
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.595, -1.015, -0.5);
                }
            }

            ModClient.CANVAS_ITEM_RENDERER.renderByItem(itemstack, ItemDisplayContext.FIXED, poseStack, bufferSource, i, 0);

            poseStack.popPose();
        }
    }
}
