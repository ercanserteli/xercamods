package xerca.xercapaint;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlitUtil {

    public static void blit(@NotNull GuiGraphics internalGuiGraphics, ResourceLocation sprite, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        internalGuiGraphics.blit(
                RenderType::guiTextured,
                sprite,
                x,
                y,
                uOffset,
                vOffset,
                uWidth,
                vHeight,
                256,
                256
        );
    }

    public static void blit(@NotNull GuiGraphics internalGuiGraphics, ResourceLocation sprite, int x, int y, float uOffset, float vOffset, int width, int height, int texWidth, int texHeight) {
        internalGuiGraphics.blit(
                RenderType::guiTextured,
                sprite,
                x,
                y,
                uOffset,
                vOffset,
                width,
                height,
                width,
                height,
                texWidth,
                texHeight
        );
    }
}
