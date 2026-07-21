package xerca.xercablocks.block_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.Blocks;

public final class BlockEntities {
    public static final BlockEntityType<FunctionalBookcaseBlockEntity> FUNCTIONAL_BOOKCASE = new BlockEntityType<>(FunctionalBookcaseBlockEntity::new, Blocks.BLOCK_BOOKCASE);

    private BlockEntities() {
    }

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        helper.register(Mod.id("functional_bookcase"), FUNCTIONAL_BOOKCASE);
    }
}
