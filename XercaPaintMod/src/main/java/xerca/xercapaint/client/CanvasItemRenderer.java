package xerca.xercapaint.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.ItemCanvas;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CanvasItemRenderer extends ItemStackTileEntityRenderer
{
    private static final ResourceLocation backLocation = new ResourceLocation("minecraft", "textures/block/birch_planks.png");
    private static final ResourceLocation emptyCanvasLocation = new ResourceLocation(XercaPaint.MODID, "textures/block/empty.png");

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (stack.getItem() instanceof ItemCanvas) {
            ItemCanvas itemCanvas = (ItemCanvas) stack.getItem();
            CompoundNBT nbt = stack.getTag();
            if(nbt != null){
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

    private void addVertex(IVertexBuilder vb, Matrix4f m, Matrix3f mn, double x, double y, double z, float tx, float ty, int lightmap, float xOff, float yOff, float zOff)
    {
        vb.vertex(m, (float) x, (float)y, (float)z).color(255, 255, 255, 255).uv(tx, ty).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(mn, xOff, yOff, zOff).endVertex();
    }

    private void renderEmptyCanvas(MatrixStack ms, IRenderTypeBuffer buffer, float width, float height, int packedLight){
        final float wScale = width/16.0f;
        final float hScale = height/16.0f;
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();

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

        textureManager.bind(emptyCanvasLocation);

        Matrix4f m = ms.last().pose();
        mn = ms.last().normal();
        IVertexBuilder vb = buffer.getBuffer(RenderType.entitySolid(emptyCanvasLocation));

        // Draw the front
        addVertex(vb, m, mn, 0.0F, 32.0F*hScale, -1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0F*wScale, 32.0F*hScale, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 32.0F*wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
        addVertex(vb, m, mn, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

        vb = buffer.getBuffer(RenderType.entitySolid(backLocation));
        // Draw the back and sides
        final float sideWidth = 1.0F/16.0F;

        textureManager.bind(emptyCanvasLocation);
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
}
