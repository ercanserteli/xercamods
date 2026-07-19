package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

import java.util.function.Consumer;

// Client Items special model renderer for drawn canvases.
public class CanvasSpecialRenderer implements SpecialModelRenderer<CanvasSpecialRenderer.CanvasData> {
    private final CanvasItemRenderer renderer = new CanvasItemRenderer();

    public record CanvasData(RenderEntityCanvas.@Nullable Instance instance, int width, int height, boolean glass) {
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
        return new CanvasData(instance, itemCanvas.getWidth(), itemCanvas.getHeight(), itemCanvas.isGlass());
    }

    @Override
    public void submit(@Nullable CanvasData data, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        if (data == null) {
            return;
        }
        renderer.renderCanvas(data.instance(), data.width(), data.height(), data.glass(), ItemDisplayContext.NONE, poseStack, collector, light);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> extents) {
        extents.accept(new Vector3f(0.0f, 0.0f, 0.5f));
        extents.accept(new Vector3f(0.0f, 1.0f, 0.5f));
        extents.accept(new Vector3f(1.0f, 0.0f, 0.5f));
        extents.accept(new Vector3f(1.0f, 1.0f, 0.5f));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<CanvasSpecialRenderer.CanvasData> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<CanvasSpecialRenderer.CanvasData> bake(SpecialModelRenderer.BakingContext context) {
            return new CanvasSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<CanvasSpecialRenderer.CanvasData>> type() {
            return MAP_CODEC;
        }
    }
}
