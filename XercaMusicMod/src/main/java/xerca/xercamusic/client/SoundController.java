package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class SoundController extends Thread {
    private final List<NoteEvent> notes;
    private final List<VolumeMarker> volumeMarkers;
    private final IItemInstrument instrument;
    private final byte bps;
    private final int spiritID;
    private final float volume;
    private volatile boolean doStop;
    private volatile double x;
    private volatile double y;
    private volatile double z;
    private TileEntityMusicBox musicBox;
    private static final AtomicInteger CONTROLLER_COUNTER = new AtomicInteger();

    // Tracks sustained notes inside volume markers for dynamic volume updates
    private final List<ActiveSound> activeSounds = new ArrayList<>();

    private record ActiveSound(NoteSound sound, NoteEvent event, VolumeMarker marker, int endBeat) {}

    public SoundController(List<NoteEvent> notes, List<VolumeMarker> volumeMarkers, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, int spiritID) {
        this.notes = notes;
        this.volumeMarkers = volumeMarkers != null ? volumeMarkers : Collections.emptyList();
        this.x = x;
        this.y = y;
        this.z = z;
        this.instrument = instrument;
        this.bps = bps;
        this.volume = volume;
        this.spiritID = spiritID;
        setDaemon(true);
        setName("XercaMusic-SoundController-" + CONTROLLER_COUNTER.incrementAndGet());
    }

    public SoundController(List<NoteEvent> notes, List<VolumeMarker> volumeMarkers, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, TileEntityMusicBox musicBox) {
        this(notes, volumeMarkers, x, y, z, instrument, bps, volume, -1);
        this.musicBox = musicBox;
    }

    public SoundController(List<NoteEvent> notes, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, int spiritID) {
        this(notes, null, x, y, z, instrument, bps, volume, spiritID);
    }

    public SoundController(List<NoteEvent> notes, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, TileEntityMusicBox musicBox) {
        this(notes, null, x, y, z, instrument, bps, volume, -1);
        this.musicBox = musicBox;
    }

    private int beatsToTicks(int beats) {
        return Math.max(1, Math.round(beats * 20.0f / bps));
    }

    @Override
    public void run() {
        if (bps == 0) {
            Mod.LOGGER.error("BPS is 0! This should not happen!");
            return;
        }

        int currentBeat = 0;
        int noteIdx = 0;
        long nanosPerBeat = Math.round(1_000_000_000.0 / bps);
        long songStartNanos = System.nanoTime();
        long pausedNanos = 0; // Total time spent paused (excluded from song clock)

        Minecraft minecraft = Minecraft.getInstance();
        while (noteIdx < notes.size()) {
            if (doStop) {
                return;
            }

            NoteEvent firstEvent = notes.get(noteIdx);
            while (firstEvent.time > currentBeat) {
                // Sleep until the absolute target time for the next beat
                long targetNanos = songStartNanos + (long)(currentBeat + 1) * nanosPerBeat + pausedNanos;
                sleepUntil(targetNanos);
                currentBeat++;
                updateActiveSounds(currentBeat);
                if (minecraft.isPaused()) {
                    long pauseStart = System.nanoTime();
                    while (minecraft.isPaused()) {
                        inaccurateSleep(1);
                    }
                    pausedNanos += System.nanoTime() - pauseStart;
                }
                if (doStop || minecraft.level == null) {
                    return;
                }
            }

            // Collect all notes at this beat into a single batch
            int batchEnd = noteIdx + 1;
            while (batchEnd < notes.size() && notes.get(batchEnd).time == firstEvent.time) {
                batchEnd++;
            }

            playNotes(noteIdx, batchEnd);
            noteIdx = batchEnd;
        }

        // Music over
        if (spiritID >= 0 && minecraft.player != null) {
            var unused = minecraft.submit(() -> ClientStuff.endMusic(spiritID, minecraft.player.getId()))
                    .whenComplete((v, t) -> {
                        if (t != null) Mod.LOGGER.error("Failed to end music", t);
                    });
        }
    }

    /**
     * Play all notes from index fromIdx (inclusive) to toIdx (exclusive) in a single
     * main-thread submission. Only spawns one particle per batch to reduce overhead.
     */
    private void playNotes(int fromIdx, int toIdx) {
        var unused = Minecraft.getInstance().submit(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            boolean particleSpawned = false;
            for (int i = fromIdx; i < toIdx; i++) {
                NoteEvent event = notes.get(i);
                if (event.note < IItemInstrument.MIN_NOTE || event.note > IItemInstrument.MAX_NOTE) continue;

                IItemInstrument.InsSound insSound = instrument.getSound(event.note);
                if (insSound == null) continue;

                // Check if any volume marker fully contains this note
                float noteVolume = event.floatVolume();
                VolumeMarker activeMarker = null;
                for (VolumeMarker marker : volumeMarkers) {
                    if (marker.fullyContains(event.time, event.length, event.note)) {
                        noteVolume = marker.getVolumeAt(event.time);
                        activeMarker = marker;
                        break;  // First matching marker wins
                    }
                }

                NoteSound sound;
                if (musicBox == null) {
                    sound = ClientStuff.playNote(insSound.sound(), x, y, z, volume * noteVolume, insSound.pitch(), (byte) beatsToTicks(event.length));
                } else {
                    sound = ClientStuff.playNoteTE(insSound.sound(), x, y, z, volume * noteVolume, insSound.pitch(), (byte) beatsToTicks(event.length));
                }

                // Spawn at most one particle per beat per controller
                if (!particleSpawned) {
                    if (musicBox == null) {
                        level.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, event.note / 24.0D, 0.0D, 0.0D);
                    } else {
                        level.addParticle(ParticleTypes.NOTE, x + 0.5D, y + 2.2D, z + 0.5D, event.note / 24.0D, 0.0D, 0.0D);
                    }
                    particleSpawned = true;
                }

                // Apply glissando (smooth pitch slide)
                if (event.hasGlissando() && sound != null) {
                    byte[] wps = event.getEffectiveWaypoints();
                    if (wps != null && wps.length > 0) {
                        float[] pitchWaypoints = new float[wps.length];
                        for (int j = 0; j < wps.length; j++) {
                            pitchWaypoints[j] = insSound.pitch() * (float) Math.pow(2.0, wps[j] / 12.0);
                        }
                        byte[] posBuf = event.getEffectivePositions();
                        if (posBuf != null && posBuf.length == wps.length) {
                            float[] posFloats = new float[posBuf.length];
                            for (int j = 0; j < posBuf.length; j++) {
                                posFloats[j] = (posBuf[j] & 0xFF) / 100.0f;
                            }
                            sound.setGlissando(pitchWaypoints, posFloats, beatsToTicks(event.length));
                        } else {
                            sound.setGlissando(pitchWaypoints, beatsToTicks(event.length));
                        }
                    }
                }

                // Track sustained notes inside volume markers for dynamic volume
                if (sound != null && activeMarker != null && event.length > 1) {
                    synchronized (activeSounds) {
                        activeSounds.add(new ActiveSound(sound, event, activeMarker, event.time + event.length));
                    }
                }
            }
        }).whenComplete((v, t) -> {
            if (t != null) Mod.LOGGER.error("Failed to play notes", t);
        });
    }

    private void updateActiveSounds(int currentBeat) {
        if (activeSounds.isEmpty()) return;
        final int beat = currentBeat;
        var unused = Minecraft.getInstance().submit(() -> {
            synchronized (activeSounds) {
                Iterator<ActiveSound> it = activeSounds.iterator();
                while (it.hasNext()) {
                    ActiveSound as = it.next();
                    if (beat >= as.endBeat || as.sound.isStopped()) {
                        it.remove();
                    } else {
                        float vol = as.marker.getVolumeAt((short) beat);
                        if (vol >= 0f) {
                            as.sound.setDynamicVolume(volume * vol);
                        }
                    }
                }
            }
        }).whenComplete((v, t) -> {
            if (t != null) Mod.LOGGER.error("Failed to update active sounds", t);
        });
    }

    public void setStop() {
        doStop = true;
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Sleep until the specified absolute nanoTime. Uses Thread.sleep for the bulk,
     * LockSupport.parkNanos for the penultimate millisecond, and hot-spin for the
     * final ~1ms to achieve precise timing without cumulative drift.
     */
    private void sleepUntil(long targetNanos) {
        long remaining = targetNanos - System.nanoTime();
        if (remaining <= 0) return;

        // Sleep the bulk of the time using Thread.sleep
        long remainingMs = remaining / 1_000_000L;
        if (remainingMs > 8) {
            try {
                sleep(remainingMs - 8);
            } catch (InterruptedException e) {
                Mod.LOGGER.warn("Interrupted while sleeping", e);
                Thread.currentThread().interrupt();
            }
        }

        // Park in small increments instead of hot spinning (much lower CPU usage)
        while (System.nanoTime() < targetNanos - 1_000_000L) {
            LockSupport.parkNanos(500_000L); // 0.5ms park
        }

        // Hot spin only the final ~1ms for precise timing
        while (System.nanoTime() < targetNanos) {
            Thread.onSpinWait();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void inaccurateSleep(long millis) {
        if (millis == 0) return;
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            Mod.LOGGER.warn("Interrupted while sleeping", e);
            Thread.currentThread().interrupt();
        }
    }
}
