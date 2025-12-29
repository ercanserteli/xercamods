package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record MusicDataResponsePacket(UUID id, int version, List<NoteEvent> notes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MusicDataResponsePacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("music_data_response"));
    public static final StreamCodec<FriendlyByteBuf, MusicDataResponsePacket> PACKET_CODEC = StreamCodec.ofMember(MusicDataResponsePacket::encode, MusicDataResponsePacket::decode);

    public static MusicDataResponsePacket decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        int version = buf.readInt();
        int eventCount = buf.readInt();
        List<NoteEvent> notes = new ArrayList<>(eventCount);
        for (int i = 0; i < eventCount; i++) {
            notes.add(NoteEvent.fromBuffer(buf));
        }
        return new MusicDataResponsePacket(id, version, notes);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeInt(version);
        buf.writeInt(notes.size());
        for (NoteEvent event : notes) {
            event.encodeToBuffer(buf);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
