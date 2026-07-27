package xerca.xercaomnichest.block_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercaomnichest.block.Blocks;

public final class BlockEntities {
    public static final BlockEntityType<BlockEntityOmniChest> OMNI_CHEST = BlockEntityType.Builder.of(BlockEntityOmniChest::new, Blocks.OMNI_CHEST).build(null);

    private BlockEntities() {
    }
}
