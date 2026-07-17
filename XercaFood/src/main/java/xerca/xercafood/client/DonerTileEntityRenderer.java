package xerca.xercafood.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xerca.xercafood.common.block_entity.BlockEntityDoner;

public class DonerTileEntityRenderer implements BlockEntityRenderer<BlockEntityDoner, DonerTileEntityRenderer.DonerRenderState> {
    public DonerTileEntityRenderer(BlockEntityRendererProvider.Context ignoredCtx) {
    }

    @Override
    public DonerRenderState createRenderState() {
        return new DonerRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityDoner blockEntity, DonerRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.animationProgress = blockEntity.getAnimationProgress(partialTick);
    }

    @Override
    public void submit(DonerRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0.f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(2 * state.animationProgress));
        poseStack.translate(-0.5f, 0.f, -0.5f);

        BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state.blockState);
        collector.submitBlockModel(poseStack, ItemBlockRenderTypes.getRenderType(state.blockState), model,
                1.0f, 1.0f, 1.0f, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    public static class DonerRenderState extends BlockEntityRenderState {
        private float animationProgress;
    }
}
