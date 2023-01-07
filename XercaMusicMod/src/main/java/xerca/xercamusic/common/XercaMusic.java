package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
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
import xerca.xercamusic.common.packets.*;
import xerca.xercamusic.common.tile_entity.TileEntities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


@Mod(XercaMusic.MODID)
public class XercaMusic
{
    public static final String MODID = "xercamusic";
    public static final Logger LOGGER = LogManager.getLogger();

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLMS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TEMPLE_VOG = GLMS.register("temple_vog", TempleLootModifier.CODEC);


    @SuppressWarnings("UnusedAssignment")
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

        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Entities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TileEntities.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        GLMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(()->{
            networkRegistry();
            registerTriggers();
            Items.setup();
            SoundEvents.setup();
        });
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
        public static void registerDataEvent(final GatherDataEvent event) {
            event.getGenerator().addProvider(event.includeServer(), new BlockTags(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        }

        @SubscribeEvent
        public static void buildContents(CreativeModeTabEvent.Register event) {
            event.registerCreativeModeTab(new ResourceLocation(MODID, "paint_tab"), builder ->
                    builder.title(Component.translatable("item_group." + MODID + ".music_tab"))
                            .icon(() -> new ItemStack(Items.GUITAR.get()))
                            .displayItems((enabledFlags, populator, hasPermissions) -> {
                                populator.accept(Items.MUSIC_SHEET.get());
                                populator.accept(Items.GUITAR.get());
                                populator.accept(Items.LYRE.get());
                                populator.accept(Items.BANJO.get());
                                populator.accept(Items.DRUM.get());
                                populator.accept(Items.CYMBAL.get());
                                populator.accept(Items.DRUM_KIT.get());
                                populator.accept(Items.XYLOPHONE.get());
                                populator.accept(Items.TUBULAR_BELL.get());
                                populator.accept(Items.SANSULA.get());
                                populator.accept(Items.VIOLIN.get());
                                populator.accept(Items.CELLO.get());
                                populator.accept(Items.FLUTE.get());
                                populator.accept(Items.SAXOPHONE.get());
                                populator.accept(Items.GOD.get());
                                populator.accept(Items.PIANO.get());
                                populator.accept(Items.OBOE.get());
                                populator.accept(Items.REDSTONE_GUITAR.get());
                                populator.accept(Items.FRENCH_HORN.get());
                                populator.accept(Items.BASS_GUITAR.get());
                                populator.accept(Items.MUSIC_BOX.get());
                                populator.accept(Items.METRONOME.get());
                            })
            );
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
