package xerca.xercapaint.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
public class RenderEntityCanvas extends EntityRenderer<EntityCanvas> {
    static public RenderEntityCanvas theInstance;
    static private final ResourceLocation backLocation = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/birch_planks.png");
    private static final int[] EMPTY_PIXELS;

    static {
        EMPTY_PIXELS = new int[1024];
        for(int i=0; i<1024; i++){
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
        int version = canvasStack.getOrDefault(Items.CANVAS_VERSION, 1);
        if(!EntityCanvas.PICTURES.containsKey(canvasId) || EntityCanvas.PICTURES.get(canvasId).version < version){
            EntityCanvas.PICTURES.put(canvasId, new EntityCanvas.Picture(version, Objects.requireNonNull(canvasStack.get(Items.CANVAS_PIXELS)).stream().mapToInt(i->i).toArray()));
        }
        return getCanvasRendererInstance(Objects.requireNonNull(canvasId), version, width, height);
    }

    Instance getCanvasRendererInstance(String canvasId, int version, int width, int height) {
        Instance instance = this.loadedCanvases.get(canvasId);
        if (instance == null) {
            instance = new Instance(canvasId, version, width, height);
            this.loadedCanvases.put(canvasId, instance);
        }else{
            if(instance.version < version || !instance.loaded){
                instance.updateCanvasTexture(canvasId, version);
            }
        }

        return instance;
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

        private int swapColor(int color){
            int i = (color & 16711680) >> 16;
            int j = (color & '\uff00') >> 8;
            int k = (color & 255);
            return k << 16 | j << 8 | i | 0xff000000;
        }

        private void updateCanvasTexture(String canvasId, int version) {
            this.version = version;
            int[] pixels = EMPTY_PIXELS;
            if(EntityCanvas.PICTURES.containsKey(canvasId)){
                pixels = EntityCanvas.PICTURES.get(canvasId).pixels;
                loaded = true;
            }
            if(loaded || !started){
                if(pixels.length < height*width){
                    Mod.LOGGER.warn("Pixels array length ({}) is smaller than canvas area ({})", pixels.length, height * width);
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

            float xOffset = facing.getStepX();
            float yOffset = facing.getStepY();
            float zOffset = facing.getStepZ();

            boolean canvasIsNull = canvas == null;

            if (!canvasIsNull) {
                int rotation = canvas.getRotation();
                if (rotation > 0) {
                    ms.mulPose(Axis.XP.rotationDegrees(pitch));
                    ms.mulPose(Axis.YP.rotationDegrees(180 - yaw));
                    ms.mulPose(Axis.ZP.rotationDegrees(90 * rotation));
                    ms.mulPose(Axis.YP.rotationDegrees(-180 + yaw));
                    ms.mulPose(Axis.XP.rotationDegrees(-pitch));
                }
            }

            float f = 1.0f/32.0f;
            if(!canvasIsNull) {
                if (facing.getAxis().isHorizontal()) {
                    ms.translate(zOffset * 0.5d * wScale, -0.5d * hScale, -xOffset * 0.5d * wScale);
                } else {
                    ms.translate(0.5 * wScale, 0 * hScale, (yOffset > 0 ? 0.5 : -0.5) * wScale);
                }
                xOffset = 0;
                yOffset = 0;
                zOffset = -1;
            }
            else{
                ms.translate(0.75, 0.5, 0.5);
                if(wScale > 1 || hScale > 1){
                    f /= 3.3f;
                }else{
                    f /= 2.0f;
                }
            }
            ms.mulPose(Axis.XP.rotationDegrees( pitch));
            ms.mulPose(Axis.YP.rotationDegrees( 180-yaw));

            ms.scale(f, f, f);

            RenderSystem.setShaderTexture(0, location);

            Matrix4f m = ms.last().pose();
            PoseStack.Pose pose = ms.last();
            VertexConsumer vb = buffer.getBuffer(RenderType.entitySolid(location));

            // Draw the front
            addVertex(vb, m, pose, 0.0F, 32.0F*hScale, -1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0F*wScale, 32.0F*hScale, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0F*wScale, 0.0F, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0F, 0.0F, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

            vb = buffer.getBuffer(RenderType.entitySolid(backLocation));
            // Draw the back and sides
            final float sideWidth = 1.0F/16.0F;

            RenderSystem.setShaderTexture(0, backLocation);
            addVertex(vb, m, pose, 0.0D, 0.0D, 1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 0.0D, 1.0D, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 32.0D*hScale, 1.0D, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0D, 32.0D*hScale, 1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);

            // Sides
            addVertex(vb, m, pose, 0.0D, 0.0D, 1.0D, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0D, 32.0D*hScale, 1.0D, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0D, 32.0D*hScale, -1.0D, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0D, 0.0D, -1.0D, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);

            addVertex(vb, m, pose, 0.0D, 32.0D*hScale, 1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 32.0D*hScale, 1.0F, 1.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 32.0D*hScale, -1.0F, 1.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0D, 32.0D*hScale, -1.0F, 0.0F, sideWidth, packedLight, xOffset, yOffset, zOffset);

            addVertex(vb, m, pose, 32.0D*wScale, 0.0D, -1.0F, 0.0F, 0.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 32.0D*hScale, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 32.0D*hScale, 1.0F, sideWidth, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 0.0D, 1.0F, sideWidth, 0.0F, packedLight, xOffset, yOffset, zOffset);

            addVertex(vb, m, pose, 0.0D, 0.0D, -1.0F, 0.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 0.0D, -1.0F, 1.0F, 1.0F, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 32.0D*wScale, 0.0D, 1.0F, 1.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);
            addVertex(vb, m, pose, 0.0D, 0.0D, 1.0F, 0.0F, 1.0F-sideWidth, packedLight, xOffset, yOffset, zOffset);

            ms.popPose();
        }

        private void addVertex(VertexConsumer vb, Matrix4f m, PoseStack.Pose pose, double x, double y, double z, float tx, float ty, int lightmap, float xOff, float yOff, float zOff)
        {
            Vector3f normal = new Vector3f(xOff, yOff, zOff);
            normal.mul(pose.normal());
            vb.addVertex(m, (float) x, (float)y, (float)z)
                    .setColor(255, 255, 255, 255)
                    .setUv(tx, ty)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightmap)
                    .setNormal(normal.x(), normal.y(), normal.z());
        }

        public void close() {
            this.canvasTexture.close();
        }
    }
}