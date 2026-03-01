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
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.entity.EntityCanvas;
import xerca.xercapaint.item.Items;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@ParametersAreNonnullByDefault
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas, RenderEntityCanvas.CanvasRenderState> {
    public static RenderEntityCanvas theInstance;
    private static final ResourceLocation backLocation = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/birch_planks.png");
    private static final int[] EMPTY_PIXELS;

    static {
        EMPTY_PIXELS = new int[1024];
        for (int i = 0; i < 1024; i++) {
            EMPTY_PIXELS[i] = PaletteUtil.Color.WHITE.rgbVal();
        }
    }

    private final TextureManager textureManager;
    private final Map<String, Instance> loadedCanvases = Maps.newHashMap();

    public RenderEntityCanvas(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.textureManager = Minecraft.getInstance().getTextureManager();
    }

    @Override
    public @NotNull CanvasRenderState createRenderState() {
        return new CanvasRenderState();
    }

    @Override
    public void extractRenderState(EntityCanvas entity, CanvasRenderState state, float partialTick) {
        state.canvas = entity;
        state.instance = getCanvasRendererInstance(entity);
        super.extractRenderState(entity, state, partialTick);
    }

    @Override
    public void render(CanvasRenderState state, PoseStack ms, MultiBufferSource buffer, int packedLight) {
        EntityCanvas canvas = state.canvas;
        float yaw = canvas.getYRot();
        float pitch = canvas.getXRot();
        state.instance.render(canvas, yaw, pitch, ms, buffer, canvas.getDirection(), packedLight);
    }

    public static class RenderEntityCanvasFactory implements EntityRendererProvider<EntityCanvas> {
        @Override
        public @NotNull EntityRenderer<EntityCanvas, CanvasRenderState> create(EntityRendererProvider.Context ctx) {
            theInstance = new RenderEntityCanvas(ctx);
            return theInstance;
        }
    }

    private Instance getCanvasRendererInstance(EntityCanvas canvas) {
        return getCanvasRendererInstance(canvas.getCanvasID(), canvas.getVersion(), canvas.getWidth(), canvas.getHeight());
    }

    Instance getCanvasRendererInstance(ItemStack canvasStack, int width, int height) {
        String canvasId = canvasStack.get(Items.CANVAS_ID);
        int version = canvasStack.getOrDefault(Items.CANVAS_VERSION, 1);
        EntityCanvas.PICTURES.compute(canvasId, (key, existingPicture) -> {
            if (existingPicture == null || existingPicture.version() < version) {
                return new EntityCanvas.Picture(version, Objects.requireNonNull(canvasStack.get(Items.CANVAS_PIXELS)).stream().mapToInt(i -> i).toArray());
            }
            return existingPicture;
        });
        return getCanvasRendererInstance(Objects.requireNonNull(canvasId), version, width, height);
    }

    Instance getCanvasRendererInstance(String canvasId, int version, int width, int height) {
        Instance instance = this.loadedCanvases.get(canvasId);
        if (instance == null) {
            instance = new Instance(canvasId, version, width, height);
            this.loadedCanvases.put(canvasId, instance);
        } else {
            if (instance.version < version || !instance.loaded) {
                instance.updateCanvasTexture(canvasId, version);
            }
        }
        return instance;
    }

    public static class CanvasRenderState extends EntityRenderState {
        public EntityCanvas canvas;
        public Instance instance;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public class Instance implements AutoCloseable {
        int version = 0;
        final int width;
        final int height;
        boolean loaded;
        boolean started;
        public final DynamicTexture canvasTexture;
        public final ResourceLocation location;

        private Instance(String canvasId, int version, int width, int height) {
            this.started = false;
            this.loaded = false;
            this.width = width;
            this.height = height;
            this.canvasTexture = new DynamicTexture(width, height, true);
            this.location = RenderEntityCanvas.this.textureManager.register("canvas/" + canvasId, this.canvasTexture);

            updateCanvasTexture(canvasId, version);
        }

        private void updateCanvasTexture(String canvasId, int version) {
            int[] pixels = EMPTY_PIXELS;
            if (EntityCanvas.PICTURES.containsKey(canvasId)) {
                pixels = EntityCanvas.PICTURES.get(canvasId).pixels();
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
                            image.setPixel(x, y, pixels[idx]);
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

            RenderSystem.setShaderTexture(0, location);
            Matrix4f m = ms.last().pose();
            PoseStack.Pose pose = ms.last();

            // FRONT (facing -Z)
            VertexConsumer front = buffer.getBuffer(RenderType.entitySolid(location));
            addVertex(front, m, pose, 0.0F, 32.0F * hScale, -1.0F, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, m, pose, 32.0F * wScale, 32.0F * hScale, -1.0F, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, m, pose, 32.0F * wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);
            addVertex(front, m, pose, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, -1.0F);

            // BACK (facing +Z)
            VertexConsumer back = buffer.getBuffer(RenderType.entitySolid(backLocation));
            final float sideWidth = 1.0F / 16.0F;
            RenderSystem.setShaderTexture(0, backLocation);
            addVertex(back, m, pose, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, m, pose, 32.0D * wScale, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, m, pose, 32.0D * wScale, 32.0D * hScale, 1.0D, 1.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);
            addVertex(back, m, pose, 0.0D, 32.0D * hScale, 1.0D, 0.0F, 1.0F, packedLight, 0.0F, 0.0F, 1.0F);

            // LEFT SIDE (x = 0, normal -X)
            addVertex(back, m, pose, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addVertex(back, m, pose, 0.0D, 32.0D * hScale, 1.0D, sideWidth, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addVertex(back, m, pose, 0.0D, 32.0D * hScale, -1.0D, 0.0F, 1.0F, packedLight, -1.0F, 0.0F, 0.0F);
            addVertex(back, m, pose, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, -1.0F, 0.0F, 0.0F);

            // TOP SIDE (y = 32*hScale, normal +Y)
            addVertex(back, m, pose, 0.0D, 32.0D * hScale, 1.0D, 0.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
            addVertex(back, m, pose, 32.0D * wScale, 32.0D * hScale, 1.0D, 1.0F, 0.0F, packedLight, 0.0F, 1.0F, 0.0F);
            addVertex(back, m, pose, 32.0D * wScale, 32.0D * hScale, -1.0D, 1.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);
            addVertex(back, m, pose, 0.0D, 32.0D * hScale, -1.0D, 0.0F, sideWidth, packedLight, 0.0F, 1.0F, 0.0F);

            // RIGHT SIDE (x = 32*wScale, normal +X)
            addVertex(back, m, pose, 32.0D * wScale, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addVertex(back, m, pose, 32.0D * wScale, 32.0D * hScale, -1.0F, 0.0F, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addVertex(back, m, pose, 32.0D * wScale, 32.0D * hScale, 1.0F, sideWidth, 1.0F, packedLight, 1.0F, 0.0F, 0.0F);
            addVertex(back, m, pose, 32.0D * wScale, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, 1.0F, 0.0F, 0.0F);

            // BOTTOM SIDE (y = 0, normal -Y)
            addVertex(back, m, pose, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
            addVertex(back, m, pose, 32.0D * wScale, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, 0.0F, -1.0F, 0.0F);
            addVertex(back, m, pose, 32.0D * wScale, 0.0D, 1.0F, 1.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);
            addVertex(back, m, pose, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F - sideWidth, packedLight, 0.0F, -1.0F, 0.0F);

            ms.popPose();
        }

        private void addVertex(VertexConsumer vb, Matrix4f m, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float nx, float ny, float nz) {
            vb.addVertex(m, (float) x, (float) y, (float) z)
                    .setColor(255, 255, 255, 255)
                    .setUv(tx, ty)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightmap)
                    .setNormal(nx, ny, nz);
        }

        @Override
        public void close() {
            this.canvasTexture.close();
            textureManager.release(location);
        }
    }
}
