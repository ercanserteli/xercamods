package xerca.xercamusic.server;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.Proxy;

public class ServerProxy implements Proxy {
    public void preInit() {

    }

    public void init() {

    }

    @Override
    public NoteSound playNote(SoundEvent event, double posX, double posY, double posZ) {
        return null;
    }

    @Override
    public NoteSound playNote(SoundEvent event, double posX, double posY, double posZ, SoundCategory category, float volume, float pitch) {
        return null;
    }

    @Override
    public void endMusic(int spiritID, int playerID) {

    }

    @Override
    public void showMusicGui() {

    }

    @Override
    public void showInstrumentGui() {

    }
}
