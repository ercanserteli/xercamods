package xerca.xercamod.client;

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
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize, 1);
    }

    // Draw the foreground for the GUI - rendered after the slots, but before the dragged items and tooltips
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        final int LABEL_XPOS = 5;
        final int LABEL_YPOS = 5;
        font.drawString(title.getFormattedText(), LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
