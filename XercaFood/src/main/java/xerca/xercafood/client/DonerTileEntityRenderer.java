package xerca.xercafood.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntityDoner;

public class DonerTileEntityRenderer implements BlockEntityRenderer<BlockEntityDoner> {
    protected static @Nullable BlockRenderDispatcher blockRenderer;

    public DonerTileEntityRenderer(BlockEntityRendererProvider.Context ignoredCtx) {
    }

    @Override
    public void render(BlockEntityDoner blockEntity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockRenderDispatcher renderer = blockRenderer;
        if (renderer == null) {
            renderer = Minecraft.getInstance().getBlockRenderer();
            blockRenderer = renderer;
        }

        float f = blockEntity.getAnimationProgress(partialTicks);
        matrixStackIn.pushPose();

        matrixStackIn.translate(0.5f, 0.f, 0.5f);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(2 * f));
        matrixStackIn.translate(-0.5f, 0.f, -0.5f);

        BlockState bs = blockEntity.getBlockState();

        Blocks.BLOCK_DONER.setRenderType(RenderShape.MODEL);
        renderer.renderSingleBlock(bs, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY);
        Blocks.BLOCK_DONER.setRenderType(RenderShape.ENTITYBLOCK_ANIMATED);

        matrixStackIn.popPose();
    }
}
