package xerca.xercamusic.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.*;
import xerca.xercamusic.common.packets.serverbound.*;
import xerca.xercamusic.common.tile_entity.BlockEntities;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

@net.neoforged.fml.common.Mod(Mod.MODID)
public class Mod {
    public static final String MODID = "xercamusic";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_NOTES_IN_PACKET = 5000;
    public static final int MAX_VOLUME_MARKERS_IN_PACKET = 2000;

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onLootTableLoad);
        LOGGER.info("{} initialized", MODID);
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
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

    public static ResourceLocation id(String location) {
        return ResourceLocation.fromNamespaceAndPath(MODID, location);
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, Blocks::registerBlocks);
        event.register(Registries.ITEM, Items::registerItems);
        event.register(Registries.RECIPE_SERIALIZER, Items::registerRecipes);
        event.register(Registries.DATA_COMPONENT_TYPE, Items::registerDataComponents);
        event.register(Registries.CREATIVE_MODE_TAB, Items::registerCreativeTab);
        event.register(Registries.ENTITY_TYPE, Entities::registerEntities);
        event.register(Registries.BLOCK_ENTITY_TYPE, BlockEntities::registerBlockEntities);
        event.register(Registries.SOUND_EVENT, SoundEvents::registerSoundEvents);
        event.register(Registries.TRIGGER_TYPE, helper -> helper.register(ResourceLocation.withDefaultNamespace("become_musician"), Triggers.BECOME_MUSICIAN));
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToClient(ExportMusicPacket.PACKET_ID, ExportMusicPacket.PACKET_CODEC, ExportMusicPacketHandler::handle);
        registrar.playToClient(ImportMusicPacket.PACKET_ID, ImportMusicPacket.PACKET_CODEC, ImportMusicPacketHandler::handle);
        registrar.playToClient(MusicBoxUpdatePacket.PACKET_ID, MusicBoxUpdatePacket.PACKET_CODEC, MusicBoxUpdatePacketHandler::handle);
        registrar.playToClient(MusicDataResponsePacket.PACKET_ID, MusicDataResponsePacket.PACKET_CODEC, MusicDataResponsePacketHandler::handle);
        registrar.playToClient(NotesPartAckFromServerPacket.PACKET_ID, NotesPartAckFromServerPacket.PACKET_CODEC, NotesPartAckFromServerPacketHandler::handle);
        registrar.playToClient(SingleNoteClientPacket.PACKET_ID, SingleNoteClientPacket.PACKET_CODEC, SingleNoteClientPacketHandler::handle);
        registrar.playToClient(TripleNoteClientPacket.PACKET_ID, TripleNoteClientPacket.PACKET_CODEC, TripleNoteClientPacketHandler::handle);
        registrar.playToServer(MusicUpdatePacket.PACKET_ID, MusicUpdatePacket.PACKET_CODEC, MusicUpdatePacketHandler::handle);
        registrar.playToServer(MusicEndedPacket.PACKET_ID, MusicEndedPacket.PACKET_CODEC, MusicEndedPacketHandler::handle);
        registrar.playToServer(ImportMusicSendPacket.PACKET_ID, ImportMusicSendPacket.PACKET_CODEC, ImportMusicSendPacketHandler::handle);
        registrar.playToServer(MusicDataRequestPacket.PACKET_ID, MusicDataRequestPacket.PACKET_CODEC, MusicDataRequestPacketHandler::handle);
        registrar.playToServer(SingleNotePacket.PACKET_ID, SingleNotePacket.PACKET_CODEC, SingleNotePacketHandler::handle);
        registrar.playToServer(SendNotesPartToServerPacket.PACKET_ID, SendNotesPartToServerPacket.PACKET_CODEC, SendNotesPartToServerPacketHandler::handle);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandImport.register(event.getDispatcher());
        CommandExport.register(event.getDispatcher());
    }

    private void onLootTableLoad(LootTableLoadEvent event) {
        if (BuiltInLootTables.DESERT_PYRAMID.equals(event.getKey())) {
            LootPool.Builder poolBuilder = LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.1f))
                    .add(LootItem.lootTableItem(Items.GOD));
            event.getTable().addPool(poolBuilder.build());
        }
    }
}
