package xerca.xercamusic.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamusic.common.XercaMusic;

import static xerca.xercamusic.common.XercaMusic.Null;


@ObjectHolder(XercaMusic.MODID)
public class Blocks {
    public static final Block BLOCK_METRONOME = Null();
    public static final Block MUSIC_BOX = Null();
    public static final Block PIANO = Null();
    public static final Block DRUM_KIT = Null();

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            XercaMusic.LOGGER.debug("XercaMusic: Registering blocks");
            event.getRegistry().registerAll(
                    new BlockMetronome(),
                    new BlockMusicBox(),
                    new BlockPiano(),
                    new BlockDrums()
            );
        }
    }

}
