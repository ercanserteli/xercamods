package xerca.xercapaint.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xerca.xercapaint.CanvasType;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class EaselRenderState extends EntityRenderState {
    public ItemStack item = ItemStack.EMPTY;
    public @Nullable CanvasType canvasType;
    public float yRot;
}
