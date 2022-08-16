package xerca.xercapaint.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.PaintCreativeTab;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.crafting.RecipeCanvasCloning;
import xerca.xercapaint.common.item.crafting.RecipeCraftPalette;
import xerca.xercapaint.common.item.crafting.RecipeFillPalette;
import xerca.xercapaint.common.item.crafting.RecipeTaglessShaped;

import static xerca.xercapaint.common.XercaPaint.Null;

@ObjectHolder(XercaPaint.MODID)
public final class Items {
    public static final ItemPalette ITEM_PALETTE = Null();
    public static final ItemCanvas ITEM_CANVAS = Null();
    public static final ItemCanvas ITEM_CANVAS_LARGE = Null();
    public static final ItemCanvas ITEM_CANVAS_LONG = Null();
    public static final ItemCanvas ITEM_CANVAS_TALL = Null();
    public static final ItemEasel ITEM_EASEL = Null();

    public static final RecipeSerializer<RecipeCraftPalette> CRAFTING_SPECIAL_PALETTE_CRAFTING = Null();
    public static final RecipeSerializer<RecipeCraftPalette> CRAFTING_SPECIAL_PALETTE_FILLING = Null();
    public static final RecipeSerializer<RecipeCanvasCloning> CRAFTING_SPECIAL_CANVAS_CLONING = Null();
    public static final RecipeSerializer<RecipeTaglessShaped> CRAFTING_TAGLESS_SHAPED = Null();

    public static PaintCreativeTab paintTab;

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerRecipes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeCraftPalette::new).setRegistryName(XercaPaint.MODID + ":crafting_special_palette_crafting"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeFillPalette::new).setRegistryName(XercaPaint.MODID + ":crafting_special_palette_filling"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeCanvasCloning::new).setRegistryName(XercaPaint.MODID + ":crafting_special_canvas_cloning"));
            event.getRegistry().register(new RecipeTaglessShaped.TaglessSerializer().setRegistryName(XercaPaint.MODID + ":crafting_tagless_shaped"));
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            paintTab = new PaintCreativeTab();

            event.getRegistry().registerAll(
                    new ItemPalette("item_palette"),
                    new ItemCanvas("item_canvas", CanvasType.SMALL),
                    new ItemCanvas("item_canvas_large", CanvasType.LARGE),
                    new ItemCanvas("item_canvas_long", CanvasType.LONG),
                    new ItemCanvas("item_canvas_tall", CanvasType.TALL),
                    new ItemEasel(new Item.Properties().tab(Items.paintTab).stacksTo(1)).setRegistryName("item_easel")
            );
        }
    }

}