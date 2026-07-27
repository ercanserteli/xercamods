package xerca.xercamusic.common.item;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.Blocks;

import java.util.List;
import java.util.UUID;

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
    public static final Item OBOE = new ItemInstrument(15, 0, 4);
    public static final Item REDSTONE_GUITAR = new ItemInstrument(16, 0, 5);
    public static final Item FRENCH_HORN = new ItemInstrument(17, 0, 5);
    public static final Item BASS_GUITAR = new ItemInstrument(18, 1, 4);
    public static final Item TRUMPET = new ItemInstrument(19, 2, 5);  // Trumpet range: F#3 to D6
    public static final Item REDSTONE_PIANO = new ItemInstrument(20, 0, 6);  // Full piano range
    public static final Item ORGAN = new ItemInstrument(21, 1, 6);  // Full organ range
    public static final Item MUSIC_SHEET = new ItemMusicSheet();

    public static final CreativeModeTab MUSIC_TAB = CreativeModeTab.builder()
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
            (IItemInstrument) TRUMPET, (IItemInstrument) REDSTONE_PIANO, (IItemInstrument) ORGAN
    );

    public static final RecipeSerializer<RecipeNoteCloning> CRAFTING_SPECIAL_NOTECLONING = new SimpleCraftingRecipeSerializer<>(RecipeNoteCloning::new);

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

    public static void registerDataComponents(RegisterEvent.RegisterHelper<DataComponentType<?>> helper) {
        helper.register(Mod.id("sheet_bps"), SHEET_BPS);
        helper.register(Mod.id("sheet_length"), SHEET_LENGTH);
        helper.register(Mod.id("sheet_version"), SHEET_VERSION);
        helper.register(Mod.id("sheet_prev_instrument"), SHEET_PREV_INSTRUMENT);
        helper.register(Mod.id("sheet_prev_instrument_locked"), SHEET_PREV_INSTRUMENT_LOCKED);
        helper.register(Mod.id("sheet_highlight_interval"), SHEET_HIGHLIGHT_INTERVAL);
        helper.register(Mod.id("sheet_volume"), SHEET_VOLUME);
        helper.register(Mod.id("sheet_id"), SHEET_ID);
        helper.register(Mod.id("sheet_title"), SHEET_TITLE);
        helper.register(Mod.id("sheet_author"), SHEET_AUTHOR);
        helper.register(Mod.id("sheet_generation"), SHEET_GENERATION);
    }

    public static void registerRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register(Mod.id("crafting_special_notecloning"), CRAFTING_SPECIAL_NOTECLONING);
    }

    public static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register(Mod.id("guitar"), GUITAR);
        helper.register(Mod.id("lyre"), LYRE);
        helper.register(Mod.id("banjo"), BANJO);
        helper.register(Mod.id("drum"), DRUM);
        helper.register(Mod.id("cymbal"), CYMBAL);
        helper.register(Mod.id("drum_kit"), DRUM_KIT);
        helper.register(Mod.id("xylophone"), XYLOPHONE);
        helper.register(Mod.id("tubular_bell"), TUBULAR_BELL);
        helper.register(Mod.id("sansula"), SANSULA);
        helper.register(Mod.id("violin"), VIOLIN);
        helper.register(Mod.id("cello"), CELLO);
        helper.register(Mod.id("flute"), FLUTE);
        helper.register(Mod.id("saxophone"), SAXOPHONE);
        helper.register(Mod.id("god"), GOD);
        helper.register(Mod.id("piano"), PIANO);
        helper.register(Mod.id("oboe"), OBOE);
        helper.register(Mod.id("redstone_guitar"), REDSTONE_GUITAR);
        helper.register(Mod.id("french_horn"), FRENCH_HORN);
        helper.register(Mod.id("bass_guitar"), BASS_GUITAR);
        helper.register(Mod.id("trumpet"), TRUMPET);
        helper.register(Mod.id("redstone_piano"), REDSTONE_PIANO);
        helper.register(Mod.id("organ"), ORGAN);
        helper.register(Mod.id("harp_mc"), HARP_MC);
        helper.register(Mod.id("music_sheet"), MUSIC_SHEET);
        helper.register(Mod.id("music_box"), new BlockItem(Blocks.MUSIC_BOX, new Item.Properties()));
        helper.register(Mod.id("metronome"), new BlockItem(Blocks.BLOCK_METRONOME, new Item.Properties()));
    }

    public static void registerCreativeTab(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        helper.register(Mod.id("music_tab"), MUSIC_TAB);
    }
}
