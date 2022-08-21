package xerca.xercamusic.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercamusic.common.XercaMusic;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, XercaMusic.MODID);
    public static final RegistryObject<Block> BLOCK_METRONOME = BLOCKS.register("block_metronome", BlockMetronome::new);
    public static final RegistryObject<Block> MUSIC_BOX = BLOCKS.register("music_box", BlockMusicBox::new);
    public static final RegistryObject<Block> PIANO = BLOCKS.register("piano", BlockPiano::new);
    public static final RegistryObject<Block> DRUM_KIT = BLOCKS.register("drum_kit", BlockDrums::new);
}
