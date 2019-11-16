package xerca.xercapaint.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.PaintCreativeTab;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.crafting.RecipeCraftPalette;
import xerca.xercapaint.common.item.crafting.RecipeFillPalette;

@ObjectHolder(XercaPaint.MODID)
public final class Items {
    public static final ItemPalette ITEM_PALETTE = null;
    public static final ItemCanvas ITEM_CANVAS = null;
    public static final ItemCanvas ITEM_CANVAS_LARGE = null;

    public static final IRecipeSerializer<RecipeCraftPalette> CRAFTING_SPECIAL_PALETTE_CRAFTING = null;
    public static final IRecipeSerializer<RecipeCraftPalette> CRAFTING_SPECIAL_PALETTE_FILLING = null;

    public static PaintCreativeTab paintTab;

    static Item makeItem(String name, ItemGroup tab){
        Item item = new Item(new Item.Properties().group(tab));
        item.setRegistryName(name);
        return item;
    }

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeCraftPalette::new).setRegistryName(XercaPaint.MODID + ":crafting_special_palette_crafting"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeFillPalette::new).setRegistryName(XercaPaint.MODID + ":crafting_special_palette_filling"));
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            paintTab = new PaintCreativeTab();

            event.getRegistry().registerAll(
                    new ItemPalette("item_palette"),
                    new ItemCanvas("item_canvas", CanvasType.SMALL),
                    new ItemCanvas("item_canvas_large", CanvasType.LARGE)
            );
        }
    }

}