package xerca.xercamusic.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;

public class SoundEvents {
    public static SoundEvent TICK = null;
    public static SoundEvent METRONOME_SET = null;
    public static SoundEvent OPEN_SCROLL = null;
    public static SoundEvent CLOSE_SCROLL = null;

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
    public static ArrayList<Pair<Integer, SoundEvent>> oboes;
    public static ArrayList<Pair<Integer, SoundEvent>> redstone_guitars;
    public static ArrayList<Pair<Integer, SoundEvent>> french_horns;
    public static ArrayList<Pair<Integer, SoundEvent>> bass_guitars;

    private static SoundEvent createSoundEvent(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(XercaMusic.MODID, soundName);
        final SoundEvent soundEvent = new SoundEvent(soundID);
        Registry.register(Registry.SOUND_EVENT, soundID, soundEvent);
        return soundEvent;
    }

    private static void addSound(ArrayList<Pair<Integer, SoundEvent>> array, String insName, int note){
        array.add(Pair.of(note, createSoundEvent(insName + note)));
    }

    public static void registerSoundEvents() {
        TICK = createSoundEvent("tick");
        METRONOME_SET = createSoundEvent("metronome_set");
        OPEN_SCROLL = createSoundEvent("open_scroll");
        CLOSE_SCROLL = createSoundEvent("close_scroll");

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
        oboes = new ArrayList<>(21);
        redstone_guitars = new ArrayList<>(11);
        french_horns = new ArrayList<>(11);
        bass_guitars = new ArrayList<>(8);

        for (int i = 21; i <= 116; i++) {
            addSound(drum_kits, "drum_kit", i);
        }
        for (int i = 27; i <= 111; i+=6) {
            addSound(harp_mcs, "harp_mc", i);
        }

        addSound(guitars, "guitar", 24);
        addSound(guitars, "guitar", 28);
        addSound(guitars, "guitar", 34);
        addSound(guitars, "guitar", 40);
        addSound(guitars, "guitar", 48);
        addSound(guitars, "guitar", 54);
        addSound(guitars, "guitar", 55);
        addSound(guitars, "guitar", 59);
        addSound(guitars, "guitar", 65);
        addSound(guitars, "guitar", 72);
        addSound(guitars, "guitar", 78);
        addSound(guitars, "guitar", 84);
        addSound(guitars, "guitar", 90);
        addSound(guitars, "guitar", 96);
        addSound(guitars, "guitar", 102);

        addSound(lyres, "lyre", 33);
        addSound(lyres, "lyre", 39);
        addSound(lyres, "lyre", 45);
        addSound(lyres, "lyre", 51);
        addSound(lyres, "lyre", 57);
        addSound(lyres, "lyre", 63);
        addSound(lyres, "lyre", 69);
        addSound(lyres, "lyre", 75);
        addSound(lyres, "lyre", 81);
        addSound(lyres, "lyre", 87);
        addSound(lyres, "lyre", 93);

        addSound(gods, "god", 27);
        addSound(gods, "god", 33);
        addSound(gods, "god", 39);
        addSound(gods, "god", 45);
        addSound(gods, "god", 51);
        addSound(gods, "god", 57);
        addSound(gods, "god", 63);
        addSound(gods, "god", 69);
        addSound(gods, "god", 75);
        addSound(gods, "god", 81);
        addSound(gods, "god", 87);

        addSound(banjos, "banjo", 27);
        addSound(banjos, "banjo", 33);
        addSound(banjos, "banjo", 39);
        addSound(banjos, "banjo", 45);
        addSound(banjos, "banjo", 51);
        addSound(banjos, "banjo", 57);
        addSound(banjos, "banjo", 63);
        addSound(banjos, "banjo", 69);
        addSound(banjos, "banjo", 75);
        addSound(banjos, "banjo", 81);

        addSound(drums, "drum", 33);
        addSound(drums, "drum", 39);
        addSound(drums, "drum", 43);
        addSound(drums, "drum", 50);
        addSound(drums, "drum", 55);
        addSound(drums, "drum", 58);
        addSound(drums, "drum", 63);
        addSound(drums, "drum", 69);
        addSound(drums, "drum", 75);
        addSound(drums, "drum", 81);

        addSound(cymbals, "cymbal", 27);
        addSound(cymbals, "cymbal", 33);
        addSound(cymbals, "cymbal", 39);
        addSound(cymbals, "cymbal", 45);
        addSound(cymbals, "cymbal", 51);
        addSound(cymbals, "cymbal", 57);
        addSound(cymbals, "cymbal", 63);
        addSound(cymbals, "cymbal", 69);
        addSound(cymbals, "cymbal", 75);
        addSound(cymbals, "cymbal", 81);

        addSound(xylophones, "xylophone", 27);
        addSound(xylophones, "xylophone", 33);
        addSound(xylophones, "xylophone", 39);
        addSound(xylophones, "xylophone", 45);
        addSound(xylophones, "xylophone", 51);
        addSound(xylophones, "xylophone", 57);
        addSound(xylophones, "xylophone", 63);
        addSound(xylophones, "xylophone", 69);
        addSound(xylophones, "xylophone", 75);
        addSound(xylophones, "xylophone", 81);
        addSound(xylophones, "xylophone", 87);

        addSound(sansulas, "sansula", 33);
        addSound(sansulas, "sansula", 39);
        addSound(sansulas, "sansula", 45);
        addSound(sansulas, "sansula", 51);
        addSound(sansulas, "sansula", 57);
        addSound(sansulas, "sansula", 63);
        addSound(sansulas, "sansula", 69);
        addSound(sansulas, "sansula", 75);
        addSound(sansulas, "sansula", 81);
        addSound(sansulas, "sansula", 87);

        addSound(tubular_bells, "tubular_bell", 33);
        addSound(tubular_bells, "tubular_bell", 39);
        addSound(tubular_bells, "tubular_bell", 45);
        addSound(tubular_bells, "tubular_bell", 51);
        addSound(tubular_bells, "tubular_bell", 57);
        addSound(tubular_bells, "tubular_bell", 63);
        addSound(tubular_bells, "tubular_bell", 69);
        addSound(tubular_bells, "tubular_bell", 75);
        addSound(tubular_bells, "tubular_bell", 81);

        addSound(cellos, "cello", 25);
        addSound(cellos, "cello", 31);
        addSound(cellos, "cello", 37);
        addSound(cellos, "cello", 43);
        addSound(cellos, "cello", 49);
        addSound(cellos, "cello", 52);
        addSound(cellos, "cello", 55);
        addSound(cellos, "cello", 58);
        addSound(cellos, "cello", 61);
        addSound(cellos, "cello", 64);
        addSound(cellos, "cello", 67);
        addSound(cellos, "cello", 70);
        addSound(cellos, "cello", 73);
        addSound(cellos, "cello", 76);
        addSound(cellos, "cello", 79);
        addSound(cellos, "cello", 82);
        addSound(cellos, "cello", 85);
        addSound(cellos, "cello", 88);
        addSound(cellos, "cello", 91);
        addSound(cellos, "cello", 94);
        addSound(cellos, "cello", 97);
        addSound(cellos, "cello", 100);

        addSound(violins, "violin", 37);
        addSound(violins, "violin", 43);
        addSound(violins, "violin", 49);
        addSound(violins, "violin", 55);
        addSound(violins, "violin", 58);
        addSound(violins, "violin", 61);
        addSound(violins, "violin", 64);
        addSound(violins, "violin", 67);
        addSound(violins, "violin", 70);
        addSound(violins, "violin", 73);
        addSound(violins, "violin", 76);
        addSound(violins, "violin", 79);
        addSound(violins, "violin", 82);
        addSound(violins, "violin", 85);
        addSound(violins, "violin", 88);
        addSound(violins, "violin", 91);

        addSound(flutes, "flute", 35);
        addSound(flutes, "flute", 41);
        addSound(flutes, "flute", 51);
        addSound(flutes, "flute", 61);
        addSound(flutes, "flute", 63);
        addSound(flutes, "flute", 65);
        addSound(flutes, "flute", 68);
        addSound(flutes, "flute", 73);
        addSound(flutes, "flute", 76);
        addSound(flutes, "flute", 78);
        addSound(flutes, "flute", 80);
        addSound(flutes, "flute", 85);
        addSound(flutes, "flute", 88);
        addSound(flutes, "flute", 90);
        addSound(flutes, "flute", 100);

        addSound(saxophones, "saxophone", 24);
        addSound(saxophones, "saxophone", 30);
        addSound(saxophones, "saxophone", 36);
        addSound(saxophones, "saxophone", 38);
        addSound(saxophones, "saxophone", 41);
        addSound(saxophones, "saxophone", 45);
        addSound(saxophones, "saxophone", 48);
        addSound(saxophones, "saxophone", 50);
        addSound(saxophones, "saxophone", 53);
        addSound(saxophones, "saxophone", 57);
        addSound(saxophones, "saxophone", 60);
        addSound(saxophones, "saxophone", 65);
        addSound(saxophones, "saxophone", 72);
        addSound(saxophones, "saxophone", 78);

        addSound(pianos, "piano", 27);
        addSound(pianos, "piano", 36);
        addSound(pianos, "piano", 40);
        addSound(pianos, "piano", 45);
        addSound(pianos, "piano", 56);
        addSound(pianos, "piano", 61);
        addSound(pianos, "piano", 66);
        addSound(pianos, "piano", 70);
        addSound(pianos, "piano", 73);
        addSound(pianos, "piano", 77);
        addSound(pianos, "piano", 82);
        addSound(pianos, "piano", 86);
        addSound(pianos, "piano", 90);
        addSound(pianos, "piano", 95);
        addSound(pianos, "piano", 103);
        addSound(pianos, "piano", 109);

        addSound(oboes, "oboe", 23);
        addSound(oboes, "oboe", 29);
        addSound(oboes, "oboe", 35);
        addSound(oboes, "oboe", 37);
        addSound(oboes, "oboe", 39);
        addSound(oboes, "oboe", 40);
        addSound(oboes, "oboe", 42);
        addSound(oboes, "oboe", 44);
        addSound(oboes, "oboe", 46);
        addSound(oboes, "oboe", 48);
        addSound(oboes, "oboe", 50);
        addSound(oboes, "oboe", 52);
        addSound(oboes, "oboe", 54);
        addSound(oboes, "oboe", 55);
        addSound(oboes, "oboe", 57);
        addSound(oboes, "oboe", 59);
        addSound(oboes, "oboe", 60);
        addSound(oboes, "oboe", 62);
        addSound(oboes, "oboe", 64);
        addSound(oboes, "oboe", 65);
        addSound(oboes, "oboe", 71);

        addSound(redstone_guitars, "redstone_guitar", 27);
        addSound(redstone_guitars, "redstone_guitar", 33);
        addSound(redstone_guitars, "redstone_guitar", 39);
        addSound(redstone_guitars, "redstone_guitar", 45);
        addSound(redstone_guitars, "redstone_guitar", 51);
        addSound(redstone_guitars, "redstone_guitar", 57);
        addSound(redstone_guitars, "redstone_guitar", 63);
        addSound(redstone_guitars, "redstone_guitar", 69);
        addSound(redstone_guitars, "redstone_guitar", 75);
        addSound(redstone_guitars, "redstone_guitar", 81);
        addSound(redstone_guitars, "redstone_guitar", 87);

        addSound(french_horns, "french_horn", 27);
        addSound(french_horns, "french_horn", 33);
        addSound(french_horns, "french_horn", 39);
        addSound(french_horns, "french_horn", 45);
        addSound(french_horns, "french_horn", 51);
        addSound(french_horns, "french_horn", 57);
        addSound(french_horns, "french_horn", 63);
        addSound(french_horns, "french_horn", 69);
        addSound(french_horns, "french_horn", 75);
        addSound(french_horns, "french_horn", 81);
        addSound(french_horns, "french_horn", 87);

        addSound(bass_guitars, "bass_guitar", 33);
        addSound(bass_guitars, "bass_guitar", 39);
        addSound(bass_guitars, "bass_guitar", 45);
        addSound(bass_guitars, "bass_guitar", 51);
        addSound(bass_guitars, "bass_guitar", 57);
        addSound(bass_guitars, "bass_guitar", 63);
        addSound(bass_guitars, "bass_guitar", 69);
        addSound(bass_guitars, "bass_guitar", 75);

        // Instrument SoundEvent setting
        ((IItemInstrument)Items.CYMBAL).setSounds(cymbals);
        ((IItemInstrument)Items.DRUM_KIT).setSounds(drum_kits);
        ((IItemInstrument)Items.GUITAR).setSounds(guitars);
        ((IItemInstrument)Items.LYRE).setSounds(lyres);
        ((IItemInstrument)Items.DRUM).setSounds(drums);
        ((IItemInstrument)Items.FLUTE).setSounds(flutes);
        ((IItemInstrument)Items.BANJO).setSounds(banjos);
        ((IItemInstrument)Items.SAXOPHONE).setSounds(saxophones);
        ((IItemInstrument)Items.GOD).setSounds(gods);
        ((IItemInstrument)Items.SANSULA).setSounds(sansulas);
        ((IItemInstrument)Items.TUBULAR_BELL).setSounds(tubular_bells);
        ((IItemInstrument)Items.VIOLIN).setSounds(violins);
        ((IItemInstrument)Items.XYLOPHONE).setSounds(xylophones);
        ((IItemInstrument)Items.CELLO).setSounds(cellos);
        ((IItemInstrument)Items.PIANO).setSounds(pianos);
        ((IItemInstrument)Items.OBOE).setSounds(oboes);
        ((IItemInstrument)Items.REDSTONE_GUITAR).setSounds(redstone_guitars);
        ((IItemInstrument)Items.FRENCH_HORN).setSounds(french_horns);
        ((IItemInstrument)Items.BASS_GUITAR).setSounds(bass_guitars);

        ((IItemInstrument)Items.HARP_MC).setSounds(harp_mcs);
    }
}