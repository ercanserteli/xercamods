package xerca.xercapaint.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.*;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    public static final ModelLayerLocation EASEL_MAIN_LAYER = new ModelLayerLocation(Mod.id("easel"), "main");
    public static final ModelLayerLocation EASEL_CANVAS_LAYER = new ModelLayerLocation(Mod.id("easel"), "canvas");
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

    static CanvasItemRenderer requireCanvasItemRenderer() {
        if (canvasItemRenderer == null) {
            throw new IllegalStateException("Canvas item renderer not initialized");
        }
        return canvasItemRenderer;
    }

    @Override
    public void onInitializeClient() {
        canvasItemRenderer = new CanvasItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_LARGE, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_LONG, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_TALL, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_GLASS, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_GLASS_LARGE, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_GLASS_LONG, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_GLASS_TALL, canvasItemRenderer);

        EntityRendererRegistry.register(Entities.EASEL, new RenderEntityEasel.RenderEntityEaselFactory());
        EntityRendererRegistry.register(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
        EntityModelLayerRegistry.registerModelLayer(EASEL_MAIN_LAYER, EaselModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EASEL_CANVAS_LAYER, EaselModel::createBodyLayer);

        ClampedItemPropertyFunction drawn = (itemStack, level, livingEntity, i) -> {
            CompoundTag tag = itemStack.getTag();
            if (tag == null || !tag.contains(ItemCanvas.TAG_PIXELS)) return 0.0f;
            else return 1.0F;
        };
        ClampedItemPropertyFunction colors = (stack, worldIn, entityIn, i) ->
                ItemPalette.basicColorCount(stack) / 16.0F;
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_LARGE, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_LONG, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_TALL, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_GLASS, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_GLASS_LARGE, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_GLASS_LONG, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_GLASS_TALL, Mod.id(DRAWN_PREDICATE_ID), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_PALETTE, Mod.id("colors"), colors);

        ClientPlayNetworking.registerGlobalReceiver(Mod.CLOSE_GUI_PACKET_ID, new CloseGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.EXPORT_PAINTING_PACKET_ID, new ExportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.IMPORT_PAINTING_PACKET_ID, new ImportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.OPEN_GUI_PACKET_ID, new OpenGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.PICTURE_SEND_PACKET_ID, new PictureSendPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.ADD_CANVAS_PACKET_ID, new ClientboundAddCanvasPacketHandler());
    }
}
