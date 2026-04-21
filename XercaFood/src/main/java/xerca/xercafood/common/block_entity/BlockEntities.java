package xerca.xercafood.common.block_entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block.Blocks;

public class BlockEntities {
    public static BlockEntityType<BlockEntityDoner> DONER;

    public static void registerBlockEntities() {
        DONER = BlockEntityType.Builder.of(BlockEntityDoner::new, Blocks.BLOCK_DONER).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("doner"), DONER);
    }
}
