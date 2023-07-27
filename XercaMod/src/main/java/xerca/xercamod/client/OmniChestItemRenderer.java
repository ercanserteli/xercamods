package xerca.xercamod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.item.BlockItemOmniChest;
import xerca.xercamod.common.tile_entity.TileEntityOmniChest;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OmniChestItemRenderer extends BlockEntityWithoutLevelRenderer
{
    final BlockEntityRenderDispatcher dispatcher;
    private final TileEntityOmniChest chest;

    public OmniChestItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
        this.dispatcher = dispatcher;
        this.chest = new TileEntityOmniChest(BlockPos.ZERO, Blocks.OMNI_CHEST.get().defaultBlockState());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (stack.getItem() instanceof BlockItemOmniChest) {
            this.dispatcher.renderItem(chest, matrixStack, buffer, combinedLight, combinedOverlay);
        }
    }
}
