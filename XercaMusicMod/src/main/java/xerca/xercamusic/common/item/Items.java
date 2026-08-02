package xerca.xercamusic.common.item;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.Blocks;

import java.util.List;
import java.util.UUID;

public final class Items {
    public static final Item HARP_MC = new ItemInstrument(-1, 0, 7, properties("harp_mc"));

    public static final Item GUITAR = new ItemInstrument(0, 0, 6, properties("guitar"));
    public static final Item LYRE = new ItemInstrument(1, 1, 5, properties("lyre"));
    public static final Item BANJO = new ItemInstrument(2, 0, 4, properties("banjo"));
    public static final Item DRUM = new ItemInstrument(3, 1, 4, properties("drum"));
    public static final Item CYMBAL = new ItemInstrument(4, 0, 4, properties("cymbal"));
    public static final Item DRUM_KIT = new ItemBlockInstrument(5, 0, 7, blockProperties("drum_kit"), Blocks.DRUM_KIT);
    public static final Item XYLOPHONE = new ItemInstrument(6, 0, 5, properties("xylophone"));
    public static final Item TUBULAR_BELL = new ItemInstrument(7, 1, 4, properties("tubular_bell"));
    public static final Item SANSULA = new ItemInstrument(8, 1, 5, properties("sansula"));
    public static final Item VIOLIN = new ItemInstrument(9, 1, 5, properties("violin"));
    public static final Item CELLO = new ItemInstrument(10, 0, 6, properties("cello"));
    public static final Item FLUTE = new ItemInstrument(11, 1, 6, properties("flute"));
    public static final Item SAXOPHONE = new ItemInstrument(12, 0, 4, properties("saxophone"));
    public static final Item GOD = new ItemInstrument(13, 0, 5, properties("god"));
    public static final Item PIANO = new ItemBlockInstrument(14, 0, 7, blockProperties("piano"), Blocks.PIANO);
    public static final Item OBOE = new ItemInstrument(15, 0, 4, properties("oboe"));
    public static final Item REDSTONE_GUITAR = new ItemInstrument(16, 0, 5, properties("redstone_guitar"));
    public static final Item FRENCH_HORN = new ItemInstrument(17, 0, 5, properties("french_horn"));
    public static final Item BASS_GUITAR = new ItemInstrument(18, 1, 4, properties("bass_guitar"));
    public static final Item TRUMPET = new ItemInstrument(19, 2, 5, properties("trumpet"));
    public static final Item REDSTONE_PIANO = new ItemInstrument(20, 0, 6, properties("redstone_piano"));
    public static final Item ORGAN = new ItemInstrument(21, 1, 6, properties("organ"));
    public static final Item HARPSICHORD = new ItemInstrument(22, 0, 5, properties("harpsichord"));
    public static final Item STEELPAN = new ItemBlockInstrument(23, 1, 5, blockProperties("steelpan"), Blocks.STEELPAN);
    public static final Item CUICA = new ItemInstrument(24, 1, 5, properties("cuica"));
    public static final Item ACCORDION = new ItemInstrument(25, 0, 5, properties("accordion"));
    public static final Item TENOR_SAXOPHONE = new ItemInstrument(26, 0, 4, properties("tenor_saxophone"));
    public static final Item MUSIC_SHEET = new ItemMusicSheet(properties("music_sheet"));

    public static final CreativeModeTab MUSIC_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GUITAR))
            .displayItems((params, output) -> {
                output.accept(MUSIC_SHEET);
                output.accept(GUITAR);
                output.accept(LYRE);
                output.accept(BANJO);
                output.accept(DRUM);
                output.accept(CYMBAL);
                output.accept(DRUM_KIT);
                output.accept(XYLOPHONE);
                output.accept(TUBULAR_BELL);
                output.accept(SANSULA);
                output.accept(VIOLIN);
                output.accept(CELLO);
                output.accept(FLUTE);
                output.accept(SAXOPHONE);
                output.accept(GOD);
                output.accept(PIANO);
                output.accept(OBOE);
                output.accept(FRENCH_HORN);
                output.accept(REDSTONE_GUITAR);
                output.accept(BASS_GUITAR);
                output.accept(TRUMPET);
                output.accept(REDSTONE_PIANO);
                output.accept(ORGAN);
                output.accept(HARPSICHORD);
                output.accept(STEELPAN);
                output.accept(CUICA);
                output.accept(ACCORDION);
                output.accept(TENOR_SAXOPHONE);
                output.accept(Blocks.MUSIC_BOX);
                output.accept(Blocks.BLOCK_METRONOME);
            })
            .title(Component.translatable("itemGroup.xercamusic.music_tab"))
            .build();

    public static final List<IItemInstrument> INSTRUMENTS = List.of(
            (IItemInstrument) GUITAR, (IItemInstrument) LYRE, (IItemInstrument) BANJO, (IItemInstrument) DRUM,
            (IItemInstrument) CYMBAL, (IItemInstrument) DRUM_KIT, (IItemInstrument) XYLOPHONE, (IItemInstrument) TUBULAR_BELL,
            (IItemInstrument) SANSULA, (IItemInstrument) VIOLIN, (IItemInstrument) CELLO, (IItemInstrument) FLUTE,
            (IItemInstrument) SAXOPHONE, (IItemInstrument) GOD, (IItemInstrument) PIANO, (IItemInstrument) OBOE,
            (IItemInstrument) REDSTONE_GUITAR, (IItemInstrument) FRENCH_HORN, (IItemInstrument) BASS_GUITAR,
            (IItemInstrument) TRUMPET, (IItemInstrument) REDSTONE_PIANO, (IItemInstrument) ORGAN, (IItemInstrument) HARPSICHORD, 
            (IItemInstrument) STEELPAN, (IItemInstrument) CUICA, (IItemInstrument) ACCORDION, (IItemInstrument) TENOR_SAXOPHONE
    );

    public static final RecipeSerializer<RecipeNoteCloning> CRAFTING_SPECIAL_NOTECLONING = new CustomRecipe.Serializer<>(RecipeNoteCloning::new);

    public static final DataComponentType<Byte> SHEET_BPS = DataComponentType.<Byte>builder().persistent(Codec.BYTE).build();
    public static final DataComponentType<Integer> SHEET_LENGTH = DataComponentType.<Integer>builder().persistent(Codec.INT).build();
    public static final DataComponentType<Integer> SHEET_VERSION = DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final DataComponentType<Byte> SHEET_PREV_INSTRUMENT = DataComponentType.<Byte>builder().persistent(Codec.BYTE).build();
    public static final DataComponentType<Boolean> SHEET_PREV_INSTRUMENT_LOCKED = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build();
    public static final DataComponentType<Byte> SHEET_HIGHLIGHT_INTERVAL = DataComponentType.<Byte>builder().persistent(Codec.BYTE).build();
    public static final DataComponentType<Float> SHEET_VOLUME = DataComponentType.<Float>builder().persistent(Codec.FLOAT).build();
    public static final DataComponentType<UUID> SHEET_ID = DataComponentType.<UUID>builder().persistent(UUIDUtil.STRING_CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build();
    public static final DataComponentType<String> SHEET_TITLE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> SHEET_AUTHOR = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<Integer> SHEET_GENERATION = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).build();

    private Items() {
    }

    private static Item.Properties properties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Mod.id(name)));
    }

    private static Item.Properties blockProperties(String name) {
        return properties(name).useBlockDescriptionPrefix();
    }

    public static void registerDataComponents() {
        registerComponentType("sheet_bps", SHEET_BPS);
        registerComponentType("sheet_length", SHEET_LENGTH);
        registerComponentType("sheet_version", SHEET_VERSION);
        registerComponentType("sheet_prev_instrument", SHEET_PREV_INSTRUMENT);
        registerComponentType("sheet_prev_instrument_locked", SHEET_PREV_INSTRUMENT_LOCKED);
        registerComponentType("sheet_highlight_interval", SHEET_HIGHLIGHT_INTERVAL);
        registerComponentType("sheet_volume", SHEET_VOLUME);
        registerComponentType("sheet_id", SHEET_ID);
        registerComponentType("sheet_title", SHEET_TITLE);
        registerComponentType("sheet_author", SHEET_AUTHOR);
        registerComponentType("sheet_generation", SHEET_GENERATION);
    }

    public static void registerRecipes() {
        registerRecipeSerializer("crafting_special_notecloning", CRAFTING_SPECIAL_NOTECLONING);
    }

    public static void registerItems() {
        registerItem("guitar", GUITAR);
        registerItem("lyre", LYRE);
        registerItem("banjo", BANJO);
        registerItem("drum", DRUM);
        registerItem("cymbal", CYMBAL);
        registerItem("drum_kit", DRUM_KIT);
        registerItem("xylophone", XYLOPHONE);
        registerItem("tubular_bell", TUBULAR_BELL);
        registerItem("sansula", SANSULA);
        registerItem("violin", VIOLIN);
        registerItem("cello", CELLO);
        registerItem("flute", FLUTE);
        registerItem("saxophone", SAXOPHONE);
        registerItem("god", GOD);
        registerItem("piano", PIANO);
        registerItem("oboe", OBOE);
        registerItem("redstone_guitar", REDSTONE_GUITAR);
        registerItem("french_horn", FRENCH_HORN);
        registerItem("bass_guitar", BASS_GUITAR);
        registerItem("trumpet", TRUMPET);
        registerItem("redstone_piano", REDSTONE_PIANO);
        registerItem("organ", ORGAN);
        registerItem("harpsichord", HARPSICHORD);
        registerItem("steelpan", STEELPAN);
        registerItem("cuica", CUICA);
        registerItem("accordion", ACCORDION);
        registerItem("tenor_saxophone", TENOR_SAXOPHONE);
        registerItem("harp_mc", HARP_MC);
        registerItem("music_sheet", MUSIC_SHEET);
        registerItem("music_box", new BlockItem(Blocks.MUSIC_BOX, blockProperties("music_box")));
        registerItem("metronome", new BlockItem(Blocks.BLOCK_METRONOME, blockProperties("metronome")));

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Mod.id("music_tab"), MUSIC_TAB);
    }

    private static void registerComponentType(String name, DataComponentType<?> type) {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Mod.id(name), type);
    }

    private static void registerItem(String name, Item item) {
        Registry.register(BuiltInRegistries.ITEM, Mod.id(name), item);
    }

    private static void registerRecipeSerializer(String name, RecipeSerializer<?> recipeSerializer) {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Mod.id(name), recipeSerializer);
    }
}
