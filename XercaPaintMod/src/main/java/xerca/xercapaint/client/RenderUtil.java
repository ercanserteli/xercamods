package xerca.xercapaint.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

public class RenderUtil {
    public static void setShaderTexture(int i, Identifier Identifier){
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(Identifier);
        //abstractTexture.setUseMipmaps(false);
        //RenderSystem.setShaderTexture(i, abstractTexture.getTextureView());
    }
}
