package xerca.xercamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.entity.EntityCushion;

@OnlyIn(Dist.CLIENT)
public class RenderCushion extends EntityRenderer<EntityCushion> {
    final RenderType renderType = RenderType.solid();

    public RenderCushion(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
    public void render(EntityCushion entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if(entity.block != null){
            BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
            matrixStackIn.pushPose();

            matrixStackIn.translate(0.0D, 0.5D, 0.0D);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees( -90.0F));
            matrixStackIn.translate(-0.5F, -0.5F, 0.5F);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            BlockState bs = entity.block.defaultBlockState();

            blockrendererdispatcher.renderSingleBlock(bs, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.popPose();
        }

        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    public ResourceLocation getTextureLocation(EntityCushion entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}