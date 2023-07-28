package xerca.xercamusic.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.CriteriaTriggers;
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
import xerca.xercamusic.common.packets.IPacket;
import xerca.xercamusic.common.packets.serverbound.*;
import xerca.xercamusic.common.tile_entity.BlockEntities;

import java.util.concurrent.Callable;
import java.util.function.Supplier;


public class XercaMusic implements ModInitializer
{
    public static final String MODID = "xercamusic";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_NOTES_IN_PACKET = 5000;


    private void networkRegistry() {
        ServerPlayNetworking.registerGlobalReceiver(MusicUpdatePacket.ID, new MusicUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(MusicEndedPacket.ID, new MusicEndedPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(ImportMusicSendPacket.ID, new ImportMusicSendPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(MusicDataRequestPacket.ID, new MusicDataRequestPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(SingleNotePacket.ID, new SingleNotePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(SendNotesPartToServerPacket.ID, new SendNotesPartToServerPacketHandler());
    }

//    private void enqueueIMC(final InterModEnqueueEvent event) {} todo this later

    private void registerTriggers() {
        for (int i = 0; i < Triggers.TRIGGER_ARRAY.length; i++) {
            CriteriaTriggers.register(Triggers.TRIGGER_ARRAY[i]);
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
        Entities.registerEntities();
        SoundEvents.registerSoundEvents();

        // Registration for loot modifier (used for Voice of God in desert temples)
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && BuiltInLootTables.DESERT_PYRAMID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .with(LootItem.lootTableItem(Items.GOD).build());
                tableBuilder.withPool(poolBuilder);
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            CommandImport.register(dispatcher);
            CommandExport.register(dispatcher);
        });
    }

    public static void sendToClient(ServerPlayer player, IPacket packet) {
        ServerPlayNetworking.send(player, packet.getID(), packet.encode());
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
}
