package xerca.xercapaint;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.*;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public class Mod {
    public static final String MOD_ID = "xercapaint";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        LOGGER.info("{} initialized", MOD_ID);
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.ITEM, Items::registerItems);
        event.register(Registries.RECIPE_SERIALIZER, Items::registerRecipes);
        event.register(Registries.DATA_COMPONENT_TYPE, Items::registerDataComponents);
        event.register(Registries.CREATIVE_MODE_TAB, Items::registerCreativeTab);
        event.register(Registries.ENTITY_TYPE, Entities::registerEntities);
        event.register(Registries.SOUND_EVENT, SoundEvents::registerSoundEvents);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToClient(CloseGuiPacket.PACKET_ID, CloseGuiPacket.PACKET_CODEC, CloseGuiPacketHandler::handle);
        registrar.playToClient(ExportPaintingPacket.PACKET_ID, ExportPaintingPacket.PACKET_CODEC, ExportPaintingPacketHandler::handle);
        registrar.playToClient(ImportPaintingPacket.PACKET_ID, ImportPaintingPacket.PACKET_CODEC, ImportPaintingPacketHandler::handle);
        registrar.playToClient(OpenGuiPacket.PACKET_ID, OpenGuiPacket.PACKET_CODEC, OpenGuiPacketHandler::handle);
        registrar.playToClient(PictureSendPacket.PACKET_ID, PictureSendPacket.PACKET_CODEC, PictureSendPacketHandler::handle);
        registrar.playToServer(CanvasUpdatePacket.PACKET_ID, CanvasUpdatePacket.PACKET_CODEC, CanvasUpdatePacketHandler::handle);
        registrar.playToServer(CanvasMiniUpdatePacket.PACKET_ID, CanvasMiniUpdatePacket.PACKET_CODEC, CanvasMiniUpdatePacketHandler::handle);
        registrar.playToServer(EaselLeftPacket.PACKET_ID, EaselLeftPacket.PACKET_CODEC, EaselLeftPacketHandler::handle);
        registrar.playToServer(ImportPaintingSendPacket.PACKET_ID, ImportPaintingSendPacket.PACKET_CODEC, ImportPaintingSendPacketHandler::handle);
        registrar.playToServer(PaletteUpdatePacket.PACKET_ID, PaletteUpdatePacket.PACKET_CODEC, PaletteUpdatePacketHandler::handle);
        registrar.playToServer(PictureRequestPacket.PACKET_ID, PictureRequestPacket.PACKET_CODEC, PictureRequestPacketHandler::handle);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandImport.register(event.getDispatcher());
        CommandExport.register(event.getDispatcher());
    }

    public static Identifier id(String location) {
        return Identifier.fromNamespaceAndPath(MOD_ID, location);
    }
}
