package xerca.xercapaint.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.ItemCanvas;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CanvasItemRenderer extends BlockEntityWithoutLevelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer
{
    private static final ResourceLocation backLocation = new ResourceLocation("minecraft", "textures/block/birch_planks.png");
    private static final ResourceLocation emptyCanvasLocation = new ResourceLocation(Mod.modId, "textures/block/empty.png");

    public CanvasItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (stack.getItem() instanceof ItemCanvas itemCanvas) {
            CompoundTag nbt = stack.getTag();
            if(nbt != null && RenderEntityCanvas.theInstance != null){
                RenderEntityCanvas.Instance canvasIns = RenderEntityCanvas.theInstance.getCanvasRendererInstance(nbt, itemCanvas.getWidth(), itemCanvas.getHeight());
                if(canvasIns != null){
                    canvasIns.render(null, 0, 0, matrixStack, buffer, Direction.UP, combinedLight);
                }
            }
            else{
                renderEmptyCanvas(matrixStack, buffer, itemCanvas.getWidth(), itemCanvas.getHeight(), combinedLight);
            }
        }
    }

    private void addVertex(VertexConsumer vb, Matrix4f m, Matrix3f mn, double x, double y, double z, float tx, float ty, int lightmap, float xOff, float yOff, float zOff)
    {
        vb.vertex(m, (float) x, (float)y, (float)z).color(255, 255, 255, 255).uv(tx, ty).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(mn, xOff, yOff, zOff).endVertex();
    }

    private void renderEmptyCanvas(PoseStack ms, MultiBufferSource buffer, float width, float height, int packedLight){
        final float wScale = width/16.0f;
        final float hScale = height/16.0f;

        ms.pushPose();
        Matrix3f mn = ms.last().normal().copy();

        float xOffset = Direction.UP.getStepX();
        float yOffset = Direction.UP.getStepY();
        float zOffset = Direction.UP.getStepZ();

        ms.last().normal().load(mn);

        float f = 1.0f/32.0f;
        ms.translate(0.75, 0.5, 0.5);
        if(wScale > 1 || hScale > 1){
            f /= 3.3f;
        }else{
            f /= 2.0f;
        }

        ms.mulPose(Vector3f.YP.rotationDegrees( 180));

        ms.scale(f, f, f);

        RenderSystem.setShaderTexture(0, emptyCanvasLocation);
//        RenderSystem.setShaderColor(1, 1, 1, 1);

        Matrix4f m = ms.last().pose();
        mn = ms.last().normal();
        VertexConsumer vb = buffer.getBuffer(RenderType.entitySolid(emptyCanvasLocation));

        // Draw the front
        addVertex(vb, m, mn, 0.0F, 32.0F*hScale, -1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0F*wScale, 32.0F*hScale, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0F*wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

        vb = buffer.getBuffer(RenderType.entitySolid(backLocation));
        // Draw the back and sides
        final float sideWidth = 1.0F/16.0F;

        RenderSystem.setShaderTexture(0, backLocation);
        addVertex(vb, m, mn, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, 1.0D, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0D, 32.0D*hScale, 1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

        // Sides
        addVertex(vb, m, mn, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0D, 32.0D*hScale, 1.0D, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0D, 32.0D*hScale, -1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);

        addVertex(vb, m, mn, 0.0D, 32.0D*hScale, 1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, 1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, -1.0F, 1.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0D, 32.0D*hScale, -1.0F, 0.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);

        addVertex(vb, m, mn, 32.0D*wScale, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, 1.0F, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);

        addVertex(vb, m, mn, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0D*wScale, 0.0D, 1.0F, 1.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);

        ms.popPose();
    }

    @Override
    public void render(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
    }
}
