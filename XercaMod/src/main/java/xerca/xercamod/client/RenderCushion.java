package xerca.xercamod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.entity.EntityCushion;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RenderCushion extends EntityRenderer<EntityCushion> {
    final RenderType renderType = RenderType.getSolid();

    public RenderCushion(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
    public void render(EntityCushion entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if(entity.block != null){
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            matrixStackIn.push();

            matrixStackIn.translate(0.0D, 0.5D, 0.0D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees( -90.0F));
            matrixStackIn.translate(-0.5F, -0.5F, 0.5F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
            BlockState bs = entity.block.getDefaultState();

            blockrendererdispatcher.renderBlock(bs, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.pop();
        }

        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    public ResourceLocation getEntityTexture(EntityCushion entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}