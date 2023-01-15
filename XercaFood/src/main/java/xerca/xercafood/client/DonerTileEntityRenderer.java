package xerca.xercafood.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntityDoner;

public class DonerTileEntityRenderer implements BlockEntityRenderer<BlockEntityDoner> {
    protected static BlockRenderDispatcher blockRenderer;

    public DonerTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(BlockEntityDoner blockEntity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if(blockRenderer == null) blockRenderer = Minecraft.getInstance().getBlockRenderer();

        float f = blockEntity.getAnimationProgress(partialTicks);
        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5f, 0.f, 0.5f);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(2*f));
        matrixStackIn.translate(-0.5f, 0.f, -0.5f);

        BlockState bs = blockEntity.getBlockState();

        Blocks.BLOCK_DONER.setRenderType(RenderShape.MODEL);
        blockRenderer.renderSingleBlock(bs, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY);
        Blocks.BLOCK_DONER.setRenderType(RenderShape.ENTITYBLOCK_ANIMATED);

        matrixStackIn.popPose();
    }
}
