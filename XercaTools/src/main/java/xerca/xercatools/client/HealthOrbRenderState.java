package xerca.xercatools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class HealthOrbRenderState extends EntityRenderState {
    public float animTime;
}
