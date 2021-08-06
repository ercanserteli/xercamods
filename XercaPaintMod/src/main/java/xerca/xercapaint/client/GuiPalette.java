package xerca.xercapaint.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.packets.PaletteUpdatePacket;

@OnlyIn(Dist.CLIENT)
public class GuiPalette extends BasePalette {

    protected GuiPalette(CompoundTag paletteTag, Component title) {
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float f) {
        super.render(matrixStack, mouseX, mouseY, f);

        renderCursor(matrixStack, mouseX, mouseY);
    }

    private void renderCursor(PoseStack matrixStack, int mouseX, int mouseY){
        if(isCarryingColor){
            carriedColor.setGLColor();
            blit(matrixStack, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);

        }else if(isCarryingWater){
            waterColor.setGLColor();
            blit(matrixStack, mouseX-brushSpriteSize/2, mouseY-brushSpriteSize/2, brushSpriteX+brushSpriteSize, brushSpriteY, dropSpriteWidth, brushSpriteSize);
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