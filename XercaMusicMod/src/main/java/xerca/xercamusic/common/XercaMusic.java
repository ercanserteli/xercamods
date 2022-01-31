package xerca.xercamusic.common;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import xerca.xercamusic.client.RenderNothingFactory;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.data.BlockTags;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.ExportMusicPacket;
import xerca.xercamusic.common.packets.ExportMusicPacketHandler;
import xerca.xercamusic.common.packets.ImportMusicPacket;
import xerca.xercamusic.common.packets.ImportMusicPacketHandler;
import xerca.xercamusic.common.packets.ImportMusicSendPacket;
import xerca.xercamusic.common.packets.ImportMusicSendPacketHandler;
import xerca.xercamusic.common.packets.MusicBoxUpdatePacket;
import xerca.xercamusic.common.packets.MusicBoxUpdatePacketHandler;
import xerca.xercamusic.common.packets.MusicDataRequestPacket;
import xerca.xercamusic.common.packets.MusicDataRequestPacketHandler;
import xerca.xercamusic.common.packets.MusicDataResponsePacket;
import xerca.xercamusic.common.packets.MusicDataResponsePacketHandler;
import xerca.xercamusic.common.packets.MusicEndedPacket;
import xerca.xercamusic.common.packets.MusicEndedPacketHandler;
import xerca.xercamusic.common.packets.MusicUpdatePacket;
import xerca.xercamusic.common.packets.MusicUpdatePacketHandler;
import xerca.xercamusic.common.packets.SingleNoteClientPacket;
import xerca.xercamusic.common.packets.SingleNoteClientPacketHandler;
import xerca.xercamusic.common.packets.SingleNotePacket;
import xerca.xercamusic.common.packets.SingleNotePacketHandler;
import xerca.xercamusic.common.packets.TripleNoteClientPacket;
import xerca.xercamusic.common.packets.TripleNoteClientPacketHandler;


@Mod(XercaMusic.MODID)
public class XercaMusic
{
    public static final String MODID = "xercamusic";
    public static final Logger LOGGER = LogManager.getLogger();
    //public static Proxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();


    private void networkRegistry(){
        int msg_id = 0;
        NETWORK_HANDLER.registerMessage(msg_id++, MusicUpdatePacket.class, MusicUpdatePacket::encode, MusicUpdatePacket::decode, MusicUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, MusicEndedPacket.class, MusicEndedPacket::encode, MusicEndedPacket::decode, MusicEndedPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, MusicBoxUpdatePacket.class, MusicBoxUpdatePacket::encode, MusicBoxUpdatePacket::decode, MusicBoxUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, SingleNotePacket.class, SingleNotePacket::encode, SingleNotePacket::decode, SingleNotePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, SingleNoteClientPacket.class, SingleNoteClientPacket::encode, SingleNoteClientPacket::decode, SingleNoteClientPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ExportMusicPacket.class, ExportMusicPacket::encode, ExportMusicPacket::decode, ExportMusicPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ImportMusicPacket.class, ImportMusicPacket::encode, ImportMusicPacket::decode, ImportMusicPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ImportMusicSendPacket.class, ImportMusicSendPacket::encode, ImportMusicSendPacket::decode, ImportMusicSendPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, MusicDataRequestPacket.class, MusicDataRequestPacket::encode, MusicDataRequestPacket::decode, MusicDataRequestPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, MusicDataResponsePacket.class, MusicDataResponsePacket::encode, MusicDataResponsePacket::decode, MusicDataResponsePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, TripleNoteClientPacket.class, TripleNoteClientPacket::encode, TripleNoteClientPacket::decode, TripleNoteClientPacketHandler::handle);
    }

    public XercaMusic() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @OnlyIn(Dist.CLIENT)
    private void setupClientRendering() {
    	RenderingRegistry.registerEntityRenderingHandler(Entities.MUSIC_SPIRIT, new RenderNothingFactory());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        networkRegistry();

        setupClientRendering();
        
        Items.setup();

        Blocks.setup();
        registerTriggers();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Send music sheet's resource location to xercamod for the bookcase interaction
        InterModComms.sendTo("xercamod", "send_note", () ->  new ResourceLocation(MODID, "music_sheet"));
    }

    private void processIMC(final InterModProcessEvent event)
    {
        LOGGER.debug("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    private void registerTriggers() {
        for (int i = 0; i < Triggers.TRIGGER_ARRAY.length; i++)
        {
            CriteriaTriggers.register(Triggers.TRIGGER_ARRAY[i]);
        }
    }

    // Registration for loot modifier (used for Voice of God in desert temples)
    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerLootModifiers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().register(new TempleLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"temple_vog")));
        }

        @SubscribeEvent
        public static void registerDataEvent(final GatherDataEvent event) {
            event.getGenerator().addProvider(new BlockTags(event.getGenerator(), event.getExistingFileHelper()));
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID)
    public static class ForgeEventHandler {
        @SubscribeEvent
        public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
            CommandImport.register(commandDispatcher);
            CommandExport.register(commandDispatcher);
        }
    }

    // This is for conveniently initializing object holders without annoying the IDE
    @SuppressWarnings({"ConstantConditions", "SameReturnValue"})
    public static <T> T Null() {
        return null;
    }
}
