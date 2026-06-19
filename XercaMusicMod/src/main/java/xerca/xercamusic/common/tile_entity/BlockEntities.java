package xerca.xercamusic.common.tile_entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.Blocks;

public class BlockEntities {
    public static final BlockEntityType<?> METRONOME = FabricBlockEntityTypeBuilder.create(TileEntityMetronome::new, Blocks.BLOCK_METRONOME).build();
    public static final BlockEntityType<?> MUSIC_BOX = FabricBlockEntityTypeBuilder.create(TileEntityMusicBox::new, Blocks.MUSIC_BOX).build();

    private BlockEntities() {
    }

    public static void registerBlockEntities() {
        Mod.LOGGER.info("XercaMusic: Registering tile entities");
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("metronome"), METRONOME);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("music_box"), MUSIC_BOX);
    }
}
