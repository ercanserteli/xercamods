package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;

public record ImportMusicSendPacket(UUID uuid, CompoundTag tag, ArrayList<NoteEvent> notes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportMusicSendPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("import_music_send"));
    public static final StreamCodec<FriendlyByteBuf, ImportMusicSendPacket> PACKET_CODEC = StreamCodec.ofMember(ImportMusicSendPacket::encode, ImportMusicSendPacket::decode);

    public static ImportMusicSendPacket create(CompoundTag tag) throws NotesTooLargeException {
        UUID uuid = null;
        ArrayList<NoteEvent> notes = null;
        if(tag.contains("id")) {
            uuid = tag.getUUID("id");
        }
        if(tag.contains("notes")) {
            notes = new ArrayList<>();
            NoteEvent.fillArrayFromNBT(notes, tag);
            tag.remove("notes");

            if(notes.size() > MAX_NOTES_IN_PACKET) {
                throw new NotesTooLargeException(notes, uuid);
            }
        }
        return new ImportMusicSendPacket(uuid, tag, notes);
    }

    public static ImportMusicSendPacket create(CompoundTag tag, ArrayList<NoteEvent> notes) {
        UUID uuid = null;
        if(tag.contains("id")) {
            uuid = tag.getUUID("id");
        }
        return new ImportMusicSendPacket(uuid, tag, notes);
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        if(notes != null) {
            buf.writeInt(notes.size());
            for(NoteEvent event : notes){
                event.encodeToBuffer(buf);
            }
        }
        else{
            buf.writeInt(0);
        }
        buf.writeNbt(tag);
        return buf;
    }

    public static ImportMusicSendPacket decode(FriendlyByteBuf buf) {
        try {
            int eventCount = buf.readInt();

            ArrayList<NoteEvent> notes = null;
            if(eventCount > 0) {
                notes = new ArrayList<>(eventCount);
                for (int i = 0; i < eventCount; i++) {
                    notes.add(NoteEvent.fromBuffer(buf));
                }
            }
            CompoundTag tag = buf.readNbt();
            return notes == null ? ImportMusicSendPacket.create(tag) : ImportMusicSendPacket.create(tag, notes);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicSendPacket: " + ioe);
            return null;
        } catch (NotesTooLargeException e) {
            System.err.println("NotesTooLargeException while reading ImportMusicSendPacket: " + e);
            return null;
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static class NotesTooLargeException extends Exception {
        public final ArrayList<NoteEvent> notes;
        public final UUID id;

        public NotesTooLargeException(ArrayList<NoteEvent> notes, UUID id) {
            this.notes = notes;
            this.id = id;
        }
    }
}
