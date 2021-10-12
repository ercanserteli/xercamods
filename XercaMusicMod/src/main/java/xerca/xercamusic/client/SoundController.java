package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.util.ArrayList;

public class SoundController extends Thread {
    private ArrayList<NoteEvent> notes;
    private volatile boolean doStop = false;
    private volatile double x;
    private volatile double y;
    private volatile double z;
    private ItemInstrument instrument;
    private byte bps;
    private int spiritID;
    private float volume;
    private NoteSound lastPlayed = null;
    private TileEntityMusicBox musicBox = null;

    public SoundController(ArrayList<NoteEvent> notes, double x, double y, double z, ItemInstrument instrument, byte bps, float volume, int spiritID){
        this.notes = notes;
        this.x = x;
        this.y = y;
        this.z = z;
        this.instrument = instrument;
        this.bps = bps;
        this.volume = volume;
        this.spiritID = spiritID;
    }
    public SoundController(ArrayList<NoteEvent> notes, double x, double y, double z, ItemInstrument instrument, byte bps, float volume, TileEntityMusicBox musicBox){
        this(notes, x, y, z, instrument, bps, volume, -1);
        this.musicBox = musicBox;
    }

    private int beatsToTicks(int beats){
        return Math.round(((float)beats) * 20.0f / ((float)bps));
    }

    @Override
    public void run() {
        for (int i=0; i<notes.size(); i++) {
            NoteEvent event = notes.get(i);
            NoteEvent nextEvent = null;
            if(i + 1 < notes.size()) {
                nextEvent = notes.get(i+1);
            }

            if (doStop){
                return;
            }

            if(bps == 0){
                System.err.println("BPS is 0! This should not happen!");
                return;
            }

            if (event.note >= ItemInstrument.minNote && event.note <= ItemInstrument.maxNote) {
                final byte note = event.note;
                Minecraft.getInstance().submitAsync(() -> {
                    ItemInstrument.InsSound insSound = instrument.getSound(note);
                    if(insSound == null){
                        return;
                    }

                    if(musicBox == null){
                        lastPlayed = ClientStuff.playNote(insSound.sound, x, y, z, volume*event.floatVolume(), insSound.pitch, (byte)beatsToTicks(event.length));
                        Minecraft.getInstance().level.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, (note) / 24.0D, 0.0D, 0.0D);
                    }
                    else{
                        lastPlayed = ClientStuff.playNoteTE(insSound.sound, x, y, z, volume*event.floatVolume(), insSound.pitch, (byte)beatsToTicks(event.length));
                        Minecraft.getInstance().level.addParticle(ParticleTypes.NOTE, x+0.5D, y + 2.2D, z+0.5D, (note) / 24.0D, 0.0D, 0.0D);
                    }
                });
            }
            try {
                if(nextEvent != null){
                    long delay = (long)((nextEvent.time - event.time)*1000.f/(float)bps);
                    if(delay > 0){
                        sleep(delay);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Music over
        if(spiritID >= 0){
            Minecraft.getInstance().submitAsync(() -> ClientStuff.endMusic(spiritID, Minecraft.getInstance().player.getId()));
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
}
