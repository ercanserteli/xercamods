package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.data.BlockTags;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.IPacket;
import xerca.xercamusic.common.packets.clientbound.*;
import xerca.xercamusic.common.packets.serverbound.*;
import xerca.xercamusic.common.tile_entity.BlockEntities;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Mod(XercaMusic.MODID)
public class XercaMusic {
    public static final String MODID = "xercamusic";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_NOTES_IN_PACKET = 5000;
    public static final int MAX_VOLUME_MARKERS_IN_PACKET = 5000;

    private static final String PROTOCOL_VERSION = Integer.toString(2);
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLMS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TEMPLE_VOG = GLMS.register("temple_vog", TempleLootModifier.CODEC);


    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static void sendToClient(ServerPlayer player, Object packet) {
        NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Nullable
    public static <T> T onlyCallOnClient(Supplier<Callable<T>> toRun) throws Exception {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return toRun.get().call();
        }
        return null;
    }

    public static void onlyRunOnClient(Supplier<Runnable> toRun) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            toRun.get().run();
        }
    }

    /**
     * The packet classes are shared with the Fabric line, where they encode themselves into a fresh buffer.
     * Forge hands us the target buffer instead, so copy the encoded bytes across.
     */
    private static <T extends IPacket> void registerPacket(int id, Class<T> type, Function<FriendlyByteBuf, T> decoder,
                                                           BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        NETWORK_HANDLER.registerMessage(id, type, (pkt, buf) -> buf.writeBytes(pkt.encode()), decoder::apply, handler);
    }

    @SuppressWarnings("UnusedAssignment")
    private void networkRegistry() {
        int msg_id = 0;
        registerPacket(msg_id++, MusicUpdatePacket.class, MusicUpdatePacket::decode, MusicUpdatePacketHandler::handle);
        registerPacket(msg_id++, MusicEndedPacket.class, MusicEndedPacket::decode, MusicEndedPacketHandler::handle);
        registerPacket(msg_id++, MusicBoxUpdatePacket.class, MusicBoxUpdatePacket::decode, MusicBoxUpdatePacketHandler::handle);
        registerPacket(msg_id++, SingleNotePacket.class, SingleNotePacket::decode, SingleNotePacketHandler::handle);
        registerPacket(msg_id++, SingleNoteClientPacket.class, SingleNoteClientPacket::decode, SingleNoteClientPacketHandler::handle);
        registerPacket(msg_id++, ExportMusicPacket.class, ExportMusicPacket::decode, ExportMusicPacketHandler::handle);
        registerPacket(msg_id++, ImportMusicPacket.class, ImportMusicPacket::decode, ImportMusicPacketHandler::handle);
        registerPacket(msg_id++, ImportMusicSendPacket.class, ImportMusicSendPacket::decode, ImportMusicSendPacketHandler::handle);
        registerPacket(msg_id++, MusicDataRequestPacket.class, MusicDataRequestPacket::decode, MusicDataRequestPacketHandler::handle);
        registerPacket(msg_id++, MusicDataResponsePacket.class, MusicDataResponsePacket::decode, MusicDataResponsePacketHandler::handle);
        registerPacket(msg_id++, TripleNoteClientPacket.class, TripleNoteClientPacket::decode, TripleNoteClientPacketHandler::handle);
        registerPacket(msg_id++, SendNotesPartToServerPacket.class, SendNotesPartToServerPacket::decode, SendNotesPartToServerPacketHandler::handle);
        registerPacket(msg_id++, NotesPartAckFromServerPacket.class, NotesPartAckFromServerPacket::decode, NotesPartAckFromServerPacketHandler::handle);
    }

    public XercaMusic() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Entities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockEntities.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        GLMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            networkRegistry();
            registerTriggers();
            Items.setup();
            SoundEvents.setup();
        });
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Send music sheet's resource location to xercamod for the bookcase interaction
        InterModComms.sendTo("xercamod", "send_note", () -> new ResourceLocation(MODID, "music_sheet"));
    }

    private void processIMC(final InterModProcessEvent event) {
        LOGGER.debug("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    private void registerTriggers() {
        for (int i = 0; i < Triggers.TRIGGER_ARRAY.length; i++) {
            CriteriaTriggers.register(Triggers.TRIGGER_ARRAY[i]);
        }
    }

    // Registration for loot modifier (used for Voice of God in desert temples)
    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerDataEvent(final GatherDataEvent event) {
            event.getGenerator().addProvider(event.includeServer(), new BlockTags(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID)
    public static class ForgeEventHandler {
        @SubscribeEvent
        public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
            CommandImport.register(commandDispatcher);
            CommandExport.register(commandDispatcher);
        }
    }

    // This is for conveniently initializing object holders without annoying the IDE
    @SuppressWarnings({"SameReturnValue"})
    public static <T> T Null() {
        return null;
    }
}
