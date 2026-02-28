package xerca.xercapaint.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityCanvas;
import xerca.xercapaint.common.item.ItemCanvas;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas> {
    public static RenderEntityCanvas theInstance;
    private static final ResourceLocation backLocation = new ResourceLocation("minecraft", "textures/block/birch_planks.png");
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
        if (!ItemCanvas.hasCanvasData(tag, width, height)) {
            return null;
        }

        String name = tag.getString("name");
        int version = tag.getInt("v");
        int[] pixels = tag.getIntArray("pixels");

        EntityCanvas.PICTURES.compute(name, (n, existing) ->
                (existing == null || existing.version < version) ? new EntityCanvas.Picture(version, pixels) : existing
        );

        return getCanvasRendererInstance(name, version, width, height);
    }

    private static String rendererKey(String name, int width, int height) {
        return name + "-" + width + "x" + height;
    }

    RenderEntityCanvas.Instance getCanvasRendererInstance(String name, int version, int width, int height) {
        String key = rendererKey(name, width, height);
        RenderEntityCanvas.Instance instance = this.loadedCanvases.get(key);
        if (instance == null) {
            instance = new Instance(key, name, version, width, height);
            this.loadedCanvases.put(key, instance);
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

        private Instance(String key, String name, int version, int width, int height) {
            this.started = false;
            this.loaded = false;
            this.width = width;
            this.height = height;
            this.canvasTexture = new DynamicTexture(width, height, true);
            this.location = RenderEntityCanvas.this.textureManager.register("canvas/" + key, this.canvasTexture);

            updateCanvasTexture(name, version);
        }

        private int swapColor(int color){
            int i = (color & 16711680) >> 16;
            int j = (color & '\uff00') >> 8;
            int k = (color & 255);
            return k << 16 | j << 8 | i | 0xff000000;
        }

        private void updateCanvasTexture(String name, int version) {
            int[] pixels = EMPTY_PIXELS;
            boolean hasPicture = EntityCanvas.PICTURES.containsKey(name);
            if (hasPicture) {
                pixels = EntityCanvas.PICTURES.get(name).pixels;
                loaded = true;
            }
            if (loaded || !started) {
                if (pixels.length < width * height) {
                    XercaPaint.LOGGER.warn("Pixels array length ({}) is smaller than canvas area ({})", pixels.length, height * width);
                    return;
                }

                NativeImage image = canvasTexture.getPixels();
                if (image != null) {
                    for (int y = 0; y < height; ++y) {
                        for (int x = 0; x < width; ++x) {
                            int idx = x + y * width;
                            image.setPixelRGBA(x, y, swapColor(pixels[idx]));
                        }
                    }
                    canvasTexture.upload();
                    this.version = version;
                    this.started = true;
                }
            }
        }

        public void render(@Nullable EntityCanvas canvas, float yaw, float pitch, PoseStack ms, MultiBufferSource buffer, Direction facing, int packedLight) {
            final float wScale = width / 16.0f;
            final float hScale = height / 16.0f;

            ms.pushPose();

            float xOffset = facing.getStepX();
            float yOffset = facing.getStepY();
            float zOffset = facing.getStepZ();

            if (canvas != null && canvas.getRotation() > 0) {
                ms.mulPose(Axis.XP.rotationDegrees(pitch));
                ms.mulPose(Axis.YP.rotationDegrees(180.f - yaw));
                ms.mulPose(Axis.ZP.rotationDegrees(90.f * canvas.getRotation()));
                ms.mulPose(Axis.YP.rotationDegrees(-180.f + yaw));
                ms.mulPose(Axis.XP.rotationDegrees(-pitch));
            }

            float f = 1.0f / 32.0f;
            if (canvas != null) {
                if (facing.getAxis().isHorizontal()) {
                    ms.translate(zOffset * 0.5d * wScale, -0.5d * hScale, -xOffset * 0.5d * wScale);
                } else {
                    ms.translate(0.5 * wScale, 0, (yOffset > 0 ? 0.5 : -0.5) * wScale);
                }
            } else {
                ms.translate(0.75d, 0.5d, 0.5d);
                if (wScale > 1 || hScale > 1) {
                    f /= 3.3f;
                } else {
                    f /= 2.0f;
                }
            }

            ms.mulPose(Axis.XP.rotationDegrees(pitch));
            ms.mulPose(Axis.YP.rotationDegrees(180 - yaw));
            ms.scale(f, f, f);

            Matrix4f m = ms.last().pose();
            Matrix3f mn = ms.last().normal();

            // FRONT (facing -Z)
            RenderSystem.setShaderTexture(0, location);
            VertexConsumer front = buffer.getBuffer(RenderType.entitySolid(location));
            addVertex(front, m, mn, 0.0F, 32.0F * hScale, -1.0F, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, m, mn, 32.0F * wScale, 32.0F * hScale, -1.0F, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, m, mn, 32.0F * wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, m, mn, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);

            // BACK (facing +Z)
            RenderSystem.setShaderTexture(0, backLocation);
            VertexConsumer back = buffer.getBuffer(RenderType.entitySolid(backLocation));
            addVertex(back, m, mn, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, m, mn, 32.0D * wScale, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, m, mn, 32.0D * wScale, 32.0D * hScale, 1.0D, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, m, mn, 0.0D, 32.0D * hScale, 1.0D, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);

            final float sideWidth = 1.0F / 16.0F;

            // LEFT SIDE (x = 0, normal -X)
            addVertex(back, m, mn, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addVertex(back, m, mn, 0.0D, 32.0D * hScale, 1.0D, sideWidth, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addVertex(back, m, mn, 0.0D, 32.0D * hScale, -1.0D, 0.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addVertex(back, m, mn, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);

            // TOP SIDE (y = 32*hScale, normal +Y)
            addVertex(back, m, mn, 0.0D, 32.0D * hScale, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
            addVertex(back, m, mn, 32.0D * wScale, 32.0D * hScale, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
            addVertex(back, m, mn, 32.0D * wScale, 32.0D * hScale, -1.0D, 1.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);
            addVertex(back, m, mn, 0.0D, 32.0D * hScale, -1.0D, 0.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);

            // RIGHT SIDE (x = 32*wScale, normal +X)
            addVertex(back, m, mn, 32.0D * wScale, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addVertex(back, m, mn, 32.0D * wScale, 32.0D * hScale, -1.0F, 0.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addVertex(back, m, mn, 32.0D * wScale, 32.0D * hScale, 1.0F, sideWidth, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addVertex(back, m, mn, 32.0D * wScale, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);

            // BOTTOM SIDE (y = 0, normal -Y)
            addVertex(back, m, mn, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
            addVertex(back, m, mn, 32.0D * wScale, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
            addVertex(back, m, mn, 32.0D * wScale, 0.0D, 1.0F, 1.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);
            addVertex(back, m, mn, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);

            ms.popPose();
        }

        private void addVertex(VertexConsumer vb, Matrix4f m, Matrix3f mn, double x, double y, double z, float tx, float ty, int lightmap, float nx, float ny, float nz)
        {
            vb.vertex(m, (float) x, (float)y, (float)z).color(255, 255, 255, 255).uv(tx, ty).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(mn, nx, ny, nz).endVertex();
        }

        public void close() {
            this.canvasTexture.close();
            textureManager.release(location);
        }
    }
}
