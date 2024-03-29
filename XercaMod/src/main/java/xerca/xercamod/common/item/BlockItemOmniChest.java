package xerca.xercamod.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.client.OmniChestItemRenderer;
import xerca.xercamod.common.Config;

import java.util.function.Consumer;

import net.minecraft.world.item.Item.Properties;

public class BlockItemOmniChest extends BlockItem {
    public BlockItemOmniChest(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer)
    {
        super.initializeClient(consumer);
        consumer.accept(RenderProp.INSTANCE);
    }

    public static class RenderProp implements IClientItemExtensions {
        public static final RenderProp INSTANCE = new RenderProp();

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return new OmniChestItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }
    }
}
