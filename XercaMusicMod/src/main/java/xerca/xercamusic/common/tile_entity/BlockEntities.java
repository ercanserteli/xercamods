package xerca.xercamusic.common.tile_entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.Blocks;

public class BlockEntities {
    public static BlockEntityType<?> METRONOME;
    public static BlockEntityType<?> MUSIC_BOX;

    private BlockEntities() {
    }

    public static void registerBlockEntities() {
        Mod.LOGGER.info("XercaMusic: Registering tile entities");
        METRONOME = BlockEntityType.Builder.of(TileEntityMetronome::new, Blocks.BLOCK_METRONOME).build();
        MUSIC_BOX = BlockEntityType.Builder.of(TileEntityMusicBox::new, Blocks.MUSIC_BOX).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("metronome"), METRONOME);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Mod.id("music_box"), MUSIC_BOX);
    }
}
