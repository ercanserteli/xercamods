package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

// Client Items special model renderer for drawn canvases.
public class CanvasSpecialRenderer implements SpecialModelRenderer<CanvasSpecialRenderer.CanvasData> {
    private final CanvasItemRenderer renderer = new CanvasItemRenderer();

    public record CanvasData(@Nullable RenderEntityCanvas.Instance instance, int width, int height, boolean glass) {
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
    public void render(@Nullable CanvasData data, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, boolean hasFoil) {
        if (data == null) {
            return;
        }
        renderer.renderCanvas(data.instance(), data.width(), data.height(), data.glass(), displayContext, poseStack, buffer, light);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
            return new CanvasSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
