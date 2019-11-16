package xerca.xercamusic.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercamusic.client.ClientProxy;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.*;
import xerca.xercamusic.server.ServerProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;


@Mod(XercaMusic.MODID)
public class XercaMusic
{
    public static final String MODID = "xercamusic";
    public static final Logger LOGGER = LogManager.getLogger();
    public static Proxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();


    private void networkRegistry(){
        int msg_id = 0;
        NETWORK_HANDLER.registerMessage(msg_id++, MusicUpdatePacket.class, MusicUpdatePacket::encode, MusicUpdatePacket::decode, MusicUpdatePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, MusicEndedPacket.class, MusicEndedPacket::encode, MusicEndedPacket::decode, MusicEndedPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id, MusicBoxUpdatePacket.class, MusicBoxUpdatePacket::encode, MusicBoxUpdatePacket::decode, MusicBoxUpdatePacketHandler::handle);
    }

    public XercaMusic() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        networkRegistry();
        proxy.preInit();

        Items.setup();

        proxy.init();

        Blocks.setup();
        registerTriggers();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Send music sheet's resource location to xercamod for the bookcase interaction
        InterModComms.sendTo("xercamod", "send_note", () ->  new ResourceLocation(MODID, "music_sheet"));
    }

    private void processIMC(final InterModProcessEvent event)
    {
        LOGGER.debug("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    private void registerTriggers() {
        Method method;
        method = ObfuscationReflectionHelper.findMethod(CriteriaTriggers.class, "func_192118_a", ICriterionTrigger.class);
        method.setAccessible(true);

        for (int i=0; i < Triggers.TRIGGER_ARRAY.length; i++)
        {
            try
            {
                method.invoke(null, Triggers.TRIGGER_ARRAY[i]);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
    }

    // This is for conveniently initializing object holders without annoying the IDE
    @SuppressWarnings({"ConstantConditions", "SameReturnValue"})
    public static <T> T Null() {
        return null;
    }

    /// Remapping events for migrating saves from the old mod

    @SubscribeEvent
    public void remapBlocks(final RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> miss : event.getAllMappings()) {
            XercaMusic.LOGGER.info("Missing block entry found: " + miss.key);
            if(miss.key.toString().equals("xercamod:block_metronome")){
                miss.remap(Blocks.BLOCK_METRONOME);
            }
            else if(miss.key.toString().equals("xercamod:music_box")){
                miss.remap(Blocks.MUSIC_BOX);
            }
        }
    }

    @SubscribeEvent
    public void remapItems(final RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> miss : event.getAllMappings()) {
            XercaMusic.LOGGER.info("Missing item entry found: " + miss.key);
            if(miss.key.toString().equals("xercamod:item_metronome")){
                miss.remap(Blocks.BLOCK_METRONOME.asItem());
            }
            else if(miss.key.toString().equals("xercamod:music_box")){
                miss.remap(Blocks.MUSIC_BOX.asItem());
            }
            else if(miss.key.toString().equals("xercamod:item_note")){
                miss.remap(Items.MUSIC_SHEET);
            }

            else if(miss.key.toString().equals("xercamod:item_guitar")){
                miss.remap(Items.GUITAR);
            }
            else if(miss.key.toString().equals("xercamod:item_banjo")){
                miss.remap(Items.BANJO);
            }
            else if(miss.key.toString().equals("xercamod:item_drum")){
                miss.remap(Items.DRUM);
            }
            else if(miss.key.toString().equals("xercamod:item_flute")){
                miss.remap(Items.FLUTE);
            }
            else if(miss.key.toString().equals("xercamod:item_god")){
                miss.remap(Items.GOD);
            }
            else if(miss.key.toString().equals("xercamod:item_lyre")){
                miss.remap(Items.LYRE);
            }
            else if(miss.key.toString().equals("xercamod:item_sansula")){
                miss.remap(Items.SANSULA);
            }
            else if(miss.key.toString().equals("xercamod:item_saxophone")){
                miss.remap(Items.SAXOPHONE);
            }
            else if(miss.key.toString().equals("xercamod:item_tubular_bell")){
                miss.remap(Items.TUBULAR_BELL);
            }
            else if(miss.key.toString().equals("xercamod:item_violin")){
                miss.remap(Items.VIOLIN);
            }
            else if(miss.key.toString().equals("xercamod:item_xylophone")){
                miss.remap(Items.XYLOPHONE);
            }

            if(miss.key.toString().equals("xercamusic:item_metronome")){
                miss.remap(Blocks.BLOCK_METRONOME.asItem());
            }
            else if(miss.key.toString().equals("xercamusic:item_note")){
                miss.remap(Items.MUSIC_SHEET);
            }
            else if(miss.key.toString().equals("xercamusic:item_guitar")){
                miss.remap(Items.GUITAR);
            }
            else if(miss.key.toString().equals("xercamusic:item_banjo")){
                miss.remap(Items.BANJO);
            }
            else if(miss.key.toString().equals("xercamusic:item_drum")){
                miss.remap(Items.DRUM);
            }
            else if(miss.key.toString().equals("xercamusic:item_flute")){
                miss.remap(Items.FLUTE);
            }
            else if(miss.key.toString().equals("xercamusic:item_god")){
                miss.remap(Items.GOD);
            }
            else if(miss.key.toString().equals("xercamusic:item_lyre")){
                miss.remap(Items.LYRE);
            }
            else if(miss.key.toString().equals("xercamusic:item_sansula")){
                miss.remap(Items.SANSULA);
            }
            else if(miss.key.toString().equals("xercamusic:item_saxophone")){
                miss.remap(Items.SAXOPHONE);
            }
            else if(miss.key.toString().equals("xercamusic:item_tubular_bell")){
                miss.remap(Items.TUBULAR_BELL);
            }
            else if(miss.key.toString().equals("xercamusic:item_violin")){
                miss.remap(Items.VIOLIN);
            }
            else if(miss.key.toString().equals("xercamusic:item_xylophone")){
                miss.remap(Items.XYLOPHONE);
            }
        }
    }

    @SubscribeEvent
    public void remapEntities(final RegistryEvent.MissingMappings<EntityType<?>> event) {
        for (RegistryEvent.MissingMappings.Mapping<EntityType<?>> miss : event.getAllMappings()) {
            XercaMusic.LOGGER.info("Missing entity entry found: " + miss.key);
            if(miss.key.toString().equals("xercamod:music_spirit")){
                miss.remap(Entities.MUSIC_SPIRIT);
            }
        }
    }

    @SubscribeEvent
    public void remapSoundEvents(final RegistryEvent.MissingMappings<SoundEvent> event) {
        for (RegistryEvent.MissingMappings.Mapping<SoundEvent> miss : event.getAllMappings()) {
            if(miss.key.getNamespace().equals("xercamod")){
                if(miss.key.getPath().equals("metronome_set")){
                    miss.remap(SoundEvents.METRONOME_SET);
                }
                else if(miss.key.getPath().equals("tick")){
                    miss.remap(SoundEvents.TICK);
                }
                else{
                    try{
                        String[] part = miss.key.getPath().split("(?<=\\D)(?=\\d)");
                        switch (part[0]) {
                            case "banjo":
                                miss.remap(SoundEvents.banjos[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "guitar":
                                miss.remap(SoundEvents.guitars[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "drum":
                                miss.remap(SoundEvents.drums[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "lyre":
                                miss.remap(SoundEvents.lyres[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "flute":
                                miss.remap(SoundEvents.flutes[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "saxophone":
                                miss.remap(SoundEvents.saxophones[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "god":
                                miss.remap(SoundEvents.gods[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "harp_mc":
                                miss.remap(SoundEvents.harp_mcs[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "sansula":
                                miss.remap(SoundEvents.sansulas[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "tubular_bell":
                                miss.remap(SoundEvents.tubular_bells[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "violin":
                                miss.remap(SoundEvents.violins[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "xylophone":
                                miss.remap(SoundEvents.xylophones[Integer.parseInt(part[1]) - 1]);
                                break;
                            case "cello":
                                miss.remap(SoundEvents.cellos[Integer.parseInt(part[1]) - 1]);
                                break;
                        }
                    }
                    catch(PatternSyntaxException ignored){}
                }
            }
        }
    }
}
