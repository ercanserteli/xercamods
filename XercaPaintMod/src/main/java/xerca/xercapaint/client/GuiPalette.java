package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.packets.PaletteUpdatePacket;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class GuiPalette extends BasePalette {

    protected GuiPalette(@NotNull ItemStack paletteStack, Component title) {
        super(title, paletteStack);
    }

    @Override
    public void init() {
        paletteX = paletteXs[paletteXs.length - 1];
        paletteY = paletteYs[paletteYs.length - 1];
        if (paletteX == -1000 || paletteY == -1000) {
            paletteX = 140;
            paletteY = 40;
        }
        updatePalettePos(0, 0);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {
        super.render(guiGraphics, mouseX, mouseY, f);

        renderCursor(guiGraphics, mouseX, mouseY);
    }

    private void renderCursor(GuiGraphics guiGraphics, int mouseX, int mouseY){
        if(isCarryingColor){
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, paletteTextures, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize, 256, 256, carriedColor.rgbVal());

        }else if(isCarryingWater){
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, paletteTextures, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize, 256, 256, waterColor.rgbVal());
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double deltaX, double deltaY) {
        double posX = mouseButtonEvent.x();
        double posY = mouseButtonEvent.y();
        int mouseButton = mouseButtonEvent.button();

        if(isCarryingPalette){
            boolean ret = super.mouseDragged(mouseButtonEvent, deltaX, deltaY);
            updatePalettePos(deltaX, deltaY);
            return ret;
        }
        return super.mouseDragged(mouseButtonEvent, deltaX, deltaY);
    }

    private void updatePalettePos(double deltaX, double deltaY) {
        paletteX += deltaX;
        paletteY += deltaY;

        paletteXs[paletteXs.length - 1] = paletteX;
        paletteYs[paletteYs.length - 1] = paletteY;
    }

    @Override
    public void removed() {
        if (paletteDirty) {
            ClientPlayNetworking.send(new PaletteUpdatePacket(customColors));
        }
    }
}