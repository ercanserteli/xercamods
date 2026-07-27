package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

public class ClientStuff {
    public static final ModelLayerLocation EASEL_MAIN_LAYER = new ModelLayerLocation(new ResourceLocation(XercaPaint.MODID, "easel"), "main");
    public static final ModelLayerLocation EASEL_CANVAS_LAYER = new ModelLayerLocation(new ResourceLocation(XercaPaint.MODID, "easel"), "canvas");
    private static final String ITEM_CANVAS_TRANSLATION_KEY = "item.xercapaint.item_canvas";
    private static final String DRAWN_PREDICATE_ID = "drawn";
    private static @Nullable CanvasItemRenderer canvasItemRenderer;

    public static void showCanvasGui(EntityEasel easel, ItemStack palette) {
        showCanvasGui(easel, palette, Minecraft.getInstance());
    }

    public static void showCanvasGui(EntityEasel easel, ItemStack paletteStack, Minecraft minecraft) {
        ItemStack canvasStack = easel.getItem();
        ItemCanvas canvasItem = (ItemCanvas) canvasStack.getItem();
        if (ItemCanvas.getGeneration(canvasStack) > 0 || paletteStack.isEmpty()) {
            minecraft.setScreen(new GuiCanvasView(canvasStack,
                    Component.translatable(ITEM_CANVAS_TRANSLATION_KEY),
                    canvasItem.getCanvasType(), canvasItem.isGlass(), easel));
        } else {
            if (minecraft.player == null) {
                return;
            }
            minecraft.setScreen(new GuiCanvasEdit(minecraft.player, canvasStack, paletteStack,
                    Component.translatable(ITEM_CANVAS_TRANSLATION_KEY),
                    canvasItem.getCanvasType(), canvasItem.isGlass(), easel));
        }
    }

    public static void showCanvasGui(Player player) {
        final ItemStack heldItem = player.getMainHandItem();
        final ItemStack offhandItem = player.getOffhandItem();
        final Minecraft minecraft = Minecraft.getInstance();

        if (heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().getId().equals(player.getGameProfile().getId()))) {
            return;
        }

        if (heldItem.getItem() instanceof ItemCanvas itemCanvas) {
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemPalette) || ItemCanvas.getGeneration(heldItem) > 0) {
                minecraft.setScreen(new GuiCanvasView(heldItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), itemCanvas.getCanvasType(), itemCanvas.isGlass(), null));
            } else {
                minecraft.setScreen(new GuiCanvasEdit(minecraft.player, heldItem, offhandItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), itemCanvas.getCanvasType(), itemCanvas.isGlass(), null));
            }
        } else if (heldItem.getItem() instanceof ItemPalette) {
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemCanvas offhandCanvas)) {
                minecraft.setScreen(new GuiPalette(heldItem, Component.translatable("item.xercapaint.item_palette")));
            } else {
                if (ItemCanvas.getGeneration(offhandItem) > 0) {
                    minecraft.setScreen(new GuiCanvasView(offhandItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), offhandCanvas.getCanvasType(), offhandCanvas.isGlass(), null));
                } else {
                    minecraft.setScreen(new GuiCanvasEdit(minecraft.player, offhandItem, heldItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), offhandCanvas.getCanvasType(), offhandCanvas.isGlass(), null));
                }
            }
        }
    }

    public static CanvasItemRenderer requireCanvasItemRenderer() {
        if (canvasItemRenderer == null) {
            canvasItemRenderer = new CanvasItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }
        return canvasItemRenderer;
    }

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusSubscriber {
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ClampedItemPropertyFunction drawn = (itemStack, level, livingEntity, i) -> {
                    CompoundTag tag = itemStack.getTag();
                    return tag != null && tag.contains(ItemCanvas.TAG_PIXELS) ? 1.0F : 0.0F;
                };
                ClampedItemPropertyFunction colors = (stack, worldIn, entityIn, i) ->
                        ItemPalette.basicColorCount(stack) / (float) ItemPalette.BASIC_COLOR_COUNT;
                registerDrawn(Items.ITEM_CANVAS.get(), drawn);
                registerDrawn(Items.ITEM_CANVAS_LARGE.get(), drawn);
                registerDrawn(Items.ITEM_CANVAS_LONG.get(), drawn);
                registerDrawn(Items.ITEM_CANVAS_TALL.get(), drawn);
                registerDrawn(Items.ITEM_CANVAS_GLASS.get(), drawn);
                registerDrawn(Items.ITEM_CANVAS_GLASS_LARGE.get(), drawn);
                registerDrawn(Items.ITEM_CANVAS_GLASS_LONG.get(), drawn);
                registerDrawn(Items.ITEM_CANVAS_GLASS_TALL.get(), drawn);
                ItemProperties.register(Items.ITEM_PALETTE.get(), new ResourceLocation(XercaPaint.MODID, "colors"), colors);
            });
        }

        private static void registerDrawn(Item item, ClampedItemPropertyFunction drawn) {
            ItemProperties.register(item, new ResourceLocation(XercaPaint.MODID, DRAWN_PREDICATE_ID), drawn);
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.CANVAS.get(), new RenderEntityCanvas.RenderEntityCanvasFactory());
            event.registerEntityRenderer(Entities.EASEL.get(), new RenderEntityEasel.RenderEntityEaselFactory());
        }

        @SubscribeEvent
        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(EASEL_MAIN_LAYER, EaselModel::createBodyLayer);
            event.registerLayerDefinition(EASEL_CANVAS_LAYER, EaselModel::createBodyLayer);
        }
    }
}
