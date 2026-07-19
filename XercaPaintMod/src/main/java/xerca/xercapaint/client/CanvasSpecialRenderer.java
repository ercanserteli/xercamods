package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

import java.util.Set;

// Client Items special model renderer for drawn canvases.
public class CanvasSpecialRenderer implements SpecialModelRenderer<CanvasSpecialRenderer.CanvasData> {
    private final CanvasItemRenderer renderer = new CanvasItemRenderer();

    public record CanvasData(@Nullable RenderEntityCanvas.Instance instance, int width, int height, boolean glass,
                             int version) {
    }

    @Override
    public @Nullable CanvasData extractArgument(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemCanvas itemCanvas)) {
            return null;
        }
        RenderEntityCanvas.Instance instance = null;
        if (stack.get(Items.CANVAS_PIXELS) != null && RenderEntityCanvas.theInstance != null) {
            instance = RenderEntityCanvas.theInstance.getCanvasRendererInstance(stack, itemCanvas.getWidth(), itemCanvas.getHeight());
        }
        int version = stack.getOrDefault(Items.CANVAS_VERSION, 1);
        return new CanvasData(instance, itemCanvas.getWidth(), itemCanvas.getHeight(), itemCanvas.isGlass(), version);
    }

    @Override
    public void submit(@Nullable CanvasData data, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        if (data == null) {
            return;
        }
        renderer.renderCanvas(data.instance(), data.width(), data.height(), data.glass(), displayContext, poseStack, collector, light);
    }

    @Override
    public void getExtents(Set<Vector3f> extents) {
        extents.add(new Vector3f(0.0f, 0.0f, 0.5f));
        extents.add(new Vector3f(0.0f, 1.0f, 0.5f));
        extents.add(new Vector3f(1.0f, 0.0f, 0.5f));
        extents.add(new Vector3f(1.0f, 1.0f, 0.5f));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            return new CanvasSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
