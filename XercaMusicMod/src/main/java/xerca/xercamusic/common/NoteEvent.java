package xerca.xercamusic.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.item.IItemInstrument;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_NOTES;

public class NoteEvent implements Serializable {
    // Articulation flags (bit flags)
    public static final byte FLAG_NONE = 0;
    public static final byte FLAG_GLISSANDO = 1;    // Smooth pitch slide to target note
    @Serial
    private static final long serialVersionUID = 1L;

    public byte note;
    public short time;
    public byte volume;
    public byte length;
    public byte flags;              // Articulation flags (see FLAG_* constants)
    public byte glissandoInterval;  // Signed semitones to slide for glissando (+up, -down); used for single-point
    public byte[] glissandoWaypoints; // Multi-point glissando: array of semitone offsets. null = use glissandoInterval
    public byte[] glissandoWaypointPositions; // Parallel to glissandoWaypoints: beat position as % of note length (1-100). null = evenly spaced.

    public NoteEvent(byte note, short time, byte volume, byte length) {
        this(note, time, volume, length, FLAG_NONE, (byte) 0);
    }

    public NoteEvent(byte note, short time, byte volume, byte length, byte flags, byte glissandoInterval) {
        this.note = note;
        this.time = time;
        this.volume = volume;
        this.length = length;
        this.flags = flags;
        this.glissandoInterval = glissandoInterval;
        this.glissandoWaypoints = null;
        this.glissandoWaypointPositions = null;
    }

    public NoteEvent() {
    }

    public NoteEvent(NoteEvent noteEvent) {
        this.note = noteEvent.note;
        this.time = noteEvent.time;
        this.volume = noteEvent.volume;
        this.length = noteEvent.length;
        this.flags = noteEvent.flags;
        this.glissandoInterval = noteEvent.glissandoInterval;
        this.glissandoWaypoints = noteEvent.glissandoWaypoints != null
                ? noteEvent.glissandoWaypoints.clone()
                : null;
        this.glissandoWaypointPositions = noteEvent.glissandoWaypointPositions != null
                ? noteEvent.glissandoWaypointPositions.clone()
                : null;
    }

    public static NoteEvent fromNBT(CompoundTag tag) {
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.deserializeNBT(tag);
        return noteEvent;
    }

    public static NoteEvent fromBuffer(FriendlyByteBuf buf) {
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.decodeFromBuffer(buf);
        return noteEvent;
    }

    public static void fillArrayFromNBT(List<NoteEvent> noteEvents, CompoundTag tag) {
        ListTag notesTag = tag.getList(KEY_NOTES, Tag.TAG_COMPOUND);
        for (int i = 0; i < notesTag.size(); i++) {
            noteEvents.add(NoteEvent.fromNBT(notesTag.getCompound(i)));
        }
        sortNotes(noteEvents);
        removeDuplicates(noteEvents);
    }

    public static void sortNotes(List<NoteEvent> notes) {
        notes.sort(Comparator.comparingInt(NoteEvent::startTime));
    }

    public static void removeDuplicates(List<NoteEvent> notes) {
        if (notes.isEmpty()) {
            return;
        }

        short currentTime = notes.get(0).time;
        long seenLo = 0L; // 0..63
        long seenHi = 0L; // 64..127

        int i = 0;
        while (i < notes.size()) {  // NOSONAR
            NoteEvent e = notes.get(i);

            if (e.note < IItemInstrument.MIN_NOTE || e.note > IItemInstrument.MAX_NOTE) {
                // invalid note
                notes.remove(i);
                continue;
            }

            // new time group -> reset bitsets
            if (e.time != currentTime) {
                currentTime = e.time;
                seenLo = 0L;
                seenHi = 0L;
            }

            int note = e.note & 0xFF;
            if (note < 64) {
                long bit = 1L << note;
                if ((seenLo & bit) != 0L) {
                    // duplicate found
                    notes.remove(i);
                    continue;
                }
                seenLo |= bit;
            } else {
                long bit = 1L << (note - 64);
                if ((seenHi & bit) != 0L) {
                    // duplicate found
                    notes.remove(i);
                    continue;
                }
                seenHi |= bit;
            }

            i++;
        }
    }

    public static void fillNBTFromArray(List<NoteEvent> noteEvents, CompoundTag tag) {
        ListTag noteList = new ListTag();
        for (NoteEvent event : noteEvents) {
            noteList.add(event.serializeNBT());
        }
        tag.put(KEY_NOTES, noteList);
    }

    public short endTime() {
        return (short) (time + length - 1);
    }

    public short startTime() {
        return time;
    }

    public boolean hasGlissando() {
        return (flags & FLAG_GLISSANDO) != 0;
    }

    public void setGlissando(boolean enabled, byte interval) {
        if (enabled) {
            flags |= FLAG_GLISSANDO;
            glissandoInterval = interval;
            glissandoWaypoints = null; // Clear multi-point
            glissandoWaypointPositions = null;
        } else {
            flags &= ~FLAG_GLISSANDO;
            glissandoInterval = 0;
            glissandoWaypoints = null;
            glissandoWaypointPositions = null;
        }
    }

    /**
     * Set multi-point glissando waypoints. Each byte is a semitone offset from the original note.
     * Waypoints are evenly distributed across the note duration.
     * Segment 0: original pitch → waypoints[0], Segment 1: waypoints[0] → waypoints[1], etc.
     */
    public void setGlissandoWaypoints(byte[] waypoints) {
        setGlissandoWaypoints(waypoints, null);
    }

    /**
     * Set multi-point glissando with optional custom timing positions.
     * @param waypoints  semitone offsets from the note; null to clear
     * @param positions  parallel array of beat positions as % of note length (1-100); null = evenly spaced
     */
    public void setGlissandoWaypoints(byte[] waypoints, byte[] positions) {
        if (waypoints != null && waypoints.length > 0) {
            flags |= FLAG_GLISSANDO;
            glissandoWaypoints = waypoints;
            glissandoInterval = waypoints[waypoints.length - 1]; // Last waypoint for compat
            glissandoWaypointPositions = (positions != null && positions.length == waypoints.length) ? positions : null;
        } else {
            flags &= ~FLAG_GLISSANDO;
            glissandoInterval = 0;
            glissandoWaypoints = null;
            glissandoWaypointPositions = null;
        }
    }

    /**
     * Returns the effective waypoints for glissando playback.
     * If multi-point waypoints exist, returns those; otherwise wraps glissandoInterval in a single-element array.
     * Returns null if no glissando.
     */
    public byte[] getEffectiveWaypoints() {
        if (glissandoWaypoints != null && glissandoWaypoints.length > 0) {
            return glissandoWaypoints;
        }
        if (hasGlissando() && glissandoInterval != 0) {
            return new byte[] { glissandoInterval };
        }
        return null;
    }

    /**
     * Returns the waypoint position fractions (1-100) if custom positioned, or null for even distribution.
     * Only meaningful when glissandoWaypoints is non-null.
     */
    public byte[] getEffectivePositions() {
        if (glissandoWaypointPositions != null && glissandoWaypoints != null
                && glissandoWaypointPositions.length == glissandoWaypoints.length) {
            return glissandoWaypointPositions;
        }
        return null;
    }

    /**
     * Returns the target note for glissando (note + glissandoInterval).
     */
    public byte glissandoTargetNote() {
        return (byte)(note + glissandoInterval);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("n", note);
        tag.putShort("d", time);
        tag.putByte("v", volume);
        tag.putByte("l", length);
        // Only save flags and glissandoInterval if they have non-default values (backwards compatibility)
        if (flags != FLAG_NONE) {
            tag.putByte("f", flags);
        }
        if (glissandoWaypoints != null && glissandoWaypoints.length > 0) {
            tag.putByteArray("gw", glissandoWaypoints);
            if (glissandoWaypointPositions != null && glissandoWaypointPositions.length == glissandoWaypoints.length) {
                tag.putByteArray("gp", glissandoWaypointPositions);
            }
        } else if (glissandoInterval != 0) {
            tag.putByte("ti", glissandoInterval);
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.note = tag.getByte("n");
        this.time = tag.getShort("d");
        this.volume = tag.getByte("v");
        this.length = tag.getByte("l");
        // Load flags and glissandoInterval with defaults for backwards compatibility
        this.flags = tag.contains("f") ? tag.getByte("f") : FLAG_NONE;
        if (tag.contains("gw")) {
            this.glissandoWaypoints = tag.getByteArray("gw");
            this.glissandoInterval = this.glissandoWaypoints.length > 0 ? this.glissandoWaypoints[this.glissandoWaypoints.length - 1] : 0;
            if (tag.contains("gp")) {
                byte[] gp = tag.getByteArray("gp");
                this.glissandoWaypointPositions = (gp.length == this.glissandoWaypoints.length) ? gp : null;
            } else {
                this.glissandoWaypointPositions = null;
            }
        } else {
            this.glissandoInterval = tag.contains("ti") ? tag.getByte("ti") : 0;
            this.glissandoWaypoints = null;
            this.glissandoWaypointPositions = null;
        }
    }

    public void encodeToBuffer(FriendlyByteBuf buf) {
        buf.writeByte(note);
        buf.writeShort(time);
        buf.writeByte(volume);
        buf.writeByte(length);
        buf.writeByte(flags);
        // Write waypoints: count followed by bytes
        byte[] wps = getEffectiveWaypoints();
        byte[] pos = getEffectivePositions();
        boolean hasPositions = pos != null && wps != null && pos.length == wps.length;
        // Encode count with high bit indicating positions are present
        int wpCount = wps != null ? wps.length : 0;
        buf.writeByte(hasPositions ? (wpCount | 0x80) : wpCount);
        if (wps != null) {
            for (byte wp : wps) {
                buf.writeByte(wp);
            }
        }
        if (hasPositions) {
            for (byte p : pos) {
                buf.writeByte(p);
            }
        }
    }

    public void decodeFromBuffer(FriendlyByteBuf buf) {
        this.note = buf.readByte();
        this.time = buf.readShort();
        this.volume = buf.readByte();
        this.length = buf.readByte();
        this.flags = buf.readByte();
        int rawCount = buf.readByte() & 0xFF;
        boolean hasPositions = (rawCount & 0x80) != 0;
        int wpCount = rawCount & 0x7F;
        if (wpCount > 0) {
            this.glissandoWaypoints = new byte[wpCount];
            for (int i = 0; i < wpCount; i++) {
                this.glissandoWaypoints[i] = buf.readByte();
            }
            this.glissandoInterval = this.glissandoWaypoints[wpCount - 1];
            if (hasPositions) {
                this.glissandoWaypointPositions = new byte[wpCount];
                for (int i = 0; i < wpCount; i++) {
                    this.glissandoWaypointPositions[i] = buf.readByte();
                }
            } else {
                this.glissandoWaypointPositions = null;
            }
        } else {
            this.glissandoWaypoints = null;
            this.glissandoInterval = 0;
            this.glissandoWaypointPositions = null;
        }
    }

    public float floatVolume() {
        return ((float)volume)/127.0f;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public NoteEvent clone() {
        NoteEvent copy = new NoteEvent(note, time, volume, length, flags, glissandoInterval);
        if (glissandoWaypoints != null) {
            copy.glissandoWaypoints = glissandoWaypoints.clone();
        }
        if (glissandoWaypointPositions != null) {
            copy.glissandoWaypointPositions = glissandoWaypointPositions.clone();
        }
        return copy;
    }
}
