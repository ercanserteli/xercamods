package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import xerca.xercamusic.common.XercaMusic;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MidiHandler {
    final ArrayList<MidiDevice> devices = new ArrayList<>();
    final ArrayList<Transmitter> transmitters = new ArrayList<>();
    final Consumer<MidiData> noteOnHandler;
    final Consumer<Integer> noteOffHandler;
    final Consumer<GuiMusicSheet.MidiControl> midiControlHandler;
    public volatile int currentOctave;

    public MidiHandler(Consumer<MidiData> noteOnHandler, Consumer<Integer> noteOffHandler, Consumer<GuiMusicSheet.MidiControl> midiControlHandler) {
        this.noteOnHandler = noteOnHandler;
        this.noteOffHandler = noteOffHandler;
        this.midiControlHandler = midiControlHandler;

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                if (device.getMaxTransmitters() == 0) {
                    continue; // not an input device
                }

                XercaMusic.LOGGER.debug(info);

                device.open();
                Transmitter trans = device.getTransmitter();
                trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));
                transmitters.add(trans);
                devices.add(device);

                XercaMusic.LOGGER.debug("{} was opened", device.getDeviceInfo());

            } catch (MidiUnavailableException exception) {
                XercaMusic.LOGGER.debug("Midi unavailable: {}", exception.getMessage());
            }
        }
    }

    public MidiHandler(Consumer<MidiData> noteOnHandler, Consumer<Integer> noteOffHandler) {
        this(noteOnHandler, noteOffHandler, null);
    }

    public void closeDevices() {
        for (Transmitter t : transmitters) {
            t.close();
        }
        transmitters.clear();

        for (MidiDevice device : devices) {
            if (device.isOpen()) {
                device.close();
            }
        }
        devices.clear();
    }

    public class MidiInputReceiver implements Receiver {
        public final String name;
        public static final int NOTE_ON = 0x90;
        public static final int NOTE_OFF = 0x80;
        public static final int CONTROL = 176;
        public static final int DATA_RECORD = 107;
        public static final int DATA_PREVIEW = 106;
        public static final int DATA_STOP = 105;
        public static final int DATA_END = 104;
        public static final int DATA_BEGINNING = 103;
        static final float ym = 0.7f;
        static final float b = (1.f / ym - 1) * (1.f / ym - 1);

        public MidiInputReceiver(String name) {
            this.name = name;
        }

        private static float volumeCurve(float x) {
            return (float) (Math.pow(b, x) / (b - 1.f) - 1.f / (b - 1.f));
        }

        @SuppressWarnings("FutureReturnValueIgnored")
        private static void submitAndCheck(Runnable runnable) {
            Minecraft.getInstance().submit(runnable).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    XercaMusic.LOGGER.error("Midi controller task failed", throwable);
                }
            });
        }

        @Override
        public void send(MidiMessage msg, long timeStamp) {
            if (msg instanceof ShortMessage sm) {
                int command = sm.getCommand();
                if (command == CONTROL && midiControlHandler != null) {
                    int data = sm.getData1();
                    int value = sm.getData2();
                    if (value == 0) {
                        // 0 is button release
                        return;
                    }
                    switch (data) {
                        case DATA_BEGINNING ->
                                submitAndCheck(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.BEGINNING));
                        case DATA_END -> submitAndCheck(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.END));
                        case DATA_STOP ->
                                submitAndCheck(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.STOP));
                        case DATA_PREVIEW ->
                                submitAndCheck(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.PREVIEW));
                        case DATA_RECORD ->
                                submitAndCheck(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.RECORD));
                        default -> XercaMusic.LOGGER.info("Unhandled midi control {}", data);
                    }
                    return;
                }

                int key = sm.getData1() - 21 + 12 * currentOctave;
                int velocity = sm.getData2();
                XercaMusic.LOGGER.debug("Note message {} key: {} vel: {}", command == NOTE_ON ? "on" : "off", key, velocity);
                if (key < 0 || key > 95) {
                    return;
                }

                if (command == NOTE_ON && velocity > 0) {
                    float vel = ((float) velocity) / 128.0f;
                    float vol = volumeCurve(vel);
                    submitAndCheck(() -> noteOnHandler.accept(new MidiData(key, vol)));
                } else if (command == NOTE_OFF || (command == NOTE_ON && velocity == 0)) {
                    submitAndCheck(() -> noteOffHandler.accept(key));
                }
            }
        }

        @Override
        public void close() {
        }
    }

    public record MidiData(int noteId, float volume) {
    }
}
