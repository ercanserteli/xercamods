package xerca.xercapaint.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CanvasRenderState extends EntityRenderState {
    public @Nullable String canvasId;
    public int version;
    public int width;
    public int height;
    public int rotation;
    public float yRot;
    public float xRot;
    public Direction direction = Direction.NORTH;
    public boolean glass;
}
