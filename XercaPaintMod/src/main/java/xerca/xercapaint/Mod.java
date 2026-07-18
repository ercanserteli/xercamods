package xerca.xercapaint;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.*;

public class Mod implements ModInitializer {
    public static final String MOD_ID = "xercapaint";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Items.registerItems();
        Items.registerRecipes();
        Items.registerDataComponents();
        Entities.registerEntities();
        SoundEvents.registerSoundEvents();

        PayloadTypeRegistry.clientboundPlay().register(CloseGuiPacket.PACKET_ID, CloseGuiPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ExportPaintingPacket.PACKET_ID, ExportPaintingPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ImportPaintingPacket.PACKET_ID, ImportPaintingPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenGuiPacket.PACKET_ID, OpenGuiPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PictureSendPacket.PACKET_ID, PictureSendPacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(CanvasUpdatePacket.PACKET_ID, CanvasUpdatePacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(CanvasMiniUpdatePacket.PACKET_ID, CanvasMiniUpdatePacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(EaselLeftPacket.PACKET_ID, EaselLeftPacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ImportPaintingSendPacket.PACKET_ID, ImportPaintingSendPacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PaletteUpdatePacket.PACKET_ID, PaletteUpdatePacket.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PictureRequestPacket.PACKET_ID, PictureRequestPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CanvasUpdatePacket.PACKET_ID, new CanvasUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(CanvasMiniUpdatePacket.PACKET_ID, new CanvasMiniUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(EaselLeftPacket.PACKET_ID, new EaselLeftPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(ImportPaintingSendPacket.PACKET_ID, new ImportPaintingSendPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(PaletteUpdatePacket.PACKET_ID, new PaletteUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(PictureRequestPacket.PACKET_ID, new PictureRequestPacketHandler());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            CommandImport.register(dispatcher);
            CommandExport.register(dispatcher);
        });
    }

    public static Identifier id(String location) {
        return Identifier.fromNamespaceAndPath(MOD_ID, location);
    }
}
