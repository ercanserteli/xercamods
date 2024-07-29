package xerca.xercapaint.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.crafting.RecipeCanvasCloning;
import xerca.xercapaint.common.item.crafting.RecipeCraftPalette;
import xerca.xercapaint.common.item.crafting.RecipeFillPalette;
import xerca.xercapaint.common.item.crafting.RecipeTaglessShaped;

import java.util.Arrays;

public final class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, XercaPaint.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, XercaPaint.MODID);

    public static final RegistryObject<ItemPalette> ITEM_PALETTE = ITEMS.register("item_palette", ItemPalette::new);
    public static final RegistryObject<ItemCanvas> ITEM_CANVAS = ITEMS.register("item_canvas", () -> new ItemCanvas(CanvasType.SMALL));
    public static final RegistryObject<ItemCanvas> ITEM_CANVAS_LARGE = ITEMS.register("item_canvas_large", () -> new ItemCanvas(CanvasType.LARGE));
    public static final RegistryObject<ItemCanvas> ITEM_CANVAS_LONG = ITEMS.register("item_canvas_long", () -> new ItemCanvas(CanvasType.LONG));
    public static final RegistryObject<ItemCanvas> ITEM_CANVAS_TALL = ITEMS.register("item_canvas_tall", () -> new ItemCanvas(CanvasType.TALL));
    public static final RegistryObject<ItemEasel> ITEM_EASEL = ITEMS.register("item_easel", () -> new ItemEasel(new Item.Properties().stacksTo(1)));

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, XercaPaint.MODID);
    public static final RegistryObject<CreativeModeTab> PAINT_TAB = TABS.register("paint_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("item_group." + XercaPaint.MODID + ".paint_tab"))
                    .icon(() -> ITEM_PALETTE.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        ItemStack fullPalette = new ItemStack(ITEM_PALETTE.get());
                        byte[] basicColors = new byte[16];
                        Arrays.fill(basicColors, (byte)1);
                        fullPalette.getOrCreateTag().putByteArray("basic", basicColors);

                        output.accept(ITEM_PALETTE.get());
                        output.accept(fullPalette);
                        output.accept(ITEM_CANVAS.get());
                        output.accept(ITEM_CANVAS_LONG.get());
                        output.accept(ITEM_CANVAS_TALL.get());
                        output.accept(ITEM_CANVAS_LARGE.get());
                        output.accept(ITEM_EASEL.get());
                    })
                    .build()
    );

    public static final RegistryObject<RecipeSerializer<RecipeCraftPalette>> CRAFTING_SPECIAL_PALETTE_CRAFTING = RECIPE_SERIALIZERS.register(
            "crafting_special_palette_crafting", () -> new SimpleCraftingRecipeSerializer<>(RecipeCraftPalette::new));
    public static final RegistryObject<RecipeSerializer<RecipeFillPalette>> CRAFTING_SPECIAL_PALETTE_FILLING = RECIPE_SERIALIZERS.register(
            "crafting_special_palette_filling", () -> new SimpleCraftingRecipeSerializer<>(RecipeFillPalette::new));
    public static final RegistryObject<RecipeSerializer<RecipeCanvasCloning>> CRAFTING_SPECIAL_CANVAS_CLONING = RECIPE_SERIALIZERS.register(
            "crafting_special_canvas_cloning", () -> new SimpleCraftingRecipeSerializer<>(RecipeCanvasCloning::new));
    public static final RegistryObject<RecipeSerializer<RecipeTaglessShaped>> CRAFTING_TAGLESS_SHAPED = RECIPE_SERIALIZERS.register(
            "crafting_tagless_shaped", RecipeTaglessShaped.TaglessSerializer::new);
}
