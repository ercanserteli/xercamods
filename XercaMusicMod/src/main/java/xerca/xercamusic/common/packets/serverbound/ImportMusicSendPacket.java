package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_ID;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_NOTES;

public record ImportMusicSendPacket(@Nullable UUID uuid, @Nullable CompoundTag tag,
                                    @Nullable List<NoteEvent> notes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportMusicSendPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("import_music_send"));
    public static final StreamCodec<FriendlyByteBuf, ImportMusicSendPacket> PACKET_CODEC = StreamCodec.ofMember(ImportMusicSendPacket::encode, ImportMusicSendPacket::decode);

    public static ImportMusicSendPacket createEmpty() {
        return new ImportMusicSendPacket(null, null, null);
    }

    public static ImportMusicSendPacket create(CompoundTag tag) throws NotesTooLargeException {
        UUID uuid = tag.read(KEY_ID, UUIDUtil.CODEC).orElse(null);
        List<NoteEvent> notes = null;
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

    public static ImportMusicSendPacket create(CompoundTag tag, @Nullable List<NoteEvent> notes) {
        UUID uuid = tag.read(KEY_ID, UUIDUtil.CODEC).orElse(null);
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
            return notes == null ? create(tag) : create(tag, notes);
        } catch (IllegalArgumentException | NotesTooLargeException e) {
            Mod.LOGGER.error("Invalid ImportMusicSendPacket", e);
            return createEmpty();
        }
    }

    public static @Nullable List<NoteEvent> notesFromBuffer(FriendlyByteBuf buf) {
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
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

    public static class NotesTooLargeException extends Exception {
        @Serial
        private static final long serialVersionUID = 1L;

        private final List<NoteEvent> notes;
        public final @Nullable UUID id;

        public List<NoteEvent> getNotes() {
            return notes;
        }

        public NotesTooLargeException(List<NoteEvent> notes, @Nullable UUID id) {
            this.notes = notes;
            this.id = id;
        }
    }
}
