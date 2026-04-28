package xerca.xercafood.common.block_entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block.Blocks;

public class BlockEntities {
    public static final BlockEntityType<BlockEntityDoner> DONER = BlockEntityType.Builder.of(BlockEntityDoner::new, Blocks.BLOCK_DONER).build();

    public static void registerBlockEntities() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("doner"), DONER);
    }
}
