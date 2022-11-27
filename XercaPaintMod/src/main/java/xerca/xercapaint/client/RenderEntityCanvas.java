package xerca.xercapaint.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityCanvas;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas> {
    static public RenderEntityCanvas theInstance;
    static private final ResourceLocation backLocation = new ResourceLocation("minecraft", "textures/block/birch_planks.png");
    private static final int[] EMPTY_PIXELS;

    static {
        EMPTY_PIXELS = new int[1024];
        for(int i=0; i<1024; i++){
            EMPTY_PIXELS[i] = PaletteUtil.Color.WHITE.rgbVal();
        }
    }

    private final TextureManager textureManager;
    private final Map<String, RenderEntityCanvas.Instance> loadedCanvases = Maps.newHashMap();

    RenderEntityCanvas(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.textureManager = Minecraft.getInstance().textureManager;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(EntityCanvas entity) {
        return getCanvasRendererInstance(entity).location;
    }

    @Override
    public void render(EntityCanvas entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        getCanvasRendererInstance(entity).render(entity, entityYaw, entity.getXRot(), matrixStackIn, bufferIn, entity.getDirection(), packedLightIn);
    }


    public static class RenderEntityCanvasFactory implements EntityRendererProvider<EntityCanvas> {
        @Override
        public @NotNull EntityRenderer<EntityCanvas> create(Context ctx) {
            theInstance = new RenderEntityCanvas(ctx);
            return theInstance;
        }
    }

    private RenderEntityCanvas.Instance getCanvasRendererInstance(EntityCanvas canvas) {
        return getCanvasRendererInstance(canvas.getCanvasName(), canvas.getCanvasVersion(), canvas.getWidth(), canvas.getHeight());
    }

    RenderEntityCanvas.Instance getCanvasRendererInstance(CompoundTag tag, int width, int height) {
        String name = tag.getString("name");
        int version = tag.getInt("v");
        if(!EntityCanvas.PICTURES.containsKey(name) || EntityCanvas.PICTURES.get(name).version < version){
            EntityCanvas.PICTURES.put(name, new EntityCanvas.Picture(version, tag.getIntArray("pixels")));
        }
        return getCanvasRendererInstance(name, version, width, height);
    }

    RenderEntityCanvas.Instance getCanvasRendererInstance(String name, int version, int width, int height) {
        RenderEntityCanvas.Instance instance = this.loadedCanvases.get(name);
        if (instance == null) {
            instance = new Instance(name, version, width, height);
            this.loadedCanvases.put(name, instance);
        }else{
            if(instance.version < version || !instance.loaded){
                instance.updateCanvasTexture(name, version);
            }
        }

        return instance;
    }

    @OnlyIn(Dist.CLIENT)
    public class Instance implements AutoCloseable {
        int version = 0;
        final int width;
        final int height;
        boolean loaded;
        boolean started;
        public final DynamicTexture canvasTexture;
        public final ResourceLocation location;

        private Instance(String name, int version, int width, int height) {
            this.started = false;
            this.loaded = false;
            this.width = width;
            this.height = height;
            this.canvasTexture = new DynamicTexture(width, height, true);
            this.location = RenderEntityCanvas.this.textureManager.register("canvas/" + name, this.canvasTexture);

            updateCanvasTexture(name, version);
        }

        private int swapColor(int color){
            int i = (color & 16711680) >> 16;
            int j = (color & '\uff00') >> 8;
            int k = (color & 255);
            return k << 16 | j << 8 | i | 0xff000000;
        }

        private void updateCanvasTexture(String name, int version) {
            this.version = version;
            int[] pixels = EMPTY_PIXELS;
            if(EntityCanvas.PICTURES.containsKey(name)){
                pixels = EntityCanvas.PICTURES.get(name).pixels;
                loaded = true;
            }
            if(loaded || !started){
                if(pixels.length < height*width){
                    XercaPaint.LOGGER.warn("Pixels array length (" + pixels.length + ") is smaller than canvas area (" + height*width + ")");
                    return;
                }

                NativeImage image = canvasTexture.getPixels();
                if(image != null) {
                    for (int i = 0; i < height; ++i) {
                        for (int j = 0; j < width; ++j) {
                            int k = j + i * width;
                            image.setPixelRGBA(j, i, swapColor(pixels[k]));
                        }
                    }
                }

                canvasTexture.upload();
            }
            this.started = true;
        }

        public void render(@Nullable EntityCanvas canvas, float yaw, float pitch, PoseStack ms, MultiBufferSource buffer, Direction facing, int packedLight) {
            final float wScale = width/16.0f;
            final float hScale = height/16.0f;

            ms.pushPose();
            Matrix3f mn = ms.last().normal().copy();

            float xOffset = facing.getStepX();
            float yOffset = facing.getStepY();
            float zOffset = facing.getStepZ();

            if(canvas != null && canvas.getRotation() > 0) {
                ms.mulPose(Vector3f.XP.rotationDegrees( pitch));
                ms.mulPose(Vector3f.YP.rotationDegrees( 180-yaw));
                ms.mulPose(Vector3f.ZP.rotationDegrees(90*canvas.getRotation()));
                ms.mulPose(Vector3f.YP.rotationDegrees( -180+yaw));
                ms.mulPose(Vector3f.XP.rotationDegrees( -pitch));
            }
            ms.last().normal().load(mn);

            float f = 1.0f/32.0f;
            if(canvas != null) {
                if (facing.getAxis().isHorizontal()) {
                    ms.translate(zOffset * 0.5d * wScale, -0.5d * hScale, -xOffset * 0.5d * wScale);
                } else {
                    ms.translate(0.5 * wScale, 0 * hScale, (yOffset > 0 ? 0.5 : -0.5) * wScale);
                }
            }
            else{
                ms.translate(0.75, 0.5, 0.5);
                if(wScale > 1 || hScale > 1){
                    f /= 3.3f;
                }else{
                    f /= 2.0f;
                }
            }
            ms.mulPose(Vector3f.XP.rotationDegrees( pitch));
            ms.mulPose(Vector3f.YP.rotationDegrees( 180-yaw));

            ms.scale(f, f, f);

            RenderSystem.setShaderTexture(0, location);

            Matrix4f m = ms.last().pose();
            mn = ms.last().normal();
            VertexConsumer vb = buffer.getBuffer(RenderType.entitySolid(location));

            // Draw the front
            addVertex(vb, m, mn, 0.0F, 32.0F*hScale, -1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0F*wScale, 32.0F*hScale, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0F*wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

            vb = buffer.getBuffer(RenderType.entitySolid(backLocation));
            // Draw the back and sides
            final float sideWidth = 1.0F/16.0F;
//            textureManager.bind(backLocation);
            RenderSystem.setShaderTexture(0, backLocation);
            addVertex(vb, m, mn, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, 1.0D, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 0.0D, 32.0D*hScale, 1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

            // Sides
            addVertex(vb, m, mn, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 0.0D, 32.0D*hScale, 1.0D, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 0.0D, 32.0D*hScale, -1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);

            addVertex(vb, m, mn, 0.0D, 32.0D*hScale, 1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, 1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, -1.0F, 1.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 0.0D, 32.0D*hScale, -1.0F, 0.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);

            addVertex(vb, m, mn, 32.0D*wScale, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 32.0D*hScale, 1.0F, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);

            addVertex(vb, m, mn, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 32.0D*wScale, 0.0D, 1.0F, 1.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, mn, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);

            ms.popPose();
        }

        private void addVertex(VertexConsumer vb, Matrix4f m, Matrix3f mn, double x, double y, double z, float tx, float ty, int lightmap, float xOff, float yOff, float zOff)
        {
            vb.vertex(m, (float) x, (float)y, (float)z).color(255, 255, 255, 255).uv(tx, ty).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(mn, xOff, yOff, zOff).endVertex();
        }

        public void close() {
            this.canvasTexture.close();
        }
    }
}