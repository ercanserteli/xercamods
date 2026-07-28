package xerca.xercamusic.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;
import java.util.List;

public class SoundEvents {
    public static SoundEvent tick;
    public static SoundEvent metronomeSet;
    public static SoundEvent openScroll;
    public static SoundEvent closeScroll;

    // Instrument SoPair<Integer, SoundEvent>on
    public static ArrayList<Pair<Integer, SoundEvent>> cymbals;
    public static ArrayList<Pair<Integer, SoundEvent>> drumKits;
    public static ArrayList<Pair<Integer, SoundEvent>> guitars;
    public static ArrayList<Pair<Integer, SoundEvent>> lyres;
    public static ArrayList<Pair<Integer, SoundEvent>> drums;
    public static ArrayList<Pair<Integer, SoundEvent>> flutes;
    public static ArrayList<Pair<Integer, SoundEvent>> banjos;
    public static ArrayList<Pair<Integer, SoundEvent>> saxophones;
    public static ArrayList<Pair<Integer, SoundEvent>> gods;
    public static ArrayList<Pair<Integer, SoundEvent>> harpMcs;
    public static ArrayList<Pair<Integer, SoundEvent>> sansulas;
    public static ArrayList<Pair<Integer, SoundEvent>> tubularBells;
    public static ArrayList<Pair<Integer, SoundEvent>> violins;
    public static ArrayList<Pair<Integer, SoundEvent>> xylophones;
    public static ArrayList<Pair<Integer, SoundEvent>> cellos;
    public static ArrayList<Pair<Integer, SoundEvent>> pianos;
    public static ArrayList<Pair<Integer, SoundEvent>> oboes;
    public static ArrayList<Pair<Integer, SoundEvent>> redstoneGuitars;
    public static ArrayList<Pair<Integer, SoundEvent>> frenchHorns;
    public static ArrayList<Pair<Integer, SoundEvent>> bassGuitars;
    public static ArrayList<Pair<Integer, SoundEvent>> trumpets;
    public static ArrayList<Pair<Integer, SoundEvent>> redstonePianos;
    public static ArrayList<Pair<Integer, SoundEvent>> organs;

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
    private static final String NAME_TRUMPET = "trumpet";
    private static final String NAME_REDSTONE_PIANO = "redstone_piano";
    private static final String NAME_ORGAN = "organ";

    private static SoundEvent createSoundEvent(String soundName, final RegisterEvent event) {
        final ResourceLocation soundID = new ResourceLocation(XercaMusic.MODID, soundName);
        final SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundID);
        event.register(ForgeRegistries.Keys.SOUND_EVENTS, soundID, () -> soundEvent);
        return soundEvent;
    }

    private static void addSound(List<Pair<Integer, SoundEvent>> array, String insName, int note, final RegisterEvent event) {
        array.add(Pair.of(note, createSoundEvent(insName + note, event)));
    }

    private static void addRange(List<Pair<Integer, SoundEvent>> array, String insName, int start, int end, final RegisterEvent event) {
        addRange(array, insName, start, end, 1, event);
    }

    private static void addRange(List<Pair<Integer, SoundEvent>> array, String insName, int start, int end, int step, final RegisterEvent event) {
        for (int i = start; i <= end; i += step) {
            addSound(array, insName, i, event);
        }
    }

    private static void addFixed(List<Pair<Integer, SoundEvent>> target, String instrumentName, final RegisterEvent event, int... notes) {
        for (int note : notes) {
            addSound(target, instrumentName, note, event);
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerSoundEvents(final RegisterEvent event) {
            // RegisterEvent fires once per registry; only the sound event pass actually registers anything,
            // so bail out on the others instead of rebuilding the lists with unregistered SoundEvents.
            if (!ForgeRegistries.Keys.SOUND_EVENTS.equals(event.getRegistryKey())) {
                return;
            }

            // core sounds
            tick = createSoundEvent("tick", event);
            metronomeSet = createSoundEvent("metronome_set", event);
            openScroll = createSoundEvent("open_scroll", event);
            closeScroll = createSoundEvent("close_scroll", event);

            // init lists
            cymbals = new ArrayList<>(48);
            drumKits = new ArrayList<>(48);
            frenchHorns = new ArrayList<>(11);
            guitars = new ArrayList<>(48);
            drums = new ArrayList<>(48);
            redstoneGuitars = new ArrayList<>(11);
            lyres = new ArrayList<>(48);
            flutes = new ArrayList<>(48);
            banjos = new ArrayList<>(48);
            saxophones = new ArrayList<>(48);
            gods = new ArrayList<>(48);
            oboes = new ArrayList<>(21);
            harpMcs = new ArrayList<>(48);
            sansulas = new ArrayList<>(48);
            tubularBells = new ArrayList<>(48);
            violins = new ArrayList<>(48);
            bassGuitars = new ArrayList<>(8);
            xylophones = new ArrayList<>(48);
            cellos = new ArrayList<>(48);
            pianos = new ArrayList<>(48);
            trumpets = new ArrayList<>(7);
            redstonePianos = new ArrayList<>(13);
            organs = new ArrayList<>(11);

            // ranges
            addRange(drumKits, NAME_DRUM_KIT, 21, 116, event);
            addRange(harpMcs, NAME_HARP_MC, 27, 111, 6, event);

            addFixed(guitars, NAME_GUITAR, event,
                    24, 28, 34, 40, 48, 54, 55, 59, 65, 72, 78, 84, 90, 96, 102);

            addFixed(lyres, NAME_LYRE, event,
                    33, 39, 45, 51, 57, 63, 69, 75, 81, 87, 93);

            addFixed(gods, NAME_VOICE_OF_GOD, event,
                    27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

            addFixed(banjos, NAME_BANJO, event,
                    27, 33, 39, 45, 51, 57, 63, 69, 75, 81);

            addFixed(drums, NAME_DRUM, event,
                    33, 39, 43, 50, 55, 58, 63, 69, 75, 81);

            addFixed(cymbals, NAME_CYMBAL, event,
                    27, 33, 39, 45, 51, 57, 63, 69, 75, 81);

            addFixed(xylophones, NAME_XYLOPHONE, event,
                    27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

            addFixed(sansulas, NAME_SANSULA, event,
                    33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

            addFixed(tubularBells, NAME_TUBULAR_BELL, event,
                    33, 39, 45, 51, 57, 63, 69, 75, 81);

            addFixed(cellos, NAME_CELLO, event,
                    25, 31, 37, 43, 49, 52, 55, 58, 61, 64, 67, 70, 73, 76, 79, 82, 85, 88, 91, 94, 97, 100);

            addFixed(violins, NAME_VIOLIN, event,
                    37, 43, 49, 55, 58, 61, 64, 67, 70, 73, 76, 79, 82, 85, 88, 91);

            addFixed(flutes, NAME_FLUTE, event,
                    35, 41, 51, 61, 63, 65, 68, 73, 76, 78, 80, 85, 88, 90, 100);

            addFixed(saxophones, NAME_SAXOPHONE, event,
                    24, 30, 36, 38, 41, 45, 48, 50, 53, 57, 60, 65, 72, 78);

            addFixed(pianos, NAME_PIANO, event,
                    27, 36, 40, 45, 56, 61, 66, 70, 73, 77, 82, 86, 90, 95, 103, 109);

            addFixed(oboes, NAME_OBOE, event,
                    23, 29, 35, 37, 39, 40, 42, 44, 46, 48, 50, 52, 54, 55, 57, 59, 60, 62, 64, 65, 71);

            addFixed(redstoneGuitars, NAME_REDSTONE_GUITAR, event,
                    27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

            addFixed(frenchHorns, NAME_FRENCH_HORN, event,
                    27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87);

            addFixed(bassGuitars, NAME_BASS_GUITAR, event,
                    33, 39, 45, 51, 57, 63, 69, 75);

            addFixed(trumpets, NAME_TRUMPET, event,
                    48, 54, 60, 66, 72, 78, 84);

            addFixed(redstonePianos, NAME_REDSTONE_PIANO, event,
                    24, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84, 90, 96);

            addFixed(organs, NAME_ORGAN, event,
                    36, 42, 48, 54, 60, 66, 72, 78, 84, 90, 96);
        }
    }

    public static void setup() {
        // Instrument SoundEvent setting
        ((IItemInstrument) Items.CYMBAL.get()).setSounds(cymbals);
        ((IItemInstrument) Items.DRUM_KIT.get()).setSounds(drumKits);
        ((IItemInstrument) Items.GUITAR.get()).setSounds(guitars);
        ((IItemInstrument) Items.LYRE.get()).setSounds(lyres);
        ((IItemInstrument) Items.DRUM.get()).setSounds(drums);
        ((IItemInstrument) Items.FLUTE.get()).setSounds(flutes);
        ((IItemInstrument) Items.BANJO.get()).setSounds(banjos);
        ((IItemInstrument) Items.SAXOPHONE.get()).setSounds(saxophones);
        ((IItemInstrument) Items.GOD.get()).setSounds(gods);
        ((IItemInstrument) Items.SANSULA.get()).setSounds(sansulas);
        ((IItemInstrument) Items.TUBULAR_BELL.get()).setSounds(tubularBells);
        ((IItemInstrument) Items.VIOLIN.get()).setSounds(violins);
        ((IItemInstrument) Items.XYLOPHONE.get()).setSounds(xylophones);
        ((IItemInstrument) Items.CELLO.get()).setSounds(cellos);
        ((IItemInstrument) Items.PIANO.get()).setSounds(pianos);
        ((IItemInstrument) Items.OBOE.get()).setSounds(oboes);
        ((IItemInstrument) Items.REDSTONE_GUITAR.get()).setSounds(redstoneGuitars);
        ((IItemInstrument) Items.FRENCH_HORN.get()).setSounds(frenchHorns);
        ((IItemInstrument) Items.BASS_GUITAR.get()).setSounds(bassGuitars);
        ((IItemInstrument) Items.TRUMPET.get()).setSounds(trumpets);
        ((IItemInstrument) Items.REDSTONE_PIANO.get()).setSounds(redstonePianos);
        ((IItemInstrument) Items.ORGAN.get()).setSounds(organs);

        Items.HARP_MC.get().setSounds(harpMcs);
    }
}
