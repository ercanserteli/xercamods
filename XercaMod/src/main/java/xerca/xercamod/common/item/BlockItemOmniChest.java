package xerca.xercamod.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;
import xerca.xercamod.client.OmniChestItemRenderer;
import xerca.xercamod.common.Config;

import java.util.function.Consumer;

public class BlockItemOmniChest extends BlockConditionedItem {
    public BlockItemOmniChest(Block block, Properties properties)
    {
        super(block, properties, Config::isOmniChestEnabled);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer)
    {
        super.initializeClient(consumer);
        consumer.accept(RenderProp.INSTANCE);
    }

    public static class RenderProp implements IItemRenderProperties {
        public static RenderProp INSTANCE = new RenderProp();

        @Override
        public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return new OmniChestItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }
    }
}
