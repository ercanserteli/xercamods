package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SendNotesPartToServerPacket(UUID uuid, int partsCount, int partId, List<NoteEvent> notes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SendNotesPartToServerPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("send_notes_part_to_server"));
    public static final StreamCodec<FriendlyByteBuf, SendNotesPartToServerPacket> PACKET_CODEC = StreamCodec.ofMember(SendNotesPartToServerPacket::encode, SendNotesPartToServerPacket::decode);

    public static SendNotesPartToServerPacket decode(FriendlyByteBuf buf) {
        try {
            UUID uuid = buf.readUUID();
            int partsCount = buf.readInt();
            int partId = buf.readInt();
            int eventCount = buf.readInt();
            ArrayList<NoteEvent> notes = null;
            if(eventCount > 0) {
                notes = new ArrayList<>(eventCount);
                for (int i = 0; i < eventCount; i++) {
                    notes.add(NoteEvent.fromBuffer(buf));
                }
            }
            return new SendNotesPartToServerPacket(uuid, partsCount, partId, notes);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading SendNotesPartToServerPacket: " + ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeInt(partsCount);
        buf.writeInt(partId);
        buf.writeInt(notes.size());
        for(NoteEvent event : notes){
            event.encodeToBuffer(buf);
        }
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

