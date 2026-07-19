package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    private final boolean guiTint;

    public CanvasSpecialRenderer(boolean guiTint) {
        this.guiTint = guiTint;
    }

    // The version is part of the record so the GUI item-atlas cache (keyed on the model identity, which
    // includes this extracted argument) is invalidated when the painting is updated.
    public record CanvasData(RenderEntityCanvas.@Nullable Instance instance, int width, int height, boolean glass,
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
    public void submit(@Nullable CanvasData data, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        if (data == null) {
            return;
        }
        ItemDisplayContext displayContext = guiTint ? ItemDisplayContext.GUI : ItemDisplayContext.NONE;
        renderer.renderCanvas(data.instance(), data.width(), data.height(), data.glass(), displayContext, poseStack, collector, light);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> extents) {
        extents.accept(new Vector3f(0.0f, 0.0f, 0.5f));
        extents.accept(new Vector3f(0.0f, 1.0f, 0.5f));
        extents.accept(new Vector3f(1.0f, 0.0f, 0.5f));
        extents.accept(new Vector3f(1.0f, 1.0f, 0.5f));
    }

    public record Unbaked(boolean guiTint) implements SpecialModelRenderer.Unbaked<CanvasSpecialRenderer.CanvasData> {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("gui_tint", false).forGetter(Unbaked::guiTint)
        ).apply(instance, Unbaked::new));

        @Override
        public SpecialModelRenderer<CanvasSpecialRenderer.CanvasData> bake(SpecialModelRenderer.BakingContext context) {
            return new CanvasSpecialRenderer(guiTint);
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<CanvasSpecialRenderer.CanvasData>> type() {
            return MAP_CODEC;
        }
    }
}
