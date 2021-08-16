package xerca.xercapaint.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercapaint.client.ClientProxy;
import xerca.xercapaint.common.packets.*;
import xerca.xercapaint.server.ServerProxy;

@Mod(XercaPaint.MODID)
public class XercaPaint {
    public static final String MODID = "xercapaint";
    public static final String NAME = "Xerca Paint";

    public static Proxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

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
    }

    private void networkRegistry(){
        int msg_id = 0;
        NETWORK_HANDLER.registerMessage(msg_id++, CanvasUpdatePacket.class, CanvasUpdatePacket::encode, CanvasUpdatePacket::decode, CanvasUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, PaletteUpdatePacket.class, PaletteUpdatePacket::encode, PaletteUpdatePacket::decode, PaletteUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, PictureRequestPacket.class, PictureRequestPacket::encode, PictureRequestPacket::decode, PictureRequestPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, PictureSendPacket.class, PictureSendPacket::encode, PictureSendPacket::decode, PictureSendPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ExportPaintingPacket.class, ExportPaintingPacket::encode, ExportPaintingPacket::decode, ExportPaintingPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ImportPaintingPacket.class, ImportPaintingPacket::encode, ImportPaintingPacket::decode, ImportPaintingPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ImportPaintingSendPacket.class, ImportPaintingSendPacket::encode, ImportPaintingSendPacket::decode, ImportPaintingSendPacketHandler::handle);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        networkRegistry();

        proxy.init();
    }


    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }
}
