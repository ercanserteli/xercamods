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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.entity.EntityCanvas;
import xerca.xercapaint.item.Items;

import java.util.List;
import java.util.Map;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas> {
    static @Nullable RenderEntityCanvas theInstance;
    private static final ResourceLocation BACK_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/birch_planks.png");
    private static final int[] EMPTY_PIXELS;

    static {
        EMPTY_PIXELS = new int[1024];
        for (int i = 0; i < 1024; i++) {
            EMPTY_PIXELS[i] = PaletteUtil.Color.WHITE.rgbVal();
        }
    }

    private final TextureManager textureManager;
    private final Map<String, Instance> loadedCanvases = Maps.newHashMap();
    /**
     * 1x1 white texture used to render painted sides
     */
    private final ResourceLocation whiteLocation;

    RenderEntityCanvas(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.textureManager = Minecraft.getInstance().getTextureManager();
        this.whiteLocation = createWhiteTexture();
    }

    private ResourceLocation createWhiteTexture() {
        DynamicTexture texture = new DynamicTexture(1, 1, false);
        NativeImage image = texture.getPixels();
        if (image != null) {
            image.setPixelRGBA(0, 0, 0xFFFFFFFF);
            texture.upload();
        }
        return textureManager.register("canvas_side_white", texture);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCanvas entity) {
        return getCanvasRendererInstance(entity).location;
    }

    @Override
    public void render(EntityCanvas entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        getCanvasRendererInstance(entity).render(entity, entityYaw, entity.getXRot(), matrixStackIn, bufferIn, entity.getDirection(), packedLightIn);
    }

    public static class RenderEntityCanvasFactory implements EntityRendererProvider<EntityCanvas> {
        @Override
        public EntityRenderer<EntityCanvas> create(Context ctx) {
            theInstance = new RenderEntityCanvas(ctx);
            return theInstance;
        }
    }

    private Instance getCanvasRendererInstance(EntityCanvas canvas) {
        return getCanvasRendererInstance(canvas.getCanvasID(), canvas.getVersion(), canvas.getWidth(), canvas.getHeight());
    }

    @Nullable Instance getCanvasRendererInstance(ItemStack canvasStack, int width, int height) {
        String canvasId = canvasStack.get(Items.CANVAS_ID);
        List<Integer> pixels = canvasStack.get(Items.CANVAS_PIXELS);
        if (canvasId == null || pixels == null) {
            return null;
        }
        int version = canvasStack.getOrDefault(Items.CANVAS_VERSION, 1);

        boolean sidesActive = canvasStack.getOrDefault(Items.CANVAS_SIDES_ACTIVE, false);
        List<Integer> sideList = canvasStack.get(Items.CANVAS_SIDE_PIXELS);
        int[] sidePixels = sideList != null ? sideList.stream().mapToInt(i -> i).toArray() : new int[0];

        EntityCanvas.PICTURES.compute(canvasId, (n, existing) ->
                (existing == null || existing.version() < version) ? new EntityCanvas.Picture(version, pixels.stream().mapToInt(i -> i).toArray(), sidesActive, sidePixels) : existing
        );

        return getCanvasRendererInstance(canvasId, version, width, height);
    }

    private static String rendererKey(String name, int width, int height) {
        return name + "-" + width + "x" + height;
    }

    Instance getCanvasRendererInstance(String name, int version, int width, int height) {
        String key = rendererKey(name, width, height);
        Instance instance = this.loadedCanvases.get(key);
        if (instance == null) {
            instance = new Instance(key, name, version, width, height);
            this.loadedCanvases.put(key, instance);
        } else {
            if (instance.version < version || !instance.loaded) {
                instance.updateCanvasTexture(name, version);
            }
        }

        return instance;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public final class Instance implements AutoCloseable {
        int version;
        final int width;
        final int height;
        boolean loaded;
        boolean started;
        boolean sidesActive;
        int[] sidePixels = new int[0];
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

        private int swapColor(int color) {
            int i = (color & 16711680) >> 16;
            int j = (color & '\uff00') >> 8;
            int k = (color & 255);
            return k << 16 | j << 8 | i | 0xff000000;
        }

        private void updateCanvasTexture(String name, int version) {
            int[] pixels = EMPTY_PIXELS;
            if (EntityCanvas.PICTURES.containsKey(name)) {
                EntityCanvas.Picture picture = EntityCanvas.PICTURES.get(name);
                pixels = picture.pixels();
                this.sidesActive = picture.sidesActive();
                this.sidePixels = picture.sidePixels();
                loaded = true;
            }
            if (loaded || !started) {
                if (pixels.length < width * height) {
                    Mod.LOGGER.warn("Pixels array length ({}) is smaller than canvas area ({})", pixels.length, height * width);
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

            PoseStack.Pose pose = ms.last();

            // FRONT (facing -Z)
            RenderSystem.setShaderTexture(0, location);
            VertexConsumer front = buffer.getBuffer(RenderType.entitySolid(location));
            addVertex(front, pose, 0.0F, 32.0F * hScale, -1.0F, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, pose, 32.0F * wScale, 32.0F * hScale, -1.0F, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, pose, 32.0F * wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, pose, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);

            // BACK (facing +Z)
            RenderSystem.setShaderTexture(0, BACK_LOCATION);
            VertexConsumer back = buffer.getBuffer(RenderType.entitySolid(BACK_LOCATION));
            final float sideWidth = 1.0F / 16.0F;
            addVertex(back, pose, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, pose, 32.0D * wScale, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, pose, 32.0D * wScale, 32.0D * hScale, 1.0D, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, pose, 0.0D, 32.0D * hScale, 1.0D, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);

            boolean paintedSides = sidesActive;
            if (!paintedSides) {
                // LEFT SIDE (x = 0, normal -X)
                addVertex(back, pose, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 0.0D, 32.0D * hScale, 1.0D, sideWidth, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 0.0D, 32.0D * hScale, -1.0D, 0.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);

                // TOP SIDE (y = 32*hScale, normal +Y)
                addVertex(back, pose, 0.0D, 32.0D * hScale, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
                addVertex(back, pose, 32.0D * wScale, 32.0D * hScale, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
                addVertex(back, pose, 32.0D * wScale, 32.0D * hScale, -1.0D, 1.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);
                addVertex(back, pose, 0.0D, 32.0D * hScale, -1.0D, 0.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);

                // RIGHT SIDE (x = 32*wScale, normal +X)
                addVertex(back, pose, 32.0D * wScale, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 32.0D * wScale, 32.0D * hScale, -1.0F, 0.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 32.0D * wScale, 32.0D * hScale, 1.0F, sideWidth, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 32.0D * wScale, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);

                // BOTTOM SIDE (y = 0, normal -Y)
                addVertex(back, pose, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
                addVertex(back, pose, 32.0D * wScale, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
                addVertex(back, pose, 32.0D * wScale, 0.0D, 1.0F, 1.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);
                addVertex(back, pose, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);
            } else {
                RenderSystem.setShaderTexture(0, RenderEntityCanvas.this.whiteLocation);
                VertexConsumer sides = buffer.getBuffer(RenderType.entitySolid(RenderEntityCanvas.this.whiteLocation));
                renderPaintedSides(sides, pose, 32.0F * wScale, 32.0F * hScale, packedLight);
            }

            ms.popPose();
        }

        private void addVertex(VertexConsumer vb, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float nx, float ny, float nz) {
            vb.addVertex(pose, (float) x, (float) y, (float) z)
                    .setColor(255, 255, 255, 255)
                    .setUv(tx, ty)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightmap)
                    .setNormal(pose, nx, ny, nz);
        }

        private int sidePixelColor(int index) {
            if (sidePixels != null && index >= 0 && index < sidePixels.length) {
                return sidePixels[index];
            }
            return CanvasSides.DEFAULT_COLOR;
        }

        private void addSideVertex(VertexConsumer vb, PoseStack.Pose pose, double x, double y, double z, int color, int lightmap, float nx, float ny, float nz) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            vb.addVertex(pose, (float) x, (float) y, (float) z)
                    .setColor(r, g, b, 255)
                    .setUv(0.0F, 0.0F)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightmap)
                    .setNormal(pose, nx, ny, nz);
        }

        /**
         * Renders the four canvas edges as a strip of solid-colored, one-pixel quads. The pixel
         * ordering matches {@link CanvasSides} so painted sides line up with the adjacent front pixels.
         */
        private void renderPaintedSides(VertexConsumer vb, PoseStack.Pose pose, float w32, float h32, int packedLight) {
            final float unit = 2.0F; // one image pixel spans two local units along an edge
            final int topOffset = 0;
            final int bottomOffset = width;
            final int leftOffset = 2 * width;
            final int rightOffset = 2 * width + height;

            // TOP edge (y = h32, normal +Y): pixel k maps to image column k -> x in [w32-(k+1)u, w32-k*u]
            for (int k = 0; k < width; k++) {
                int c = sidePixelColor(topOffset + k);
                float x0 = w32 - (k + 1) * unit;
                float x1 = w32 - k * unit;
                addSideVertex(vb, pose, x0, h32, 1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
                addSideVertex(vb, pose, x1, h32, 1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
                addSideVertex(vb, pose, x1, h32, -1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
                addSideVertex(vb, pose, x0, h32, -1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
            }
            // BOTTOM edge (y = 0, normal -Y): same x mapping as the top
            for (int k = 0; k < width; k++) {
                int c = sidePixelColor(bottomOffset + k);
                float x0 = w32 - (k + 1) * unit;
                float x1 = w32 - k * unit;
                addSideVertex(vb, pose, x0, 0.0D, -1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
                addSideVertex(vb, pose, x1, 0.0D, -1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
                addSideVertex(vb, pose, x1, 0.0D, 1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
                addSideVertex(vb, pose, x0, 0.0D, 1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
            }
            // LEFT edge (image left, x = w32, normal +X): pixel i maps to image row i -> y in [h32-(i+1)u, h32-i*u]
            for (int i = 0; i < height; i++) {
                int c = sidePixelColor(leftOffset + i);
                float y0 = h32 - (i + 1) * unit;
                float y1 = h32 - i * unit;
                addSideVertex(vb, pose, w32, y0, -1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, w32, y1, -1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, w32, y1, 1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, w32, y0, 1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
            }
            // RIGHT edge (image right, x = 0, normal -X): same y mapping as the left
            for (int i = 0; i < height; i++) {
                int c = sidePixelColor(rightOffset + i);
                float y0 = h32 - (i + 1) * unit;
                float y1 = h32 - i * unit;
                addSideVertex(vb, pose, 0.0D, y0, 1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, 0.0D, y1, 1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, 0.0D, y1, -1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, 0.0D, y0, -1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
            }
        }

        @Override
        public void close() {
            this.canvasTexture.close();
            textureManager.release(location);
        }
    }
}
