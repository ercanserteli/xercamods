package xerca.xercamusic.common.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

public final class Items {
    public static final CreativeModeTab musicTab = FabricItemGroupBuilder.build(new ResourceLocation(XercaMusic.MODID, "music_tab"),
            () -> new ItemStack(Items.GUITAR));

    public static final Item HARP_MC = new ItemInstrument(-1, 0, 7, new Item.Properties());

    public static final Item GUITAR = new ItemInstrument(0, 0, 6);
    public static final Item LYRE = new ItemInstrument(1, 1, 5);
    public static final Item BANJO = new ItemInstrument(2, 0, 4);
    public static final Item DRUM = new ItemInstrument(3, 1, 4);
    public static final Item CYMBAL = new ItemInstrument(4, 0, 4);
    public static final Item DRUM_KIT = new ItemBlockInstrument(5, 0, 7, Blocks.DRUM_KIT);
    public static final Item XYLOPHONE = new ItemInstrument(6, 0, 5);
    public static final Item TUBULAR_BELL = new ItemInstrument(7, 1, 4);
    public static final Item SANSULA = new ItemInstrument(8, 1, 5);
    public static final Item VIOLIN = new ItemInstrument(9, 1, 5);
    public static final Item CELLO = new ItemInstrument(10, 0, 6);
    public static final Item FLUTE = new ItemInstrument(11, 1, 6);
    public static final Item SAXOPHONE = new ItemInstrument(12, 0, 4);
    public static final Item GOD = new ItemInstrument(13, 0, 5);
    public static final Item PIANO = new ItemBlockInstrument(14, 0, 7, Blocks.PIANO);
    public static final Item OBOE = new ItemInstrument(15,0, 4);
    public static final Item REDSTONE_GUITAR = new ItemInstrument(16,0, 5);
    public static final Item FRENCH_HORN = new ItemInstrument(17,0, 5);
    public static final Item BASS_GUITAR = new ItemInstrument(18,1, 4);
    public static final Item MUSIC_SHEET = new ItemMusicSheet();

    public static final IItemInstrument[] instruments = new IItemInstrument[]{
            (IItemInstrument) GUITAR, (IItemInstrument) LYRE, (IItemInstrument) BANJO, (IItemInstrument) DRUM,
            (IItemInstrument) CYMBAL, (IItemInstrument) DRUM_KIT, (IItemInstrument) XYLOPHONE, (IItemInstrument) TUBULAR_BELL,
            (IItemInstrument) SANSULA, (IItemInstrument) VIOLIN, (IItemInstrument) CELLO, (IItemInstrument) FLUTE,
            (IItemInstrument) SAXOPHONE, (IItemInstrument) GOD, (IItemInstrument) PIANO, (IItemInstrument) OBOE,
            (IItemInstrument) REDSTONE_GUITAR, (IItemInstrument) FRENCH_HORN, (IItemInstrument) BASS_GUITAR
    };

    public static final RecipeSerializer<RecipeNoteCloning> CRAFTING_SPECIAL_NOTECLONING = new SimpleRecipeSerializer<>(RecipeNoteCloning::new);


    public static void registerRecipes() {
        Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(XercaMusic.MODID,
                "crafting_special_notecloning"), CRAFTING_SPECIAL_NOTECLONING);
    }

    public static void registerItems() {
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "guitar"), GUITAR);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "lyre"), LYRE);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "banjo"), BANJO);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "drum"), DRUM);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "cymbal"), CYMBAL);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "drum_kit"), DRUM_KIT);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "xylophone"), XYLOPHONE);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "tubular_bell"), TUBULAR_BELL);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "sansula"), SANSULA);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "violin"), VIOLIN);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "cello"), CELLO);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "flute"), FLUTE);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "saxophone"), SAXOPHONE);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "god"), GOD);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "piano"), PIANO);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "oboe"), OBOE);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "redstone_guitar"), REDSTONE_GUITAR);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "french_horn"), FRENCH_HORN);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "bass_guitar"), BASS_GUITAR);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "harp_mc"), HARP_MC);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "music_sheet"), MUSIC_SHEET);
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "music_box"), new BlockItem(Blocks.MUSIC_BOX, new Item.Properties().tab(Items.musicTab)));
        Registry.register(Registry.ITEM, new ResourceLocation(XercaMusic.MODID, "metronome"), new BlockItem(Blocks.BLOCK_METRONOME, new Item.Properties().tab(Items.musicTab)));
    }
}