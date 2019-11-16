package xerca.xercapaint.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CanvasRenderer implements AutoCloseable {
    private final TextureManager textureManager;
    private final Map<String, Instance> loadedCanvases = Maps.newHashMap();

    public CanvasRenderer(TextureManager textureManagerIn) {
        this.textureManager = textureManagerIn;
    }
    public void updateMapTexture(CompoundNBT textureData) {
        this.getMapRendererInstance(textureData).updateCanvasTexture(textureData);
    }

    public void renderCanvas(CompoundNBT textureData) {
        if(textureData != null && textureData.contains("name")){
            this.getMapRendererInstance(textureData).render();
        }
    }

    private Instance getMapRendererInstance(CompoundNBT textureData) {
        Instance instance = this.loadedCanvases.get(textureData.getString("name"));
        if (instance == null) {
            instance = new Instance(textureData);
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
    public Instance getMapInstanceIfExists(String name) {
        return this.loadedCanvases.get(name);
    }

    /**
     * Clears the currently loaded maps and removes their corresponding textures
     */
    public void clearLoadedMaps() {
        for(Instance instance : this.loadedCanvases.values()) {
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
        private final DynamicTexture canvasTexture;
        private final ResourceLocation location;

        private Instance(CompoundNBT canvasData) {
            this.canvasTexture = new DynamicTexture(16, 16, true);
            this.location = CanvasRenderer.this.textureManager.getDynamicTextureLocation("canvas/" + canvasData.getString("name"), this.canvasTexture);

            updateCanvasTexture(canvasData);
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
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    int k = j + i * 16;

                    canvasTexture.getTextureData().setPixelRGBA(j, i, swapColor(pixels[k]));
                }
            }

            canvasTexture.updateDynamicTexture();
        }

        public void render() {
            GlStateManager.disableLighting();
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float f = 0.0078125F;
            GlStateManager.scalef(f, f, f);
            GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
            GlStateManager.translatef(0.0F, 0.0F, -1.0F);


            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            textureManager.bindTexture(location);
//        GlStateManager.enableBlend();
//        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            GlStateManager.disableAlphaTest();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, 128.0D, (double) -0.01F).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(128.0D, 128.0D, (double) -0.01F).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos(128.0D, 0.0D, (double) -0.01F).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, (double) -0.01F).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.enableAlphaTest();
//        GlStateManager.disableBlend();
        }

        public void close() {
            this.canvasTexture.close();
        }
    }
}
