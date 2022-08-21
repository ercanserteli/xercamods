package xerca.xercamusic.common.tile_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

public class TileEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, XercaMusic.MODID);

    public static final RegistryObject<BlockEntityType<?>> METRONOME = BLOCK_ENTITIES.register("metronome", () -> BlockEntityType.Builder.of(TileEntityMetronome::new, Blocks.BLOCK_METRONOME.get()).build(null));
    public static final RegistryObject<BlockEntityType<?>> MUSIC_BOX = BLOCK_ENTITIES.register("music_box", () -> BlockEntityType.Builder.of(TileEntityMusicBox::new, Blocks.MUSIC_BOX.get()).build(null));

}
