package xerca.xercamusic.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.IPacket;
import xerca.xercamusic.common.packets.clientbound.*;
import xerca.xercamusic.common.packets.serverbound.MusicEndedPacket;

import java.util.UUID;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ClientStuff implements ClientModInitializer {

    public static void showMusicGui() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet) {
                player.playSound(SoundEvents.openScroll, 1.0f, 0.8f + player.level().random.nextFloat() * 0.4f);
                CompoundTag noteTag = heldItem.getTag();
                if (noteTag != null && !noteTag.isEmpty() && noteTag.contains("id") && noteTag.contains("ver")) {
                    UUID id = noteTag.getUUID("id");
                    int version = noteTag.getInt("ver");
                    MusicManagerClient.checkMusicDataAndRun(id, version, () -> Minecraft.getInstance().setScreen(new GuiMusicSheet(player, noteTag, Component.translatable("item.xercamusic.music_sheet"))));
                } else {
                    Minecraft.getInstance().setScreen(new GuiMusicSheet(player, noteTag, Component.translatable("item.xercamusic.music_sheet")));
                }
            }
        }
    }

    public static void showInstrumentGui() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof IItemInstrument itemInstrument) {
                Minecraft.getInstance().setScreen(new GuiInstrument(player, itemInstrument, Component.translatable("item.xercamusic.instrument_gui"), null));
            }
        }
    }

    public static void showInstrumentGui(IItemInstrument instrument, BlockPos blockInsPos) {
        LocalPlayer player = Minecraft.getInstance().player;
        Minecraft.getInstance().setScreen(new GuiInstrument(player, instrument, Component.translatable("item.xercamusic.instrument_gui"), blockInsPos));
    }

    public static NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        return playNote(event, x, y, z, SoundSource.PLAYERS, volume, pitch, lengthTicks);
    }

    public static NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch) {
        return playNote(event, x, y, z, SoundSource.PLAYERS, volume, pitch, (byte) -1);
    }

    public static void playNoteTE(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        playNote(event, x, y, z, SoundSource.RECORDS, volume, pitch, lengthTicks);
    }

    public static NoteSound playNote(SoundEvent event, double x, double y, double z, SoundSource category, float volume, float pitch, byte lengthTicks) {
        NoteSound sound = new NoteSound(event, category, (float) x, (float) y, (float) z, volume, pitch, lengthTicks);
        Minecraft.getInstance().getSoundManager().play(sound);
        return sound;
    }

    public static void endMusic(int spiritID, int playerID) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && playerID == player.getId()) {
            MusicEndedPacket pack = new MusicEndedPacket(spiritID);
            sendToServer(pack);
        }
    }

    public static void sendToServer(IPacket packet) {
        ClientPlayNetworking.send(packet.getID(), packet.encode());
    }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Entities.MUSIC_SPIRIT, new RenderNothingFactory());

        ClientPlayNetworking.registerGlobalReceiver(ExportMusicPacket.ID, new ExportMusicPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ImportMusicPacket.ID, new ImportMusicPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(MusicBoxUpdatePacket.ID, new MusicBoxUpdatePacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(MusicDataResponsePacket.ID, new MusicDataResponsePacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(SingleNoteClientPacket.ID, new SingleNoteClientPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(TripleNoteClientPacket.ID, new TripleNoteClientPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(NotesPartAckFromServerPacket.ID, new NotesPartAckFromServerPacketHandler());

        ClientPlayConnectionEvents.JOIN.register((ClientPacketListener handler, PacketSender sender, Minecraft client) -> {
            Mod.LOGGER.debug("ClientPacketListener Join Event");
            MusicManagerClient.load();
        });
        ClientPlayNetworking.registerGlobalReceiver(EntityMusicSpirit.SPAWN_PACKET_ID, (client, handler, buf, responseSender) -> {
            EntityMusicSpirit newSpirit = new EntityMusicSpirit(client.level);
            ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(buf);
            newSpirit.recreateFromPacket(packet);
            newSpirit.readSpawnData(buf);
            client.execute(() -> {
                if (client.level != null) {
                    client.level.putNonPlayerEntity(newSpirit.getId(), newSpirit);
                } else {
                    Mod.LOGGER.warn("Could not add music spirit - Client level is null");
                }
            });
        });
    }
}
