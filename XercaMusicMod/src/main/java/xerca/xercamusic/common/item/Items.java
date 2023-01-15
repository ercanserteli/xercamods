package xerca.xercamusic.common.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.Blocks;

public final class Items {
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

    public static final CreativeModeTab paintTab = FabricItemGroup.builder(new ResourceLocation(XercaMusic.MODID, "music_tab"))
            .icon(() -> new ItemStack(Items.GUITAR))
            .displayItems((enabledFeatures, entries, operatorEnabled) -> {
                entries.accept(MUSIC_SHEET);
                entries.accept(GUITAR);
                entries.accept(LYRE);
                entries.accept(BANJO);
                entries.accept(DRUM);
                entries.accept(CYMBAL);
                entries.accept(DRUM_KIT);
                entries.accept(XYLOPHONE);
                entries.accept(TUBULAR_BELL);
                entries.accept(SANSULA);
                entries.accept(VIOLIN);
                entries.accept(CELLO);
                entries.accept(FLUTE);
                entries.accept(SAXOPHONE);
                entries.accept(GOD);
                entries.accept(PIANO);
                entries.accept(OBOE);
                entries.accept(FRENCH_HORN);
                entries.accept(REDSTONE_GUITAR);
                entries.accept(BASS_GUITAR);
                entries.accept(Blocks.MUSIC_BOX);
                entries.accept(Blocks.BLOCK_METRONOME);
            })
            .build();

    public static final IItemInstrument[] instruments = new IItemInstrument[]{
            (IItemInstrument) GUITAR, (IItemInstrument) LYRE, (IItemInstrument) BANJO, (IItemInstrument) DRUM,
            (IItemInstrument) CYMBAL, (IItemInstrument) DRUM_KIT, (IItemInstrument) XYLOPHONE, (IItemInstrument) TUBULAR_BELL,
            (IItemInstrument) SANSULA, (IItemInstrument) VIOLIN, (IItemInstrument) CELLO, (IItemInstrument) FLUTE,
            (IItemInstrument) SAXOPHONE, (IItemInstrument) GOD, (IItemInstrument) PIANO, (IItemInstrument) OBOE,
            (IItemInstrument) REDSTONE_GUITAR, (IItemInstrument) FRENCH_HORN, (IItemInstrument) BASS_GUITAR
    };

    public static final RecipeSerializer<RecipeNoteCloning> CRAFTING_SPECIAL_NOTECLONING = new SimpleCraftingRecipeSerializer<>(RecipeNoteCloning::new);


    public static void registerRecipes() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(XercaMusic.MODID,
                "crafting_special_notecloning"), CRAFTING_SPECIAL_NOTECLONING);
    }

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "guitar"), GUITAR);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "lyre"), LYRE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "banjo"), BANJO);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "drum"), DRUM);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "cymbal"), CYMBAL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "drum_kit"), DRUM_KIT);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "xylophone"), XYLOPHONE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "tubular_bell"), TUBULAR_BELL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "sansula"), SANSULA);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "violin"), VIOLIN);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "cello"), CELLO);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "flute"), FLUTE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "saxophone"), SAXOPHONE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "god"), GOD);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "piano"), PIANO);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "oboe"), OBOE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "redstone_guitar"), REDSTONE_GUITAR);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "french_horn"), FRENCH_HORN);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "bass_guitar"), BASS_GUITAR);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "harp_mc"), HARP_MC);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "music_sheet"), MUSIC_SHEET);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "music_box"), new BlockItem(Blocks.MUSIC_BOX, new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(XercaMusic.MODID, "metronome"), new BlockItem(Blocks.BLOCK_METRONOME, new Item.Properties()));
    }
}