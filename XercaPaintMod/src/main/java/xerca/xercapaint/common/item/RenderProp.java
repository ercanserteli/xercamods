package xerca.xercapaint.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import xerca.xercapaint.client.CanvasItemRenderer;

public class RenderProp implements IClientItemExtensions {
    public static final RenderProp INSTANCE = new RenderProp();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return new CanvasItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
}
