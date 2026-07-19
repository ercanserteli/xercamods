package xerca.xercafood.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import xerca.xercafood.common.block_entity.BlockEntityDoner;

public class DonerTileEntityRenderer implements BlockEntityRenderer<BlockEntityDoner, DonerTileEntityRenderer.DonerRenderState> {
    public DonerTileEntityRenderer(BlockEntityRendererProvider.Context ignoredCtx) {
    }

    @Override
    public DonerRenderState createRenderState() {
        return new DonerRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityDoner blockEntity, DonerRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, breakProgress);
        state.animationProgress = blockEntity.getAnimationProgress(partialTick);
        state.movingBlockRenderState.randomSeedPos = blockEntity.getBlockPos();
        state.movingBlockRenderState.blockPos = blockEntity.getBlockPos();
        state.movingBlockRenderState.blockState = blockEntity.getBlockState();
        if (blockEntity.getLevel() instanceof ClientLevel clientLevel) {
            state.movingBlockRenderState.biome = clientLevel.getBiome(blockEntity.getBlockPos());
            state.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
            state.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
        }
    }

    @Override
    public void submit(DonerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0.f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(2 * state.animationProgress));
        poseStack.translate(-0.5f, 0.f, -0.5f);

        collector.submitMovingBlock(poseStack, state.movingBlockRenderState, 0);

        poseStack.popPose();
    }

    public static class DonerRenderState extends BlockEntityRenderState {
        private float animationProgress;
        final MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
    }
}
