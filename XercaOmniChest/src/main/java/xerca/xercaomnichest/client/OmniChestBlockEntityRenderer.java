package xerca.xercaomnichest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.BlockOmniChest;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;

public final class OmniChestBlockEntityRenderer implements BlockEntityRenderer<BlockEntityOmniChest> {
    private static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, Mod.id("entity/chest/omni_chest"));

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public OmniChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = root.getChild("bottom");
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
    }

    @Override
    public void render(BlockEntityOmniChest blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, Vec3 cameraPos) {
        BlockState state = blockEntity.getBlockState();
        Direction direction = state.hasProperty(BlockOmniChest.FACING) ? state.getValue(BlockOmniChest.FACING) : Direction.SOUTH;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-direction.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        float openness = blockEntity.getOpenNess(partialTick);
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;
        lid.xRot = -(openness * ((float) Math.PI / 2F));
        lock.xRot = lid.xRot;

        VertexConsumer vertexConsumer = MATERIAL.buffer(buffer, RenderType::entityCutout);
        lid.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        lock.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
