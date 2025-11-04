package xerca.xercapaint.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class RenderUtil {
    public static void setShaderTexture(int i, ResourceLocation resourceLocation){
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(resourceLocation);
        abstractTexture.setUseMipmaps(false);
        RenderSystem.setShaderTexture(i, abstractTexture.getTextureView());
    }
}
