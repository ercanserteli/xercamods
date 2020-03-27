package xerca.xercapaint.client;

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

        paletteX = 140;
        paletteY = 40;
    }

    @Override
    public void init() {
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        super.render(mouseX, mouseY, f);

        renderCursor(mouseX, mouseY);
    }

    private void renderCursor(int mouseX, int mouseY){
        if(isCarryingColor){
            carriedColor.setGLColor();
            blit(mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);

        }else if(isCarryingWater){
            waterColor.setGLColor();
            blit(mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);
        }
    }

    @Override
    public void removed() {
        if (dirty) {
            PaletteUpdatePacket pack = new PaletteUpdatePacket(customColors);
            XercaPaint.NETWORK_HANDLER.sendToServer(pack);
        }
    }
}