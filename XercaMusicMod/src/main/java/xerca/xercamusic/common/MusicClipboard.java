package xerca.xercamusic.common;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class MusicClipboard {
    public static final byte COPY_BEGIN_BYTE = (byte) 50;
    private static final int FIXED_NOTE_BYTES = 5;
    private static final int MIN_EXTENDED_NOTE_BYTES = 7;
    private static final int MARKER_BYTES = 8;
    private static final int MAX_LENGTH_BEATS = 32000;

    private MusicClipboard() {
    }

    public record ParsedMusic(int length, List<NoteEvent> notes, List<VolumeMarker> volumeMarkers) {
    }

    public static @Nullable ParsedMusic decode(String encodedMusic) {
        String trimmedMusic = encodedMusic.trim();
        if (trimmedMusic.isEmpty()) {
            return null;
        }

        byte[] byteArray;
        try {
            byteArray = Base64.getDecoder().decode(trimmedMusic);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return decodeBytes(byteArray);
    }

    public static @Nullable ParsedMusic decodeBytes(byte[] byteArray) {
        if (byteArray.length == 0) {
            return null;
        }

        if (byteArray[0] != COPY_BEGIN_BYTE) {
            return decodeVeryOldBytes(byteArray);
        }

        if (byteArray.length < 9) {
            return null;
        }

        FriendlyByteBuf header = new FriendlyByteBuf(Unpooled.wrappedBuffer(byteArray));
        header.readByte();
        int copiedLength = header.readInt() + 1;
        int count = header.readInt();
        if (copiedLength <= 0 || copiedLength > MAX_LENGTH_BEATS || count < 0 || count > Mod.MAX_NOTES_IN_PACKET) {
            return null;
        }

        int payloadBytes = byteArray.length - 9;
        if (payloadBytes == count * FIXED_NOTE_BYTES) {
            return decodeFixedNoteBytes(byteArray, copiedLength, count);
        }

        return decodeExtendedBytes(byteArray, copiedLength, count);
    }

    private static @Nullable ParsedMusic decodeVeryOldBytes(byte[] byteArray) {
        for (byte b : byteArray) {
            if (b < 0 || b > 48) {
                Mod.LOGGER.info("User tried to copy invalid data into music: {}", b);
                return null;
            }
        }

        List<NoteEvent> notes = ItemMusicSheet.oldMusicToNotes(byteArray);
        if (notes.isEmpty()) {
            return null;
        }
        int length = 0;
        for (NoteEvent event : notes) {
            length = Math.max(length, event.time + event.length);
        }
        return new ParsedMusic(length, notes, new ArrayList<>());
    }

    private static @Nullable ParsedMusic decodeFixedNoteBytes(byte[] byteArray, int length, int count) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(byteArray));
        buffer.readerIndex(9);
        ArrayList<NoteEvent> notes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            NoteEvent event = new NoteEvent(
                    buffer.readByte(),
                    buffer.readShort(),
                    buffer.readByte(),
                    buffer.readByte()
            );
            if (!isValidNote(event)) {
                return null;
            }
            notes.add(event);
        }
        return new ParsedMusic(length, notes, new ArrayList<>());
    }

    private static @Nullable ParsedMusic decodeExtendedBytes(byte[] byteArray, int length, int count) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(byteArray));
        buffer.readerIndex(9);
        ArrayList<NoteEvent> notes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            NoteEvent event = readExtendedNote(buffer);
            if (event == null) {
                return null;
            }
            notes.add(event);
        }

        ArrayList<VolumeMarker> markers = new ArrayList<>();
        if (buffer.readableBytes() == 0) {
            return new ParsedMusic(length, notes, markers);
        }
        if (buffer.readableBytes() < Integer.BYTES) {
            return null;
        }
        int markerCount = buffer.readInt();
        if (markerCount < 0 || markerCount > Mod.MAX_VOLUME_MARKERS_IN_PACKET
                || buffer.readableBytes() != markerCount * MARKER_BYTES) {
            return null;
        }
        for (int i = 0; i < markerCount; i++) {
            VolumeMarker marker = readMarker(buffer);
            if (marker == null) {
                return null;
            }
            markers.add(marker);
        }
        return new ParsedMusic(length, notes, markers);
    }

    private static @Nullable NoteEvent readExtendedNote(FriendlyByteBuf buffer) {
        if (buffer.readableBytes() < MIN_EXTENDED_NOTE_BYTES) {
            return null;
        }

        NoteEvent event = new NoteEvent();
        event.note = buffer.readByte();
        event.time = buffer.readShort();
        event.volume = buffer.readByte();
        event.length = buffer.readByte();
        event.flags = buffer.readByte();
        int rawCount = buffer.readByte() & 0xFF;
        boolean hasPositions = (rawCount & 0x80) != 0;
        int waypointCount = rawCount & 0x7F;
        int waypointBytes = waypointCount + (hasPositions ? waypointCount : 0);
        if (buffer.readableBytes() < waypointBytes) {
            return null;
        }
        if (waypointCount > 0) {
            event.glissandoWaypoints = new byte[waypointCount];
            for (int i = 0; i < waypointCount; i++) {
                event.glissandoWaypoints[i] = buffer.readByte();
            }
            event.glissandoInterval = event.glissandoWaypoints[waypointCount - 1];
            if (hasPositions) {
                event.glissandoWaypointPositions = new byte[waypointCount];
                for (int i = 0; i < waypointCount; i++) {
                    event.glissandoWaypointPositions[i] = buffer.readByte();
                }
            }
        }
        return isValidNote(event) ? event : null;
    }

    private static @Nullable VolumeMarker readMarker(FriendlyByteBuf buffer) {
        VolumeMarker marker = VolumeMarker.fromBuffer(buffer);
        if (!marker.isValid()
                || !isValidVolume(marker.startVolume)
                || !isValidVolume(marker.endVolume)
                || !isValidNoteValue(marker.lowNote)
                || !isValidNoteValue(marker.highNote)
                || marker.startTime < 0
                || marker.endTime > MAX_LENGTH_BEATS) {
            return null;
        }
        return marker;
    }

    private static boolean isValidNote(NoteEvent event) {
        if (!isValidNoteValue(event.note)
                || event.time < 0
                || event.length <= 0
                || event.time + event.length > MAX_LENGTH_BEATS
                || !isValidVolume(event.volume)
                || (event.flags & ~NoteEvent.FLAG_GLISSANDO) != 0) {
            return false;
        }
        byte[] waypoints = event.glissandoWaypoints;
        byte[] positions = event.glissandoWaypointPositions;
        if (waypoints == null) {
            return positions == null;
        }
        if ((event.flags & NoteEvent.FLAG_GLISSANDO) == 0) {
            return false;
        }
        if (positions != null && positions.length != waypoints.length) {
            return false;
        }
        for (int i = 0; i < waypoints.length; i++) {
            int targetNote = event.note + waypoints[i];
            if (targetNote < IItemInstrument.MIN_NOTE || targetNote > IItemInstrument.MAX_NOTE) {
                return false;
            }
            if (positions != null) {
                int position = positions[i] & 0xFF;
                if (position < 1 || position > 100) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isValidNoteValue(byte note) {
        return note >= IItemInstrument.MIN_NOTE && note <= IItemInstrument.MAX_NOTE;
    }

    private static boolean isValidVolume(byte volume) {
        return volume >= 0;
    }
}
