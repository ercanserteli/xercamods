package xerca.xercacushion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercacushion.entity.EntityCushion;

public class CushionRenderer extends EntityRenderer<EntityCushion, CushionRenderer.CushionRenderState> {
    public CushionRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public CushionRenderState createRenderState() {
        return new CushionRenderState();
    }

    @Override
    public void extractRenderState(EntityCushion entity, CushionRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.blockState = entity.getVariantBlock().defaultBlockState();
    }

    @Override
    public void render(CushionRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        dispatcher.renderSingleBlock(state.blockState, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }

    public static class CushionRenderState extends EntityRenderState {
        private BlockState blockState = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
    }
}
