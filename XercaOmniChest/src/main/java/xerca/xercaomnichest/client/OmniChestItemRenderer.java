package xerca.xercaomnichest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import xerca.xercaomnichest.block.Blocks;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class OmniChestItemRenderer extends BlockEntityWithoutLevelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final BlockEntityOmniChest chest;

    public OmniChestItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
        this.chest = new BlockEntityOmniChest(BlockPos.ZERO, Blocks.OMNI_CHEST.defaultBlockState());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        if (dispatcher != null) {
            dispatcher.renderItem(chest, matrices, vertexConsumers, light, overlay);
        }
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext displayContext, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        renderByItem(stack, displayContext, matrices, vertexConsumers, light, overlay);
    }
}
