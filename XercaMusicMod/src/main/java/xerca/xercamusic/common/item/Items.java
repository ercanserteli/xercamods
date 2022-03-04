package xerca.xercamusic.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamusic.common.MusicCreativeTab;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

import static xerca.xercamusic.common.XercaMusic.Null;

@ObjectHolder(XercaMusic.MODID)
public final class Items {
    public static final ItemInstrument HARP_MC = Null();
    public static final ItemInstrument CYMBAL = Null();
    public static final ItemInstrument DRUM_KIT = Null();
    public static final ItemInstrument CELLO = Null();
    public static final ItemInstrument GUITAR = Null();
    public static final ItemInstrument LYRE = Null();
    public static final ItemInstrument DRUM = Null();
    public static final ItemInstrument FLUTE = Null();
    public static final ItemInstrument BANJO = Null();
    public static final ItemInstrument GOD = Null();
    public static final ItemInstrument SANSULA = Null();
    public static final ItemInstrument SAXOPHONE = Null();
    public static final ItemInstrument TUBULAR_BELL = Null();
    public static final ItemInstrument VIOLIN = Null();
    public static final ItemInstrument XYLOPHONE = Null();
    public static final ItemInstrument PIANO = Null();
    public static final ItemInstrument OBOE = Null();
    public static final ItemInstrument REDSTONE_GUITAR = Null();
    public static final ItemInstrument FRENCH_HORN = Null();
    public static final ItemInstrument BASS_GUITAR = Null();
    public static final ItemMusicSheet MUSIC_SHEET = Null();

    public static ItemInstrument[] instruments;

    public static MusicCreativeTab musicTab;

    public static final RecipeSerializer<RecipeNoteCloning> CRAFTING_SPECIAL_NOTECLONING = Null();

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerRecipes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeNoteCloning::new).setRegistryName(XercaMusic.MODID + ":crafting_special_notecloning"));
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            XercaMusic.LOGGER.info("XercaMusic: Registering items");
            musicTab = new MusicCreativeTab();

            instruments = new ItemInstrument[]{
                    new ItemInstrument("guitar", false, 0, 0, 6),
                    new ItemInstrument("lyre", false, 1, 1, 5),
                    new ItemInstrument("banjo", false, 2, 0, 4),
                    new ItemInstrument("drum", false, 3, 1, 4),
                    new ItemInstrument("cymbal", false, 4, 0, 4),
                    new ItemBlockInstrument("drum_kit", false, 5, Blocks.DRUM_KIT, 0, 7),
                    new ItemInstrument("xylophone", false, 6, 0, 5),
                    new ItemInstrument("tubular_bell", false, 7, 1, 4),
                    new ItemInstrument("sansula", false, 8, 1, 5),
                    new ItemInstrument("violin", true, 9, 1, 5),
                    new ItemInstrument("cello", true, 10, 0, 6),
                    new ItemInstrument("flute", true, 11, 1, 6),
                    new ItemInstrument("saxophone", true, 12, 0, 4),
                    new ItemInstrument("god", false, 13, 0, 5),
                    new ItemBlockInstrument("piano", false, 14, Blocks.PIANO, 0, 7),
                    new ItemInstrument("oboe", true, 15,0, 4),
                    new ItemInstrument("redstone_guitar", true, 16,0, 5),
                    new ItemInstrument("french_horn", true, 17,0, 5),
                    new ItemInstrument("bass_guitar", false, 18,1, 4),
            };

            event.getRegistry().registerAll(instruments);
            event.getRegistry().registerAll(
                    new ItemInstrument("harp_mc", false, -1, 0, 7, new Item.Properties()),
                    new ItemMusicSheet(),
                    new BlockItem(Blocks.MUSIC_BOX, new Item.Properties().tab(Items.musicTab)).setRegistryName("music_box"),
                    new BlockItem(Blocks.BLOCK_METRONOME, new Item.Properties().tab(Items.musicTab)).setRegistryName("metronome")
            );

            for(ItemInstrument i : instruments){
                if(i instanceof ItemBlockInstrument){
                    ((ItemBlockInstrument)i).addToBlockToItemMap(Item.BY_BLOCK, i);
                }
            }
        }
    }

}