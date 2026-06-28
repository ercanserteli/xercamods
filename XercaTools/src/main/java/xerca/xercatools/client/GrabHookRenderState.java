package xerca.xercatools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class GrabHookRenderState extends EntityRenderState {
    public boolean hasPlayer;
    public float chainDx;
    public float chainDy;
    public float chainDz;
}
