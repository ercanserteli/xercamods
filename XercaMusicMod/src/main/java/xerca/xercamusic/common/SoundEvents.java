package xerca.xercamusic.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemInstrument.Pair;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;

@ObjectHolder(XercaMusic.MODID)
public class SoundEvents {
    public static final SoundEvent TICK = null;
    public static final SoundEvent METRONOME_SET = null;

    // Instrument SoPair<Integer, SoundEvent>on
    public static ArrayList<Pair<Integer, SoundEvent>> cymbals;
    public static ArrayList<Pair<Integer, SoundEvent>> drum_kits;
    public static ArrayList<Pair<Integer, SoundEvent>> guitars;
    public static ArrayList<Pair<Integer, SoundEvent>> lyres;
    public static ArrayList<Pair<Integer, SoundEvent>> drums;
    public static ArrayList<Pair<Integer, SoundEvent>> flutes;
    public static ArrayList<Pair<Integer, SoundEvent>> banjos;
    public static ArrayList<Pair<Integer, SoundEvent>> saxophones;
    public static ArrayList<Pair<Integer, SoundEvent>> gods;
    public static ArrayList<Pair<Integer, SoundEvent>> harp_mcs;
    public static ArrayList<Pair<Integer, SoundEvent>> sansulas;
    public static ArrayList<Pair<Integer, SoundEvent>> tubular_bells;
    public static ArrayList<Pair<Integer, SoundEvent>> violins;
    public static ArrayList<Pair<Integer, SoundEvent>> xylophones;
    public static ArrayList<Pair<Integer, SoundEvent>> cellos;
    public static ArrayList<Pair<Integer, SoundEvent>> pianos;

    public static final ItemInstrument fakeHarpIns = new ItemInstrument("harp_mc", false, -1, 0, 7);

    private static SoundEvent createSoundEvent(String soundName, final RegistryEvent.Register<SoundEvent> event) {
        final ResourceLocation soundID = new ResourceLocation(XercaMusic.MODID, soundName);
        final SoundEvent soundEvent = new SoundEvent(soundID).setRegistryName(soundID);
        event.getRegistry().register(soundEvent);
        return soundEvent;
    }

    private static void addSound(ArrayList<Pair<Integer, SoundEvent>> array, String insName, int note, final RegistryEvent.Register<SoundEvent> event){
        array.add(Pair.of(note, createSoundEvent(insName + note, event)));
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
            createSoundEvent("tick", event);
            createSoundEvent("metronome_set", event);

            // Instrument SoundEvent initialization
            cymbals = new ArrayList<>(48);
            drum_kits = new ArrayList<>(48);
            guitars = new ArrayList<>(48);
            drums = new ArrayList<>(48);
            lyres = new ArrayList<>(48);
            flutes = new ArrayList<>(48);
            banjos = new ArrayList<>(48);
            saxophones = new ArrayList<>(48);
            gods = new ArrayList<>(48);
            harp_mcs = new ArrayList<>(48);
            sansulas = new ArrayList<>(48);
            tubular_bells = new ArrayList<>(48);
            violins = new ArrayList<>(48);
            xylophones = new ArrayList<>(48);
            cellos = new ArrayList<>(48);
            pianos = new ArrayList<>(48);
            for (int i = 21; i <= 116; i++) {
                addSound(drum_kits, "drum_kit", i, event);
            }
            for (int i = 27; i <= 111; i+=6) {
                addSound(banjos, "banjo", i, event);
                addSound(cymbals, "cymbal", i, event);
                addSound(drums, "drum", i, event);
                addSound(gods, "god", i, event);
                addSound(harp_mcs, "harp_mc", i, event);
                addSound(lyres, "lyre", i, event);
                addSound(sansulas, "sansula", i, event);
                addSound(tubular_bells, "tubular_bell", i, event);
                addSound(xylophones, "xylophone", i, event);
            }

            addSound(guitars, "guitar", 24, event);
            addSound(guitars, "guitar", 28, event);
            addSound(guitars, "guitar", 34, event);
            addSound(guitars, "guitar", 40, event);
            addSound(guitars, "guitar", 43, event);
            addSound(guitars, "guitar", 48, event);
            addSound(guitars, "guitar", 54, event);
            addSound(guitars, "guitar", 55, event);
            addSound(guitars, "guitar", 59, event);
            addSound(guitars, "guitar", 65, event);
            addSound(guitars, "guitar", 72, event);
            addSound(guitars, "guitar", 78, event);
            addSound(guitars, "guitar", 84, event);
            addSound(guitars, "guitar", 90, event);
            addSound(guitars, "guitar", 96, event);
            addSound(guitars, "guitar", 102, event);

            addSound(cellos, "cello", 25, event);
            addSound(cellos, "cello", 31, event);
            addSound(cellos, "cello", 37, event);
            addSound(cellos, "cello", 43, event);
            addSound(cellos, "cello", 49, event);
            addSound(cellos, "cello", 52, event);
            addSound(cellos, "cello", 55, event);
            addSound(cellos, "cello", 58, event);
            addSound(cellos, "cello", 61, event);
            addSound(cellos, "cello", 64, event);
            addSound(cellos, "cello", 67, event);
            addSound(cellos, "cello", 70, event);
            addSound(cellos, "cello", 73, event);
            addSound(cellos, "cello", 76, event);
            addSound(cellos, "cello", 79, event);
            addSound(cellos, "cello", 82, event);
            addSound(cellos, "cello", 85, event);
            addSound(cellos, "cello", 88, event);
            addSound(cellos, "cello", 91, event);
            addSound(cellos, "cello", 94, event);
            addSound(cellos, "cello", 97, event);
            addSound(cellos, "cello", 100, event);
            addSound(cellos, "cello", 106, event);
            addSound(cellos, "cello", 112, event);

            addSound(violins, "violin", 37, event);
            addSound(violins, "violin", 43, event);
            addSound(violins, "violin", 49, event);
            addSound(violins, "violin", 55, event);
            addSound(violins, "violin", 58, event);
            addSound(violins, "violin", 61, event);
            addSound(violins, "violin", 64, event);
            addSound(violins, "violin", 67, event);
            addSound(violins, "violin", 70, event);
            addSound(violins, "violin", 73, event);
            addSound(violins, "violin", 76, event);
            addSound(violins, "violin", 79, event);
            addSound(violins, "violin", 82, event);
            addSound(violins, "violin", 85, event);
            addSound(violins, "violin", 88, event);
            addSound(violins, "violin", 91, event);
            addSound(violins, "violin", 94, event);
            addSound(violins, "violin", 100, event);
            addSound(violins, "violin", 106, event);

            addSound(flutes, "flute", 35, event);
            addSound(flutes, "flute", 41, event);
            addSound(flutes, "flute", 51, event);
            addSound(flutes, "flute", 61, event);
            addSound(flutes, "flute", 63, event);
            addSound(flutes, "flute", 65, event);
            addSound(flutes, "flute", 68, event);
            addSound(flutes, "flute", 73, event);
            addSound(flutes, "flute", 76, event);
            addSound(flutes, "flute", 78, event);
            addSound(flutes, "flute", 80, event);
            addSound(flutes, "flute", 85, event);
            addSound(flutes, "flute", 88, event);
            addSound(flutes, "flute", 90, event);
            addSound(flutes, "flute", 100, event);

            addSound(saxophones, "saxophone", 24, event);
            addSound(saxophones, "saxophone", 30, event);
            addSound(saxophones, "saxophone", 36, event);
            addSound(saxophones, "saxophone", 38, event);
            addSound(saxophones, "saxophone", 41, event);
            addSound(saxophones, "saxophone", 45, event);
            addSound(saxophones, "saxophone", 48, event);
            addSound(saxophones, "saxophone", 50, event);
            addSound(saxophones, "saxophone", 53, event);
            addSound(saxophones, "saxophone", 57, event);
            addSound(saxophones, "saxophone", 60, event);
            addSound(saxophones, "saxophone", 65, event);
            addSound(saxophones, "saxophone", 72, event);
            addSound(saxophones, "saxophone", 78, event);
            addSound(saxophones, "saxophone", 84, event);
            addSound(saxophones, "saxophone", 90, event);
            addSound(saxophones, "saxophone", 96, event);
            addSound(saxophones, "saxophone", 102, event);
            addSound(saxophones, "saxophone", 108, event);

            addSound(pianos, "piano", 27, event);
            addSound(pianos, "piano", 36, event);
            addSound(pianos, "piano", 40, event);
            addSound(pianos, "piano", 45, event);
            addSound(pianos, "piano", 56, event);
            addSound(pianos, "piano", 61, event);
            addSound(pianos, "piano", 66, event);
            addSound(pianos, "piano", 70, event);
            addSound(pianos, "piano", 73, event);
            addSound(pianos, "piano", 77, event);
            addSound(pianos, "piano", 82, event);
            addSound(pianos, "piano", 86, event);
            addSound(pianos, "piano", 90, event);
            addSound(pianos, "piano", 95, event);
            addSound(pianos, "piano", 103, event);
            addSound(pianos, "piano", 109, event);

            // Instrument SoundEvent setting
            Items.CYMBAL.setSounds(cymbals);
            Items.DRUM_KIT.setSounds(drum_kits);
            Items.GUITAR.setSounds(guitars);
            Items.LYRE.setSounds(lyres);
            Items.DRUM.setSounds(drums);
            Items.FLUTE.setSounds(flutes);
            Items.BANJO.setSounds(banjos);
            Items.SAXOPHONE.setSounds(saxophones);
            Items.GOD.setSounds(gods);
            Items.SANSULA.setSounds(sansulas);
            Items.TUBULAR_BELL.setSounds(tubular_bells);
            Items.VIOLIN.setSounds(violins);
            Items.XYLOPHONE.setSounds(xylophones);
            Items.CELLO.setSounds(cellos);
            Items.PIANO.setSounds(pianos);

            fakeHarpIns.setSounds(harp_mcs);
        }
    }
}