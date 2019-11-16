package xerca.xercamod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xerca.xercamod.common.entity.EntityConfettiBall;

public class RenderConfettiBallFactory implements IRenderFactory<EntityConfettiBall> {

    @Override
    public EntityRenderer<EntityConfettiBall> createRenderFor(EntityRendererManager manager) {
        return new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer());
    }
}
