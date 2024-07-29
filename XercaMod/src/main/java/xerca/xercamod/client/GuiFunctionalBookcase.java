package xerca.xercamod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.ContainerFunctionalBookcase;
import xerca.xercamod.common.XercaMod;

@OnlyIn(Dist.CLIENT)
public class GuiFunctionalBookcase extends AbstractContainerScreen<ContainerFunctionalBookcase> {

    // This is the resource location for the background image for the GUI
    private static final ResourceLocation texture = new ResourceLocation(XercaMod.MODID, "textures/gui/bookcase.png");

    public GuiFunctionalBookcase(ContainerFunctionalBookcase container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        // Width and height of the gui
        imageWidth = 176;
        imageHeight = 166;
    }

    // Draw the background for the GUI - rendered first
    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
