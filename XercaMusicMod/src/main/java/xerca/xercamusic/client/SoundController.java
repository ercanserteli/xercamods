package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import xerca.xercamusic.common.item.Items;

import static xerca.xercamusic.client.ClientStuff.task;

public class SoundController extends Thread {
    private byte[] music;

    public SoundController(byte[] music){
        this.music = music;
    }

    @Override
    public void run() {
        for (byte b : music) {
            if (b > 0) {
                final byte note = b;
                Minecraft.getInstance().submitAsync(() -> task(Items.PIANO.getSound(note - 1)));
            }
            try {
                sleep(125);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
