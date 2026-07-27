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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.common.CanvasSides;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.entity.EntityCanvas;
import xerca.xercapaint.common.item.ItemCanvas;

import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas> {
    static @Nullable RenderEntityCanvas theInstance;
    private static final ResourceLocation BACK_LOCATION = new ResourceLocation("minecraft", "textures/block/birch_planks.png");
    private static final ResourceLocation GLASS_FRAME_LOCATION = new ResourceLocation("minecraft", "textures/block/glass.png");
    static final int NO_TINT = 0xFFFFFFFF;
    /**
     * Alpha of the faint glass sheet drawn behind a tinted glass painting so transparent pixels are tinted too
     */
    private static final int GLASS_TINT_OVERLAY_ALPHA = 0x40;
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
        getCanvasRendererInstance(entity).render(entity, entityYaw, entity.getXRot(), matrixStackIn, bufferIn, entity.getDirection(), packedLightIn, entity.isGlass(), NO_TINT);
    }

    public static class RenderEntityCanvasFactory implements EntityRendererProvider<EntityCanvas> {
        @Override
        public EntityRenderer<EntityCanvas> create(Context ctx) {
            RenderEntityCanvas instance = new RenderEntityCanvas(ctx);
            theInstance = instance;
            return instance;
        }
    }

    private Instance getCanvasRendererInstance(EntityCanvas canvas) {
        return getCanvasRendererInstance(canvas.getCanvasName(), canvas.getCanvasVersion(), canvas.getWidth(), canvas.getHeight());
    }

    @Nullable Instance getCanvasRendererInstance(ItemStack canvasStack, int width, int height) {
        CompoundTag tag = canvasStack.getTag();
        if (tag == null || !tag.contains(ItemCanvas.TAG_CANVAS_ID) || !tag.contains(ItemCanvas.TAG_PIXELS)) {
            return null;
        }
        String canvasId = tag.getString(ItemCanvas.TAG_CANVAS_ID);
        int[] pixels = tag.getIntArray(ItemCanvas.TAG_PIXELS);
        int version = tag.contains(ItemCanvas.TAG_VERSION) ? tag.getInt(ItemCanvas.TAG_VERSION) : 1;

        boolean sidesActive = tag.getBoolean(ItemCanvas.TAG_SIDES_ACTIVE);
        int[] sidePixels = tag.getIntArray(ItemCanvas.TAG_SIDE_PIXELS);

        EntityCanvas.PICTURES.compute(canvasId, (n, existing) ->
                (existing == null || existing.version() < version) ? new EntityCanvas.Picture(version, pixels, sidesActive, sidePixels) : existing
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

    @OnlyIn(Dist.CLIENT)
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
            // Preserve source alpha so glass keeps transparent pixels
            int a = (color >> 24) & 0xFF;
            return k << 16 | j << 8 | i | (a << 24);
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

        public void render(@Nullable EntityCanvas canvas, float yaw, float pitch, PoseStack ms, MultiBufferSource buffer, Direction facing, int packedLight, boolean glass, int tint) {
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
            final float w32 = 32.0F * wScale;
            final float h32 = 32.0F * hScale;
            final float sideWidth = 1.0F / 16.0F;

            // FRONT (facing -Z): glass uses single-sided cutout so transparent pixels are see-through
            RenderSystem.setShaderTexture(0, location);
            VertexConsumer front = buffer.getBuffer(glass ? RenderType.entityCutout(location) : RenderType.entitySolid(location));
            addVertex(front, pose, 0.0F, h32, -1.0F, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, pose, w32, h32, -1.0F, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, pose, w32, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, pose, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);

            if (glass) {
                // BACK (facing +Z): the front image seen through the glass appears mirrored
                VertexConsumer back = buffer.getBuffer(RenderType.entityCutout(location));
                addVertex(back, pose, 0.0D, 0.0D, 1.0D, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);
                addVertex(back, pose, w32, 0.0D, 1.0D, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);
                addVertex(back, pose, w32, h32, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
                addVertex(back, pose, 0.0D, h32, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);

                if (tint != NO_TINT) {
                    // Tint the transparent pixels
                    int overlay = (GLASS_TINT_OVERLAY_ALPHA << 24) | (tint & 0xFFFFFF);
                    RenderSystem.setShaderTexture(0, RenderEntityCanvas.this.whiteLocation);
                    VertexConsumer glassSheet = buffer.getBuffer(RenderType.entityTranslucent(RenderEntityCanvas.this.whiteLocation));
                    addVertex(glassSheet, pose, 0.0D, h32, 0.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F, overlay);
                    addVertex(glassSheet, pose, w32, h32, 0.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F, overlay);
                    addVertex(glassSheet, pose, w32, 0.0D, 0.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F, overlay);
                    addVertex(glassSheet, pose, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F, overlay);
                }

                if (sidesActive) {
                    // Painted side pixels (no-cull so they are visible from inside the canvas too)
                    RenderSystem.setShaderTexture(0, RenderEntityCanvas.this.whiteLocation);
                    VertexConsumer sides = buffer.getBuffer(RenderType.entityCutoutNoCull(RenderEntityCanvas.this.whiteLocation));
                    renderPaintedSides(sides, pose, w32, h32, packedLight, true);
                } else {
                    // Glass-pane frame
                    RenderSystem.setShaderTexture(0, GLASS_FRAME_LOCATION);
                    VertexConsumer frame = buffer.getBuffer(RenderType.entityTranslucent(GLASS_FRAME_LOCATION));
                    renderGlassFrame(frame, pose, w32, h32, packedLight);
                }
                ms.popPose();
                return;
            }

            // BACK (facing +Z)
            RenderSystem.setShaderTexture(0, BACK_LOCATION);
            VertexConsumer back = buffer.getBuffer(RenderType.entitySolid(BACK_LOCATION));
            addVertex(back, pose, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, pose, w32, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, pose, w32, h32, 1.0D, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, pose, 0.0D, h32, 1.0D, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);

            boolean paintedSides = sidesActive;
            if (!paintedSides) {
                // LEFT SIDE (x = 0, normal -X)
                addVertex(back, pose, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 0.0D, h32, 1.0D, sideWidth, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 0.0D, h32, -1.0D, 0.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
                addVertex(back, pose, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);

                // TOP SIDE (y = 32*hScale, normal +Y)
                addVertex(back, pose, 0.0D, h32, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
                addVertex(back, pose, w32, h32, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
                addVertex(back, pose, w32, h32, -1.0D, 1.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);
                addVertex(back, pose, 0.0D, h32, -1.0D, 0.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);

                // RIGHT SIDE (x = 32*wScale, normal +X)
                addVertex(back, pose, w32, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
                addVertex(back, pose, w32, h32, -1.0F, 0.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
                addVertex(back, pose, w32, h32, 1.0F, sideWidth, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
                addVertex(back, pose, w32, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);

                // BOTTOM SIDE (y = 0, normal -Y)
                addVertex(back, pose, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
                addVertex(back, pose, w32, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
                addVertex(back, pose, w32, 0.0D, 1.0F, 1.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);
                addVertex(back, pose, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);
            } else {
                // No-cull so painted sides are visible from inside the canvas too
                RenderSystem.setShaderTexture(0, RenderEntityCanvas.this.whiteLocation);
                VertexConsumer sides = buffer.getBuffer(RenderType.entityCutoutNoCull(RenderEntityCanvas.this.whiteLocation));
                renderPaintedSides(sides, pose, w32, h32, packedLight, false);
            }

            ms.popPose();
        }

        private void addVertex(VertexConsumer vb, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float nx, float ny, float nz) {
            addVertex(vb, pose, x, y, z, tx, ty, lightmap, nx, ny, nz, NO_TINT);
        }

        private void addVertex(VertexConsumer vb, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float nx, float ny, float nz, int tint) {
            int r = (tint >> 16) & 0xFF;
            int g = (tint >> 8) & 0xFF;
            int b = tint & 0xFF;
            int a = (tint >>> 24) & 0xFF;
            vb.vertex(pose.pose(), (float) x, (float) y, (float) z)
                    .color(r, g, b, a)
                    .uv(tx, ty)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(lightmap)
                    .normal(pose.normal(), nx, ny, nz)
                    .endVertex();
        }

        private int sidePixelColor(int index) {
            if (index >= 0 && index < sidePixels.length) {
                return sidePixels[index];
            }
            return CanvasSides.DEFAULT_COLOR;
        }

        private void addSideVertex(VertexConsumer vb, PoseStack.Pose pose, double x, double y, double z, int color, int lightmap, float nx, float ny, float nz) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            vb.vertex(pose.pose(), (float) x, (float) y, (float) z)
                    .color(r, g, b, 255)
                    .uv(0.0F, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(lightmap)
                    .normal(pose.normal(), nx, ny, nz)
                    .endVertex();
        }

        private void addFrameVertex(VertexConsumer vb, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float nx, float ny, float nz) {
            vb.vertex(pose.pose(), (float) x, (float) y, (float) z)
                    .color(255, 255, 255, 255)
                    .uv(tx, ty)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(lightmap)
                    .normal(pose.normal(), nx, ny, nz)
                    .endVertex();
        }

        /**
         * Renders the four glass-canvas edges using the vanilla glass-pane texture.
         */
        private void renderGlassFrame(VertexConsumer vb, PoseStack.Pose pose, float w32, float h32, int packedLight) {
            double eps = 0.001;
            // LEFT (x = 0, normal -X)
            addFrameVertex(vb, pose, eps, eps, 1.0D, 0.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addFrameVertex(vb, pose, eps, h32 - eps, 1.0D, 0.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addFrameVertex(vb, pose, eps, h32 - eps, -1.0D, 1.0F / 16.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addFrameVertex(vb, pose, eps, eps, -1.0D, 1.0F / 16.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
            // TOP (y = h32, normal +Y)
            addFrameVertex(vb, pose, eps, h32 - eps, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
            addFrameVertex(vb, pose, w32 - eps, h32 - eps, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
            addFrameVertex(vb, pose, w32 - eps, h32 - eps, -1.0D, 1.0F, 1.0F / 16.0F, packedLight, 0.0F, 1.0F, 0.0F);
            addFrameVertex(vb, pose, eps, h32 - eps, -1.0D, 0.0F, 1.0F / 16.0F, packedLight, 0.0F, 1.0F, 0.0F);
            // RIGHT (x = w32, normal +X)
            addFrameVertex(vb, pose, w32 - eps, eps, -1.0D, 0.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addFrameVertex(vb, pose, w32 - eps, h32 - eps, -1.0D, 0.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addFrameVertex(vb, pose, w32 - eps, h32 - eps, 1.0D, 1.0F / 16.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addFrameVertex(vb, pose, w32 - eps, eps, 1.0D, 1.0F / 16.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
            // BOTTOM (y = 0, normal -Y)
            addFrameVertex(vb, pose, eps, eps, -1.0D, 0.0F, 0.0F, packedLight, 0.0F, -1.0F, 0.0F);
            addFrameVertex(vb, pose, w32 - eps, eps, -1.0D, 1.0F, 0.0F, packedLight, 0.0F, -1.0F, 0.0F);
            addFrameVertex(vb, pose, w32 - eps, eps, 1.0D, 1.0F, 1.0F / 16.0F, packedLight, 0.0F, -1.0F, 0.0F);
            addFrameVertex(vb, pose, eps, eps, 1.0D, 0.0F, 1.0F / 16.0F, packedLight, 0.0F, -1.0F, 0.0F);
        }

        /**
         * Renders the four canvas edges as a strip of solid-colored, one-pixel quads. The pixel
         * ordering matches {@link CanvasSides} so painted sides line up with the adjacent front pixels.
         * When skipTransparent is set (glass), fully transparent side pixels are skipped.
         */
        private void renderPaintedSides(VertexConsumer vb, PoseStack.Pose pose, float w32, float h32, int packedLight, boolean skipTransparent) {
            double eps = 0.001;
            final float unit = 2.0F; // one image pixel spans two local units along an edge
            final int topOffset = 0;
            final int bottomOffset = width;
            final int leftOffset = bottomOffset + width;
            final int rightOffset = leftOffset + height;

            // TOP edge (y = h32, normal +Y): pixel k maps to image column k -> x in [w32-(k+1)u, w32-k*u]
            for (int k = 0; k < width; k++) {
                int c = sidePixelColor(topOffset + k);
                if (skipTransparent && ((c >> 24) & 0xFF) == 0) continue;
                float x0 = w32 - (k + 1) * unit;
                float x1 = w32 - k * unit;
                addSideVertex(vb, pose, x0, h32 - eps, 1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
                addSideVertex(vb, pose, x1, h32 - eps, 1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
                addSideVertex(vb, pose, x1, h32 - eps, -1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
                addSideVertex(vb, pose, x0, h32 - eps, -1.0D, c, packedLight, 0.0F, 1.0F, 0.0F);
            }
            // BOTTOM edge (y = 0, normal -Y): same x mapping as the top
            for (int k = 0; k < width; k++) {
                int c = sidePixelColor(bottomOffset + k);
                if (skipTransparent && ((c >> 24) & 0xFF) == 0) continue;
                float x0 = w32 - (k + 1) * unit;
                float x1 = w32 - k * unit;
                addSideVertex(vb, pose, x0, eps, -1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
                addSideVertex(vb, pose, x1, eps, -1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
                addSideVertex(vb, pose, x1, eps, 1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
                addSideVertex(vb, pose, x0, eps, 1.0D, c, packedLight, 0.0F, -1.0F, 0.0F);
            }
            // LEFT edge (image left, x = w32, normal +X): pixel i maps to image row i -> y in [h32-(i+1)u, h32-i*u]
            for (int i = 0; i < height; i++) {
                int c = sidePixelColor(leftOffset + i);
                if (skipTransparent && ((c >> 24) & 0xFF) == 0) continue;
                float y0 = h32 - (i + 1) * unit;
                float y1 = h32 - i * unit;
                addSideVertex(vb, pose, w32 - eps, y0, -1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, w32 - eps, y1, -1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, w32 - eps, y1, 1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, w32 - eps, y0, 1.0D, c, packedLight, 1.0F, 0.0F, 0.0F);
            }
            // RIGHT edge (image right, x = 0, normal -X): same y mapping as the left
            for (int i = 0; i < height; i++) {
                int c = sidePixelColor(rightOffset + i);
                if (skipTransparent && ((c >> 24) & 0xFF) == 0) continue;
                float y0 = h32 - (i + 1) * unit;
                float y1 = h32 - i * unit;
                addSideVertex(vb, pose, eps, y0, 1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, eps, y1, 1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, eps, y1, -1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
                addSideVertex(vb, pose, eps, y0, -1.0D, c, packedLight, -1.0F, 0.0F, 0.0F);
            }
        }

        @Override
        public void close() {
            this.canvasTexture.close();
            textureManager.release(location);
        }
    }
}
