package xerca.xercafood.common.tile_entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercafood.common.XercaFood;
import xerca.xercafood.common.block.Blocks;

public class TileEntities {
    public static BlockEntityType<?> DONER;

    public static void registerBlockEntities() {
        DONER = FabricBlockEntityTypeBuilder.create(TileEntityDoner::new, Blocks.BLOCK_DONER).build();
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(XercaFood.MODID, "doner"), DONER);
    }
}
