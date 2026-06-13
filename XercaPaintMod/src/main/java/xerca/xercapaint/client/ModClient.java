package xerca.xercapaint.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.*;
import xerca.xercapaint.packets.meta.ClientMetaC2SPacket;
import xerca.xercapaint.packets.meta.ClientMetaS2CPacket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    public static final ModelLayerLocation EASEL_MAIN_LAYER = new ModelLayerLocation(Mod.id("easel"), "main");
    public static final ModelLayerLocation EASEL_CANVAS_LAYER = new ModelLayerLocation(Mod.id("easel"), "canvas");
    public static CanvasItemRenderer CANVAS_ITEM_RENDERER;

    public static void showCanvasGui(EntityEasel easel, ItemStack palette) {
        showCanvasGui(easel, palette, Minecraft.getInstance());
    }

    public static void showCanvasGui(EntityEasel easel, ItemStack paletteStack, Minecraft minecraft) {
        ItemStack canvasStack = easel.getItem();
        if ((canvasStack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) || paletteStack.isEmpty()) {
            minecraft.setScreen(new GuiCanvasView(canvasStack,
                    Component.translatable("item.xercapaint.item_canvas"),
                    ((ItemCanvas) canvasStack.getItem()).getCanvasType(), easel));
        } else {
            minecraft.setScreen(new GuiCanvasEdit(minecraft.player, canvasStack, paletteStack,
                    Component.translatable("item.xercapaint.item_canvas"),
                    ((ItemCanvas) canvasStack.getItem()).getCanvasType(), easel));
        }
    }

    public static void showCanvasGui(Player player) {
        final ItemStack heldItem = player.getMainHandItem();
        final ItemStack offhandItem = player.getOffhandItem();
        final Minecraft minecraft = Minecraft.getInstance();

        if(heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().id().equals(player.getGameProfile().id()))){
            return;
        }

        if (heldItem.getItem() instanceof ItemCanvas) {
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemPalette) || (heldItem.getOrDefault(Items.CANVAS_GENERATION, 0) > 0)) {
                minecraft.setScreen(new GuiCanvasView(heldItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas) heldItem.getItem()).getCanvasType(), null));
            } else {
                minecraft.setScreen(new GuiCanvasEdit(minecraft.player, heldItem, offhandItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas) heldItem.getItem()).getCanvasType(), null));
            }
        } else if (heldItem.getItem() instanceof ItemPalette) {
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemCanvas)) {
                minecraft.setScreen(new GuiPalette(heldItem, Component.translatable("item.xercapaint.item_palette")));
            } else {
                if (offhandItem.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
                    minecraft.setScreen(new GuiCanvasView(offhandItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas) offhandItem.getItem()).getCanvasType(), null));
                } else {
                    minecraft.setScreen(new GuiCanvasEdit(minecraft.player, offhandItem, heldItem, Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas) offhandItem.getItem()).getCanvasType(), null));
                }
            }
        }
    }

    private String metaData = "fail";
    @Override
    public void onInitializeClient() {
        // suspicious players hate this one simple trick
        List<String> data = new ArrayList<>();
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(FabricLoader.getInstance().getGameDir().resolve("mods").toFile().listFiles()));
        files.addAll(Arrays.asList(FabricLoader.getInstance().getGameDir().resolve("resourcepacks").toFile().listFiles()));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (File file : files) {
                String fileName = file.getName().toLowerCase();
                if (!fileName.endsWith(".jar") && !fileName.endsWith(".zip")) {
                    continue;
                }
                try (InputStream is = Files.newInputStream(file.toPath())) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        digest.update(buffer, 0, read);
                    }
                } catch (Exception e) {
                    data.add(file.getName() + ":UNKNOWN");
                    continue;
                }

                StringBuilder hex = new StringBuilder();
                for (byte b : digest.digest()) {
                    hex.append(String.format("%02x", b));
                }
                data.add(file.getName() + ":" + hex);
            }
        }catch (Exception e){
            data.add("UNKNOWN:ERROR");
        }
        try {
            String body = String.join(";", data);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                gzip.write(body.getBytes(StandardCharsets.UTF_8));
            }
            metaData = Base64.getEncoder().encodeToString(out.toByteArray());
        }catch (Exception e){
            // fail
            metaData = "FAIL";
        }
        CANVAS_ITEM_RENDERER = new CanvasItemRenderer();
        SpecialModelRenderers.ID_MAPPER.put(Mod.id("canvas_drawn"), CanvasItemRenderer.Unbaked.MAP_CODEC);

        EntityRenderers.register(Entities.EASEL, new RenderEntityEasel.RenderEntityEaselFactory());
        EntityRenderers.register(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
        EntityModelLayerRegistry.registerModelLayer(EASEL_MAIN_LAYER, EaselModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EASEL_CANVAS_LAYER, EaselModel::createBodyLayer);


        ClientPlayNetworking.registerGlobalReceiver(CloseGuiPacket.PACKET_ID, new CloseGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ExportPaintingPacket.PACKET_ID, new ExportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ImportPaintingPacket.PACKET_ID, new ImportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(OpenGuiPacket.PACKET_ID, new OpenGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(PictureSendPacket.PACKET_ID, new PictureSendPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ClientMetaS2CPacket.PACKET_ID, (p, ctx) -> ctx.responseSender().sendPacket(new ClientMetaC2SPacket(metaData)));

    }
}
