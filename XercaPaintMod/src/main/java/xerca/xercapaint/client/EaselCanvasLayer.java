package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.RenderProp;

public class EaselCanvasLayer extends RenderLayer<EntityEasel, EaselModel> {
    public EaselCanvasLayer(RenderLayerParent<EntityEasel, EaselModel> p_117183_) {
        super(p_117183_);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int i, EntityEasel entity, float v, float v1, float v2, float v3, float v4, float v5) {
        ItemStack itemstack = entity.getItem();
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
                    poseStack.scale(2F, 2f, 2f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F));
                    poseStack.translate(-0.595, -1.015, -0.5);
                }
            }

            RenderProp.INSTANCE.getCustomRenderer().renderByItem(itemstack, ItemDisplayContext.FIXED, poseStack, bufferSource, i, 0);

            poseStack.popPose();
        }
    }
}
