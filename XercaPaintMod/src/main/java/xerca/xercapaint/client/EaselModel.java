package xerca.xercapaint.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import xerca.xercapaint.common.entity.EntityEasel;


public class EaselModel extends EntityModel<EntityEasel> {
    private final ModelRenderer bb_main;
    private final ModelRenderer cube_r1;
    private final ModelRenderer cube_r2;
    private final ModelRenderer cube_r3;
    private final ModelRenderer cube_r4;
    private final ModelRenderer cube_r5;

    public EaselModel() {
        texWidth = 16;
        texHeight = 32;

        bb_main = new ModelRenderer(this);
        bb_main.setPos(0.0F, 24.0F, 0.0F);


        cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(5.0F, 0.0F, -5.0F);
        bb_main.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.2618F, 0.0F, 0.0F);
        cube_r1.texOffs(12, 0).addBox(-5.5F, -33.5F, -0.8F, 1.0F, 26.0F, 1.0F, 0.0F, false);

        cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(0.0F, -7.5F, -3.5F);
        bb_main.addChild(cube_r2);
        setRotationAngle(cube_r2, -0.2618F, 0.0F, 0.0F);
        cube_r2.texOffs(2, 2).addBox(-3.0F, -14.0F, -0.5F, 6.0F, 1.0F, 1.0F, 0.0F, false);
        cube_r2.texOffs(0, 0).addBox(-4.5F, -0.5F, -0.5F, 9.0F, 1.0F, 1.0F, 0.0F, false);

        cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(0.0F, 0.0F, 6.0F);
        bb_main.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.2618F, 0.0F, 0.0F);
        cube_r3.texOffs(8, 0).addBox(-0.5F, -21.0F, -1.0F, 1.0F, 21.0F, 1.0F, 0.0F, false);

        cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(5.0F, 0.0F, -5.0F);
        bb_main.addChild(cube_r4);
        setRotationAngle(cube_r4, -0.2618F, 0.0F, -0.1309F);
        cube_r4.texOffs(4, 0).addBox(0.0F, -29.0F, -1.0F, 1.0F, 29.0F, 1.0F, 0.0F, false);

        cube_r5 = new ModelRenderer(this);
        cube_r5.setPos(-5.0F, 0.0F, -5.0F);
        bb_main.addChild(cube_r5);
        setRotationAngle(cube_r5, -0.2618F, 0.0F, 0.1309F);
        cube_r5.texOffs(0, 0).addBox(-1.0F, -29.0F, -1.0F, 1.0F, 29.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(EntityEasel entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}