package xerca.xercafood.common.block_entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercafood.common.XercaFood;
import xerca.xercafood.common.block.Blocks;

public class BlockEntities {
    public static BlockEntityType<BlockEntityDoner> DONER;

    public static void registerBlockEntities() {
        DONER = FabricBlockEntityTypeBuilder.create(BlockEntityDoner::new, Blocks.BLOCK_DONER).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(XercaFood.MODID, "doner"), DONER);
    }
}
