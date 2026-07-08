package xerca.xercapaint.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

public class CanvasItemRenderer {
    private static final ResourceLocation BACK_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/birch_planks.png");
    private static final ResourceLocation EMPTY_CANVAS_LOCATION = Mod.id("textures/block/empty.png");
    private static final ResourceLocation GLASS_FRAME_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/glass.png");
    private static final int GLASS_INVENTORY_TINT = 0xFFDCE6FF;

    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (stack.getItem() instanceof ItemCanvas itemCanvas) {
            RenderEntityCanvas.Instance canvasIns = null;
            if (stack.get(Items.CANVAS_PIXELS) != null && RenderEntityCanvas.theInstance != null) {
                canvasIns = RenderEntityCanvas.theInstance.getCanvasRendererInstance(stack, itemCanvas.getWidth(), itemCanvas.getHeight());
            }
            renderCanvas(canvasIns, itemCanvas.getWidth(), itemCanvas.getHeight(), itemCanvas.isGlass(), displayContext, matrixStack, buffer, combinedLight);
        }
    }

    void renderCanvas(@Nullable RenderEntityCanvas.Instance canvasIns, int width, int height, boolean glass, ItemDisplayContext displayContext, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight) {
        if (canvasIns != null) {
            int tint = (glass && displayContext == ItemDisplayContext.GUI) ? GLASS_INVENTORY_TINT : RenderEntityCanvas.NO_TINT;
            canvasIns.render(false, 0, 0, 0, matrixStack, buffer, Direction.UP, combinedLight, glass, tint);
        } else {
            renderEmptyCanvas(matrixStack, buffer, width, height, combinedLight, glass);
        }
    }

    private void addVertex(VertexConsumer vb, Matrix4f m, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float xOff, float yOff, float zOff) {
        vb.addVertex(m, (float) x, (float) y, (float) z).setColor(255, 255, 255, 255).setUv(tx, ty).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightmap).setNormal(pose, xOff, yOff, zOff);
    }

    private void renderEmptyCanvas(PoseStack ms, MultiBufferSource buffer, float width, float height, int packedLight, boolean glass) {
        final float wScale = width / 16.0f;
        final float hScale = height / 16.0f;

        ms.pushPose();

        float f = 1.0f / 32.0f;
        ms.translate(0.75, 0.5, 0.5);
        if (wScale > 1 || hScale > 1) {
            f /= 3.3f;
        } else {
            f /= 2.0f;
        }

        ms.mulPose(Axis.YP.rotationDegrees(180));

        ms.scale(f, f, f);

        Matrix4f m = ms.last().pose();
        PoseStack.Pose pose = ms.last();

        final float w32 = 32.0F * wScale;
        final float h32 = 32.0F * hScale;

        if (glass) {
            // Empty glass canvas: transparent front and back, only the glass-pane frame on the edges
            renderGlassFrame(buffer, m, pose, w32, h32, packedLight);
            ms.popPose();
            return;
        }

        RenderSystem.setShaderTexture(0, EMPTY_CANVAS_LOCATION);

        VertexConsumer vb = buffer.getBuffer(RenderType.entitySolid(EMPTY_CANVAS_LOCATION));

        // Draw the front (normal -Z)
        addVertex(vb, m, pose, 0.0F, h32, -1.0F, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
        addVertex(vb, m, pose, w32, h32, -1.0F, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
        addVertex(vb, m, pose, w32, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);
        addVertex(vb, m, pose, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);

        vb = buffer.getBuffer(RenderType.entitySolid(BACK_LOCATION));
        // Draw the back and sides
        final float sideWidth = 1.0F / 16.0F;

        RenderSystem.setShaderTexture(0, BACK_LOCATION);
        // BACK (normal +Z)
        addVertex(vb, m, pose, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
        addVertex(vb, m, pose, w32, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
        addVertex(vb, m, pose, w32, h32, 1.0D, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);
        addVertex(vb, m, pose, 0.0D, h32, 1.0D, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);

        // LEFT SIDE (x = 0, normal -X)
        addVertex(vb, m, pose, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, 0.0D, h32, 1.0D, sideWidth, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, 0.0D, h32, -1.0D, 0.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);

        // TOP SIDE (y = h32, normal +Y)
        addVertex(vb, m, pose, 0.0D, h32, 1.0F, 0.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
        addVertex(vb, m, pose, w32, h32, 1.0F, 1.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
        addVertex(vb, m, pose, w32, h32, -1.0F, 1.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);
        addVertex(vb, m, pose, 0.0D, h32, -1.0F, 0.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);

        // RIGHT SIDE (x = w32, normal +X)
        addVertex(vb, m, pose, w32, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, w32, h32, -1.0F, 0.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, w32, h32, 1.0F, sideWidth, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, w32, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);

        // BOTTOM SIDE (y = 0, normal -Y)
        addVertex(vb, m, pose, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
        addVertex(vb, m, pose, w32, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
        addVertex(vb, m, pose, w32, 0.0D, 1.0F, 1.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);
        addVertex(vb, m, pose, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);

        ms.popPose();
    }

    /**
     * Renders the four glass-canvas edges using the vanilla glass-pane texture. Mirrors
     * renderGlassFramefor the empty-canvas item path.
     */
    private void renderGlassFrame(MultiBufferSource buffer, Matrix4f m, PoseStack.Pose pose, float w32, float h32, int packedLight) {
        RenderSystem.setShaderTexture(0, GLASS_FRAME_LOCATION);
        VertexConsumer vb = buffer.getBuffer(RenderType.entityTranslucent(GLASS_FRAME_LOCATION));
        final double eps = 0.001;
        final float depth = 1.0F / 16.0F;
        // LEFT (x = 0, normal -X)
        addVertex(vb, m, pose, eps, eps, 1.0D, 0.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, eps, h32 - eps, 1.0D, 0.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, eps, h32 - eps, -1.0D, depth, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, eps, eps, -1.0D, depth, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
        // TOP (y = h32, normal +Y)
        addVertex(vb, m, pose, eps, h32 - eps, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
        addVertex(vb, m, pose, w32 - eps, h32 - eps, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
        addVertex(vb, m, pose, w32 - eps, h32 - eps, -1.0D, 1.0F, depth, packedLight, 0.0F, 1.0F, 0.0F);
        addVertex(vb, m, pose, eps, h32 - eps, -1.0D, 0.0F, depth, packedLight, 0.0F, 1.0F, 0.0F);
        // RIGHT (x = w32, normal +X)
        addVertex(vb, m, pose, w32 - eps, eps, -1.0D, 0.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, w32 - eps, h32 - eps, -1.0D, 0.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, w32 - eps, h32 - eps, 1.0D, depth, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
        addVertex(vb, m, pose, w32 - eps, eps, 1.0D, depth, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
        // BOTTOM (y = 0, normal -Y)
        addVertex(vb, m, pose, eps, eps, -1.0D, 0.0F, 0.0F, packedLight, 0.0F, -1.0F, 0.0F);
        addVertex(vb, m, pose, w32 - eps, eps, -1.0D, 1.0F, 0.0F, packedLight, 0.0F, -1.0F, 0.0F);
        addVertex(vb, m, pose, w32 - eps, eps, 1.0D, 1.0F, depth, packedLight, 0.0F, -1.0F, 0.0F);
        addVertex(vb, m, pose, eps, eps, 1.0D, 0.0F, depth, packedLight, 0.0F, -1.0F, 0.0F);
    }
}
