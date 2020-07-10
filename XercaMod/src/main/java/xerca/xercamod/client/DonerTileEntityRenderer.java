package xerca.xercamod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.tile_entity.TileEntityDoner;

@OnlyIn(Dist.CLIENT)
public class DonerTileEntityRenderer extends TileEntityRenderer<TileEntityDoner> {
    protected static BlockRendererDispatcher blockRenderer;

    public DonerTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityDoner tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if(blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();

        float f = tileEntityIn.getAnimationProgress(partialTicks);
        matrixStackIn.push();

        matrixStackIn.translate(0.5f, 0.f, 0.5f);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(2*f));
        matrixStackIn.translate(-0.5f, 0.f, -0.5f);

        BlockState bs = tileEntityIn.getBlockState();

//        IBakedModel ibakedmodel = blockRenderer.getModelForState(bs);
        Blocks.BLOCK_DONER.setRenderType(BlockRenderType.MODEL);
        blockRenderer.renderBlock(bs, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY);
        Blocks.BLOCK_DONER.setRenderType(BlockRenderType.ENTITYBLOCK_ANIMATED);

//        blockRenderer.renderModel(bs, tileEntityIn.getPos(), );


        matrixStackIn.pop();
    }
}
