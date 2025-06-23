package xerca.xercapaint.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.*;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    public static final ModelLayerLocation EASEL_MAIN_LAYER = new ModelLayerLocation(Mod.id("easel"), "main");
    public static final ModelLayerLocation EASEL_CANVAS_LAYER = new ModelLayerLocation(Mod.id("easel"), "canvas");
    public static CanvasItemRenderer CANVAS_ITEM_RENDERER;

    public static void showCanvasGui(EntityEasel easel, ItemStack palette){
        showCanvasGui(easel, palette, Minecraft.getInstance());
    }

    public static void showCanvasGui(EntityEasel easel, ItemStack paletteStack, Minecraft minecraft){
        ItemStack canvasStack = easel.getItem();
        if((canvasStack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) || paletteStack.isEmpty()){
            minecraft.setScreen(new GuiCanvasView(canvasStack,
                    Component.translatable("item.xercapaint.item_canvas"),
                    ((ItemCanvas)canvasStack.getItem()).getCanvasType(), easel));
        }
        else{
            minecraft.setScreen(new GuiCanvasEdit(minecraft.player, canvasStack, paletteStack,
                    Component.translatable("item.xercapaint.item_canvas"),
                    ((ItemCanvas)canvasStack.getItem()).getCanvasType(), easel));
        }
    }

    public static void showCanvasGui(Player player){
        final ItemStack heldItem = player.getMainHandItem();
        final ItemStack offhandItem = player.getOffhandItem();
        final Minecraft minecraft = Minecraft.getInstance();

        if(heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().getId().equals(player.getGameProfile().getId()))){
            return;
        }

        if(heldItem.getItem() instanceof ItemCanvas){
            if(offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemPalette) || (heldItem.getOrDefault(Items.CANVAS_GENERATION, 0) > 0)){
                minecraft.setScreen(new GuiCanvasView(heldItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType(), null));
            }
            else{
                minecraft.setScreen(new GuiCanvasEdit(minecraft.player, heldItem, offhandItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType(), null));
            }
        }
        else if(heldItem.getItem() instanceof ItemPalette){
            if(offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemCanvas)){
                minecraft.setScreen(new GuiPalette(heldItem, Component.translatable("item.xercapaint.item_palette")));
            }
            else{
                if(offhandItem.getOrDefault(Items.CANVAS_GENERATION, 0) > 0){
                    minecraft.setScreen(new GuiCanvasView(offhandItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType(), null));
                }
                else{
                    minecraft.setScreen(new GuiCanvasEdit(minecraft.player, offhandItem, heldItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType(), null));
                }
            }
        }
    }

    @Override
    public void onInitializeClient() {
        CANVAS_ITEM_RENDERER = new CanvasItemRenderer();
        SpecialModelRenderers.ID_MAPPER.put(Mod.id("item_canvas"), CanvasItemRenderer.UnbakedSmall.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(Mod.id("item_canvas_large"), CanvasItemRenderer.UnbakedLarge.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(Mod.id("item_canvas_long"), CanvasItemRenderer.UnbakedLong.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(Mod.id("item_canvas_tall"), CanvasItemRenderer.UnbakedTall.MAP_CODEC);

        EntityRendererRegistry.register(Entities.EASEL, new RenderEntityEasel.RenderEntityEaselFactory());
        EntityRendererRegistry.register(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
        EntityModelLayerRegistry.registerModelLayer(EASEL_MAIN_LAYER, EaselModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EASEL_CANVAS_LAYER, EaselModel::createBodyLayer);


        ClientPlayNetworking.registerGlobalReceiver(CloseGuiPacket.PACKET_ID, new CloseGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ExportPaintingPacket.PACKET_ID, new ExportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ImportPaintingPacket.PACKET_ID, new ImportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(OpenGuiPacket.PACKET_ID, new OpenGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(PictureSendPacket.PACKET_ID, new PictureSendPacketHandler());
    }
}
