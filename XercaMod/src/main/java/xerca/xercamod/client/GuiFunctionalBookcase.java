package xerca.xercamod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }
}
