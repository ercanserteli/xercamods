package xerca.xercapaint.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.packets.PaletteUpdatePacket;

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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float f) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, f);

        renderCursor(guiGraphics, mouseX, mouseY);
    }

    private void renderCursor(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (isCarryingColor && carriedColor != null) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, mouseX - BRUSH_SPRITE_SIZE / 2, mouseY - BRUSH_SPRITE_SIZE / 2, BRUSH_SPRITE_X + BRUSH_SPRITE_SIZE, BRUSH_SPRITE_Y, DROP_SPRITE_WIDTH, BRUSH_SPRITE_SIZE, 256, 256, carriedColor.rgbVal());

        } else if (isCarryingWater) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PALETTE_TEXTURES, mouseX - BRUSH_SPRITE_SIZE / 2, mouseY - BRUSH_SPRITE_SIZE / 2, BRUSH_SPRITE_X + BRUSH_SPRITE_SIZE, BRUSH_SPRITE_Y, DROP_SPRITE_WIDTH, BRUSH_SPRITE_SIZE, 256, 256, WATER_COLOR.rgbVal());
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (isCarryingPalette) {
            boolean ret = super.mouseDragged(event, deltaX, deltaY);
            updatePalettePos(deltaX, deltaY);
            return ret;
        }
        return super.mouseDragged(event, deltaX, deltaY);
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
            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new PaletteUpdatePacket(customColors));
        }
    }
}
