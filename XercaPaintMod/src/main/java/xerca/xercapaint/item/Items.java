package xerca.xercapaint.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;
import xerca.xercapaint.item.crafting.RecipeCraftPalette;
import xerca.xercapaint.item.crafting.RecipeFillPalette;
import xerca.xercapaint.item.crafting.RecipeTaglessShaped;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public final class Items {
    public static final ItemPalette ITEM_PALETTE = new ItemPalette();
    public static final ItemCanvas ITEM_CANVAS = new ItemCanvas(CanvasType.SMALL);
    public static final ItemCanvas ITEM_CANVAS_LARGE = new ItemCanvas(CanvasType.LARGE);
    public static final ItemCanvas ITEM_CANVAS_LONG = new ItemCanvas(CanvasType.LONG);
    public static final ItemCanvas ITEM_CANVAS_TALL = new ItemCanvas(CanvasType.TALL);
    public static final ItemEasel ITEM_EASEL = new ItemEasel(new Item.Properties().stacksTo(1));

    public static final RecipeSerializer<RecipeCraftPalette> CRAFTING_SPECIAL_PALETTE_CRAFTING = new SimpleCraftingRecipeSerializer<>(RecipeCraftPalette::new);
    public static final RecipeSerializer<RecipeFillPalette> CRAFTING_SPECIAL_PALETTE_FILLING = new SimpleCraftingRecipeSerializer<>(RecipeFillPalette::new);
    public static final RecipeSerializer<RecipeCanvasCloning> CRAFTING_SPECIAL_CANVAS_CLONING = new SimpleCraftingRecipeSerializer<>(RecipeCanvasCloning::new);
    public static final RecipeSerializer<RecipeTaglessShaped> CRAFTING_TAGLESS_SHAPED = new RecipeTaglessShaped.TaglessSerializer();
    public static final DataComponentType<List<Integer>> CANVAS_PIXELS = DataComponentType.<List<Integer>>builder().persistent(Codec.list(Codec.INT)).networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(Codec.INT))).build();
    public static final DataComponentType<Integer> CANVAS_VERSION = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<String> CANVAS_ID = DataComponentType.<String>builder().persistent(ExtraCodecs.NON_EMPTY_STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build();
    public static final DataComponentType<String> CANVAS_TITLE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> CANVAS_AUTHOR = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<Integer> CANVAS_GENERATION = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).build();
    public static final DataComponentType<byte[]> PALETTE_BASIC_COLORS = DataComponentType.<byte[]>builder().persistent(Codec.BYTE_BUFFER.flatXmap(byteBuffer -> DataResult.success(byteBuffer.array()), bytes -> DataResult.success(ByteBuffer.wrap(bytes)))).networkSynchronized(ByteBufCodecs.BYTE_ARRAY).build();
    public static final DataComponentType<ItemPalette.ComponentCustomColor> PALETTE_CUSTOM_COLORS = DataComponentType.<ItemPalette.ComponentCustomColor>builder().persistent(ItemPalette.ComponentCustomColor.CODEC).build();

    public static final CreativeModeTab paintTab = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.ITEM_PALETTE))
            .displayItems((params, output) -> {
                ItemStack fullPalette = new ItemStack(ITEM_PALETTE);
                byte[] basicColors = new byte[16];
                Arrays.fill(basicColors, (byte)1);
                fullPalette.set(PALETTE_BASIC_COLORS, basicColors);

                output.accept(ITEM_PALETTE);
                output.accept(fullPalette);
                output.accept(ITEM_CANVAS);
                output.accept(ITEM_CANVAS_LONG);
                output.accept(ITEM_CANVAS_TALL);
                output.accept(ITEM_CANVAS_LARGE);
                output.accept(ITEM_EASEL);
            })
            .title(Component.translatable("itemGroup.xercapaint.paint_tab"))
            .build();

    public static void registerRecipes() {
        registerRecipeSerializer("crafting_special_palette_crafting", CRAFTING_SPECIAL_PALETTE_CRAFTING);
        registerRecipeSerializer("crafting_special_palette_filling", CRAFTING_SPECIAL_PALETTE_FILLING);
        registerRecipeSerializer("crafting_special_canvas_cloning", CRAFTING_SPECIAL_CANVAS_CLONING);
        registerRecipeSerializer("crafting_tagless_shaped", CRAFTING_TAGLESS_SHAPED);
    }

    public static void registerItems() {
        registerItem("item_palette", ITEM_PALETTE);
        registerItem("item_canvas", ITEM_CANVAS);
        registerItem("item_canvas_large", ITEM_CANVAS_LARGE);
        registerItem("item_canvas_long", ITEM_CANVAS_LONG);
        registerItem("item_canvas_tall", ITEM_CANVAS_TALL);
        registerItem("item_easel", ITEM_EASEL);

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Mod.id("paint_tab"), paintTab);
    }

    public static void registerDataComponents() {
        registerComponentType("canvas_generation", CANVAS_GENERATION);
        registerComponentType("canvas_version", CANVAS_VERSION);
        registerComponentType("canvas_id", CANVAS_ID);
        registerComponentType("canvas_title", CANVAS_TITLE);
        registerComponentType("canvas_author", CANVAS_AUTHOR);
        registerComponentType("canvas_pixels", CANVAS_PIXELS);
        registerComponentType("palette_basic_colors", PALETTE_BASIC_COLORS);
        registerComponentType("palette_custom_colors", PALETTE_CUSTOM_COLORS);
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