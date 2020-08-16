package xerca.xercamusic.common;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.item.ItemInstrument;

public interface Proxy {

    void preInit();

    void init();

    NoteSound playNote(SoundEvent event, double posX, double posY, double posZ);

    NoteSound playNote(SoundEvent event, double posX, double posY, double posZ, SoundCategory category, float volume, float pitch);

    void endMusic(int spiritID, int playerID);

    void showMusicGui();

    void showInstrumentGui();

    void showInstrumentGui(ItemInstrument instrument);

}
