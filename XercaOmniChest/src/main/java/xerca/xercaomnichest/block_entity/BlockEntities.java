package xerca.xercaomnichest.block_entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.Blocks;

public final class BlockEntities {
    public static final BlockEntityType<BlockEntityOmniChest> OMNI_CHEST = FabricBlockEntityTypeBuilder.create(BlockEntityOmniChest::new, Blocks.OMNI_CHEST).build();

    private BlockEntities() {
    }

    public static void registerBlockEntities() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("omni_chest"), OMNI_CHEST);
    }
}
