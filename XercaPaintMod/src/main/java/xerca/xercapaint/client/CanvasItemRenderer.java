package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

import java.util.Set;

public class CanvasItemRenderer implements SpecialModelRenderer<ItemStack> {
    private static final ResourceLocation backLocation = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/birch_planks.png");
    private static final ResourceLocation emptyCanvasLocation = Mod.id("textures/block/empty.png");

    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int combinedLight, int combinedOverlay) {
        if (stack.getItem() instanceof ItemCanvas itemCanvas) {
            boolean rendered = false;
            if(stack.get(Items.CANVAS_PIXELS) != null && RenderEntityCanvas.theInstance != null){
                RenderEntityCanvas.Instance canvasIns = RenderEntityCanvas.theInstance.getCanvasRendererInstance(stack, itemCanvas.getWidth(), itemCanvas.getHeight());
                if(canvasIns != null){
                    canvasIns.render(null, 0, 0, matrixStack, submitNodeCollector, Direction.UP, combinedLight);
                    rendered = true;
                }
            }

            if(!rendered) {
                renderEmptyCanvas(matrixStack, submitNodeCollector, itemCanvas.getWidth(), itemCanvas.getHeight(), combinedLight);
            }
        }
    }

    private void addVertex(VertexConsumer vb, Matrix4f m, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float xOff, float yOff, float zOff)
    {
        vb.addVertex(m, (float) x, (float)y, (float)z).setColor(255, 255, 255, 255).setUv(tx, ty).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightmap).setNormal(pose, xOff, yOff, zOff);
    }

    private void renderEmptyCanvas(PoseStack ms, SubmitNodeCollector submitNodeCollector, float width, float height, int packedLight){
        final float wScale = width/16.0f;
        final float hScale = height/16.0f;

        ms.pushPose();
        Matrix3f mn = ms.last().normal();

        float xOffset = Direction.UP.getStepX();
        float yOffset = Direction.UP.getStepY();
        float zOffset = Direction.UP.getStepZ();

        ms.last().normal().set(mn);

        float f = 1.0f/32.0f;
        ms.translate(0.75, 0.5, 0.5);
        if(wScale > 1 || hScale > 1){
            f /= 3.3f;
        }else{
            f /= 2.0f;
        }

        ms.mulPose(Axis.YP.rotationDegrees(180));

        ms.scale(f, f, f);

        RenderUtil.setShaderTexture(0, emptyCanvasLocation);

        Matrix4f m = ms.last().pose();
        PoseStack.Pose pose = ms.last();
        submitNodeCollector.submitCustomGeometry(ms, RenderType.entitySolid(emptyCanvasLocation), (p, vb) -> {
            // Draw the front
            addVertex(vb, m, pose, 0.0F, 32.0F*hScale, -1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0F*wScale, 32.0F*hScale, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0F*wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

            submitNodeCollector.submitCustomGeometry(ms, RenderType.entitySolid(backLocation), (p2, vb2) -> {
                // Draw the back and sides
                final float sideWidth = 1.0F/16.0F;

                RenderUtil.setShaderTexture(0, backLocation);
                addVertex(vb2, m, pose, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 32.0D*hScale, 1.0D, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 0.0D, 32.0D*hScale, 1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

                // Sides
                addVertex(vb2, m, pose, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 0.0D, 32.0D*hScale, 1.0D, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 0.0D, 32.0D*hScale, -1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);

                addVertex(vb2, m, pose, 0.0D, 32.0D*hScale, 1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 32.0D*hScale, 1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 32.0D*hScale, -1.0F, 1.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 0.0D, 32.0D*hScale, -1.0F, 0.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);

                addVertex(vb2, m, pose, 32.0D*wScale, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 32.0D*hScale, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 32.0D*hScale, 1.0F, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);

                addVertex(vb2, m, pose, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 32.0D*wScale, 0.0D, 1.0F, 1.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);
                addVertex(vb2, m, pose, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);


            });

        });
        ms.popPose();
    }

    @Override
    public void submit(@Nullable ItemStack stack, ItemDisplayContext displayContext, PoseStack matrices, SubmitNodeCollector submitNodeCollector, int light, int overlay, boolean bl, int k) {
        renderByItem(stack, displayContext, matrices, submitNodeCollector, light, overlay);
    }

    @Override
    public void getExtents(Set<Vector3f> set) {

    }

    @Override
    public @Nullable ItemStack extractArgument(ItemStack itemStack) {
        return itemStack;
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @Nullable SpecialModelRenderer<?> bake(BakingContext bakingContext) {
            return new CanvasItemRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}