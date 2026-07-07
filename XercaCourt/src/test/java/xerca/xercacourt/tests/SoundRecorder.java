package xerca.xercacourt.tests;

import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public final class SoundRecorder {
    private static final List<SoundEvent> PLAYED = new ArrayList<>();

    private SoundRecorder() {
    }

    public static synchronized void record(SoundEvent sound) {
        PLAYED.add(sound);
    }

    public static synchronized void clear() {
        PLAYED.clear();
    }

    public static synchronized boolean played(SoundEvent sound) {
        return PLAYED.contains(sound);
    }
}
