package xerca.xercamod.common;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.data.BlockTags;
import xerca.xercamod.common.entity.Entities;
import xerca.xercamod.common.entity.EntityConfettiBall;
import xerca.xercamod.common.entity.EntityTomato;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.packets.*;
import xerca.xercamod.common.tile_entity.TileEntities;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Mod(XercaMod.MODID)
public class XercaMod {
    public static final String MODID = "xercamod";
    public static final String NAME = "Xerca Mod";

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel NETWORK_HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(XercaMod.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLMS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> RICE_SEEDS = GLMS.register("rice_seeds", SeedLootModifier::makeCodec);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TOMATO_SEEDS = GLMS.register("tomato_seeds", SeedLootModifier::makeCodec);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TEA_SEEDS = GLMS.register("tea_seeds", SeedLootModifier::makeCodec);

    public XercaMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("xercamod-common.toml"));
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Entities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TileEntities.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TileEntities.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SoundEvents.SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        GLMS.register(FMLJavaModLoadingContext.get().getModEventBus());
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
        event.enqueueWork(()->{
            networkRegistry();

            registerTriggers();

            // Making confetti ball dispensable by dispenser
            DispenserBlock.registerBehavior(Items.ITEM_CONFETTI_BALL.get(), new AbstractProjectileDispenseBehavior()
            {
                @Nonnull
                protected Projectile getProjectile(@Nonnull Level worldIn, @Nonnull Position position, @Nonnull ItemStack stackIn)
                {
                    return new EntityConfettiBall(worldIn, position.x(), position.y(), position.z());
                }
            });
            // Making tomato dispensable by dispenser
            DispenserBlock.registerBehavior(Items.ITEM_TOMATO.get(), new AbstractProjectileDispenseBehavior()
            {
                @Nonnull
                protected Projectile getProjectile(@Nonnull Level worldIn, @Nonnull Position position, @Nonnull ItemStack stackIn)
                {
                    return new EntityTomato(worldIn, position.x(), position.y(), position.z());
                }
            });

            // Making confetti dispensable by dispenser
            DispenserBlock.registerBehavior(Items.ITEM_CONFETTI.get(), new ConfettiDispenseItemBehavior());

            Items.registerCompostables();
            DecoCreativeTab.initItemList();
            registerPotions(event);
            Entities.setup();
        });
    }

    private void registerPotions(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(
                    Ingredient.of(PotionUtils.setPotion(new ItemStack(net.minecraft.world.item.Items.POTION), Potions.WATER)),
                    Ingredient.of(new ItemStack(Items.COLA_POWDER.get())),
                    new ItemStack(Items.COLA_EXTRACT.get()));
        });
    }

    private void registerTriggers() {
        for (int i = 0; i < Triggers.TRIGGER_ARRAY.length; i++) {
            CriteriaTriggers.register(Triggers.TRIGGER_ARRAY[i]);
        }

        CriteriaTriggers.register(Triggers.CONFIG_CHECK);
    }

    private void processIMC(final InterModProcessEvent event)
    {
        List<InterModComms.IMCMessage> list = event.getIMCStream().collect(Collectors.toList());//.map(m->m.getMessageSupplier().get())
        LOGGER.debug("Got IMC {}", list);
        for(InterModComms.IMCMessage msg : list) {
            if(msg.getMethod().equals("send_note")) {
                Object result = msg.getMessageSupplier().get();
                if(result instanceof ResourceLocation itemNoteResourceLocation){
                    ContainerFunctionalBookcase.acceptedItems.add(itemNoteResourceLocation);
                }
                else {
                    LOGGER.error("send_note from XercaMusic failed to sent a ResourceLocation");
                }
            }
        }
    }
    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerDataEvent(final GatherDataEvent event) {
            event.getGenerator().addProvider(event.includeServer(), new BlockTags(event.getGenerator(), event.getExistingFileHelper()));
        }
    }
}
