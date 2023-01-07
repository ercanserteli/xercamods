package xerca.xercamusic.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;

import static xerca.xercamusic.common.XercaMusic.Null;

public class SoundEvents {
    @ObjectHolder(registryName = "minecraft:sound_event", value = XercaMusic.MODID+":tick")
    public static final SoundEvent TICK = Null();
    @ObjectHolder(registryName = "minecraft:sound_event", value = XercaMusic.MODID+":metronome_set")
    public static final SoundEvent METRONOME_SET = Null();
    @ObjectHolder(registryName = "minecraft:sound_event", value = XercaMusic.MODID+":open_scroll")
    public static final SoundEvent OPEN_SCROLL = Null();
    @ObjectHolder(registryName = "minecraft:sound_event", value = XercaMusic.MODID+":close_scroll")
    public static final SoundEvent CLOSE_SCROLL = Null();

    // Instrument SoPair<Integer, SoundEvent>on
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> cymbals;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> drum_kits;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> guitars;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> lyres;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> drums;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> flutes;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> banjos;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> saxophones;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> gods;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> harp_mcs;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> sansulas;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> tubular_bells;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> violins;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> xylophones;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> cellos;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> pianos;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> oboes;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> redstone_guitars;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> french_horns;
    public static ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> bass_guitars;

    private static SoundEvent createSoundEvent(String soundName, final RegisterEvent event) {
        final ResourceLocation soundID = new ResourceLocation(XercaMusic.MODID, soundName);
        final SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundID);
        event.register(ForgeRegistries.Keys.SOUND_EVENTS, soundID, ()->soundEvent);
        return soundEvent;
    }

    private static void addSound(ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> array, String insName, int note, final RegisterEvent event){
        array.add(IItemInstrument.Pair.of(note, createSoundEvent(insName + note, event)));
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerSoundEvents(final RegisterEvent event) {
            createSoundEvent("tick", event);
            createSoundEvent("metronome_set", event);
            createSoundEvent("open_scroll", event);
            createSoundEvent("close_scroll", event);

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
                addSound(drum_kits, "drum_kit", i, event);
            }
            for (int i = 27; i <= 111; i+=6) {
                addSound(harp_mcs, "harp_mc", i, event);
            }

            addSound(guitars, "guitar", 24, event);
            addSound(guitars, "guitar", 28, event);
            addSound(guitars, "guitar", 34, event);
            addSound(guitars, "guitar", 40, event);
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

            addSound(lyres, "lyre", 33, event);
            addSound(lyres, "lyre", 39, event);
            addSound(lyres, "lyre", 45, event);
            addSound(lyres, "lyre", 51, event);
            addSound(lyres, "lyre", 57, event);
            addSound(lyres, "lyre", 63, event);
            addSound(lyres, "lyre", 69, event);
            addSound(lyres, "lyre", 75, event);
            addSound(lyres, "lyre", 81, event);
            addSound(lyres, "lyre", 87, event);
            addSound(lyres, "lyre", 93, event);

            addSound(gods, "god", 27, event);
            addSound(gods, "god", 33, event);
            addSound(gods, "god", 39, event);
            addSound(gods, "god", 45, event);
            addSound(gods, "god", 51, event);
            addSound(gods, "god", 57, event);
            addSound(gods, "god", 63, event);
            addSound(gods, "god", 69, event);
            addSound(gods, "god", 75, event);
            addSound(gods, "god", 81, event);
            addSound(gods, "god", 87, event);

            addSound(banjos, "banjo", 27, event);
            addSound(banjos, "banjo", 33, event);
            addSound(banjos, "banjo", 39, event);
            addSound(banjos, "banjo", 45, event);
            addSound(banjos, "banjo", 51, event);
            addSound(banjos, "banjo", 57, event);
            addSound(banjos, "banjo", 63, event);
            addSound(banjos, "banjo", 69, event);
            addSound(banjos, "banjo", 75, event);
            addSound(banjos, "banjo", 81, event);

            addSound(drums, "drum", 33, event);
            addSound(drums, "drum", 39, event);
            addSound(drums, "drum", 43, event);
            addSound(drums, "drum", 50, event);
            addSound(drums, "drum", 55, event);
            addSound(drums, "drum", 58, event);
            addSound(drums, "drum", 63, event);
            addSound(drums, "drum", 69, event);
            addSound(drums, "drum", 75, event);
            addSound(drums, "drum", 81, event);

            addSound(cymbals, "cymbal", 27, event);
            addSound(cymbals, "cymbal", 33, event);
            addSound(cymbals, "cymbal", 39, event);
            addSound(cymbals, "cymbal", 45, event);
            addSound(cymbals, "cymbal", 51, event);
            addSound(cymbals, "cymbal", 57, event);
            addSound(cymbals, "cymbal", 63, event);
            addSound(cymbals, "cymbal", 69, event);
            addSound(cymbals, "cymbal", 75, event);
            addSound(cymbals, "cymbal", 81, event);

            addSound(xylophones, "xylophone", 27, event);
            addSound(xylophones, "xylophone", 33, event);
            addSound(xylophones, "xylophone", 39, event);
            addSound(xylophones, "xylophone", 45, event);
            addSound(xylophones, "xylophone", 51, event);
            addSound(xylophones, "xylophone", 57, event);
            addSound(xylophones, "xylophone", 63, event);
            addSound(xylophones, "xylophone", 69, event);
            addSound(xylophones, "xylophone", 75, event);
            addSound(xylophones, "xylophone", 81, event);
            addSound(xylophones, "xylophone", 87, event);

            addSound(sansulas, "sansula", 33, event);
            addSound(sansulas, "sansula", 39, event);
            addSound(sansulas, "sansula", 45, event);
            addSound(sansulas, "sansula", 51, event);
            addSound(sansulas, "sansula", 57, event);
            addSound(sansulas, "sansula", 63, event);
            addSound(sansulas, "sansula", 69, event);
            addSound(sansulas, "sansula", 75, event);
            addSound(sansulas, "sansula", 81, event);
            addSound(sansulas, "sansula", 87, event);

            addSound(tubular_bells, "tubular_bell", 33, event);
            addSound(tubular_bells, "tubular_bell", 39, event);
            addSound(tubular_bells, "tubular_bell", 45, event);
            addSound(tubular_bells, "tubular_bell", 51, event);
            addSound(tubular_bells, "tubular_bell", 57, event);
            addSound(tubular_bells, "tubular_bell", 63, event);
            addSound(tubular_bells, "tubular_bell", 69, event);
            addSound(tubular_bells, "tubular_bell", 75, event);
            addSound(tubular_bells, "tubular_bell", 81, event);

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

            addSound(oboes, "oboe", 23, event);
            addSound(oboes, "oboe", 29, event);
            addSound(oboes, "oboe", 35, event);
            addSound(oboes, "oboe", 37, event);
            addSound(oboes, "oboe", 39, event);
            addSound(oboes, "oboe", 40, event);
            addSound(oboes, "oboe", 42, event);
            addSound(oboes, "oboe", 44, event);
            addSound(oboes, "oboe", 46, event);
            addSound(oboes, "oboe", 48, event);
            addSound(oboes, "oboe", 50, event);
            addSound(oboes, "oboe", 52, event);
            addSound(oboes, "oboe", 54, event);
            addSound(oboes, "oboe", 55, event);
            addSound(oboes, "oboe", 57, event);
            addSound(oboes, "oboe", 59, event);
            addSound(oboes, "oboe", 60, event);
            addSound(oboes, "oboe", 62, event);
            addSound(oboes, "oboe", 64, event);
            addSound(oboes, "oboe", 65, event);
            addSound(oboes, "oboe", 71, event);

            addSound(redstone_guitars, "redstone_guitar", 27, event);
            addSound(redstone_guitars, "redstone_guitar", 33, event);
            addSound(redstone_guitars, "redstone_guitar", 39, event);
            addSound(redstone_guitars, "redstone_guitar", 45, event);
            addSound(redstone_guitars, "redstone_guitar", 51, event);
            addSound(redstone_guitars, "redstone_guitar", 57, event);
            addSound(redstone_guitars, "redstone_guitar", 63, event);
            addSound(redstone_guitars, "redstone_guitar", 69, event);
            addSound(redstone_guitars, "redstone_guitar", 75, event);
            addSound(redstone_guitars, "redstone_guitar", 81, event);
            addSound(redstone_guitars, "redstone_guitar", 87, event);

            addSound(french_horns, "french_horn", 27, event);
            addSound(french_horns, "french_horn", 33, event);
            addSound(french_horns, "french_horn", 39, event);
            addSound(french_horns, "french_horn", 45, event);
            addSound(french_horns, "french_horn", 51, event);
            addSound(french_horns, "french_horn", 57, event);
            addSound(french_horns, "french_horn", 63, event);
            addSound(french_horns, "french_horn", 69, event);
            addSound(french_horns, "french_horn", 75, event);
            addSound(french_horns, "french_horn", 81, event);
            addSound(french_horns, "french_horn", 87, event);

            addSound(bass_guitars, "bass_guitar", 33, event);
            addSound(bass_guitars, "bass_guitar", 39, event);
            addSound(bass_guitars, "bass_guitar", 45, event);
            addSound(bass_guitars, "bass_guitar", 51, event);
            addSound(bass_guitars, "bass_guitar", 57, event);
            addSound(bass_guitars, "bass_guitar", 63, event);
            addSound(bass_guitars, "bass_guitar", 69, event);
            addSound(bass_guitars, "bass_guitar", 75, event);
        }
    }

    public static void setup() {
        // Instrument SoundEvent setting
        ((IItemInstrument)Items.CYMBAL.get()).setSounds(cymbals);
        ((IItemInstrument)Items.DRUM_KIT.get()).setSounds(drum_kits);
        ((IItemInstrument)Items.GUITAR.get()).setSounds(guitars);
        ((IItemInstrument)Items.LYRE.get()).setSounds(lyres);
        ((IItemInstrument)Items.DRUM.get()).setSounds(drums);
        ((IItemInstrument)Items.FLUTE.get()).setSounds(flutes);
        ((IItemInstrument)Items.BANJO.get()).setSounds(banjos);
        ((IItemInstrument)Items.SAXOPHONE.get()).setSounds(saxophones);
        ((IItemInstrument)Items.GOD.get()).setSounds(gods);
        ((IItemInstrument)Items.SANSULA.get()).setSounds(sansulas);
        ((IItemInstrument)Items.TUBULAR_BELL.get()).setSounds(tubular_bells);
        ((IItemInstrument)Items.VIOLIN.get()).setSounds(violins);
        ((IItemInstrument)Items.XYLOPHONE.get()).setSounds(xylophones);
        ((IItemInstrument)Items.CELLO.get()).setSounds(cellos);
        ((IItemInstrument)Items.PIANO.get()).setSounds(pianos);
        ((IItemInstrument)Items.OBOE.get()).setSounds(oboes);
        ((IItemInstrument)Items.REDSTONE_GUITAR.get()).setSounds(redstone_guitars);
        ((IItemInstrument)Items.FRENCH_HORN.get()).setSounds(french_horns);
        ((IItemInstrument)Items.BASS_GUITAR.get()).setSounds(bass_guitars);

        Items.HARP_MC.get().setSounds(harp_mcs);
    }
}