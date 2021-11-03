package xerca.xercamusic.client;

import xerca.xercamusic.common.XercaMusic;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MidiHandler
{
    ArrayList<MidiDevice> devices = new ArrayList<>();
    Consumer<MidiData> noteOnHandler;
    Consumer<Integer> noteOffHandler;

    public MidiHandler(Consumer<MidiData> noteOnHandler, Consumer<Integer> noteOffHandler)
    {
        this.noteOnHandler = noteOnHandler;
        this.noteOffHandler = noteOffHandler;

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

            } catch (MidiUnavailableException e) {
            }
        }
    }

    public void closeDevices() {
        for(MidiDevice device : devices){
            if(device.isOpen()){
                device.close();
            }
        }
    }

    public class MidiInputReceiver implements Receiver {
        public String name;
        public static final int NOTE_ON = 0x90;
        public static final int NOTE_OFF = 0x80;
        static final float ym = 0.8f;
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
                int key = sm.getData1() - 24;
                int velocity = sm.getData2();
                if(key < 0 || key > 95){
                    return;
                }

                if (sm.getCommand() == NOTE_ON && velocity > 0) {
                    float vel = ((float)velocity)/128.0f;
                    float vol = volumeCurve(vel);

                    noteOnHandler.accept(new MidiData(key, vol));

                } else if (sm.getCommand() == NOTE_OFF || (sm.getCommand() == NOTE_ON && velocity == 0)) {
                    noteOffHandler.accept(key);
                }
            }
        }

        @Override
        public void close() {}
    }

    public static record MidiData(int noteId, float volume) {}
}