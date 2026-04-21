package xerca.xercablocks.block_entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.Blocks;

public final class BlockEntities {
    public static BlockEntityType<FunctionalBookcaseBlockEntity> FUNCTIONAL_BOOKCASE;

    private BlockEntities() {
    }

    public static void registerBlockEntities() {
        FUNCTIONAL_BOOKCASE = BlockEntityType.Builder.of(FunctionalBookcaseBlockEntity::new, Blocks.BLOCK_BOOKCASE).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("functional_bookcase"), FUNCTIONAL_BOOKCASE);
    }
}
