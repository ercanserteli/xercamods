package xerca.xercamusic.common.packets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class ImportMusicPacketHandler {
    public static void handle(final ImportMusicPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(ImportMusicPacket msg) {
        String filename = msg.getName() + ".sheet";
        String filepath = "music_sheets/" + filename;
        try {
            CompoundTag tag = NbtIo.read(new File(filepath));
            try {
                ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
                XercaMusic.NETWORK_HANDLER.sendToServer(pack);
            }
            catch (ImportMusicSendPacket.NotesTooLargeException e) {
                if(e.id == null) {
                    throw new IOException("Music has many notes, but no UUID!");
                }
                int partsCount = (int)Math.ceil((double)e.notes.size()/(double)MAX_NOTES_IN_PACKET);
                tag.remove("notes");
                ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
                NotesPartAckFromServerPacketHandler.addCallback(e.id, ()-> XercaMusic.NETWORK_HANDLER.sendToServer(pack));
                for(int i=0; i<partsCount; i++) {
                    SendNotesPartToServerPacket partPack = new SendNotesPartToServerPacket(e.id, partsCount, i, e.notes.subList(i*MAX_NOTES_IN_PACKET, Math.min((i+1)*MAX_NOTES_IN_PACKET, e.notes.size())));
                    XercaMusic.NETWORK_HANDLER.sendToServer(partPack);
                }
            }
        } catch (IOException | ImportMusicSendPacket.NotesTooLargeException | NullPointerException e) {
            e.printStackTrace();
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendSystemMessage(Component.translatable("import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }
}
