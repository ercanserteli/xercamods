package xerca.xercamusic.common.tile_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.Blocks;

public class BlockEntities {
    public static final BlockEntityType<?> METRONOME = BlockEntityType.Builder.of(TileEntityMetronome::new, Blocks.BLOCK_METRONOME).build(null);
    public static final BlockEntityType<?> MUSIC_BOX = BlockEntityType.Builder.of(TileEntityMusicBox::new, Blocks.MUSIC_BOX).build(null);

    private BlockEntities() {
    }

    public static void registerBlockEntities(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        Mod.LOGGER.info("XercaMusic: Registering tile entities");
        helper.register(Mod.id("metronome"), METRONOME);
        helper.register(Mod.id("music_box"), MUSIC_BOX);
    }
}
