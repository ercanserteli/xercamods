package xerca.xercapaint.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;
import xerca.xercapaint.item.crafting.RecipeFillPalette;
import xerca.xercapaint.item.crafting.RecipeTaglessShaped;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public final class Items {
    private Items() {
    }

    // Wraps the palette's basic-color counts. A raw byte[] can't be a DataComponentType value on NeoForge
    // (it must have value equals/hashCode and be immutable); this record provides both (defensive clone).
    public record BasicColors(byte[] value) {
        public BasicColors(byte[] value) {
            this.value = value.clone();
        }

        @Override
        public byte[] value() {
            return value.clone();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BasicColors other && Arrays.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }

        @Override
        public String toString() {
            return "BasicColors" + Arrays.toString(value);
        }
    }

    public static final ItemPalette ITEM_PALETTE = new ItemPalette();
    public static final ItemCanvas ITEM_CANVAS = new ItemCanvas(CanvasType.SMALL);
    public static final ItemCanvas ITEM_CANVAS_LARGE = new ItemCanvas(CanvasType.LARGE);
    public static final ItemCanvas ITEM_CANVAS_LONG = new ItemCanvas(CanvasType.LONG);
    public static final ItemCanvas ITEM_CANVAS_TALL = new ItemCanvas(CanvasType.TALL);
    public static final ItemCanvas ITEM_CANVAS_GLASS = new ItemCanvas(CanvasType.SMALL, true);
    public static final ItemCanvas ITEM_CANVAS_GLASS_LARGE = new ItemCanvas(CanvasType.LARGE, true);
    public static final ItemCanvas ITEM_CANVAS_GLASS_LONG = new ItemCanvas(CanvasType.LONG, true);
    public static final ItemCanvas ITEM_CANVAS_GLASS_TALL = new ItemCanvas(CanvasType.TALL, true);
    public static final ItemEasel ITEM_EASEL = new ItemEasel(new Item.Properties().stacksTo(1));

    public static final RecipeSerializer<RecipeFillPalette> CRAFTING_SPECIAL_PALETTE_FILLING = new SimpleCraftingRecipeSerializer<>(RecipeFillPalette::new);
    public static final RecipeSerializer<RecipeCanvasCloning> CRAFTING_SPECIAL_CANVAS_CLONING = new SimpleCraftingRecipeSerializer<>(RecipeCanvasCloning::new);
    public static final RecipeSerializer<RecipeTaglessShaped> CRAFTING_TAGLESS_SHAPED = new RecipeTaglessShaped.TaglessSerializer();
    public static final DataComponentType<List<Integer>> CANVAS_PIXELS = DataComponentType.<List<Integer>>builder().persistent(Codec.list(Codec.INT)).networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(Codec.INT))).build();
    public static final DataComponentType<Boolean> CANVAS_SIDES_ACTIVE = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<List<Integer>> CANVAS_SIDE_PIXELS = DataComponentType.<List<Integer>>builder().persistent(Codec.list(Codec.INT)).networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(Codec.INT))).build();
    public static final DataComponentType<Integer> CANVAS_VERSION = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<String> CANVAS_ID = DataComponentType.<String>builder().persistent(ExtraCodecs.NON_EMPTY_STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build();
    public static final DataComponentType<String> CANVAS_TITLE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> CANVAS_AUTHOR = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<Integer> CANVAS_GENERATION = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).build();
    public static final DataComponentType<BasicColors> PALETTE_BASIC_COLORS = DataComponentType.<BasicColors>builder().persistent(Codec.BYTE_BUFFER.flatXmap(byteBuffer -> DataResult.success(byteBuffer.array()), bytes -> DataResult.success(ByteBuffer.wrap(bytes))).xmap(BasicColors::new, BasicColors::value)).networkSynchronized(ByteBufCodecs.BYTE_ARRAY.map(BasicColors::new, BasicColors::value)).build();
    public static final DataComponentType<ItemPalette.ComponentCustomColor> PALETTE_CUSTOM_COLORS = DataComponentType.<ItemPalette.ComponentCustomColor>builder().persistent(ItemPalette.ComponentCustomColor.CODEC).build();

    public static final CreativeModeTab PAINT_TAB = CreativeModeTab.builder()
            .icon(() -> new ItemStack(ITEM_PALETTE))
            .displayItems((params, output) -> {
                ItemStack fullPalette = new ItemStack(ITEM_PALETTE);
                byte[] basicColors = new byte[16];
                Arrays.fill(basicColors, (byte) 1);
                fullPalette.set(PALETTE_BASIC_COLORS, new BasicColors(basicColors));

                output.accept(ITEM_PALETTE);
                output.accept(fullPalette);
                output.accept(ITEM_CANVAS);
                output.accept(ITEM_CANVAS_LONG);
                output.accept(ITEM_CANVAS_TALL);
                output.accept(ITEM_CANVAS_LARGE);
                output.accept(ITEM_CANVAS_GLASS);
                output.accept(ITEM_CANVAS_GLASS_LONG);
                output.accept(ITEM_CANVAS_GLASS_TALL);
                output.accept(ITEM_CANVAS_GLASS_LARGE);
                output.accept(ITEM_EASEL);
            })
            .title(Component.translatable("itemGroup.xercapaint.paint_tab"))
            .build();

    public static void registerRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register(Mod.id("crafting_special_palette_filling"), CRAFTING_SPECIAL_PALETTE_FILLING);
        helper.register(Mod.id("crafting_special_canvas_cloning"), CRAFTING_SPECIAL_CANVAS_CLONING);
        helper.register(Mod.id("crafting_tagless_shaped"), CRAFTING_TAGLESS_SHAPED);
    }

    public static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register(Mod.id("item_palette"), ITEM_PALETTE);
        helper.register(Mod.id("item_canvas"), ITEM_CANVAS);
        helper.register(Mod.id("item_canvas_large"), ITEM_CANVAS_LARGE);
        helper.register(Mod.id("item_canvas_long"), ITEM_CANVAS_LONG);
        helper.register(Mod.id("item_canvas_tall"), ITEM_CANVAS_TALL);
        helper.register(Mod.id("item_canvas_glass"), ITEM_CANVAS_GLASS);
        helper.register(Mod.id("item_canvas_glass_large"), ITEM_CANVAS_GLASS_LARGE);
        helper.register(Mod.id("item_canvas_glass_long"), ITEM_CANVAS_GLASS_LONG);
        helper.register(Mod.id("item_canvas_glass_tall"), ITEM_CANVAS_GLASS_TALL);
        helper.register(Mod.id("item_easel"), ITEM_EASEL);
    }

    public static void registerCreativeTab(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        helper.register(Mod.id("paint_tab"), PAINT_TAB);
    }

    public static void registerDataComponents(RegisterEvent.RegisterHelper<DataComponentType<?>> helper) {
        helper.register(Mod.id("canvas_generation"), CANVAS_GENERATION);
        helper.register(Mod.id("canvas_version"), CANVAS_VERSION);
        helper.register(Mod.id("canvas_id"), CANVAS_ID);
        helper.register(Mod.id("canvas_title"), CANVAS_TITLE);
        helper.register(Mod.id("canvas_author"), CANVAS_AUTHOR);
        helper.register(Mod.id("canvas_pixels"), CANVAS_PIXELS);
        helper.register(Mod.id("canvas_sides_active"), CANVAS_SIDES_ACTIVE);
        helper.register(Mod.id("canvas_side_pixels"), CANVAS_SIDE_PIXELS);
        helper.register(Mod.id("palette_basic_colors"), PALETTE_BASIC_COLORS);
        helper.register(Mod.id("palette_custom_colors"), PALETTE_CUSTOM_COLORS);
    }
}
