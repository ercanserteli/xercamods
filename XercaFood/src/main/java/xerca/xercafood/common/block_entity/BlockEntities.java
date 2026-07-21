package xerca.xercafood.common.block_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block.Blocks;

public class BlockEntities {
    private BlockEntities() {
    }

    public static final BlockEntityType<BlockEntityDoner> DONER = new BlockEntityType<>(BlockEntityDoner::new, Blocks.BLOCK_DONER);

    public static void registerBlockEntities(net.neoforged.neoforge.registries.RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        helper.register(Mod.id("doner"), DONER);
    }
}
