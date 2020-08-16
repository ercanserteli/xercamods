package xerca.xercamusic.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamusic.common.item.Items;

@ObjectHolder(XercaMusic.MODID)
public class SoundEvents {
    public static final SoundEvent TICK = null;
    public static final SoundEvent METRONOME_SET = null;

    // Instrument SoundEvents declaration
    public static SoundEvent[] cymbals;
    public static SoundEvent[] drum_kits;
    public static SoundEvent[] guitars;
    public static SoundEvent[] lyres;
    public static SoundEvent[] drums;
    public static SoundEvent[] flutes;
    public static SoundEvent[] banjos;
    public static SoundEvent[] saxophones;
    public static SoundEvent[] gods;
    public static SoundEvent[] harp_mcs;
    public static SoundEvent[] sansulas;
    public static SoundEvent[] tubular_bells;
    public static SoundEvent[] violins;
    public static SoundEvent[] xylophones;
    public static SoundEvent[] cellos;
    public static SoundEvent[] pianos;

    /**
     * Register a {@link SoundEvent}.
     *
     * @param soundName The SoundEvent's name without the testmod3 prefix
     * @return The SoundEvent
     */
    private static SoundEvent createSoundEvent(String soundName) {
        final ResourceLocation soundID = new ResourceLocation(XercaMusic.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
            event.getRegistry().registerAll(
                    createSoundEvent("tick"),
                    createSoundEvent("metronome_set")
            );

            // Instrument SoundEvent initialization
            cymbals = new SoundEvent[48];
            drum_kits = new SoundEvent[48];
            guitars = new SoundEvent[48];
            drums = new SoundEvent[48];
            lyres = new SoundEvent[48];
            flutes = new SoundEvent[48];
            banjos = new SoundEvent[48];
            saxophones = new SoundEvent[48];
            gods = new SoundEvent[48];
            harp_mcs = new SoundEvent[48];
            sansulas = new SoundEvent[48];
            tubular_bells = new SoundEvent[48];
            violins = new SoundEvent[48];
            xylophones = new SoundEvent[48];
            cellos = new SoundEvent[48];
            pianos = new SoundEvent[48];
            for (int i = 0; i < 48; i++) {
                // Instrument SoundEvent creation
                cymbals[i] = createSoundEvent("cymbal" + (i + 1));
                drum_kits[i] = createSoundEvent("drum_kit" + (i + 1));
                guitars[i] = createSoundEvent("guitar" + (i + 1));
                drums[i] = createSoundEvent("drum" + (i + 1));
                lyres[i] = createSoundEvent("lyre" + (i + 1));
                flutes[i] = createSoundEvent("flute" + (i + 1));
                banjos[i] = createSoundEvent("banjo" + (i + 1));
                saxophones[i] = createSoundEvent("saxophone" + (i + 1));
                gods[i] = createSoundEvent("god" + (i + 1));
                harp_mcs[i] = createSoundEvent("harp_mc" + (i + 1));
                sansulas[i] = createSoundEvent("sansula" + (i + 1));
                tubular_bells[i] = createSoundEvent("tubular_bell" + (i + 1));
                violins[i] = createSoundEvent("violin" + (i + 1));
                xylophones[i] = createSoundEvent("xylophone" + (i + 1));
                cellos[i] = createSoundEvent("cello" + (i + 1));
                pianos[i] = createSoundEvent("piano" + (i + 1));
            }
            // Instrument SoundEvent registration
            event.getRegistry().registerAll(cymbals);
            event.getRegistry().registerAll(drum_kits);
            event.getRegistry().registerAll(guitars);
            event.getRegistry().registerAll(lyres);
            event.getRegistry().registerAll(drums);
            event.getRegistry().registerAll(flutes);
            event.getRegistry().registerAll(banjos);
            event.getRegistry().registerAll(saxophones);
            event.getRegistry().registerAll(gods);
            event.getRegistry().registerAll(harp_mcs);
            event.getRegistry().registerAll(sansulas);
            event.getRegistry().registerAll(tubular_bells);
            event.getRegistry().registerAll(violins);
            event.getRegistry().registerAll(xylophones);
            event.getRegistry().registerAll(cellos);
            event.getRegistry().registerAll(pianos);

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
        }
    }
}