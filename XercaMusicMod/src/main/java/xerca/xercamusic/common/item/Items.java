package xerca.xercamusic.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

public final class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, XercaMusic.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, XercaMusic.MODID);

    public static final RegistryObject<ItemInstrument> HARP_MC = ITEMS.register("harp_mc", () -> new ItemInstrument(false, -1, 0, 7, new Item.Properties()));
    public static final RegistryObject<ItemMusicSheet> MUSIC_SHEET = ITEMS.register("music_sheet", ItemMusicSheet::new);

    public static final RegistryObject<Item> GUITAR = ITEMS.register("guitar", () -> new ItemInstrument(false, 0, 0, 6));
    public static final RegistryObject<Item> LYRE = ITEMS.register("lyre", () -> new ItemInstrument(false, 1, 1, 5));
    public static final RegistryObject<Item> BANJO = ITEMS.register("banjo", () -> new ItemInstrument(false, 2, 0, 4));
    public static final RegistryObject<Item> DRUM = ITEMS.register("drum", () -> new ItemInstrument(false, 3, 1, 4));
    public static final RegistryObject<Item> CYMBAL = ITEMS.register("cymbal", () -> new ItemInstrument(false, 4, 0, 4));
    public static final RegistryObject<Item> DRUM_KIT = ITEMS.register("drum_kit", () -> new ItemBlockInstrument(false, 5, 0, 7, Blocks.DRUM_KIT.get()));
    public static final RegistryObject<Item> XYLOPHONE = ITEMS.register( "xylophone", () -> new ItemInstrument(false, 6, 0, 5));
    public static final RegistryObject<Item> TUBULAR_BELL = ITEMS.register( "tubular_bell", () -> new ItemInstrument(false, 7, 1, 4));
    public static final RegistryObject<Item> SANSULA = ITEMS.register( "sansula", () -> new ItemInstrument(false, 8, 1, 5));
    public static final RegistryObject<Item> VIOLIN = ITEMS.register( "violin", () -> new ItemInstrument(true, 9, 1, 5));
    public static final RegistryObject<Item> CELLO = ITEMS.register( "cello", () -> new ItemInstrument(true, 10, 0, 6));
    public static final RegistryObject<Item> FLUTE = ITEMS.register( "flute", () -> new ItemInstrument(true, 11, 1, 6));
    public static final RegistryObject<Item> SAXOPHONE = ITEMS.register( "saxophone", () -> new ItemInstrument(true, 12, 0, 4));
    public static final RegistryObject<Item> GOD = ITEMS.register( "god", () -> new ItemInstrument(false, 13, 0, 5));
    public static final RegistryObject<Item> PIANO = ITEMS.register("piano", () -> new ItemBlockInstrument(false, 14, 0, 7, Blocks.PIANO.get()));
    public static final RegistryObject<Item> OBOE = ITEMS.register( "oboe", () -> new ItemInstrument(true, 15,0, 4));
    public static final RegistryObject<Item> REDSTONE_GUITAR = ITEMS.register( "redstone_guitar", () -> new ItemInstrument(true, 16,0, 5));
    public static final RegistryObject<Item> FRENCH_HORN = ITEMS.register( "french_horn", () -> new ItemInstrument(true, 17,0, 5));
    public static final RegistryObject<Item> BASS_GUITAR = ITEMS.register( "bass_guitar", () -> new ItemInstrument(false, 18,1, 4));

    public static final RegistryObject<Item> MUSIC_BOX = ITEMS.register("music_box", () -> new BlockItem(Blocks.MUSIC_BOX.get(), new Item.Properties()));
    public static final RegistryObject<Item> METRONOME = ITEMS.register("metronome", () -> new BlockItem(Blocks.BLOCK_METRONOME.get(), new Item.Properties()));

    public static IItemInstrument[] instruments;


    public static final RegistryObject<RecipeSerializer<RecipeNoteCloning>> CRAFTING_SPECIAL_NOTECLONING = RECIPE_SERIALIZERS.register(
            "crafting_special_notecloning", () -> new SimpleCraftingRecipeSerializer<>(RecipeNoteCloning::new));

    public static void setup() {
        instruments = new IItemInstrument[]{
                (IItemInstrument)GUITAR.get(),
                (IItemInstrument)LYRE.get(),
                (IItemInstrument)BANJO.get(),
                (IItemInstrument)DRUM.get(),
                (IItemInstrument)CYMBAL.get(),
                (IItemInstrument)DRUM_KIT.get(),
                (IItemInstrument)XYLOPHONE.get(),
                (IItemInstrument)TUBULAR_BELL.get(),
                (IItemInstrument)SANSULA.get(),
                (IItemInstrument)VIOLIN.get(),
                (IItemInstrument)CELLO.get(),
                (IItemInstrument)FLUTE.get(),
                (IItemInstrument)SAXOPHONE.get(),
                (IItemInstrument)GOD.get(),
                (IItemInstrument)PIANO.get(),
                (IItemInstrument)OBOE.get(),
                (IItemInstrument)REDSTONE_GUITAR.get(),
                (IItemInstrument)FRENCH_HORN.get(),
                (IItemInstrument)BASS_GUITAR.get()
        };
    }
}