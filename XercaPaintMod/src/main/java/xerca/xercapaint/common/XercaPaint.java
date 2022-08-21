package xerca.xercapaint.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.packets.*;

@Mod(XercaPaint.MODID)
public class XercaPaint {
    public static final String MODID = "xercapaint";

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(XercaPaint.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static final Logger LOGGER = LogManager.getLogger();

    public XercaPaint() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Entities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        SoundEvents.SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SuppressWarnings({"UnusedAssignment"})
    private void networkRegistry(){
        int msg_id = 0;
        NETWORK_HANDLER.registerMessage(msg_id++, CanvasUpdatePacket.class, CanvasUpdatePacket::encode, CanvasUpdatePacket::decode, CanvasUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, PaletteUpdatePacket.class, PaletteUpdatePacket::encode, PaletteUpdatePacket::decode, PaletteUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, PictureRequestPacket.class, PictureRequestPacket::encode, PictureRequestPacket::decode, PictureRequestPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, PictureSendPacket.class, PictureSendPacket::encode, PictureSendPacket::decode, PictureSendPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ExportPaintingPacket.class, ExportPaintingPacket::encode, ExportPaintingPacket::decode, ExportPaintingPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ImportPaintingPacket.class, ImportPaintingPacket::encode, ImportPaintingPacket::decode, ImportPaintingPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ImportPaintingSendPacket.class, ImportPaintingSendPacket::encode, ImportPaintingSendPacket::decode, ImportPaintingSendPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, OpenGuiPacket.class, OpenGuiPacket::encode, OpenGuiPacket::decode, OpenGuiPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, CloseGuiPacket.class, CloseGuiPacket::encode, CloseGuiPacket::decode, CloseGuiPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, CanvasMiniUpdatePacket.class, CanvasMiniUpdatePacket::encode, CanvasMiniUpdatePacket::decode, CanvasMiniUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, EaselLeftPacket.class, EaselLeftPacket::encode, EaselLeftPacket::decode, EaselLeftPacketHandler::handle);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(this::networkRegistry);
    }


    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }
}
