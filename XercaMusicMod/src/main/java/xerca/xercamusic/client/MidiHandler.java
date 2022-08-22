package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import xerca.xercamusic.common.XercaMusic;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MidiHandler
{
    final ArrayList<MidiDevice> devices = new ArrayList<>();
    final Consumer<MidiData> noteOnHandler;
    final Consumer<Integer> noteOffHandler;
    final Consumer<GuiMusicSheet.MidiControl> midiControlHandler;
    public volatile int currentOctave;

    public MidiHandler(Consumer<MidiData> noteOnHandler, Consumer<Integer> noteOffHandler, Consumer<GuiMusicSheet.MidiControl> midiControlHandler)
    {
        this.noteOnHandler = noteOnHandler;
        this.noteOffHandler = noteOffHandler;
        this.midiControlHandler = midiControlHandler;

        MidiDevice device;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            try {
                device = MidiSystem.getMidiDevice(info);

                XercaMusic.LOGGER.debug(info);
                List<Transmitter> transmitters = device.getTransmitters();

                for (Transmitter transmitter : transmitters) {
                    transmitter.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString())
                    );
                }

                Transmitter trans = device.getTransmitter();
                trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));

                device.open();
                devices.add(device);

                XercaMusic.LOGGER.debug(device.getDeviceInfo() + " was opened");

            } catch (MidiUnavailableException ignored) {
            }
        }
    }

    public MidiHandler(Consumer<MidiData> noteOnHandler, Consumer<Integer> noteOffHandler)
    {
        this(noteOnHandler, noteOffHandler, null);
    }

    public void closeDevices() {
        for(MidiDevice device : devices){
            if(device.isOpen()){
                device.close();
            }
        }
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
        static final float b = (1.f/ym - 1) * (1.f/ym - 1);

        public MidiInputReceiver(String name) {
            this.name = name;
        }

        private static float volumeCurve(float x) {
            return (float)(Math.pow(b, x)/(b-1.f) - 1.f/(b-1.f));
        }

        @Override
        public void send(MidiMessage msg, long timeStamp) {
            if (msg instanceof ShortMessage sm) {
                if(((ShortMessage) msg).getCommand() == CONTROL && midiControlHandler != null){
                    int data = sm.getData1();
                    switch (data){
                        case DATA_BEGINNING -> Minecraft.getInstance().submitAsync(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.BEGINNING));
                        case DATA_END-> Minecraft.getInstance().submitAsync(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.END));
                        case DATA_STOP -> Minecraft.getInstance().submitAsync(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.STOP));
                        case DATA_PREVIEW -> Minecraft.getInstance().submitAsync(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.PREVIEW));
                        case DATA_RECORD -> Minecraft.getInstance().submitAsync(() -> midiControlHandler.accept(GuiMusicSheet.MidiControl.RECORD));
                    }
                    return;
                }

                int key = sm.getData1() - 21 + 12*currentOctave;
                int velocity = sm.getData2();
                System.out.println("Note message " + (sm.getCommand() == NOTE_ON ? "on" : "off") + " key: " + key + " vel: " + velocity);
                if(key < 0 || key > 95){
                    return;
                }

                if (sm.getCommand() == NOTE_ON && velocity > 0) {
                    float vel = ((float)velocity)/128.0f;
                    float vol = volumeCurve(vel);
                    Minecraft.getInstance().submitAsync(() -> noteOnHandler.accept(new MidiData(key, vol)));
                } else if (sm.getCommand() == NOTE_OFF || (sm.getCommand() == NOTE_ON && velocity == 0)) {
                    Minecraft.getInstance().submitAsync(() -> noteOffHandler.accept(key));
                }
            }
        }

        @Override
        public void close() {}
    }

    public record MidiData(int noteId, float volume) {}
}