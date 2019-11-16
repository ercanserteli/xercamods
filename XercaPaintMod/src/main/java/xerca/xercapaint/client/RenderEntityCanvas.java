package xerca.xercapaint.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xerca.xercapaint.common.entity.EntityCanvas;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas> {
    static private final ResourceLocation backLocation = new ResourceLocation("minecraft", "textures/block/birch_planks.png");

    private final TextureManager textureManager;
    private final Map<String, RenderEntityCanvas.Instance> loadedCanvases = Maps.newHashMap();

    RenderEntityCanvas(EntityRendererManager renderManager) {
        super(renderManager);
        this.textureManager = Minecraft.getInstance().textureManager;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCanvas entity) {
        return getMapRendererInstance(entity).location;
    }

    @Override
    public void doRender(EntityCanvas entity, double x, double y, double z, float yaw, float partialTick) {
        CompoundNBT tag = entity.getCanvasNBT();
        if(tag != null && tag.contains("name") ){
            getMapRendererInstance(entity).render(x, y, z, yaw, partialTick, entity.getHorizontalFacing());
        }
        super.doRender(entity, x, y, z, yaw, partialTick);
    }


    public static class RenderEntityCanvasFactory implements IRenderFactory<EntityCanvas> {
        @Override
        public EntityRenderer<? super EntityCanvas> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityCanvas(manager);
        }
    }


//    public void updateMapTexture(CompoundNBT textureData) {
//        this.getMapRendererInstance(textureData).updateCanvasTexture(textureData);
//    }

    private RenderEntityCanvas.Instance getMapRendererInstance(EntityCanvas canvas) {
        CompoundNBT textureData = canvas.getCanvasNBT();
        RenderEntityCanvas.Instance instance = this.loadedCanvases.get(textureData.getString("name"));
        if (instance == null) {
            instance = new Instance(canvas);
            this.loadedCanvases.put(textureData.getString("name"), instance);
        }else{
            int currentVersion = textureData.getInt("v");
            if(instance.version != currentVersion){
                instance.updateCanvasTexture(textureData);
            }
        }

        return instance;
    }

    @Nullable
    public RenderEntityCanvas.Instance getMapInstanceIfExists(String name) {
        return this.loadedCanvases.get(name);
    }

    /**
     * Clears the currently loaded maps and removes their corresponding textures
     */
    public void clearLoadedMaps() {
        for(RenderEntityCanvas.Instance instance : this.loadedCanvases.values()) {
            instance.close();
        }

        this.loadedCanvases.clear();
    }

    public void close() {
        this.clearLoadedMaps();
    }

    @OnlyIn(Dist.CLIENT)
    class Instance implements AutoCloseable {
        int version = 0;
        int width;
        int height;
        private final DynamicTexture canvasTexture;
        private final ResourceLocation location;

        private Instance(EntityCanvas canvas) {
            CompoundNBT tag = canvas.getCanvasNBT();
            this.width = canvas.getWidthPixels();
            this.height = canvas.getHeightPixels();
            this.canvasTexture = new DynamicTexture(width, height, true);
            this.location = RenderEntityCanvas.this.textureManager.getDynamicTextureLocation("canvas/" + tag.getString("name"), this.canvasTexture);

            updateCanvasTexture(tag);
        }

        private int swapColor(int color){
            int i = (color & 16711680) >> 16;
            int j = (color & '\uff00') >> 8;
            int k = (color & 255);
            return k << 16 | j << 8 | i;
        }

        private void updateCanvasTexture(CompoundNBT textureData) {
            this.version = textureData.getInt("v");

            int[] pixels = textureData.getIntArray("pixels");
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    int k = j + i * width;

                    canvasTexture.getTextureData().setPixelRGBA(j, i, swapColor(pixels[k]));
                }
            }

            canvasTexture.updateDynamicTexture();
        }

        public void render(double x, double y, double z, float yaw, float partialTick, Direction facing) {
            final float wScale = width/16.0f;
            final float hScale = height/16.0f;

            GlStateManager.pushMatrix();
            final float xOffset = facing.getXOffset();
            final float zOffset = facing.getZOffset();
            final float yOffset = -1.0f;
            if(wScale == 1.0f && hScale == 1.0f){
                GlStateManager.translated(x + zOffset*0.5d, y + yOffset*0.5f, z - xOffset*0.5d);
            }else{
                GlStateManager.translated(x + zOffset, y + yOffset, z - xOffset);
            }

            GlStateManager.rotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
//            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();

            float f = 1.0f/32.0f;
            GlStateManager.scalef(f, f, f);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            textureManager.bindTexture(location);
            GlStateManager.disableAlphaTest();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

            // Draw the front
//            bufferbuilder.pos(0.0D, 32.0D*hScale, -1.0D).tex(1.0D, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
//            bufferbuilder.pos(32.0D*wScale, 32.0D*hScale, -1.0D).tex(0.0D, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
//            bufferbuilder.pos(32.0D*wScale, 0.0D, -1.0D).tex(0.0D, 1.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
//            bufferbuilder.pos(0.0D, 0.0D, -1.0D).tex(1.0D, 1.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
            bufferbuilder.pos(0.0D, 32.0D*hScale, -1.0D).tex(1.0D, 0.0D).normal(xOffset, 0.0F, zOffset).endVertex();
            bufferbuilder.pos(32.0D*wScale, 32.0D*hScale, -1.0D).tex(0.0D, 0.0D).normal(xOffset, 0.0F, zOffset).endVertex();
            bufferbuilder.pos(32.0D*wScale, 0.0D, -1.0D).tex(0.0D, 1.0D).normal(xOffset, 0.0F, zOffset).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -1.0D).tex(1.0D, 1.0D).normal(xOffset, 0.0F, zOffset).endVertex();
            tessellator.draw();

            // Draw the back and sides
            final double sideWidth = 1.0D/16.0D;
            bufferbuilder = tessellator.getBuffer();
            textureManager.bindTexture(backLocation);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, 0.0D, 1.0F).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 0.0D, 1.0F).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 32.0D*hScale, 1.0F).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos(0.0D, 32.0D*hScale, 1.0F).tex(0.0D, 1.0D).endVertex();

            // Sides
            bufferbuilder.pos(0.0D, 0.0D, 1.0F).tex(sideWidth, 0.0D).endVertex();
            bufferbuilder.pos(0.0D, 32.0D*hScale, 1.0F).tex(sideWidth, 1.0D).endVertex();
            bufferbuilder.pos(0.0D, 32.0D*hScale, -1.0F).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -1.0F).tex(0.0D, 0.0D).endVertex();

            bufferbuilder.pos(0.0D, 32.0D*hScale, 1.0F).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 32.0D*hScale, 1.0F).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 32.0D*hScale, -1.0F).tex(1.0D, sideWidth).endVertex();
            bufferbuilder.pos(0.0D, 32.0D*hScale, -1.0F).tex(0.0D, sideWidth).endVertex();

            bufferbuilder.pos(32.0D*wScale, 0.0D, -1.0F).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 32.0D*hScale, -1.0F).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 32.0D*hScale, 1.0F).tex(sideWidth, 1.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 0.0D, 1.0F).tex(sideWidth, 0.0D).endVertex();

            bufferbuilder.pos(0.0D, 0.0D, -1.0F).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 0.0D, -1.0F).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos(32.0D*wScale, 0.0D, 1.0F).tex(1.0D, 1.0D-sideWidth).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, 1.0F).tex(0.0D, 1.0D-sideWidth).endVertex();

            tessellator.draw();
            GlStateManager.enableAlphaTest();

//            GlStateManager.enableLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }

        public void close() {
            this.canvasTexture.close();
        }
    }
}