package xerca.xercamusic.common.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
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
    public static final ItemMusicSheet MUSIC_SHEET = Null();

    public static ItemInstrument[] instruments;

    public static MusicCreativeTab musicTab;

    public static final IRecipeSerializer<RecipeNoteCloning> CRAFTING_SPECIAL_NOTECLONING = Null();


    public static void setup() {
    }

    static Item makeItem(String name, ItemGroup tab){
        Item item = new Item(new Item.Properties().group(tab));
        item.setRegistryName(name);
        return item;
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeNoteCloning::new).setRegistryName(XercaMusic.MODID + ":crafting_special_notecloning"));
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            XercaMusic.LOGGER.info("XercaMusic: Registering items");
            musicTab = new MusicCreativeTab();

            instruments = new ItemInstrument[]{
                    new ItemInstrument("guitar", false),
                    new ItemInstrument("lyre", false),
                    new ItemInstrument("banjo", false),
                    new ItemInstrument("drum", false),
                    new ItemInstrument("cymbal", false),
                    new ItemInstrument("drum_kit", false),
                    new ItemInstrument("xylophone", false),
                    new ItemInstrument("tubular_bell", false),
                    new ItemInstrument("sansula", false),
                    new ItemInstrument("violin", true),
                    new ItemInstrument("cello", true),
                    new ItemInstrument("flute", true),
                    new ItemInstrument("saxophone", true),
                    new ItemInstrument("god", false),
            };

            event.getRegistry().registerAll(instruments);
            event.getRegistry().registerAll(
                    new ItemMusicSheet(),
                    new BlockItem(Blocks.MUSIC_BOX, new Item.Properties().group(Items.musicTab)).setRegistryName("music_box"),
                    new BlockItem(Blocks.BLOCK_METRONOME, new Item.Properties().group(Items.musicTab)).setRegistryName("metronome")
            );
        }
    }

}