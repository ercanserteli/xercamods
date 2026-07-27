package xerca.xercamusic.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercamusic.common.Mod;


public class Blocks {
    public static final Block BLOCK_METRONOME = new BlockMetronome(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD));
    public static final Block MUSIC_BOX = new BlockMusicBox(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD).isRedstoneConductor((blockState, blockGetter, blockPos) -> false));
    public static final Block PIANO = new BlockPiano();
    public static final Block DRUM_KIT = new BlockDrums();

    private Blocks() {
    }

    public static void registerBlocks(RegisterEvent.RegisterHelper<Block> helper) {
        helper.register(Mod.id("block_metronome"), BLOCK_METRONOME);
        helper.register(Mod.id("music_box"), MUSIC_BOX);
        helper.register(Mod.id("piano"), PIANO);
        helper.register(Mod.id("drum_kit"), DRUM_KIT);
    }
}
