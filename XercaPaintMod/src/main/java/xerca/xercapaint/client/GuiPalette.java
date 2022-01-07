package xerca.xercapaint.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.packets.PaletteUpdatePacket;

@OnlyIn(Dist.CLIENT)
public class GuiPalette extends BasePalette {

    protected GuiPalette(CompoundNBT paletteTag, ITextComponent title) {
        super(title, paletteTag);
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
    public void tick() {
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float f) {
        super.render(matrixStack, mouseX, mouseY, f);

        renderCursor(matrixStack, mouseX, mouseY);
    }

    private void renderCursor(MatrixStack matrixStack, int mouseX, int mouseY){
        if(isCarryingColor){
            carriedColor.setGLColor();
            blit(matrixStack, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);

        }else if(isCarryingWater){
            waterColor.setGLColor();
            blit(matrixStack, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);
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
        if (dirty) {
            PaletteUpdatePacket pack = new PaletteUpdatePacket(customColors);
            XercaPaint.NETWORK_HANDLER.sendToServer(pack);
        }
    }
}