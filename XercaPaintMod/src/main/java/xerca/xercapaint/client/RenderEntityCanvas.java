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
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.entity.EntityCanvas;
import xerca.xercapaint.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas> {
    static RenderEntityCanvas theInstance;
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

    RenderEntityCanvas(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.textureManager = Minecraft.getInstance().getTextureManager();
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

    private Instance getCanvasRendererInstance(EntityCanvas canvas) {
        return getCanvasRendererInstance(canvas.getCanvasID(), canvas.getVersion(), canvas.getWidth(), canvas.getHeight());
    }

    Instance getCanvasRendererInstance(ItemStack canvasStack, int width, int height) {
        String canvasId = canvasStack.get(Items.CANVAS_ID);
        List<Integer> pixels = canvasStack.get(Items.CANVAS_PIXELS);
        if (canvasId == null || pixels == null) {
            return null;
        }
        int version = canvasStack.getOrDefault(Items.CANVAS_VERSION, 1);

        EntityCanvas.PICTURES.compute(canvasId, (n, existing) ->
                (existing == null || existing.version() < version) ? new EntityCanvas.Picture(version, pixels.stream().mapToInt(i -> i).toArray()) : existing
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

        private int swapColor(int color) {
            int i = (color & 16711680) >> 16;
            int j = (color & '\uff00') >> 8;
            int k = (color & 255);
            return k << 16 | j << 8 | i | 0xff000000;
        }

        private void updateCanvasTexture(String name, int version) {
            int[] pixels = EMPTY_PIXELS;
            if (EntityCanvas.PICTURES.containsKey(name)) {
                pixels = EntityCanvas.PICTURES.get(name).pixels();
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

        @Override
        public void close() {
            this.canvasTexture.close();
            textureManager.release(location);
        }
    }
}
