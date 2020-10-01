package xerca.xercamod.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.ContainerFunctionalBookcase;
import xerca.xercamod.common.XercaMod;

import java.awt.*;

import static net.minecraftforge.fml.client.gui.GuiUtils.drawTexturedModalRect;

@OnlyIn(Dist.CLIENT)
public class GuiFunctionalBookcase extends ContainerScreen<ContainerFunctionalBookcase> {

    // This is the resource location for the background image for the GUI
    private static final ResourceLocation texture = new ResourceLocation(XercaMod.MODID, "textures/gui/bookcase.png");

    public GuiFunctionalBookcase(ContainerFunctionalBookcase container, PlayerInventory invPlayer, ITextComponent title) {
        super(container, invPlayer, title);
        // Width and height of the gui
        xSize = 176;
        ySize = 166;
    }

    // Draw the background for the GUI - rendered first
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize, 1);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
}
