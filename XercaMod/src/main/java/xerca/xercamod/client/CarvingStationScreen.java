package xerca.xercamod.client;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import xerca.xercamod.common.ContainerCarvingStation;
import xerca.xercamod.common.crafting.RecipeCarvingStation;

import java.util.List;

public class CarvingStationScreen extends AbstractContainerScreen<ContainerCarvingStation> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/container/stonecutter.png");
    private float sliderProgress;
    /** Is {@code true} if the player clicked on the scroll wheel in the GUI. */
    private boolean clickedOnSroll;
    /**
     * The index of the first recipe to display.
     * The number of recipes displayed at any time is 12 (4 recipes per row, and 3 rows). If the player scrolled down one
     * row, this value would be 4 (representing the index of the first slot on the second row).
     */
    private int recipeIndexOffset;
    private boolean hasItemsInInputSlot;

    public CarvingStationScreen(ContainerCarvingStation containerIn, Inventory playerInv, Component titleIn) {
        super(containerIn, playerInv, titleIn);
        containerIn.setInventoryUpdateListener(this::onInventoryUpdate);
        --this.titleLabelY;
    }

    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k = (int)(41.0F * this.sliderProgress);
        this.blit(matrixStack, i + 119, j + 15 + k, 176 + (this.canScroll() ? 0 : 12), 0, 12, 15);
        int l = this.leftPos + 52;
        int i1 = this.topPos + 14;
        int j1 = this.recipeIndexOffset + 12;
        this.renderButtons(matrixStack, x, y, l, i1, j1);
        this.drawRecipesItems(l, i1, j1);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int x, int y) {
        super.renderTooltip(matrixStack, x, y);
        if (this.hasItemsInInputSlot) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.recipeIndexOffset + 12;
            List<RecipeCarvingStation> list = this.menu.getRecipeList();

            for(int l = this.recipeIndexOffset; l < k && l < this.menu.getRecipeListSize(); ++l) {
                int i1 = l - this.recipeIndexOffset;
                int j1 = i + i1 % 4 * 16;
                int k1 = j + i1 / 4 * 18 + 2;
                if (x >= j1 && x < j1 + 16 && y >= k1 && y < k1 + 18) {
                    this.renderTooltip(matrixStack, list.get(l).getResultItem(), x, y);
                }
            }
        }

    }

    private void renderButtons(PoseStack p_238853_1_, int p_238853_2_, int p_238853_3_, int p_238853_4_, int p_238853_5_, int p_238853_6_) {
        for(int i = this.recipeIndexOffset; i < p_238853_6_ && i < this.menu.getRecipeListSize(); ++i) {
            int j = i - this.recipeIndexOffset;
            int k = p_238853_4_ + j % 4 * 16;
            int l = j / 4;
            int i1 = p_238853_5_ + l * 18 + 2;
            int j1 = this.imageHeight;
            if (i == this.menu.getSelectedRecipe()) {
                j1 += 18;
            } else if (p_238853_2_ >= k && p_238853_3_ >= i1 && p_238853_2_ < k + 16 && p_238853_3_ < i1 + 18) {
                j1 += 36;
            }

            this.blit(p_238853_1_, k, i1 - 1, 0, j1, 16, 18);
        }

    }

    private void drawRecipesItems(int left, int top, int recipeIndexOffsetMax) {
        List<RecipeCarvingStation> list = this.menu.getRecipeList();

        for(int i = this.recipeIndexOffset; i < recipeIndexOffsetMax && i < this.menu.getRecipeListSize(); ++i) {
            int j = i - this.recipeIndexOffset;
            int k = left + j % 4 * 16;
            int l = j / 4;
            int i1 = top + l * 18 + 2;
            this.minecraft.getItemRenderer().renderAndDecorateItem(list.get(i).getResultItem(), k, i1);
        }

    }

    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
        this.clickedOnSroll = false;
        if (this.hasItemsInInputSlot) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.recipeIndexOffset + 12;

            for(int l = this.recipeIndexOffset; l < k; ++l) {
                int i1 = l - this.recipeIndexOffset;
                double d0 = mouseX - (double)(i + i1 % 4 * 16);
                double d1 = mouseY - (double)(j + i1 / 4 * 18);
                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && this.menu.clickMenuButton(this.minecraft.player, l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
                    return true;
                }
            }

            i = this.leftPos + 119;
            j = this.topPos + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54)) {
                this.clickedOnSroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, p_231044_5_);
    }

    public boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
        if (this.clickedOnSroll && this.canScroll()) {
            int i = this.topPos + 14;
            int j = i + 54;
            this.sliderProgress = ((float)p_231045_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.recipeIndexOffset = (int)((double)(this.sliderProgress * (float)this.getHiddenRows()) + 0.5D) * 4;
            return true;
        } else {
            return super.mouseDragged(p_231045_1_, p_231045_3_, p_231045_5_, p_231045_6_, p_231045_8_);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double p_231043_5_) {
        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float)((double)this.sliderProgress - p_231043_5_ / (double)i);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.recipeIndexOffset = (int)((double)(this.sliderProgress * (float)i) + 0.5D) * 4;
        }

        return true;
    }

    private boolean canScroll() {
        return this.hasItemsInInputSlot && this.menu.getRecipeListSize() > 12;
    }

    protected int getHiddenRows() {
        return (this.menu.getRecipeListSize() + 4 - 1) / 4 - 3;
    }

    /**
     * Called every time this screen's container is changed (is marked as dirty).
     */
    private void onInventoryUpdate() {
        this.hasItemsInInputSlot = this.menu.hasItemsinInputSlot();
        if (!this.hasItemsInInputSlot) {
            this.sliderProgress = 0.0F;
            this.recipeIndexOffset = 0;
        }

    }
}
