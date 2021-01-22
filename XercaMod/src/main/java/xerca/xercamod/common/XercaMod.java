package xerca.xercamod.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercamod.client.ClientProxy;
import xerca.xercamod.common.entity.EntityConfettiBall;
import xerca.xercamod.common.entity.EntityTomato;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.packets.*;
import xerca.xercamod.server.ServerProxy;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Mod(XercaMod.MODID)
public class XercaMod {
    public static final String MODID = "xercamod";
    public static final String NAME = "Xerca Mod";

    public static Proxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(XercaMod.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static final Logger LOGGER = LogManager.getLogger();

    public XercaMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("xercamod-common.toml"));
    }

    private void networkRegistry(){
        int msg_id = 0;
        NETWORK_HANDLER.registerMessage(msg_id++, HammerAttackPacket.class, HammerAttackPacket::encode, HammerAttackPacket::decode, HammerAttackPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, KnifeAttackPacket.class, KnifeAttackPacket::encode, KnifeAttackPacket::decode, KnifeAttackPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, HammerQuakePacket.class, HammerQuakePacket::encode, HammerQuakePacket::decode, HammerQuakePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ConfettiParticlePacket.class, ConfettiParticlePacket::encode, ConfettiParticlePacket::decode, ConfettiParticlePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, QuakeParticlePacket.class, QuakeParticlePacket::encode, QuakeParticlePacket::decode, QuakeParticlePacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ConfigSyncPacket.class, ConfigSyncPacket::encode, ConfigSyncPacket::decode, ConfigSyncPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id++, ScytheAttackPacket.class, ScytheAttackPacket::encode, ScytheAttackPacket::decode, ScytheAttackPacketHandler::handle);
        NETWORK_HANDLER.registerMessage(msg_id, BeheadParticlePacket.class, BeheadParticlePacket::encode, BeheadParticlePacket::decode, BeheadParticlePacketHandler::handle);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        networkRegistry();

        registerTriggers();

        // Making confetti ball dispensable by dispenser
        DispenserBlock.registerDispenseBehavior(Items.ITEM_CONFETTI_BALL, new ProjectileDispenseBehavior()
        {
            @Nonnull
            protected ProjectileEntity getProjectileEntity(@Nonnull World worldIn, @Nonnull IPosition position, @Nonnull ItemStack stackIn)
            {
                return new EntityConfettiBall(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        // Making tomato dispensable by dispenser
        DispenserBlock.registerDispenseBehavior(Items.ITEM_TOMATO, new ProjectileDispenseBehavior()
        {
            @Nonnull
            protected ProjectileEntity getProjectileEntity(@Nonnull World worldIn, @Nonnull IPosition position, @Nonnull ItemStack stackIn)
            {
                return new EntityTomato(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });

        // Making confetti dispensable by dispenser
        DispenserBlock.registerDispenseBehavior(Items.ITEM_CONFETTI, new ConfettiDispenseItemBehavior());

        Items.registerCompostables();
        DecoCreativeTab.initItemList();
    }

    private void registerTriggers() {
        Method method;
        method = ObfuscationReflectionHelper.findMethod(CriteriaTriggers.class, "func_192118_a", ICriterionTrigger.class);
        method.setAccessible(true);

        for (int i = 0; i < Triggers.TRIGGER_ARRAY.length; i++)
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


    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
        List<InterModComms.IMCMessage> list = event.getIMCStream().collect(Collectors.toList());//.map(m->m.getMessageSupplier().get())
        LOGGER.debug("Got IMC {}", list);
        for(InterModComms.IMCMessage msg : list){
            if(msg.getMethod().equals("send_note")){
                Object result = msg.getMessageSupplier().get();
                if(result instanceof ResourceLocation){
                    ResourceLocation itemNoteResourceLocation = (ResourceLocation) result;
                    ContainerFunctionalBookcase.acceptedItems.add(itemNoteResourceLocation);
                }
                else{
                    LOGGER.error("send_note from XercaMusic failed to sent a ResourceLocation");
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerLootModifiers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().register(new SeedLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"rice_seeds")));
            event.getRegistry().register(new SeedLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"tomato_seeds")));
            event.getRegistry().register(new SeedLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"tea_seeds")));
        }
    }
}
