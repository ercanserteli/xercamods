package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.packets.PaletteUpdatePacket;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class GuiPalette extends BasePalette {

    protected GuiPalette(ItemStack paletteStack, Component title) {
        super(title, paletteStack);
    }

    @Override
    public void init() {
        paletteX = PALETTE_XS[PALETTE_XS.length - 1];
        paletteY = PALETTE_YS[PALETTE_YS.length - 1];
        if (paletteX == -1000 || paletteY == -1000) {
            paletteX = 140;
            paletteY = 40;
        }
        updatePalettePos(0, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {
        super.render(guiGraphics, mouseX, mouseY, f);

        renderCursor(guiGraphics, mouseX, mouseY);
    }

    private void renderCursor(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isCarryingColor && carriedColor != null) {
            carriedColor.setGLColor();
            guiGraphics.blit(PALETTE_TEXTURES, mouseX - BRUSH_SPRITE_SIZE / 2, mouseY - BRUSH_SPRITE_SIZE / 2, BRUSH_SPRITE_X + BRUSH_SPRITE_SIZE, BRUSH_SPRITE_Y, DROP_SPRITE_WIDTH, BRUSH_SPRITE_SIZE);

        } else if (isCarryingWater) {
            WATER_COLOR.setGLColor();
            guiGraphics.blit(PALETTE_TEXTURES, mouseX - BRUSH_SPRITE_SIZE / 2, mouseY - BRUSH_SPRITE_SIZE / 2, BRUSH_SPRITE_X + BRUSH_SPRITE_SIZE, BRUSH_SPRITE_Y, DROP_SPRITE_WIDTH, BRUSH_SPRITE_SIZE);
        }
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        if (isCarryingPalette) {
            boolean ret = super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
            updatePalettePos(deltaX, deltaY);
            return ret;
        }
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    private void updatePalettePos(double deltaX, double deltaY) {
        paletteX += deltaX;
        paletteY += deltaY;

        PALETTE_XS[PALETTE_XS.length - 1] = paletteX;
        PALETTE_YS[PALETTE_YS.length - 1] = paletteY;
    }

    @Override
    public void removed() {
        if (paletteDirty) {
            ClientPlayNetworking.send(new PaletteUpdatePacket(customColors));
        }
    }
}
