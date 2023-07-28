package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;

import java.io.File;
import java.io.IOException;

import static xerca.xercamusic.client.ClientStuff.sendToServer;
import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class ImportMusicPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
        private static void processMessage(ImportMusicPacket msg, LocalPlayer player) {
            String filename = msg.getName() + ".sheet";
            String filepath = "music_sheets/" + filename;
            try {
                CompoundTag tag = NbtIo.read(new File(filepath));
                if(tag == null) {
                    throw new IOException("File not found!");
                }
                try {
                    ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
                    sendToServer(pack);
                }
                catch (ImportMusicSendPacket.NotesTooLargeException e) {
                    if(e.id == null) {
                        throw new IOException("Music has many notes, but no UUID!");
                    }
                    int partsCount = (int)Math.ceil((double)e.notes.size()/(double)MAX_NOTES_IN_PACKET);
                    tag.remove("notes");
                    ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
                    NotesPartAckFromServerPacketHandler.addCallback(e.id, ()-> sendToServer(pack));
                    for(int i=0; i<partsCount; i++) {
                        SendNotesPartToServerPacket partPack = new SendNotesPartToServerPacket(e.id, partsCount, i, e.notes.subList(i*MAX_NOTES_IN_PACKET, Math.min((i+1)*MAX_NOTES_IN_PACKET, e.notes.size())));
                        sendToServer(partPack);
                    }
                }
            } catch (IOException | ImportMusicSendPacket.NotesTooLargeException | NullPointerException e) {
                e.printStackTrace();
                if (player != null) {
                    player.sendSystemMessage(Component.translatable("import.fail.4", filepath).withStyle(ChatFormatting.RED));
                }
            }
        }

        @Override
        public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
            ImportMusicPacket packet = ImportMusicPacket.decode(buf);
            if(packet != null){
                client.execute(()->processMessage(packet, client.player));
            }
        }
    }
