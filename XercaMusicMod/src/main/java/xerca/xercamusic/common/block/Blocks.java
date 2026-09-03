package xerca.xercamusic.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import xerca.xercamusic.common.Mod;


public class Blocks {
    public static final Block BLOCK_METRONOME = new BlockMetronome(properties("block_metronome").mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD));
    public static final Block MUSIC_BOX = new BlockMusicBox(properties("music_box").mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.f, 6.f).sound(SoundType.WOOD).isRedstoneConductor((blockState, blockGetter, blockPos) -> false));
    public static final Block PIANO = new BlockPiano(properties("piano"));
    public static final Block DRUM_KIT = new BlockDrums(properties("drum_kit"));
    public static final Block STEELPAN = new BlockSteelpan(properties("steelpan"));

    private Blocks() {
    }

    private static BlockBehaviour.Properties properties(String name) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Mod.id(name)));
    }

    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.BLOCK, Mod.id("block_metronome"), BLOCK_METRONOME);
        Registry.register(BuiltInRegistries.BLOCK, Mod.id("music_box"), MUSIC_BOX);
        Registry.register(BuiltInRegistries.BLOCK, Mod.id("piano"), PIANO);
        Registry.register(BuiltInRegistries.BLOCK, Mod.id("drum_kit"), DRUM_KIT);
        Registry.register(BuiltInRegistries.BLOCK, Mod.id("steelpan"), STEELPAN);
    }
}
