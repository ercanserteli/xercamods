package xerca.xercamusic.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import xerca.xercamusic.common.XercaMusic;


public class Blocks {
    public static final Block BLOCK_METRONOME = new BlockMetronome(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD));
    public static final Block MUSIC_BOX = new BlockMusicBox(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD).isRedstoneConductor((blockState, blockGetter, blockPos)->false));
    public static final Block PIANO = new BlockPiano();
    public static final Block DRUM_KIT = new BlockDrums();

    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(XercaMusic.MODID, "block_metronome"), BLOCK_METRONOME);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(XercaMusic.MODID, "music_box"), MUSIC_BOX);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(XercaMusic.MODID, "piano"), PIANO);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(XercaMusic.MODID, "drum_kit"), DRUM_KIT);
    }
}
