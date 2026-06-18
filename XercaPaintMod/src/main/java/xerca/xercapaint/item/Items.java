package xerca.xercapaint.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

    public static final ItemPalette ITEM_PALETTE = new ItemPalette(props("item_palette"));
    public static final ItemCanvas ITEM_CANVAS = new ItemCanvas(CanvasType.SMALL, false, props("item_canvas"));
    public static final ItemCanvas ITEM_CANVAS_LARGE = new ItemCanvas(CanvasType.LARGE, false, props("item_canvas_large"));
    public static final ItemCanvas ITEM_CANVAS_LONG = new ItemCanvas(CanvasType.LONG, false, props("item_canvas_long"));
    public static final ItemCanvas ITEM_CANVAS_TALL = new ItemCanvas(CanvasType.TALL, false, props("item_canvas_tall"));
    public static final ItemCanvas ITEM_CANVAS_GLASS = new ItemCanvas(CanvasType.SMALL, true, props("item_canvas_glass"));
    public static final ItemCanvas ITEM_CANVAS_GLASS_LARGE = new ItemCanvas(CanvasType.LARGE, true, props("item_canvas_glass_large"));
    public static final ItemCanvas ITEM_CANVAS_GLASS_LONG = new ItemCanvas(CanvasType.LONG, true, props("item_canvas_glass_long"));
    public static final ItemCanvas ITEM_CANVAS_GLASS_TALL = new ItemCanvas(CanvasType.TALL, true, props("item_canvas_glass_tall"));
    public static final ItemEasel ITEM_EASEL = new ItemEasel(props("item_easel").stacksTo(1));

    private static Item.Properties props(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Mod.id(name)));
    }

    public static final RecipeSerializer<RecipeFillPalette> CRAFTING_SPECIAL_PALETTE_FILLING = new CustomRecipe.Serializer<>(RecipeFillPalette::new);
    public static final RecipeSerializer<RecipeCanvasCloning> CRAFTING_SPECIAL_CANVAS_CLONING = new CustomRecipe.Serializer<>(RecipeCanvasCloning::new);
    public static final RecipeSerializer<RecipeTaglessShaped> CRAFTING_TAGLESS_SHAPED = new RecipeTaglessShaped.TaglessSerializer();
    public static final DataComponentType<List<Integer>> CANVAS_PIXELS = DataComponentType.<List<Integer>>builder().persistent(Codec.list(Codec.INT)).networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(Codec.INT))).build();
    public static final DataComponentType<Boolean> CANVAS_SIDES_ACTIVE = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<List<Integer>> CANVAS_SIDE_PIXELS = DataComponentType.<List<Integer>>builder().persistent(Codec.list(Codec.INT)).networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(Codec.INT))).build();
    public static final DataComponentType<Integer> CANVAS_VERSION = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build();
    public static final DataComponentType<String> CANVAS_ID = DataComponentType.<String>builder().persistent(ExtraCodecs.NON_EMPTY_STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build();
    public static final DataComponentType<String> CANVAS_TITLE = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<String> CANVAS_AUTHOR = DataComponentType.<String>builder().persistent(Codec.STRING).build();
    public static final DataComponentType<Integer> CANVAS_GENERATION = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).build();
    public static final DataComponentType<byte[]> PALETTE_BASIC_COLORS = DataComponentType.<byte[]>builder().persistent(Codec.BYTE_BUFFER.flatXmap(byteBuffer -> DataResult.success(byteBuffer.array()), bytes -> DataResult.success(ByteBuffer.wrap(bytes)))).networkSynchronized(ByteBufCodecs.BYTE_ARRAY).build();
    public static final DataComponentType<ItemPalette.ComponentCustomColor> PALETTE_CUSTOM_COLORS = DataComponentType.<ItemPalette.ComponentCustomColor>builder().persistent(ItemPalette.ComponentCustomColor.CODEC).build();

    public static final CreativeModeTab PAINT_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ITEM_PALETTE))
            .displayItems((params, output) -> {
                ItemStack fullPalette = new ItemStack(ITEM_PALETTE);
                byte[] basicColors = new byte[16];
                Arrays.fill(basicColors, (byte) 1);
                fullPalette.set(PALETTE_BASIC_COLORS, basicColors);

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

    public static void registerRecipes() {
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
        registerItem("item_canvas_glass", ITEM_CANVAS_GLASS);
        registerItem("item_canvas_glass_large", ITEM_CANVAS_GLASS_LARGE);
        registerItem("item_canvas_glass_long", ITEM_CANVAS_GLASS_LONG);
        registerItem("item_canvas_glass_tall", ITEM_CANVAS_GLASS_TALL);
        registerItem("item_easel", ITEM_EASEL);

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Mod.id("paint_tab"), PAINT_TAB);
    }

    public static void registerDataComponents() {
        registerComponentType("canvas_generation", CANVAS_GENERATION);
        registerComponentType("canvas_version", CANVAS_VERSION);
        registerComponentType("canvas_id", CANVAS_ID);
        registerComponentType("canvas_title", CANVAS_TITLE);
        registerComponentType("canvas_author", CANVAS_AUTHOR);
        registerComponentType("canvas_pixels", CANVAS_PIXELS);
        registerComponentType("canvas_sides_active", CANVAS_SIDES_ACTIVE);
        registerComponentType("canvas_side_pixels", CANVAS_SIDE_PIXELS);
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
