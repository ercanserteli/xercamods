package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;

import java.io.IOException;
import java.nio.file.Path;

import static xerca.xercamusic.client.ClientStuff.sendToServer;
import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;

public class ImportMusicPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<ImportMusicPacket> {
    private static void processMessage(ImportMusicPacket msg, LocalPlayer player) {
        String filename = msg.name() + ".sheet";
        String filepath = "music_sheets/" + filename;
        try {
            CompoundTag tag = NbtIo.read(Path.of(filepath));
            if(tag == null) {
                throw new IOException("File not found!");
            }
            try {
                ImportMusicSendPacket pack = ImportMusicSendPacket.create(tag);
                sendToServer(pack);
            }
            catch (ImportMusicSendPacket.NotesTooLargeException e) {
                if(e.id == null) {
                    throw new IOException("Music has many notes, but no UUID!");
                }
                int partsCount = (int)Math.ceil((double)e.notes.size()/(double)MAX_NOTES_IN_PACKET);
                tag.remove("notes");
                ImportMusicSendPacket pack = ImportMusicSendPacket.create(tag);
                NotesPartAckFromServerPacketHandler.addCallback(e.id, ()-> sendToServer(pack));
                for(int i=0; i<partsCount; i++) {
                    SendNotesPartToServerPacket partPack = new SendNotesPartToServerPacket(e.id, partsCount, i, e.notes.subList(i*MAX_NOTES_IN_PACKET, Math.min((i+1)*MAX_NOTES_IN_PACKET, e.notes.size())));
                    sendToServer(partPack);
                }
            }
        } catch (IOException | ImportMusicSendPacket.NotesTooLargeException | NullPointerException e) {
            e.printStackTrace();
            if (player != null) {
                player.sendSystemMessage(Component.translatable("xercamusic.import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void receive(ImportMusicPacket packet, ClientPlayNetworking.Context context) {
        if(packet != null){
            context.client().execute(()->processMessage(packet, context.player()));
        }
    }
}
