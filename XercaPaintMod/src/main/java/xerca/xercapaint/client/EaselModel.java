package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;

public class EaselModel extends EntityModel<EntityEasel> {
    private final ModelPart bb_main;
    private final ModelPart cube_r1;
    private final ModelPart bottomBar;
    private final ModelPart topBar;
    private final ModelPart cube_r3;
    private final ModelPart cube_r4;
    private final ModelPart cube_r5;

    public EaselModel(ModelPart model) {
        this.bb_main = model;
        this.cube_r1 = model.getChild("cube_r1");
        this.bottomBar = model.getChild("bottomBar");
        this.topBar = model.getChild("topBar");
        this.cube_r3 = model.getChild("cube_r3");
        this.cube_r4 = model.getChild("cube_r4");
        this.cube_r5 = model.getChild("cube_r5");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, 24.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        partDefinition.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(12, 0).addBox(-5.5f, -31.5f, -0.8f, 1.0f, 26.0f, 1.0f),
                PartPose.offsetAndRotation(5.0f, 24.0f, -5.0f, -0.2618f, 0.0f, 0.0f));

        partDefinition.addOrReplaceChild("bottomBar", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.5f, -0.5f, -0.5f, 9.0f, 1.0f, 1.0f),
                PartPose.offsetAndRotation(0.0f, 16.1f, -4.0f, -0.2618f, 0.0f, 0.0f));

        partDefinition.addOrReplaceChild("topBar", CubeListBuilder.create()
                        .texOffs(2, 2).addBox(-3.0f, -14.0f, -0.5f, 6.0f, 1.0f, 1.0f),
                PartPose.offsetAndRotation(0.0f, 16.75f, -4.0f, -0.2618f, 0.0f, 0.0f));

        partDefinition.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                        .texOffs(8, 0).addBox(-0.5f, -21.0f, -1.0f, 1.0f, 21.0f, 1.0f),
                PartPose.offsetAndRotation(0.0f, 24.0f, 6.0f, 0.2618f, 0.0f, 0.0f));

        partDefinition.addOrReplaceChild("cube_r4", CubeListBuilder.create()
                        .texOffs(4, 0).addBox(0.0f, -29.0f, -1.0f, 1.0f, 29.0f, 1.0f),
                PartPose.offsetAndRotation(5.0f, 24.0f, -5.0f, -0.2618f, 0.0f, -0.1309f));

        partDefinition.addOrReplaceChild("cube_r5", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.0f, -29.0f, -1.0f, 1.0f, 29.0f, 1.0f),
                PartPose.offsetAndRotation(-5.0f, 24.0f, -5.0f, -0.2618f, 0.0f, 0.1309f));

        return LayerDefinition.create(meshDefinition, 16, 32);
    }

    @Override
    public void setupAnim(EntityEasel entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entity.getItem().getItem() instanceof ItemCanvas itemCanvas){
            if(itemCanvas.getCanvasType() == CanvasType.LONG){
                bottomBar.y = 13.5f;
                bottomBar.z = -3.25f;
                topBar.y = 16.25f;
                topBar.z = -4.0f;
                return;
            }
            if(itemCanvas.getCanvasType() == CanvasType.LARGE || itemCanvas.getCanvasType() == CanvasType.TALL){
                bottomBar.y = 16.5f;
                bottomBar.z = -4.0f;
                topBar.y = 9.8f;
                topBar.z = -2.25f;
                return;
            }
        }

        // Default values
        bottomBar.y = 16.1f;
        bottomBar.z = -4.0f;
        topBar.y = 16.75f;
        topBar.z = -4.0f;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, buffer, packedLight, packedOverlay);
    }
}