package xerca.xercafood.common.block_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block.Blocks;

public class BlockEntities {
    private BlockEntities() {
    }

    public static final BlockEntityType<BlockEntityDoner> DONER = BlockEntityType.Builder.of(BlockEntityDoner::new, Blocks.BLOCK_DONER).build(null);

    public static void registerBlockEntities(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        helper.register(Mod.id("doner"), DONER);
    }
}
