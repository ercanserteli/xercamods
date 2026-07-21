package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jspecify.annotations.Nullable;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public class ModClient {
    public static final ModelLayerLocation EASEL_MAIN_LAYER = new ModelLayerLocation(Mod.id("easel"), "main");
    public static final ModelLayerLocation EASEL_CANVAS_LAYER = new ModelLayerLocation(Mod.id("easel"), "canvas");
    private static final String ITEM_CANVAS_TRANSLATION_KEY = "item.xercapaint.item_canvas";
    private static @Nullable CanvasItemRenderer canvasItemRenderer;

    private ModClient() {
    }

    public static void showCanvasGui(EntityEasel easel, ItemStack palette) {
        showCanvasGui(easel, palette, Minecraft.getInstance());
    }

    public static void showCanvasGui(EntityEasel easel, ItemStack paletteStack, Minecraft minecraft) {
        ItemStack canvasStack = easel.getItem();
        ItemCanvas canvasItem = (ItemCanvas) canvasStack.getItem();
        if ((canvasStack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) || paletteStack.isEmpty()) {
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

        if (heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().id().equals(player.getGameProfile().id()))) {
            return;
        }

        if (heldItem.getItem() instanceof ItemCanvas itemCanvas) {
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemPalette) || (heldItem.getOrDefault(Items.CANVAS_GENERATION, 0) > 0)) {
                minecraft.setScreen(new GuiCanvasView(heldItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), itemCanvas.getCanvasType(), itemCanvas.isGlass(), null));
            } else {
                minecraft.setScreen(new GuiCanvasEdit(minecraft.player, heldItem, offhandItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), itemCanvas.getCanvasType(), itemCanvas.isGlass(), null));
            }
        } else if (heldItem.getItem() instanceof ItemPalette) {
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemCanvas offhandCanvas)) {
                minecraft.setScreen(new GuiPalette(heldItem, Component.translatable("item.xercapaint.item_palette")));
            } else {
                if (offhandItem.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                    minecraft.setScreen(new GuiCanvasView(offhandItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), offhandCanvas.getCanvasType(), offhandCanvas.isGlass(), null));
                } else {
                    minecraft.setScreen(new GuiCanvasEdit(minecraft.player, offhandItem, heldItem, Component.translatable(ITEM_CANVAS_TRANSLATION_KEY), offhandCanvas.getCanvasType(), offhandCanvas.isGlass(), null));
                }
            }
        }
    }

    static CanvasItemRenderer requireCanvasItemRenderer() {
        if (canvasItemRenderer == null) {
            throw new IllegalStateException("Canvas item renderer not initialized");
        }
        return canvasItemRenderer;
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.EASEL, new RenderEntityEasel.RenderEntityEaselFactory());
        event.registerEntityRenderer(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EASEL_MAIN_LAYER, EaselModel::createBodyLayer);
        event.registerLayerDefinition(EASEL_CANVAS_LAYER, EaselModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            canvasItemRenderer = new CanvasItemRenderer();
            SpecialModelRenderers.ID_MAPPER.put(Mod.id("canvas"), CanvasSpecialRenderer.Unbaked.MAP_CODEC);
            RangeSelectItemModelProperties.ID_MAPPER.put(ColorsProperty.ID, ColorsProperty.MAP_CODEC);
        });
    }
}
