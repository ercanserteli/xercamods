package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.util.ArrayList;

public class SoundController extends Thread {
    private final ArrayList<NoteEvent> notes;
    private volatile boolean doStop = false;
    private volatile double x;
    private volatile double y;
    private volatile double z;
    private final IItemInstrument instrument;
    private final byte bps;
    private final int spiritID;
    private final float volume;
    private TileEntityMusicBox musicBox = null;

    public SoundController(ArrayList<NoteEvent> notes, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, int spiritID){
        this.notes = notes;
        this.x = x;
        this.y = y;
        this.z = z;
        this.instrument = instrument;
        this.bps = bps;
        this.volume = volume;
        this.spiritID = spiritID;
    }
    public SoundController(ArrayList<NoteEvent> notes, double x, double y, double z, IItemInstrument instrument, byte bps, float volume, TileEntityMusicBox musicBox){
        this(notes, x, y, z, instrument, bps, volume, -1);
        this.musicBox = musicBox;
    }

    private int beatsToTicks(int beats){
        return Math.max(1, Math.round(((float)beats) * 20.0f / ((float)bps)));
    }

    @Override
    public void run() {
        if(bps == 0){
            XercaMusic.LOGGER.error("BPS is 0! This should not happen!");
            return;
        }

        int msPerBeat = Math.round(1000.0f/(float)bps);
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
        if(spiritID >= 0){
            if(minecraft.player != null) {
                minecraft.submit(() -> ClientStuff.endMusic(spiritID, minecraft.player.getId()));
            }
        }
    }

    private void playNote(NoteEvent event){
        if (event.note >= IItemInstrument.minNote && event.note <= IItemInstrument.maxNote) {
            final byte note = event.note;
            Minecraft.getInstance().submit(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                IItemInstrument.InsSound insSound = instrument.getSound(note);
                if(level == null || insSound == null){
                    return;
                }

                if(musicBox == null){
                    ClientStuff.playNote(insSound.sound, x, y, z, volume*event.floatVolume(), insSound.pitch, (byte)beatsToTicks(event.length));
                    level.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, (note) / 24.0D, 0.0D, 0.0D);
                }
                else{
                    ClientStuff.playNoteTE(insSound.sound, x, y, z, volume*event.floatVolume(), insSound.pitch, (byte)beatsToTicks(event.length));
                    level.addParticle(ParticleTypes.NOTE, x+0.5D, y + 2.2D, z+0.5D, (note) / 24.0D, 0.0D, 0.0D);
                }
            });
        }
    }

    public void setStop(){
        doStop = true;
    }

    public void setPos(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void accurateSleep(long millis) {
        if(millis == 0) return;
        long start = System.currentTimeMillis();
        if(millis > 12) {
            try {
                sleep(millis - 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //noinspection StatementWithEmptyBody
        while (System.currentTimeMillis() < start + millis){}
    }

    @SuppressWarnings("SameParameterValue")
    private void inaccurateSleep(long millis) {
        if(millis == 0) return;
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
