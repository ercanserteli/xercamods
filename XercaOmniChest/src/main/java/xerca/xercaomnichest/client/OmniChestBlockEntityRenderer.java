package xerca.xercaomnichest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.BlockOmniChest;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;

public final class OmniChestBlockEntityRenderer implements BlockEntityRenderer<BlockEntityOmniChest, OmniChestBlockEntityRenderer.OmniChestRenderState> {
    private static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, Mod.id("entity/chest/omni_chest"));

    private final ChestModel model;
    private final MaterialSet materials;

    public OmniChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.materials = context.materials();
        this.model = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
    }

    @Override
    public OmniChestRenderState createRenderState() {
        return new OmniChestRenderState();
    }

    @Override
    public void extractRenderState(BlockEntityOmniChest blockEntity, OmniChestRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        Direction direction = blockState.hasProperty(BlockOmniChest.FACING) ? blockState.getValue(BlockOmniChest.FACING) : Direction.SOUTH;
        state.angle = direction.toYRot();
        state.open = blockEntity.getOpenNess(partialTick);
    }

    @Override
    public void submit(OmniChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.angle));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float openness = 1.0F - state.open;
        openness = 1.0F - openness * openness * openness;

        collector.submitModel(model, openness, poseStack, MATERIAL.renderType(RenderType::entityCutout),
                state.lightCoords, OverlayTexture.NO_OVERLAY, -1, materials.get(MATERIAL), 0, state.breakProgress);
        poseStack.popPose();
    }

    public static class OmniChestRenderState extends BlockEntityRenderState {
        private float angle;
        private float open;
    }
}
