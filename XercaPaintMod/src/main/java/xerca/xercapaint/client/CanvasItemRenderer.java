package xerca.xercapaint.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import xerca.xercapaint.common.item.ItemCanvas;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CanvasItemRenderer extends ItemStackTileEntityRenderer
{
    @Override
//    public void render(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (stack.getItem() instanceof ItemCanvas) {
            CompoundNBT nbt = stack.getTag();
            if(nbt != null){
                ItemCanvas itemCanvas = (ItemCanvas) stack.getItem();
                RenderEntityCanvas.Instance canvasIns = RenderEntityCanvas.theInstance.getCanvasRendererInstance(nbt, itemCanvas.getWidth(), itemCanvas.getHeight());
                if(canvasIns != null){
                    canvasIns.render(null, 0, 0, matrixStack, buffer, Direction.UP, combinedLight);
                }
            }
        }
    }
}
