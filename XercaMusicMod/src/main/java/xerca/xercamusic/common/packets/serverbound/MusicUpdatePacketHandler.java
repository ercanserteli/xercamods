package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.Triggers;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.item.Items;

import java.util.List;
import java.util.UUID;

public class MusicUpdatePacketHandler implements ServerPlayNetworking.PlayPayloadHandler<MusicUpdatePacket> {
    private static byte sanitizeBps(byte bps) {
        return (byte) Math.max(1, Math.min(50, bps & 0xFF));
    }

    private static float sanitizeVolume(float volume) {
        return Math.max(0.0f, Math.min(1.0f, volume));
    }

    private static byte sanitizeHighlightInterval(byte interval) {
        return (byte) Math.max(1, Math.min(24, interval & 0xFF));
    }

    private static void processMessage(MusicUpdatePacket msg, ServerPlayer pl) {
        ItemStack note = pl.getMainHandItem();
        if (!note.isEmpty() && note.getItem() == Items.MUSIC_SHEET) {
            MusicUpdatePacket.FieldFlag flag = msg.availability();
            if (flag.hasId) note.set(Items.SHEET_ID, msg.id());
            if (flag.hasVersion) note.set(Items.SHEET_VERSION, msg.version());
            if (flag.hasLength) note.set(Items.SHEET_LENGTH, (int) msg.lengthBeats());
            if (flag.hasBps) note.set(Items.SHEET_BPS, sanitizeBps(msg.bps()));
            if (flag.hasVolume) note.set(Items.SHEET_VOLUME, sanitizeVolume(msg.volume()));
            if (flag.hasPrevIns) note.set(Items.SHEET_PREV_INSTRUMENT, msg.prevInstrument());
            if (flag.hasPrevInsLocked) note.set(Items.SHEET_PREV_INSTRUMENT_LOCKED, msg.prevInsLocked());
            if (flag.hasHlInterval) note.set(Items.SHEET_HIGHLIGHT_INTERVAL, sanitizeHighlightInterval(msg.highlightInterval()));
            if (flag.hasSigned && msg.signed()) {
                if (flag.hasTitle) note.set(Items.SHEET_TITLE, msg.title().trim());
                note.set(Items.SHEET_AUTHOR, pl.getName().getString());
                note.set(Items.SHEET_GENERATION, 1);
                Triggers.BECOME_MUSICIAN.trigger(pl);
            }
            if (flag.hasNotes) {
                List<NoteEvent> notes = msg.notes();
                UUID id = note.get(Items.SHEET_ID);
                if (notes == null) {
                    // Get if a large sheet was sent in parts
                    notes = MusicManager.getFinishedNotesFromBuffer(id);
                    if (notes.isEmpty()) {
                        return;
                    }
                }
                List<VolumeMarker> volumeMarkers = flag.hasVolumeMarkers ? msg.volumeMarkers() : null;
                MusicManager.setMusicData(id, note.getOrDefault(Items.SHEET_VERSION, 0), notes, volumeMarkers, pl.server);
                if (note.get(Items.SHEET_BPS) == null) {
                    note.set(Items.SHEET_BPS, (byte) 8);
                }
            }
        }
    }

    @Override
    public void receive(MusicUpdatePacket packet, ServerPlayNetworking.Context context) {
        if (packet != null) {
            context.server().execute(() -> processMessage(packet, context.player()));
        }
    }
}
