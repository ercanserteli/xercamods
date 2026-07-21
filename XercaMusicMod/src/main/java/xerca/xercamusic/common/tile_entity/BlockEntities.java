package xerca.xercamusic.common.tile_entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.Blocks;

public class BlockEntities {
    public static final BlockEntityType<?> METRONOME = new BlockEntityType<>(TileEntityMetronome::new, Blocks.BLOCK_METRONOME);
    public static final BlockEntityType<?> MUSIC_BOX = new BlockEntityType<>(TileEntityMusicBox::new, Blocks.MUSIC_BOX);

    private BlockEntities() {
    }

    public static void register(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        helper.register(Mod.id("metronome"), METRONOME);
        helper.register(Mod.id("music_box"), MUSIC_BOX);
    }
}
