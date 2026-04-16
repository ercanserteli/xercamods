package xerca.xercamusic.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

/**
 * Represents a volume change region (crescendo or decrescendo) on a music sheet.
 * The volume interpolates linearly from startVolume to endVolume over the time range.
 */
public class VolumeMarker {
    public static final String KEY_VOLUME_MARKERS = "volumeMarkers";

    public short startTime;    // Beat position where the marker begins
    public short endTime;      // Exclusive beat position where the marker ends
    public byte startVolume;   // Starting volume (0-127)
    public byte endVolume;     // Ending volume (0-127)
    public byte lowNote;       // Lowest note affected (for visual display height)
    public byte highNote;      // Highest note affected (for visual display height)

    public VolumeMarker(short startTime, short endTime, byte startVolume, byte endVolume, byte lowNote, byte highNote) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startVolume = startVolume;
        this.endVolume = endVolume;
        this.lowNote = lowNote;
        this.highNote = highNote;
    }

    public VolumeMarker() {
    }


    public boolean isCrescendo() {
        return endVolume > startVolume;
    }


    public boolean isDecrescendo() {
        return endVolume < startVolume;
    }

    public boolean isValid() {
        return endTime - startTime >= 2 && lowNote <= highNote;
    }

    /**
     * Calculates the interpolated volume at a given beat start.
     * Returns -1 if the time is outside this marker's range.
     */
    public float getVolumeAt(short time) {
        if (!containsTime(time)) {
            return -1f;
        }
        int duration = endTime - startTime;
        if (duration <= 1) {
            return startVolume / 127f;
        }
        float progress = (float) (time - startTime) / (float) (duration - 1);
        float volume = startVolume + progress * (endVolume - startVolume);
        return volume / 127f;
    }

    /**
     * Checks if a given beat start falls within this marker's half-open range.
     */
    public boolean containsTime(short time) {
        return time >= startTime && time < endTime;
    }

    /**
     * Checks if a given note pitch is within the affected note range.
     */
    public boolean containsNote(byte note) {
        return note >= lowNote && note <= highNote;
    }

    /**
     * Checks if both time and note are within this marker's range.
     */
    public boolean affects(short time, byte note) {
        return containsTime(time) && containsNote(note);
    }

    /**
     * Checks if a note is fully contained within this marker's range,
     * meaning its full half-open interval [time, time + length) falls within the marker
     * and its pitch is within the note range.
     */
    public boolean fullyContains(short time, short length, byte note) {
        return time >= startTime && (short)(time + length) <= endTime && containsNote(note);
    }

    public boolean overlaps(VolumeMarker other) {
        boolean timeOverlap = startTime < other.endTime && endTime > other.startTime;
        boolean noteOverlap = lowNote <= other.highNote && highNote >= other.lowNote;
        return timeOverlap && noteOverlap;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putShort("st", startTime);
        tag.putShort("et", endTime);
        tag.putByte("sv", startVolume);
        tag.putByte("ev", endVolume);
        tag.putByte("ln", lowNote);
        tag.putByte("hn", highNote);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.startTime = tag.getShort("st");
        this.endTime = tag.getShort("et");
        this.startVolume = tag.getByte("sv");
        this.endVolume = tag.getByte("ev");
        this.lowNote = tag.getByte("ln");
        this.highNote = tag.getByte("hn");
    }

    public static VolumeMarker fromNBT(CompoundTag tag) {
        VolumeMarker marker = new VolumeMarker();
        marker.deserializeNBT(tag);
        return marker;
    }

    public void encodeToBuffer(FriendlyByteBuf buf) {
        buf.writeShort(startTime);
        buf.writeShort(endTime);
        buf.writeByte(startVolume);
        buf.writeByte(endVolume);
        buf.writeByte(lowNote);
        buf.writeByte(highNote);
    }

    public void decodeFromBuffer(FriendlyByteBuf buf) {
        this.startTime = buf.readShort();
        this.endTime = buf.readShort();
        this.startVolume = buf.readByte();
        this.endVolume = buf.readByte();
        this.lowNote = buf.readByte();
        this.highNote = buf.readByte();
    }

    public static VolumeMarker fromBuffer(FriendlyByteBuf buf) {
        VolumeMarker marker = new VolumeMarker();
        marker.decodeFromBuffer(buf);
        return marker;
    }

    public static void fillArrayFromNBT(List<VolumeMarker> markers, CompoundTag tag) {
        if (tag.contains(KEY_VOLUME_MARKERS)) {
            ListTag markerList = tag.getList(KEY_VOLUME_MARKERS, Tag.TAG_COMPOUND);
            for (int i = 0; i < markerList.size(); i++) {
                markers.add(VolumeMarker.fromNBT(markerList.getCompound(i)));
            }
        }
    }

    public static void fillNBTFromArray(List<VolumeMarker> markers, CompoundTag tag) {
        ListTag markerList = new ListTag();
        for (VolumeMarker marker : markers) {
            markerList.add(marker.serializeNBT());
        }
        tag.put(KEY_VOLUME_MARKERS, markerList);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public VolumeMarker clone() {
        return new VolumeMarker(startTime, endTime, startVolume, endVolume, lowNote, highNote);
    }
}
