package xerca.xercamusic.common.packets.serverbound;

import java.io.Serial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_ID;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_NOTES;

public record ImportMusicSendPacket(UUID uuid, CompoundTag tag,
                                    List<NoteEvent> notes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportMusicSendPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("import_music_send"));
    public static final StreamCodec<FriendlyByteBuf, ImportMusicSendPacket> PACKET_CODEC = StreamCodec.ofMember(ImportMusicSendPacket::encode, ImportMusicSendPacket::decode);

    public static ImportMusicSendPacket createEmpty() {
        return new ImportMusicSendPacket(null, null, null);
    }

    public static ImportMusicSendPacket create(CompoundTag tag) throws NotesTooLargeException {
        UUID uuid = null;
        List<NoteEvent> notes = null;
        if (tag.contains(KEY_ID)) {
            uuid = tag.getUUID(KEY_ID);
        }
        if (tag.contains(KEY_NOTES)) {
            notes = new ArrayList<>();
            NoteEvent.fillArrayFromNBT(notes, tag);
            tag.remove(KEY_NOTES);

            if (notes.size() > MAX_NOTES_IN_PACKET) {
                throw new NotesTooLargeException(notes, uuid);
            }
        }
        return new ImportMusicSendPacket(uuid, tag, notes);
    }

    public static ImportMusicSendPacket create(CompoundTag tag, List<NoteEvent> notes) {
        UUID uuid = null;
        if (tag.contains(KEY_ID)) {
            uuid = tag.getUUID(KEY_ID);
        }
        return new ImportMusicSendPacket(uuid, tag, notes);
    }

    public static ImportMusicSendPacket decode(FriendlyByteBuf buf) {
        try {
            List<NoteEvent> notes = notesFromBuffer(buf);
            CompoundTag tag = buf.readNbt();
            if (tag == null) {
                Mod.LOGGER.error("CompoundTag was null in ImportMusicSendPacket");
                return createEmpty();
            }
            return notes == null ? ImportMusicSendPacket.create(tag) : ImportMusicSendPacket.create(tag, notes);
        } catch (IllegalArgumentException | NotesTooLargeException e) {
            Mod.LOGGER.error("Invalid ImportMusicSendPacket", e);
            return createEmpty();
        }
    }

    public static List<NoteEvent> notesFromBuffer(FriendlyByteBuf buf) {
        int eventCount = buf.readInt();
        if (eventCount < 0 || eventCount > MAX_NOTES_IN_PACKET) {
            throw new IllegalArgumentException("eventCount=" + eventCount);
        }
        List<NoteEvent> notes = null;
        if (eventCount > 0) {
            notes = new ArrayList<>(eventCount);
            for (int i = 0; i < eventCount; i++) {
                notes.add(NoteEvent.fromBuffer(buf));
            }
        }
        return notes;
    }

    public void encode(FriendlyByteBuf buf) {
        if (notes != null) {
            buf.writeInt(notes.size());
            for (NoteEvent event : notes) {
                event.encodeToBuffer(buf);
            }
        } else {
            buf.writeInt(0);
        }
        buf.writeNbt(tag);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static class NotesTooLargeException extends Exception {
        @Serial
        private static final long serialVersionUID = 1L;

        public final List<NoteEvent> notes;
        public final UUID id;

        public NotesTooLargeException(List<NoteEvent> notes, UUID id) {
            this.notes = notes;
            this.id = id;
        }
    }
}
