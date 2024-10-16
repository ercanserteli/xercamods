package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
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
        if(paletteX == -1000 || paletteY == -1000){
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
            carriedColor.setGLColor();
            guiGraphics.blit(paletteTextures, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);

        }else if(isCarryingWater){
            waterColor.setGLColor();
            guiGraphics.blit(paletteTextures, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);
        }
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        if(isCarryingPalette){
            boolean ret = super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            updatePalettePos(deltaX, deltaY);
            return ret;
        }
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    private void updatePalettePos(double deltaX, double deltaY){
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