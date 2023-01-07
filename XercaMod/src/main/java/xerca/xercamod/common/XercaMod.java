package xerca.xercamod.common;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CreativeModeTabs;
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
import net.minecraftforge.event.CreativeModeTabEvent;
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
            event.getGenerator().addProvider(event.includeServer(), new BlockTags(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        }

        @SubscribeEvent
        public static void buildContents(CreativeModeTabEvent.BuildContents event) {
            if (event.getTab() == CreativeModeTabs.FOOD_AND_DRINKS) {
                if(Config.isFoodEnabled()) {
                    event.accept(Items.ITEM_TOMATO);
                    event.accept(Items.RAW_SHISH_KEBAB);
                    event.accept(Items.ITEM_SHISH_KEBAB);
                    event.accept(Items.ITEM_YOGHURT);
                    event.accept(Items.ITEM_HONEYBERRY_YOGHURT);
                    event.accept(Items.ITEM_HONEY_CUPCAKE);
                    event.accept(Items.ITEM_DONER_WRAP);
                    event.accept(Items.ITEM_CHUBBY_DONER);
                    event.accept(Items.ITEM_ALEXANDER);
                    event.accept(Items.ITEM_AYRAN);
                    event.accept(Items.DONER_SLICE);
                    event.accept(Items.BAKED_RICE_PUDDING);
                    event.accept(Items.SWEET_BERRY_JUICE);
                    event.accept(Items.RICE_PUDDING);
                    event.accept(Items.SWEET_BERRY_CUPCAKE_FANCY);
                    event.accept(Items.SWEET_BERRY_CUPCAKE);
                    event.accept(Items.ENDER_CUPCAKE);
                    event.accept(Items.SASHIMI);
                    event.accept(Items.OYAKODON);
                    event.accept(Items.BEEF_DONBURI);
                    event.accept(Items.EGG_SUSHI);
                    event.accept(Items.NIGIRI_SUSHI);
                    event.accept(Items.OMURICE);
                    event.accept(Items.SAKE);
                    event.accept(Items.RICEBALL);
                    event.accept(Items.SUSHI);
                    event.accept(Items.COOKED_RICE);
                    event.accept(Items.COLA);
                    event.accept(Items.ITEM_APPLE_CUPCAKE);
                    event.accept(Items.ITEM_PUMPKIN_CUPCAKE);
                    event.accept(Items.ITEM_COCOA_CUPCAKE);
                    event.accept(Items.ITEM_MELON_CUPCAKE);
                    event.accept(Items.ITEM_CARROT_CUPCAKE);
                    event.accept(Items.ITEM_FANCY_APPLE_CUPCAKE);
                    event.accept(Items.ITEM_FANCY_PUMPKIN_CUPCAKE);
                    event.accept(Items.GLOWBERRY_CUPCAKE);
                    event.accept(Items.ITEM_DONUT);
                    event.accept(Items.ITEM_FANCY_DONUT);
                    event.accept(Items.ITEM_SPRINKLES);
                    event.accept(Items.ITEM_CHOCOLATE);
                    event.accept(Items.ITEM_BUN);
                    event.accept(Items.ITEM_RAW_PATTY);
                    event.accept(Items.ITEM_COOKED_PATTY);
                    event.accept(Items.ITEM_RAW_CHICKEN_PATTY);
                    event.accept(Items.ITEM_COOKED_CHICKEN_PATTY);
                    event.accept(Items.ITEM_HAMBURGER);
                    event.accept(Items.ITEM_CHICKEN_BURGER);
                    event.accept(Items.ITEM_MUSHROOM_BURGER);
                    event.accept(Items.ITEM_ULTIMATE_BOTTOM);
                    event.accept(Items.ITEM_ULTIMATE_TOP);
                    event.accept(Items.CHEESEBURGER);
                    event.accept(Items.COLA_EXTRACT);
                    event.accept(Items.COLA_POWDER);
                    event.accept(Items.CARBONATED_WATER);
                    event.accept(Items.ITEM_ULTIMATE_BURGER);
                    event.accept(Items.ITEM_ROTTEN_BURGER);
                    event.accept(Items.ITEM_COOKED_SAUSAGE);
                    event.accept(Items.ITEM_HOTDOG);
                    event.accept(Items.ITEM_FISH_BREAD);
                    event.accept(Items.ITEM_DAISY_SANDWICH);
                    event.accept(Items.ITEM_CHICKEN_WRAP);
                    event.accept(Items.ITEM_RAW_SCHNITZEL);
                    event.accept(Items.ITEM_COOKED_SCHNITZEL);
                    event.accept(Items.ITEM_FRIED_EGG);
                    event.accept(Items.ITEM_CROISSANT);
                    event.accept(Items.ITEM_POTATO_FRIES);
                    event.accept(Items.ITEM_ICE_TEA);
                    event.accept(Items.ITEM_APPLE_JUICE);
                    event.accept(Items.ITEM_CARROT_JUICE);
                    event.accept(Items.ITEM_MELON_JUICE);
                    event.accept(Items.ITEM_PUMPKIN_JUICE);
                    event.accept(Items.ITEM_TOMATO_JUICE);
                    event.accept(Items.ITEM_WHEAT_JUICE);
                    event.accept(Items.ITEM_GLASS_OF_MILK);
                    event.accept(Items.ITEM_GLASS_OF_WATER);
                    event.accept(Items.SODA);
                    event.accept(Items.CHEESE_TOAST);
                    event.accept(Items.SQUID_INK_PAELLA);
                    event.accept(Items.GLOW_SQUID_INK_PAELLA);
                    event.accept(Items.ITEM_APPLE_PIE);
                    event.accept(Items.SWEET_BERRY_PIE);
                    event.accept(Items.RAW_PIZZAS.get(Items.RAW_PIZZAS.size() - 1));
                    event.accept(Items.PIZZAS.get(Items.PIZZAS.size() - 1));
                    event.accept(Items.ITEM_TOMATO_SLICES);
                    event.accept(Items.ITEM_POTATO_SLICES);
                    event.accept(Items.ITEM_RAW_SAUSAGE);
                    event.accept(Items.CHEESE_WHEEL);
                    event.accept(Items.CHEESE_SLICE);
                }
                if(Config.isTeaEnabled()) {
                    event.accept(Items.ITEM_TEA_LEAF);
                    event.accept(Items.ITEM_TEA_DRIED);
                    event.accept(Items.ITEM_FULL_TEACUP_0);
                    event.accept(Items.ITEM_HOT_TEAPOT_7);
                }
            }
            else if (event.getTab() == CreativeModeTabs.COMBAT) {
                if(Config.isWarhammerEnabled()) {
                    event.accept(Items.ITEM_NETHERITE_WARHAMMER);
                    event.accept(Items.ITEM_DIAMOND_WARHAMMER);
                    event.accept(Items.ITEM_GOLD_WARHAMMER);
                    event.accept(Items.ITEM_IRON_WARHAMMER);
                    event.accept(Items.ITEM_STONE_WARHAMMER);
                }
                if(Config.isScytheEnabled()) {
                    event.accept(Items.NETHERITE_SCYTHE);
                    event.accept(Items.DIAMOND_SCYTHE);
                    event.accept(Items.GOLDEN_SCYTHE);
                    event.accept(Items.IRON_SCYTHE);
                    event.accept(Items.STONE_SCYTHE);
                }
                event.accept(Items.ITEM_KNIFE);
                if(Config.isGrabHookEnabled()) {
                    event.accept(Items.ITEM_GRAB_HOOK);
                }
                if(Config.isEnderFlaskEnabled()) {
                    event.accept(Items.ENDER_BOW);
                }
            }
            else if (event.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
                if(Config.isFoodEnabled()) {
                    event.accept(Items.ITEM_TOMATO_SEEDS);
                    event.accept(Items.ITEM_RICE_SEEDS);
                    event.accept(Items.ITEM_TEA_SEEDS);
                }
            }
            else if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                if(Config.isFoodEnabled()) {
                    event.accept(Items.VAT);
                }
                if(Config.isRopeEnabled()) {
                    event.accept(Items.ROPE);
                }
                if(Config.isBookcaseEnabled()) {
                    event.accept(Items.ITEM_BOOKCASE);
                }
                if(Config.isCarvedWoodEnabled()) {
                    event.accept(Items.CARVING_STATION);
                }
                if(Config.isOmniChestEnabled()) {
                    event.accept(Items.OMNI_CHEST);
                }
            }
            else if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(Items.ITEM_KNIFE);
                if(Config.isGrabHookEnabled()) {
                    event.accept(Items.ITEM_GRAB_HOOK);
                }
                if(Config.isEnderFlaskEnabled()) {
                    event.accept(Items.FLASK);
                    event.accept(Items.ENDER_BOW);
                }
                if(Config.isConfettiEnabled()) {
                    event.accept(Items.ITEM_CONFETTI_BALL);
                    event.accept(Items.ITEM_CONFETTI);
                }
                if(Config.isCoinsEnabled()) {
                    event.accept(Items.ITEM_GOLDEN_COIN_1);
                    event.accept(Items.ITEM_GOLDEN_COIN_5);
                }
                if(Config.isCourtroomEnabled()) {
                    event.accept(Items.ITEM_GAVEL);
                    event.accept(Items.ITEM_PROSECUTOR_BADGE);
                    event.accept(Items.ITEM_ATTORNEY_BADGE);
                }
            }
            else if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
                if(Config.isFoodEnabled()) {
                    event.accept(Items.ITEM_GLASS);
                }
                if(Config.isTeaEnabled()) {
                    event.accept(Items.ITEM_TEACUP);
                    event.accept(Items.ITEM_TEAPOT);
                }
            }
            else if (event.getTab() == CreativeModeTabs.COLORED_BLOCKS) {
                if(Config.isTerracottaTileEnabled()) {
                    event.accept(Items.BLACK_TERRATILE);
                    event.accept(Items.BLUE_TERRATILE);
                    event.accept(Items.BROWN_TERRATILE);
                    event.accept(Items.CYAN_TERRATILE);
                    event.accept(Items.GRAY_TERRATILE);
                    event.accept(Items.GREEN_TERRATILE);
                    event.accept(Items.LIGHT_BLUE_TERRATILE);
                    event.accept(Items.LIGHT_GRAY_TERRATILE);
                    event.accept(Items.LIME_TERRATILE);
                    event.accept(Items.MAGENTA_TERRATILE);
                    event.accept(Items.ORANGE_TERRATILE);
                    event.accept(Items.PINK_TERRATILE);
                    event.accept(Items.PURPLE_TERRATILE);
                    event.accept(Items.RED_TERRATILE);
                    event.accept(Items.WHITE_TERRATILE);
                    event.accept(Items.YELLOW_TERRATILE);
                    event.accept(Items.TERRATILE);
                    event.accept(Items.BLACK_TERRATILE_SLAB);
                    event.accept(Items.BLUE_TERRATILE_SLAB);
                    event.accept(Items.BROWN_TERRATILE_SLAB);
                    event.accept(Items.CYAN_TERRATILE_SLAB);
                    event.accept(Items.GRAY_TERRATILE_SLAB);
                    event.accept(Items.GREEN_TERRATILE_SLAB);
                    event.accept(Items.LIGHT_BLUE_TERRATILE_SLAB);
                    event.accept(Items.LIGHT_GRAY_TERRATILE_SLAB);
                    event.accept(Items.LIME_TERRATILE_SLAB);
                    event.accept(Items.MAGENTA_TERRATILE_SLAB);
                    event.accept(Items.ORANGE_TERRATILE_SLAB);
                    event.accept(Items.PINK_TERRATILE_SLAB);
                    event.accept(Items.PURPLE_TERRATILE_SLAB);
                    event.accept(Items.RED_TERRATILE_SLAB);
                    event.accept(Items.WHITE_TERRATILE_SLAB);
                    event.accept(Items.YELLOW_TERRATILE_SLAB);
                    event.accept(Items.TERRATILE_SLAB);
                    event.accept(Items.BLACK_TERRATILE_STAIRS);
                    event.accept(Items.BLUE_TERRATILE_STAIRS);
                    event.accept(Items.BROWN_TERRATILE_STAIRS);
                    event.accept(Items.CYAN_TERRATILE_STAIRS);
                    event.accept(Items.GRAY_TERRATILE_STAIRS);
                    event.accept(Items.GREEN_TERRATILE_STAIRS);
                    event.accept(Items.LIGHT_BLUE_TERRATILE_STAIRS);
                    event.accept(Items.LIGHT_GRAY_TERRATILE_STAIRS);
                    event.accept(Items.LIME_TERRATILE_STAIRS);
                    event.accept(Items.MAGENTA_TERRATILE_STAIRS);
                    event.accept(Items.ORANGE_TERRATILE_STAIRS);
                    event.accept(Items.PINK_TERRATILE_STAIRS);
                    event.accept(Items.PURPLE_TERRATILE_STAIRS);
                    event.accept(Items.RED_TERRATILE_STAIRS);
                    event.accept(Items.WHITE_TERRATILE_STAIRS);
                    event.accept(Items.YELLOW_TERRATILE_STAIRS);
                    event.accept(Items.TERRATILE_STAIRS);
                }
                if(Config.isCushionEnabled()) {
                    event.accept(Items.BLACK_CUSHION);
                    event.accept(Items.BLUE_CUSHION);
                    event.accept(Items.BROWN_CUSHION);
                    event.accept(Items.CYAN_CUSHION);
                    event.accept(Items.GRAY_CUSHION);
                    event.accept(Items.GREEN_CUSHION);
                    event.accept(Items.LIGHT_BLUE_CUSHION);
                    event.accept(Items.LIGHT_GRAY_CUSHION);
                    event.accept(Items.LIME_CUSHION);
                    event.accept(Items.MAGENTA_CUSHION);
                    event.accept(Items.ORANGE_CUSHION);
                    event.accept(Items.PINK_CUSHION);
                    event.accept(Items.PURPLE_CUSHION);
                    event.accept(Items.RED_CUSHION);
                    event.accept(Items.WHITE_CUSHION);
                    event.accept(Items.YELLOW_CUSHION);
                }
            }
            else if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
                if(Config.isLeatherStrawEnabled()) {
                    event.accept(Items.ITEM_BLOCK_LEATHER);
                    event.accept(Items.ITEM_BLOCK_STRAW);
                }
                if(Config.isCarvedWoodEnabled()) {
                    event.accept(Items.CARVED_OAK_1);
                    event.accept(Items.CARVED_OAK_2);
                    event.accept(Items.CARVED_OAK_3);
                    event.accept(Items.CARVED_OAK_4);
                    event.accept(Items.CARVED_OAK_5);
                    event.accept(Items.CARVED_OAK_6);
                    event.accept(Items.CARVED_OAK_7);
                    event.accept(Items.CARVED_OAK_8);
                    event.accept(Items.CARVED_BIRCH_1);
                    event.accept(Items.CARVED_BIRCH_2);
                    event.accept(Items.CARVED_BIRCH_3);
                    event.accept(Items.CARVED_BIRCH_4);
                    event.accept(Items.CARVED_BIRCH_5);
                    event.accept(Items.CARVED_BIRCH_6);
                    event.accept(Items.CARVED_BIRCH_7);
                    event.accept(Items.CARVED_BIRCH_8);
                    event.accept(Items.CARVED_DARK_OAK_1);
                    event.accept(Items.CARVED_DARK_OAK_2);
                    event.accept(Items.CARVED_DARK_OAK_3);
                    event.accept(Items.CARVED_DARK_OAK_4);
                    event.accept(Items.CARVED_DARK_OAK_5);
                    event.accept(Items.CARVED_DARK_OAK_6);
                    event.accept(Items.CARVED_DARK_OAK_7);
                    event.accept(Items.CARVED_DARK_OAK_8);
                    event.accept(Items.CARVED_ACACIA_1);
                    event.accept(Items.CARVED_ACACIA_2);
                    event.accept(Items.CARVED_ACACIA_3);
                    event.accept(Items.CARVED_ACACIA_4);
                    event.accept(Items.CARVED_ACACIA_5);
                    event.accept(Items.CARVED_ACACIA_6);
                    event.accept(Items.CARVED_ACACIA_7);
                    event.accept(Items.CARVED_ACACIA_8);
                    event.accept(Items.CARVED_JUNGLE_1);
                    event.accept(Items.CARVED_JUNGLE_2);
                    event.accept(Items.CARVED_JUNGLE_3);
                    event.accept(Items.CARVED_JUNGLE_4);
                    event.accept(Items.CARVED_JUNGLE_5);
                    event.accept(Items.CARVED_JUNGLE_6);
                    event.accept(Items.CARVED_JUNGLE_7);
                    event.accept(Items.CARVED_JUNGLE_8);
                    event.accept(Items.CARVED_SPRUCE_1);
                    event.accept(Items.CARVED_SPRUCE_2);
                    event.accept(Items.CARVED_SPRUCE_3);
                    event.accept(Items.CARVED_SPRUCE_4);
                    event.accept(Items.CARVED_SPRUCE_5);
                    event.accept(Items.CARVED_SPRUCE_6);
                    event.accept(Items.CARVED_SPRUCE_7);
                    event.accept(Items.CARVED_SPRUCE_8);
                    event.accept(Items.CARVED_CRIMSON_1);
                    event.accept(Items.CARVED_CRIMSON_2);
                    event.accept(Items.CARVED_CRIMSON_3);
                    event.accept(Items.CARVED_CRIMSON_4);
                    event.accept(Items.CARVED_CRIMSON_5);
                    event.accept(Items.CARVED_CRIMSON_6);
                    event.accept(Items.CARVED_CRIMSON_7);
                    event.accept(Items.CARVED_CRIMSON_8);
                    event.accept(Items.CARVED_WARPED_1);
                    event.accept(Items.CARVED_WARPED_2);
                    event.accept(Items.CARVED_WARPED_3);
                    event.accept(Items.CARVED_WARPED_4);
                    event.accept(Items.CARVED_WARPED_5);
                    event.accept(Items.CARVED_WARPED_6);
                    event.accept(Items.CARVED_WARPED_7);
                    event.accept(Items.CARVED_WARPED_8);
                }
            }
        }
    }
}
