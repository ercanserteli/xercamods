package xerca.xercapaint.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

import java.util.Arrays;

public final class Items {
    public static final ItemPalette ITEM_PALETTE = new ItemPalette();
    public static final ItemCanvas ITEM_CANVAS = new ItemCanvas(CanvasType.SMALL);
    public static final ItemCanvas ITEM_CANVAS_LARGE = new ItemCanvas(CanvasType.LARGE);
    public static final ItemCanvas ITEM_CANVAS_LONG = new ItemCanvas(CanvasType.LONG);
    public static final ItemCanvas ITEM_CANVAS_TALL = new ItemCanvas(CanvasType.TALL);
    public static final ItemEasel ITEM_EASEL = new ItemEasel(new Item.Properties().stacksTo(1));
    public static final CreativeModeTab paintTab = FabricItemGroup.builder(new ResourceLocation(Mod.modId, "paint_tab"))
            .icon(() -> new ItemStack(Items.ITEM_PALETTE))
            .displayItems((params, output) -> {
                ItemStack fullPalette = new ItemStack(ITEM_PALETTE);
                byte[] basicColors = new byte[16];
                Arrays.fill(basicColors, (byte)1);
                fullPalette.getOrCreateTag().putByteArray("basic", basicColors);

                output.accept(ITEM_PALETTE);
                output.accept(fullPalette);
                output.accept(ITEM_CANVAS);
                output.accept(ITEM_CANVAS_LARGE);
                output.accept(ITEM_CANVAS_LONG);
                output.accept(ITEM_CANVAS_TALL);
                output.accept(ITEM_EASEL);
            })
            .build();

    public static final RecipeSerializer<RecipeCraftPalette> CRAFTING_SPECIAL_PALETTE_CRAFTING = new SimpleCraftingRecipeSerializer<>(RecipeCraftPalette::new);
    public static final RecipeSerializer<RecipeFillPalette> CRAFTING_SPECIAL_PALETTE_FILLING = new SimpleCraftingRecipeSerializer<>(RecipeFillPalette::new);
    public static final RecipeSerializer<RecipeCanvasCloning> CRAFTING_SPECIAL_CANVAS_CLONING = new SimpleCraftingRecipeSerializer<>(RecipeCanvasCloning::new);
    public static final RecipeSerializer<RecipeTaglessShaped> CRAFTING_TAGLESS_SHAPED = new RecipeTaglessShaped.TaglessSerializer();

    public static void registerRecipes() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Mod.modId, "crafting_special_palette_crafting"), CRAFTING_SPECIAL_PALETTE_CRAFTING);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Mod.modId, "crafting_special_palette_filling"), CRAFTING_SPECIAL_PALETTE_FILLING);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Mod.modId, "crafting_special_canvas_cloning"), CRAFTING_SPECIAL_CANVAS_CLONING);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Mod.modId, "crafting_tagless_shaped"), CRAFTING_TAGLESS_SHAPED);
    }

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mod.modId, "item_palette"), ITEM_PALETTE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mod.modId, "item_canvas"), ITEM_CANVAS);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mod.modId, "item_canvas_large"), ITEM_CANVAS_LARGE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mod.modId, "item_canvas_long"), ITEM_CANVAS_LONG);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mod.modId, "item_canvas_tall"), ITEM_CANVAS_TALL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mod.modId, "item_easel"), ITEM_EASEL);
    }

}