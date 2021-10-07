package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

public class SoundController extends Thread {
    private byte[] music;
    private volatile boolean doStop = false;
    private volatile double x;
    private volatile double y;
    private volatile double z;
    private ItemInstrument instrument;
    private byte pause;
    private int spiritID;
    private NoteSound lastPlayed = null;
    private TileEntityMusicBox musicBox = null;

    public SoundController(byte[] music, double x, double y, double z, ItemInstrument instrument, byte pause, int spiritID){
        this.music = music;
        this.x = x;
        this.y = y;
        this.z = z;
        this.instrument = instrument;
        this.pause = pause;
        this.spiritID = spiritID;
    }
    public SoundController(byte[] music, double x, double y, double z, ItemInstrument instrument, byte pause, TileEntityMusicBox musicBox){
        this(music, x, y, z, instrument, pause, -1);
        this.musicBox = musicBox;
    }

    @Override
    public void run() {
        for (byte b : music) {
            if (doStop){
                return;
            }

            if(pause == 0){
                System.err.println("pause is 0! THIS SHOULD NOT HAPPEN!");
                return;
            }

            if (b > 0 && b <= 48) {
                if(instrument.shouldCutOff && lastPlayed != null){
                    Minecraft.getInstance().submitAsync(() -> {
                        lastPlayed.stopSound();
                    });
                }
                final byte note = b;
                Minecraft.getInstance().submitAsync(() -> {
                    if(musicBox == null){
                        lastPlayed = ClientStuff.playNote(instrument.getSound(note - 1), x, y, z);
                        Minecraft.getInstance().level.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, (note - 1) / 24.0D, 0.0D, 0.0D);
                    }
                    else{
                        lastPlayed = ClientStuff.playNoteTE(instrument.getSound(note - 1), x, y, z);
                        Minecraft.getInstance().level.addParticle(ParticleTypes.NOTE, x+0.5D, y + 2.2D, z+0.5D, (note - 1) / 24.0D, 0.0D, 0.0D);
                    }
                });
            }
            try {
                sleep(pause*50);
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
