package xerca.xercamod.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.entity.EntityCushion;

@OnlyIn(Dist.CLIENT)
public class RenderCushion extends EntityRenderer<EntityCushion> {
    public RenderCushion(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityCushion entity, double x, double y, double z, float entityYaw, float partialTicks) {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y + 0.5F, (float) z);

        this.bindEntityTexture(entity);
        GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
        if(entity.block != null){
            blockrendererdispatcher.renderBlockBrightness(entity.block.getDefaultState(), entity.getBrightness());
        }
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
            if(entity.block != null) {
                blockrendererdispatcher.renderBlockBrightness(entity.block.getDefaultState(), 1.0F);
            }
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCushion entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}