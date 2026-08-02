package xerca.xercamusic.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;
import java.util.List;

public class SoundEvents {
    public static final SoundEvent TICK = createSoundEvent("tick");
    public static final SoundEvent METRONOME_SET = createSoundEvent("metronome_set");
    public static final SoundEvent OPEN_SCROLL = createSoundEvent("open_scroll");
    public static final SoundEvent CLOSE_SCROLL = createSoundEvent("close_scroll");

    // Instrument sounds
    public static final List<Pair<Integer, SoundEvent>> cymbals = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> drum_kits = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> guitars = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> lyres = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> drums = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> flutes = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> banjos = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> saxophones = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> gods = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> harp_mcs = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> sansulas = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> tubular_bells = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> violins = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> xylophones = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> cellos = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> pianos = new ArrayList<>(48);
    public static final List<Pair<Integer, SoundEvent>> oboes = new ArrayList<>(21);
    public static final List<Pair<Integer, SoundEvent>> redstone_guitars = new ArrayList<>(11);
    public static final List<Pair<Integer, SoundEvent>> french_horns = new ArrayList<>(11);
    public static final List<Pair<Integer, SoundEvent>> bass_guitars = new ArrayList<>(8);
    public static final List<Pair<Integer, SoundEvent>> trumpets = new ArrayList<>(7);
    public static final List<Pair<Integer, SoundEvent>> redstone_pianos = new ArrayList<>(13);
    public static final List<Pair<Integer, SoundEvent>> organs = new ArrayList<>(11);
    public static final List<Pair<Integer, SoundEvent>> harpsichords = new ArrayList<>(11);
    public static final List<Pair<Integer, SoundEvent>> steelpans = new ArrayList<>(11);
    public static final List<Pair<Integer, SoundEvent>> cuicas = new ArrayList<>(11);
    public static final List<Pair<Integer, SoundEvent>> accordions = new ArrayList<>(11);
    public static final List<Pair<Integer, SoundEvent>> tenor_saxophones = new ArrayList<>(11);

    private static final String NAME_GUITAR = "guitar";
    private static final String NAME_DRUM_KIT = "drum_kit";
    private static final String NAME_LYRE = "lyre";
    private static final String NAME_BANJO = "banjo";
    private static final String NAME_DRUM = "drum";
    private static final String NAME_CYMBAL = "cymbal";
    private static final String NAME_XYLOPHONE = "xylophone";
    private static final String NAME_SANSULA = "sansula";
    private static final String NAME_TUBULAR_BELL = "tubular_bell";
    private static final String NAME_CELLO = "cello";
    private static final String NAME_VOICE_OF_GOD = "god";
    private static final String NAME_VIOLIN = "violin";
    private static final String NAME_FLUTE = "flute";
    private static final String NAME_SAXOPHONE = "saxophone";
    private static final String NAME_PIANO = "piano";
    private static final String NAME_OBOE = "oboe";
    private static final String NAME_REDSTONE_GUITAR = "redstone_guitar";
    private static final String NAME_FRENCH_HORN = "french_horn";
    private static final String NAME_BASS_GUITAR = "bass_guitar";
    private static final String NAME_HARP_MC = "harp_mc";
    private static final String NAME_REDSTONE_PIANO = "redstone_piano";
        private static final String NAME_TRUMPET = "trumpet";
    private static final String NAME_ORGAN = "organ";
    private static final String NAME_HARPSICHORD = "harpsichord";
    private static final String NAME_STEELPAN = "steelpan";
    private static final String NAME_CUICA = "cuica";
    private static final String NAME_ACCORDION = "accordion";
    private static final String NAME_TENOR_SAXOPHONE = "tenor_saxophone";

    private SoundEvents() {
    }

    private static SoundEvent createSoundEvent(String soundName) {
        final Identifier soundID = Mod.id(soundName);
        final SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundID);
        Registry.register(BuiltInRegistries.SOUND_EVENT, soundID, soundEvent);
        return soundEvent;
    }

    private static void addSound(List<Pair<Integer, SoundEvent>> array, String insName, int note) {
        array.add(Pair.of(note, createSoundEvent(insName + note)));
    }

    private static void addRange(List<Pair<Integer, SoundEvent>> array, String insName, int start, int end) {
        addRange(array, insName, start, end, 1);
    }

    private static void addRange(List<Pair<Integer, SoundEvent>> array, String insName, int start, int end, int step) {
        for (int i = start; i <= end; i += step) {
            addSound(array, insName, i);
        }
    }

    public static void registerSoundEvents() {
        cymbals.clear();
        drum_kits.clear();
        french_horns.clear();
        guitars.clear();
        drums.clear();
        redstone_guitars.clear();
        lyres.clear();
        flutes.clear();
        banjos.clear();
        saxophones.clear();
        gods.clear();
        oboes.clear();
        harp_mcs.clear();
        sansulas.clear();
        tubular_bells.clear();
        violins.clear();
        bass_guitars.clear();
        xylophones.clear();
        cellos.clear();
        pianos.clear();
        trumpets.clear();
        redstone_pianos.clear();
        organs.clear();
        harpsichords.clear();
        steelpans.clear();
        cuicas.clear();
        accordions.clear();
        tenor_saxophones.clear();
        // ranges
        addRange(drum_kits, NAME_DRUM_KIT, 21, 116);
        addRange(harp_mcs, NAME_HARP_MC, 27, 111, 6);

        addFixed(guitars, NAME_GUITAR,
                24, 28, 34, 40, 48, 54, 55, 59, 65, 72, 78, 84, 90, 96, 102);

        addFixed(lyres, NAME_LYRE,
                33, 39, 45, 51, 57, 63, 69, 75, 81, 87, 93);

        addFixed(gods, NAME_VOICE_OF_GOD,
                27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

        addFixed(banjos, NAME_BANJO,
                27, 33, 39, 45, 51, 57, 63, 69, 75, 81);

        addFixed(drums, NAME_DRUM,
                33, 39, 43, 50, 55, 58, 63, 69, 75, 81);

        addFixed(cymbals, NAME_CYMBAL,
                27, 33, 39, 45, 51, 57, 63, 69, 75, 81);

        addFixed(xylophones, NAME_XYLOPHONE,
                27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

        addFixed(sansulas, NAME_SANSULA,
                33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

        addFixed(tubular_bells, NAME_TUBULAR_BELL,
                33, 39, 45, 51, 57, 63, 69, 75, 81);

        addFixed(cellos, NAME_CELLO,
                25, 31, 37, 43, 49, 52, 55, 58, 61, 64, 67, 70, 73, 76, 79, 82, 85, 88, 91, 94, 97, 100);

        addFixed(violins, NAME_VIOLIN,
                37, 43, 49, 55, 58, 61, 64, 67, 70, 73, 76, 79, 82, 85, 88, 91);

        addFixed(flutes, NAME_FLUTE,
                35, 41, 51, 61, 63, 65, 68, 73, 76, 78, 80, 85, 88, 90, 100);

        addFixed(saxophones, NAME_SAXOPHONE,
                24, 30, 36, 38, 41, 45, 48, 50, 53, 57, 60, 65, 72, 78);

        addFixed(pianos, NAME_PIANO,
                27, 36, 40, 45, 56, 61, 66, 70, 73, 77, 82, 86, 90, 95, 103, 109);

        addFixed(oboes, NAME_OBOE,
                23, 29, 35, 37, 39, 40, 42, 44, 46, 48, 50, 52, 54, 55, 57, 59, 60, 62, 64, 65, 71);

        addFixed(redstone_guitars, NAME_REDSTONE_GUITAR,
                27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

        addFixed(french_horns, NAME_FRENCH_HORN,
                27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

        addFixed(bass_guitars, NAME_BASS_GUITAR,
                33, 39, 45, 51, 57, 63, 69, 75);

        // Trumpet samples (MIDI 48-84, step 6)
        addFixed(trumpets, NAME_TRUMPET, 48, 54, 60, 66, 72, 78, 84);

        // Eredstone Piano samples (MIDI 24-96, step 6)
        addFixed(redstone_pianos, NAME_REDSTONE_PIANO,
                24, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84, 90, 96);

        // Organ samples (MIDI 36-96, step 6)
        addFixed(organs, NAME_ORGAN,
                36, 42, 48, 54, 60, 66, 72, 78, 84, 90, 96);
        
        addFixed(harpsichords, NAME_HARPSICHORD, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84);
        
        addFixed(steelpans, NAME_STEELPAN, 42, 48, 54, 60, 66, 72, 78, 84, 90);
        addFixed(cuicas, NAME_CUICA,  36, 42, 48, 54, 60, 66, 72, 78, 84, 90);
        addFixed(accordions, NAME_ACCORDION, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84, 90);
        addFixed(tenor_saxophones, NAME_TENOR_SAXOPHONE, 24, 30, 36, 42, 48, 54, 60, 66, 72, 78);
        // Instrument SoundEvent setting
        ((IItemInstrument) Items.CYMBAL).setSounds(cymbals);
        ((IItemInstrument) Items.DRUM_KIT).setSounds(drum_kits);
        ((IItemInstrument) Items.GUITAR).setSounds(guitars);
        ((IItemInstrument) Items.LYRE).setSounds(lyres);
        ((IItemInstrument) Items.DRUM).setSounds(drums);
        ((IItemInstrument) Items.FLUTE).setSounds(flutes);
        ((IItemInstrument) Items.BANJO).setSounds(banjos);
        ((IItemInstrument) Items.SAXOPHONE).setSounds(saxophones);
        ((IItemInstrument) Items.GOD).setSounds(gods);
        ((IItemInstrument) Items.SANSULA).setSounds(sansulas);
        ((IItemInstrument) Items.TUBULAR_BELL).setSounds(tubular_bells);
        ((IItemInstrument) Items.VIOLIN).setSounds(violins);
        ((IItemInstrument) Items.XYLOPHONE).setSounds(xylophones);
        ((IItemInstrument) Items.CELLO).setSounds(cellos);
        ((IItemInstrument) Items.PIANO).setSounds(pianos);
        ((IItemInstrument) Items.OBOE).setSounds(oboes);
        ((IItemInstrument) Items.REDSTONE_GUITAR).setSounds(redstone_guitars);
        ((IItemInstrument) Items.FRENCH_HORN).setSounds(french_horns);
        ((IItemInstrument) Items.BASS_GUITAR).setSounds(bass_guitars);
        ((IItemInstrument) Items.TRUMPET).setSounds(trumpets);
        ((IItemInstrument) Items.REDSTONE_PIANO).setSounds(redstone_pianos);
        ((IItemInstrument) Items.ORGAN).setSounds(organs);
        ((IItemInstrument) Items.HARPSICHORD).setSounds(harpsichords);
        ((IItemInstrument) Items.STEELPAN).setSounds(steelpans);
        ((IItemInstrument) Items.CUICA).setSounds(cuicas);
        ((IItemInstrument) Items.ACCORDION).setSounds(accordions);
        ((IItemInstrument) Items.TENOR_SAXOPHONE).setSounds(tenor_saxophones);
        ((IItemInstrument) Items.HARP_MC).setSounds(harp_mcs);
    }

    private static void addFixed(List<Pair<Integer, SoundEvent>> target, String instrumentName, int... notes) {
        for (int note : notes) {
            addSound(target, instrumentName, note);
        }
    }
}
