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

        int msPerBeat = Math.round(1000.0f / bps);
        int currentBeat = 0;

        Minecraft minecraft = Minecraft.getInstance();
        for (NoteEvent event : notes) {
            if (doStop) {
                return;
            }

            while (event.time > currentBeat) {
                accurateSleep(msPerBeat);
                currentBeat++;
                updateActiveSounds(currentBeat);
                while (minecraft.isPaused()) {
                    inaccurateSleep(1);
                }
                if (doStop || minecraft.level == null) {
                    return;
                }
            }
            playNote(event);
        }

        // Music over
        if (spiritID >= 0 && minecraft.player != null) {
            minecraft.submit(() -> ClientStuff.endMusic(spiritID, minecraft.player.getId()))
                    .whenComplete((v, t) -> {
                        if (t != null) Mod.LOGGER.error("Failed to end music", t);
                    });
        }
    }

    private void playNote(NoteEvent event) {
        if (event.note >= IItemInstrument.MIN_NOTE && event.note <= IItemInstrument.MAX_NOTE) {
            final byte note = event.note;
            Minecraft.getInstance().submit(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                IItemInstrument.InsSound insSound = instrument.getSound(note);
                if (level == null || insSound == null) {
                    return;
                }

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
                    level.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, note / 24.0D, 0.0D, 0.0D);
                } else {
                    sound = ClientStuff.playNoteTE(insSound.sound(), x, y, z, volume * noteVolume, insSound.pitch(), (byte) beatsToTicks(event.length));
                    level.addParticle(ParticleTypes.NOTE, x + 0.5D, y + 2.2D, z + 0.5D, note / 24.0D, 0.0D, 0.0D);
                }

                // Apply glissando (smooth pitch slide)
                if (event.hasGlissando() && sound != null) {
                    byte[] wps = event.getEffectiveWaypoints();
                    if (wps != null && wps.length > 0) {
                        float[] pitchWaypoints = new float[wps.length];
                        for (int i = 0; i < wps.length; i++) {
                            pitchWaypoints[i] = insSound.pitch() * (float) Math.pow(2.0, wps[i] / 12.0);
                        }
                        sound.setGlissando(pitchWaypoints, beatsToTicks(event.length));
                    }
                }

                // Track sustained notes inside volume markers for dynamic volume
                if (sound != null && activeMarker != null && event.length > 1
                        && activeMarker.containsTime((short)(event.time + event.length))) {
                    synchronized (activeSounds) {
                        activeSounds.add(new ActiveSound(sound, event, activeMarker, event.time + event.length));
                    }
                }
            }).whenComplete((v, t) -> {
                if (t != null) Mod.LOGGER.error("Failed to play note", t);
            });
        }
    }

    private void updateActiveSounds(int currentBeat) {
        if (activeSounds.isEmpty()) return;
        final int beat = currentBeat;
        Minecraft.getInstance().submit(() -> {
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

    private void accurateSleep(long millis) {
        if (millis == 0) return;
        long start = System.currentTimeMillis();
        if (millis > 8) {
            try {
                sleep(millis - 8);
            } catch (InterruptedException e) {
                Mod.LOGGER.warn("Interrupted while sleeping", e);
                Thread.currentThread().interrupt();
            }
        }
        while (System.currentTimeMillis() < start + millis) {
            // hot sleep
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