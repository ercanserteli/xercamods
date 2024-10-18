package xerca.xercamusic.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.*;
import xerca.xercamusic.common.packets.serverbound.*;
import xerca.xercamusic.common.tile_entity.BlockEntities;

import java.util.concurrent.Callable;
import java.util.function.Supplier;


public class Mod implements ModInitializer
{
    public static final String MODID = "xercamusic";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_NOTES_IN_PACKET = 5000;

    private void networkRegistry() {
        PayloadTypeRegistry.playS2C().register(ExportMusicPacket.PACKET_ID, ExportMusicPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ImportMusicPacket.PACKET_ID, ImportMusicPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MusicBoxUpdatePacket.PACKET_ID, MusicBoxUpdatePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MusicDataResponsePacket.PACKET_ID, MusicDataResponsePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(NotesPartAckFromServerPacket.PACKET_ID, NotesPartAckFromServerPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SingleNoteClientPacket.PACKET_ID, SingleNoteClientPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TripleNoteClientPacket.PACKET_ID, TripleNoteClientPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(MusicUpdatePacket.PACKET_ID, MusicUpdatePacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(MusicEndedPacket.PACKET_ID, MusicEndedPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ImportMusicSendPacket.PACKET_ID, ImportMusicSendPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(MusicDataRequestPacket.PACKET_ID, MusicDataRequestPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SingleNotePacket.PACKET_ID, SingleNotePacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SendNotesPartToServerPacket.PACKET_ID, SendNotesPartToServerPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(MusicUpdatePacket.PACKET_ID, new MusicUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(MusicEndedPacket.PACKET_ID, new MusicEndedPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(ImportMusicSendPacket.PACKET_ID, new ImportMusicSendPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(MusicDataRequestPacket.PACKET_ID, new MusicDataRequestPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(SingleNotePacket.PACKET_ID, new SingleNotePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(SendNotesPartToServerPacket.PACKET_ID, new SendNotesPartToServerPacketHandler());
    }

//    private void enqueueIMC(final InterModEnqueueEvent event) {} todo this later

    private void registerTriggers() {
        for (int i = 0; i < Triggers.TRIGGER_ARRAY.length; i++) {
            Registry.register(BuiltInRegistries.TRIGGER_TYPES, "become_musician", Triggers.TRIGGER_ARRAY[i]);
        }
    }

    @Override
    public void onInitialize() {
        networkRegistry();
        registerTriggers();
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Items.registerItems();
        Items.registerRecipes();
        Items.registerDataComponents();
        Entities.registerEntities();
        SoundEvents.registerSoundEvents();

        // Registration for loot modifier (used for Voice of God in desert temples)
        LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
            if (source.isBuiltin() && BuiltInLootTables.DESERT_PYRAMID.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .add(LootItem.lootTableItem(Items.GOD));
                tableBuilder.withPool(poolBuilder);
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            CommandImport.register(dispatcher);
            CommandExport.register(dispatcher);
        });
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static <T> T onlyCallOnClient(Supplier<Callable<T>> toRun) {
        if (EnvType.CLIENT == FabricLoader.getInstance().getEnvironmentType()) {
            try {
                return toRun.get().call();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void onlyRunOnClient(Supplier<Runnable> toRun) {
        if (EnvType.CLIENT == FabricLoader.getInstance().getEnvironmentType()) {
            try {
                toRun.get().run();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ResourceLocation id(String location) {
        return ResourceLocation.fromNamespaceAndPath(MODID, location);
    }
}
