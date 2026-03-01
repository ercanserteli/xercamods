package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SoundController extends Thread {
    private final List<NoteEvent> notes;
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

    public SoundController(List<NoteEvent> notes, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, int spiritID) {
        this.notes = notes;
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

    public SoundController(List<NoteEvent> notes, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, TileEntityMusicBox musicBox) {
        this(notes, x, y, z, instrument, bps, volume, -1);
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

                if (musicBox == null) {
                    ClientStuff.playNote(insSound.sound(), x, y, z, volume * event.floatVolume(), insSound.pitch(), (byte) beatsToTicks(event.length));
                    level.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, note / 24.0D, 0.0D, 0.0D);
                } else {
                    ClientStuff.playNoteTE(insSound.sound(), x, y, z, volume * event.floatVolume(), insSound.pitch(), (byte) beatsToTicks(event.length));
                    level.addParticle(ParticleTypes.NOTE, x + 0.5D, y + 2.2D, z + 0.5D, note / 24.0D, 0.0D, 0.0D);
                }
            }).whenComplete((v, t) -> {
                if (t != null) Mod.LOGGER.error("Failed to play note", t);
            });
        }
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
