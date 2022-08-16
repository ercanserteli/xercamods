package xerca.xercapaint.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;
import xerca.xercapaint.client.CanvasItemRenderer;

public class RenderProp implements IItemRenderProperties {
    public static final RenderProp INSTANCE = new RenderProp();

    @Override
    public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
        return new CanvasItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
}
