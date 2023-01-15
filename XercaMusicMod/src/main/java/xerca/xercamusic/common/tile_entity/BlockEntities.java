package xerca.xercamusic.common.tile_entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

public class BlockEntities {
    public static BlockEntityType<?> METRONOME;
    public static BlockEntityType<?> MUSIC_BOX;

    public static void registerBlockEntities() {
        XercaMusic.LOGGER.info("XercaMusic: Registering tile entities");
        METRONOME = FabricBlockEntityTypeBuilder.create(TileEntityMetronome::new, Blocks.BLOCK_METRONOME).build();
        MUSIC_BOX = FabricBlockEntityTypeBuilder.create(TileEntityMusicBox::new, Blocks.MUSIC_BOX).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(XercaMusic.MODID, "metronome"), METRONOME);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(XercaMusic.MODID, "music_box"), MUSIC_BOX);
    }
}
