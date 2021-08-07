package xerca.xercamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.tile_entity.TileEntityDoner;

@OnlyIn(Dist.CLIENT)
public class DonerTileEntityRenderer implements BlockEntityRenderer<TileEntityDoner> {
    protected static BlockRenderDispatcher blockRenderer;

    public DonerTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {
//        ctx.bakeLayer()
    }

    @Override
    public void render(TileEntityDoner tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if(blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRenderer();

        float f = tileEntityIn.getAnimationProgress(partialTicks);
        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5f, 0.f, 0.5f);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(2*f));
        matrixStackIn.translate(-0.5f, 0.f, -0.5f);

        BlockState bs = tileEntityIn.getBlockState();

//        IBakedModel ibakedmodel = blockRenderer.getModelForState(bs);
        Blocks.BLOCK_DONER.setRenderType(RenderShape.MODEL);
        blockRenderer.renderSingleBlock(bs, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY);
        Blocks.BLOCK_DONER.setRenderType(RenderShape.ENTITYBLOCK_ANIMATED);

//        blockRenderer.renderModel(bs, tileEntityIn.getPos(), );


        matrixStackIn.popPose();
    }
}
