package xerca.xercamod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.DecoCreativeTab;
import xerca.xercamod.common.TeaCreativeTab;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.BlockPizza;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.crafting.*;
import xerca.xercamod.common.enchantments.*;

import javax.annotation.Nullable;
import java.util.List;

@ObjectHolder(XercaMod.MODID)
public final class Items {
    public static final ItemEnderBow ENDER_BOW = null;
    public static final Item RAW_SHISH_KEBAB = null;
    public static final Item ITEM_YOGHURT = null;
    public static final Item ITEM_HONEYBERRY_YOGHURT = null;
    public static final Item ITEM_HONEY_CUPCAKE = null;
    public static final Item ITEM_DONER_WRAP = null;
    public static final Item ITEM_CHUBBY_DONER = null;
    public static final Item ITEM_ALEXANDER = null;
    public static final Item ITEM_AYRAN = null;
    public static final Item DONER_SLICE = null;
    public static final Item BAKED_RICE_PUDDING = null;
    public static final Item SWEET_BERRY_JUICE = null;
    public static final Item RICE_PUDDING = null;
    public static final Item SWEET_BERRY_CUPCAKE_FANCY = null;
    public static final Item SWEET_BERRY_CUPCAKE = null;
    public static final Item ENDER_CUPCAKE = null;
    public static final Item SASHIMI = null;
    public static final Item OYAKODON = null;
    public static final Item BEEF_DONBURI = null;
    public static final Item EGG_SUSHI = null;
    public static final Item NIGIRI_SUSHI = null;
    public static final Item OMURICE = null;
    public static final Item SAKE = null;
    public static final Item RICEBALL = null;
    public static final Item SUSHI = null;
    public static final Item COOKED_RICE = null;
    public static final Item COLA = null;
    public static final Item COLA_EXTRACT = null;
    public static final Item COLA_POWDER = null;
    public static final Item CARBONATED_WATER = null;
    public static final ItemGrabHook ITEM_GRAB_HOOK = null;
    public static final ItemWarhammer ITEM_NETHERITE_WARHAMMER = null;
    public static final ItemWarhammer ITEM_DIAMOND_WARHAMMER = null;
    public static final ItemWarhammer ITEM_GOLD_WARHAMMER = null;
    public static final ItemWarhammer ITEM_IRON_WARHAMMER = null;
    public static final ItemWarhammer ITEM_STONE_WARHAMMER = null;
    public static final ItemConfettiBall ITEM_CONFETTI_BALL = null;
    public static final ItemConfetti ITEM_CONFETTI = null;
    public static final ItemBadge ITEM_PROSECUTOR_BADGE = null;
    public static final ItemBadge ITEM_ATTORNEY_BADGE = null;
    public static final Item ITEM_TEACUP = null;
    public static final ItemTeacup ITEM_FULL_TEACUP_0 = null;
    public static final ItemTeacup ITEM_FULL_TEACUP_1 = null;
    public static final ItemTeacup ITEM_FULL_TEACUP_2 = null;
    public static final ItemTeacup ITEM_FULL_TEACUP_3 = null;
    public static final ItemTeacup ITEM_FULL_TEACUP_4 = null;
    public static final ItemTeacup ITEM_FULL_TEACUP_5 = null;
    public static final ItemTeacup ITEM_FULL_TEACUP_6 = null;

    public static final ItemTeapot ITEM_FULL_TEAPOT_1 = null;
    public static final ItemTeapot ITEM_HOT_TEAPOT_1 = null;
    public static final ItemTeapot ITEM_HOT_TEAPOT_2 = null;
    public static final ItemTeapot ITEM_HOT_TEAPOT_3 = null;
    public static final ItemTeapot ITEM_HOT_TEAPOT_4 = null;
    public static final ItemTeapot ITEM_HOT_TEAPOT_5 = null;
    public static final ItemTeapot ITEM_HOT_TEAPOT_6 = null;
    public static final ItemTeapot ITEM_HOT_TEAPOT_7 = null;
    public static final ItemEmptyTeapot ITEM_TEAPOT = null;
    public static final Item ITEM_TEA_SEEDS = null;
    public static final Item ITEM_TEA_DRIED = null;
    public static final Item ITEM_CHOCOLATE = null;
    public static final Item ITEM_TEA_LEAF = null;

    public static final ItemGoldenCupcake ITEM_GOLDEN_CUPCAKE = null;

    public static final ItemKnife ITEM_KNIFE = null;
    public static final ItemGlass ITEM_GLASS = null;
    public static final ItemTomato ITEM_TOMATO = null;
    public static final Item ITEM_TOMATO_SEEDS = null;
    public static final Item ITEM_RICE_SEEDS = null;
    public static final ItemGavel ITEM_GAVEL = null;

    public static final Item ITEM_TOMATO_SLICES = null;
    public static final Item ITEM_POTATO_SLICES = null;
    public static final Item ITEM_RAW_PATTY = null;
    public static final Item ITEM_RAW_CHICKEN_PATTY = null;
    public static final Item ITEM_RAW_SAUSAGE = null;
    public static final Item ITEM_BUN = null;

    public static final ItemCushion BLACK_CUSHION = null;
    public static final ItemCushion BLUE_CUSHION = null;
    public static final ItemCushion BROWN_CUSHION = null;
    public static final ItemCushion CYAN_CUSHION = null;
    public static final ItemCushion GRAY_CUSHION = null;
    public static final ItemCushion GREEN_CUSHION = null;
    public static final ItemCushion LIGHT_BLUE_CUSHION = null;
    public static final ItemCushion LIGHT_GRAY_CUSHION = null;
    public static final ItemCushion LIME_CUSHION = null;
    public static final ItemCushion MAGENTA_CUSHION = null;
    public static final ItemCushion ORANGE_CUSHION = null;
    public static final ItemCushion PINK_CUSHION = null;
    public static final ItemCushion PURPLE_CUSHION = null;
    public static final ItemCushion RED_CUSHION = null;
    public static final ItemCushion WHITE_CUSHION = null;
    public static final ItemCushion YELLOW_CUSHION = null;

    public static final ItemFlask FLASK = null;
    public static final ItemFlask FLASK_MILK = null;

    public static final Item ITEM_BLOCK_LEATHER = null;
    public static final Item ITEM_BLOCK_STRAW = null;

    public static final Item CARVED_OAK_1 = null;
    public static final Item CARVED_OAK_2 = null;
    public static final Item CARVED_OAK_3 = null;
    public static final Item CARVED_OAK_4 = null;
    public static final Item CARVED_OAK_5 = null;
    public static final Item CARVED_OAK_6 = null;
    public static final Item CARVED_OAK_7 = null;
    public static final Item CARVED_OAK_8 = null;
    public static final Item CARVED_BIRCH_1 = null;
    public static final Item CARVED_BIRCH_2 = null;
    public static final Item CARVED_BIRCH_3 = null;
    public static final Item CARVED_BIRCH_4 = null;
    public static final Item CARVED_BIRCH_5 = null;
    public static final Item CARVED_BIRCH_6 = null;
    public static final Item CARVED_BIRCH_7 = null;
    public static final Item CARVED_BIRCH_8 = null;
    public static final Item CARVED_DARK_OAK_1 = null;
    public static final Item CARVED_DARK_OAK_2 = null;
    public static final Item CARVED_DARK_OAK_3 = null;
    public static final Item CARVED_DARK_OAK_4 = null;
    public static final Item CARVED_DARK_OAK_5 = null;
    public static final Item CARVED_DARK_OAK_6 = null;
    public static final Item CARVED_DARK_OAK_7 = null;
    public static final Item CARVED_DARK_OAK_8 = null;
    public static final Item CARVED_ACACIA_1 = null;
    public static final Item CARVED_ACACIA_2 = null;
    public static final Item CARVED_ACACIA_3 = null;
    public static final Item CARVED_ACACIA_4 = null;
    public static final Item CARVED_ACACIA_5 = null;
    public static final Item CARVED_ACACIA_6 = null;
    public static final Item CARVED_ACACIA_7 = null;
    public static final Item CARVED_ACACIA_8 = null;
    public static final Item CARVED_JUNGLE_1 = null;
    public static final Item CARVED_JUNGLE_2 = null;
    public static final Item CARVED_JUNGLE_3 = null;
    public static final Item CARVED_JUNGLE_4 = null;
    public static final Item CARVED_JUNGLE_5 = null;
    public static final Item CARVED_JUNGLE_6 = null;
    public static final Item CARVED_JUNGLE_7 = null;
    public static final Item CARVED_JUNGLE_8 = null;
    public static final Item CARVED_SPRUCE_1 = null;
    public static final Item CARVED_SPRUCE_2 = null;
    public static final Item CARVED_SPRUCE_3 = null;
    public static final Item CARVED_SPRUCE_4 = null;
    public static final Item CARVED_SPRUCE_5 = null;
    public static final Item CARVED_SPRUCE_6 = null;
    public static final Item CARVED_SPRUCE_7 = null;
    public static final Item CARVED_SPRUCE_8 = null;
    public static final Item CARVED_CRIMSON_1 = null;
    public static final Item CARVED_CRIMSON_2 = null;
    public static final Item CARVED_CRIMSON_3 = null;
    public static final Item CARVED_CRIMSON_4 = null;
    public static final Item CARVED_CRIMSON_5 = null;
    public static final Item CARVED_CRIMSON_6 = null;
    public static final Item CARVED_CRIMSON_7 = null;
    public static final Item CARVED_CRIMSON_8 = null;
    public static final Item CARVED_WARPED_1 = null;
    public static final Item CARVED_WARPED_2 = null;
    public static final Item CARVED_WARPED_3 = null;
    public static final Item CARVED_WARPED_4 = null;
    public static final Item CARVED_WARPED_5 = null;
    public static final Item CARVED_WARPED_6 = null;
    public static final Item CARVED_WARPED_7 = null;
    public static final Item CARVED_WARPED_8 = null;

    public static final Item BLACK_TERRATILE = null;
    public static final Item BLUE_TERRATILE = null;
    public static final Item BROWN_TERRATILE = null;
    public static final Item CYAN_TERRATILE = null;
    public static final Item GRAY_TERRATILE = null;
    public static final Item GREEN_TERRATILE = null;
    public static final Item LIGHT_BLUE_TERRATILE = null;
    public static final Item LIGHT_GRAY_TERRATILE = null;
    public static final Item LIME_TERRATILE = null;
    public static final Item MAGENTA_TERRATILE = null;
    public static final Item ORANGE_TERRATILE = null;
    public static final Item PINK_TERRATILE = null;
    public static final Item PURPLE_TERRATILE = null;
    public static final Item RED_TERRATILE = null;
    public static final Item WHITE_TERRATILE = null;
    public static final Item YELLOW_TERRATILE = null;
    public static final Item TERRATILE = null;
    public static final Item BLACK_TERRATILE_SLAB = null;
    public static final Item BLUE_TERRATILE_SLAB = null;
    public static final Item BROWN_TERRATILE_SLAB = null;
    public static final Item CYAN_TERRATILE_SLAB = null;
    public static final Item GRAY_TERRATILE_SLAB = null;
    public static final Item GREEN_TERRATILE_SLAB = null;
    public static final Item LIGHT_BLUE_TERRATILE_SLAB = null;
    public static final Item LIGHT_GRAY_TERRATILE_SLAB = null;
    public static final Item LIME_TERRATILE_SLAB = null;
    public static final Item MAGENTA_TERRATILE_SLAB = null;
    public static final Item ORANGE_TERRATILE_SLAB = null;
    public static final Item PINK_TERRATILE_SLAB = null;
    public static final Item PURPLE_TERRATILE_SLAB = null;
    public static final Item RED_TERRATILE_SLAB = null;
    public static final Item WHITE_TERRATILE_SLAB = null;
    public static final Item YELLOW_TERRATILE_SLAB = null;
    public static final Item TERRATILE_SLAB = null;
    public static final Item BLACK_TERRATILE_STAIRS = null;
    public static final Item BLUE_TERRATILE_STAIRS = null;
    public static final Item BROWN_TERRATILE_STAIRS = null;
    public static final Item CYAN_TERRATILE_STAIRS = null;
    public static final Item GRAY_TERRATILE_STAIRS = null;
    public static final Item GREEN_TERRATILE_STAIRS = null;
    public static final Item LIGHT_BLUE_TERRATILE_STAIRS = null;
    public static final Item LIGHT_GRAY_TERRATILE_STAIRS = null;
    public static final Item LIME_TERRATILE_STAIRS = null;
    public static final Item MAGENTA_TERRATILE_STAIRS = null;
    public static final Item ORANGE_TERRATILE_STAIRS = null;
    public static final Item PINK_TERRATILE_STAIRS = null;
    public static final Item PURPLE_TERRATILE_STAIRS = null;
    public static final Item RED_TERRATILE_STAIRS = null;
    public static final Item WHITE_TERRATILE_STAIRS = null;
    public static final Item YELLOW_TERRATILE_STAIRS = null;
    public static final Item TERRATILE_STAIRS = null;
    public static final Item CARVING_STATION = null;
    public static final Item ITEM_BOOKCASE = null;

    public static final Item ROPE = null;

    public static final Item VAT = null;
    public static final Item CHEESE_WHEEL = null;
    public static final Item CHEESE_SLICE = null;

    public static final BlockItemOmniChest OMNI_CHEST = null;

    public static final ItemScythe WOODEN_SCYTHE = null;
    public static final ItemScythe STONE_SCYTHE = null;
    public static final ItemScythe IRON_SCYTHE = null;
    public static final ItemScythe GOLDEN_SCYTHE = null;
    public static final ItemScythe DIAMOND_SCYTHE = null;
    public static final ItemScythe NETHERITE_SCYTHE = null;

    public static final Enchantment ENCHANTMENT_STEALTH = null;
    public static final Enchantment ENCHANTMENT_POISON = null;
    public static final Enchantment ENCHANTMENT_HEAVY = null;
    public static final Enchantment ENCHANTMENT_MAIM = null;
    public static final Enchantment ENCHANTMENT_QUICK = null;
    public static final Enchantment ENCHANTMENT_QUAKE = null;
    public static final Enchantment ENCHANTMENT_GRAPPLING = null;
    public static final Enchantment ENCHANTMENT_UPPERCUT = null;
    public static final Enchantment ENCHANTMENT_TURBO_GRAB = null;
    public static final Enchantment ENCHANTMENT_GENTLE_GRAB = null;
    public static final Enchantment ENCHANTMENT_GUILLOTINE = null;
    public static final Enchantment ENCHANTMENT_RANGE = null;
    public static final Enchantment ENCHANTMENT_CAPACITY = null;
    public static final Enchantment ENCHANTMENT_CHUG = null;
    public static final Enchantment ENCHANTMENT_DEVOUR = null;

    public static TeaCreativeTab teaTab;
    public static DecoCreativeTab decoTab;

    public static final RecipeSerializer<RecipeTeaSugaring> CRAFTING_SPECIAL_TEA_SUGARING =            null;
    public static final RecipeSerializer<RecipeTeaPouring> CRAFTING_SPECIAL_TEA_POURING =              null;
    public static final RecipeSerializer<RecipeTeaFilling> CRAFTING_SPECIAL_TEA_FILLING =              null;
    public static final RecipeSerializer<RecipeTeaRefilling> CRAFTING_SPECIAL_TEA_REFILLING =          null;
    public static final RecipeSerializer<RecipeFlaskFilling> CRAFTING_SPECIAL_FLASK_FILLING =          null;
    public static final RecipeSerializer<RecipeFlaskFilling> CRAFTING_SPECIAL_ENDER_BOW_FILLING =      null;
    public static final RecipeSerializer<RecipeFlaskMilkFilling> CRAFTING_SPECIAL_FLASK_MILK_FILLING = null;
    public static final RecipeSerializer<RecipeWoodCarving> CRAFTING_SPECIAL_WOOD_CARVING =            null;
    public static final RecipeSerializer<RecipeCarvingStation> CARVING =                               null;

    public static final RecipeSerializer<RecipeConditionShaped> CRAFTING_CONDITION_SHAPED_SCYTHE =  null;
    public static final RecipeSerializer<RecipeConditionShaped> CRAFTING_CONDITION_SHAPED_WARHAMMER =  null;
    public static final RecipeSerializer<RecipeConditionShaped> CRAFTING_CONDITION_SHAPED_OMNI_CHEST =  null;

    public static RecipeType<RecipeCarvingStation> CARVING_STATION_TYPE = RecipeType.register("carving");

    static Item makeItem(String name, CreativeModeTab tab){
        Item item = new Item(new Item.Properties().tab(tab));
        item.setRegistryName(name);
        return item;
    }

    static Item makeFoodItem(String name, FoodProperties food){
        Item item = new ItemConditioned(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(food), Config::isFoodEnabled);
        item.setRegistryName(name);
        return item;
    }

    static Item makeContainedFoodItem(String name, FoodProperties food, Item container, int stackSize){
        return new ItemConditionedContainedFood(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(food).
                craftRemainder(container), container, stackSize).setRegistryName(name);
    }

    static Item makeDrinkItem(String name, FoodProperties food, Item container){
        return new ItemDrink(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(food), container).setRegistryName(name);
    }

    static void registerCompostable(float chance, ItemLike itemIn) {
        ComposterBlock.COMPOSTABLES.put(itemIn.asItem(), chance);
    }

    public static void registerCompostables() {
        registerCompostable(0.3f, ITEM_TEA_SEEDS);
        registerCompostable(0.3f, ITEM_TOMATO_SEEDS);
        registerCompostable(0.3f, ITEM_RICE_SEEDS);
    }

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerRecipes(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeTeaSugaring::new).setRegistryName(     XercaMod.MODID + ":crafting_special_tea_sugaring"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeTeaPouring::new).setRegistryName(      XercaMod.MODID + ":crafting_special_tea_pouring"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeTeaFilling::new).setRegistryName(      XercaMod.MODID + ":crafting_special_tea_filling"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeTeaRefilling::new).setRegistryName(    XercaMod.MODID + ":crafting_special_tea_refilling"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeFlaskFilling::new).setRegistryName(    XercaMod.MODID + ":crafting_special_flask_filling"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeEnderBowFilling::new).setRegistryName( XercaMod.MODID + ":crafting_special_ender_bow_filling"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeFlaskMilkFilling::new).setRegistryName(XercaMod.MODID + ":crafting_special_flask_milk_filling"));
            event.getRegistry().register(new SimpleRecipeSerializer<>(RecipeWoodCarving::new).setRegistryName(     XercaMod.MODID + ":crafting_special_wood_carving"));

            event.getRegistry().register(new RecipeCarvingStation.Serializer<>(RecipeCarvingStation::new).setRegistryName(XercaMod.MODID + ":carving"));

            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isScytheEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_scythe"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isWarhammerEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_warhammer"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isGrabHookEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_grab_hook"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isCushionEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_cushion"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isTeaEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_tea"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isTeaEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_tea"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_food"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_food"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isEnderFlaskEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_ender_flask"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isConfettiEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_confetti"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isConfettiEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_confetti"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isCourtroomEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_courtroom"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isCourtroomEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_courtroom"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isCarvedWoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_carved_wood"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isLeatherStrawEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_leather_straw"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isLeatherStrawEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_leather_straw"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isBookcaseEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_bookcase"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isCoinsEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_coins"));
            event.getRegistry().register(new RecipeConditionShapeless.Serializer(Config::isRopeEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shapeless_rope"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isTerracottaTileEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_terracotta_tile"));
            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isOmniChestEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_omni_chest"));

            event.getRegistry().register(new RecipeConditionSmelting.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_smelting_food"));
            event.getRegistry().register(new RecipeConditionCampfire.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_campfire_food"));
            event.getRegistry().register(new RecipeConditionSmoking.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_smoking_food"));
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            teaTab = new TeaCreativeTab();
            decoTab = new DecoCreativeTab();
            Item glass = new ItemGlass();
            Item teaCup = new ItemTea(new Item.Properties().tab(Items.teaTab)).setRegistryName("item_teacup");

            event.getRegistry().registerAll(
                    new ItemEnderBow().setRegistryName("ender_bow"),
                    new ItemConditioned(new Item.Properties().tab(CreativeModeTab.TAB_MISC), Config::isCoinsEnabled).setRegistryName("item_golden_coin_1"),
                    new ItemConditioned(new Item.Properties().tab(CreativeModeTab.TAB_MISC), Config::isCoinsEnabled).setRegistryName("item_golden_coin_5"),
                    new ItemGrabHook(),
                    new ItemWarhammer("item_netherite_warhammer", Tiers.NETHERITE),
                    new ItemWarhammer("item_diamond_warhammer", Tiers.DIAMOND),
                    new ItemWarhammer("item_gold_warhammer", Tiers.GOLD),
                    new ItemWarhammer("item_iron_warhammer", Tiers.IRON),
                    new ItemWarhammer("item_stone_warhammer", Tiers.STONE),
                    new ItemConfettiBall(),
                    new ItemConfetti(),
                    new ItemBadge("item_prosecutor_badge"),
                    new ItemBadge("item_attorney_badge"),
                    new ItemGavel(),

                    teaCup,
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 0, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 1, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 2, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 3, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 4, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 5, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 6, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 7, true),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 0, false),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 1, false),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 2, false),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 3, false),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 4, false),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 5, false),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 6, false),
                    new ItemTeapot(Blocks.BLOCK_TEAPOT, 7, false),
                    new ItemTeacup(0, teaCup),
                    new ItemTeacup(1, teaCup),
                    new ItemTeacup(2, teaCup),
                    new ItemTeacup(3, teaCup),
                    new ItemTeacup(4, teaCup),
                    new ItemTeacup(5, teaCup),
                    new ItemTeacup(6, teaCup),
                    new ItemEmptyTeapot(new Item.Properties().tab(Items.teaTab)).setRegistryName("item_teapot"),
                    new BlockNamedConditionedItem(Blocks.BLOCK_TEA_PLANT, new Item.Properties().tab(Items.teaTab), Config::isTeaEnabled).setRegistryName("item_tea_seeds"),
                    new ItemTea(new Item.Properties().tab(Items.teaTab)).setRegistryName("item_tea_dried"),
                    new ItemTea(new Item.Properties().tab(Items.teaTab)).setRegistryName("item_tea_leaf"),

                    new ItemKnife(),
                    glass,
                    new ItemTomato(),
                    new BlockNamedConditionedItem(Blocks.BLOCK_TOMATO_PLANT, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS), Config::isFoodEnabled).setRegistryName("item_tomato_seeds"),
                    new BlockNamedConditionedItem(Blocks.BLOCK_RICE_PLANT, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS), Config::isFoodEnabled).setRegistryName("item_rice_seeds"),
                    new ItemGoldenCupcake(),
                    makeFoodItem("item_apple_cupcake", Foods.APPLE_CUPCAKE),
                    makeFoodItem("item_pumpkin_cupcake", Foods.PUMPKIN_CUPCAKE),
                    makeFoodItem("item_cocoa_cupcake", Foods.COCOA_CUPCAKE),
                    makeFoodItem("item_melon_cupcake", Foods.MELON_CUPCAKE),
                    makeFoodItem("item_carrot_cupcake", Foods.CARROT_CUPCAKE),
                    makeFoodItem("item_fancy_apple_cupcake", Foods.FANCY_APPLE_CUPCAKE),
                    makeFoodItem("item_fancy_pumpkin_cupcake", Foods.FANCY_PUMPKIN_CUPCAKE),
                    makeFoodItem("glowberry_cupcake", Foods.GLOWBERRY_CUPCAKE),
                    makeFoodItem("item_donut", Foods.DONUT),
                    makeFoodItem("item_fancy_donut", Foods.FANCY_DONUT),
                    makeFoodItem("item_sprinkles", Foods.SPRINKLES),
                    makeFoodItem("item_chocolate", Foods.CHOCOLATE),
                    makeFoodItem("item_bun", Foods.BUN),
                    makeFoodItem("item_raw_patty", Foods.RAW_PATTY),
                    makeFoodItem("item_cooked_patty", Foods.COOKED_PATTY),
                    makeFoodItem("item_raw_chicken_patty", Foods.RAW_CHICKEN_PATTY),
                    makeFoodItem("item_cooked_chicken_patty", Foods.COOKED_CHICKEN_PATTY),
                    makeFoodItem("item_hamburger", Foods.HAMBURGER),
                    makeFoodItem("item_chicken_burger", Foods.CHICKEN_BURGER),
                    makeFoodItem("item_mushroom_burger", Foods.MUSHROOM_BURGER),
                    makeFoodItem("item_ultimate_bottom", Foods.ULTIMATE_BOTTOM),
                    makeFoodItem("item_ultimate_top", Foods.ULTIMATE_TOP),
                    makeFoodItem("cheeseburger", Foods.CHEESEBURGER),
                    new ItemUltimateBurger(),
                    new ItemEnderCupcake(),
                    makeFoodItem( "item_rotten_burger", Foods.ROTTEN_BURGER),
                    makeFoodItem("item_raw_sausage", Foods.RAW_SAUSAGE),
                    makeFoodItem("item_cooked_sausage", Foods.COOKED_SAUSAGE),
                    makeFoodItem( "item_hotdog", Foods.HOTDOG),
                    makeFoodItem("item_fish_bread", Foods.FISH_BREAD),
                    makeFoodItem( "item_daisy_sandwich", Foods.DAISY_SANDWICH),
                    makeFoodItem("item_chicken_wrap", Foods.CHICKEN_WRAP),
                    makeFoodItem("item_raw_schnitzel", Foods.RAW_SCHNITZEL),
                    makeFoodItem("item_cooked_schnitzel", Foods.COOKED_SCHNITZEL),
                    makeFoodItem("item_fried_egg", Foods.FRIED_EGG),
                    makeFoodItem( "item_croissant", Foods.CROISSANT),
                    makeFoodItem( "item_potato_slices", Foods.POTATO_SLICES),
                    makeFoodItem( "item_potato_fries", Foods.POTATO_FRIES),
                    makeFoodItem( "item_shish_kebab", Foods.SHISH_KEBAB),
                    makeFoodItem("item_tomato_slices", Foods.TOMATO_SLICES),
                    makeDrinkItem("item_ice_tea", Foods.ICE_TEA, glass),
                    makeDrinkItem("item_apple_juice", Foods.APPLE_JUICE, glass),
                    makeDrinkItem("item_carrot_juice", Foods.CARROT_JUICE, glass),
                    makeDrinkItem("item_melon_juice", Foods.MELON_JUICE, glass),
                    makeDrinkItem("item_pumpkin_juice", Foods.PUMPKIN_JUICE, glass),
                    makeDrinkItem("item_tomato_juice", Foods.TOMATO_JUICE, glass),
                    makeDrinkItem("item_wheat_juice", Foods.WHEAT_JUICE, glass),
                    makeDrinkItem("item_glass_of_milk", Foods.GLASS_OF_MILK, glass),
                    new ItemGlassOfWater(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(Foods.GLASS_OF_WATER), glass).setRegistryName("item_glass_of_water"),
                    makeDrinkItem("sweet_berry_juice", Foods.SWEET_BERRY_JUICE, glass),
                    makeDrinkItem("item_ayran", Foods.AYRAN, glass),
                    makeDrinkItem("sake", Foods.SAKE, glass),
                    makeDrinkItem("carbonated_water", Foods.CARBONATED_WATER, glass),
                    makeDrinkItem("soda", Foods.SODA, glass),
                    makeDrinkItem("cola", Foods.COLA, glass),
                    makeContainedFoodItem("oyakodon", Foods.OYAKODON, net.minecraft.world.item.Items.BOWL, 16),
                    makeContainedFoodItem("beef_donburi", Foods.BEEF_DONBURI, net.minecraft.world.item.Items.BOWL, 16),
                    makeFoodItem("egg_sushi", Foods.EGG_SUSHI),
                    makeFoodItem("nigiri_sushi", Foods.NIGIRI_SUSHI),
                    makeFoodItem("omurice", Foods.OMURICE),
                    makeFoodItem("riceball", Foods.RICEBALL),
                    makeFoodItem("sushi", Foods.SUSHI),
                    makeFoodItem("cooked_rice", Foods.COOKED_RICE),
                    makeFoodItem("sashimi", Foods.SASHIMI),
                    makeContainedFoodItem("rice_pudding", Foods.RICE_PUDDING, net.minecraft.world.item.Items.BOWL, 16),
                    makeContainedFoodItem("baked_rice_pudding", Foods.BAKED_RICE_PUDDING, net.minecraft.world.item.Items.BOWL, 16),
                    makeFoodItem("sweet_berry_cupcake_fancy", Foods.SWEET_BERRY_CUPCAKE_FANCY),
                    makeFoodItem("sweet_berry_cupcake", Foods.SWEET_BERRY_CUPCAKE),
                    makeFoodItem("doner_slice", Foods.DONER_SLICE),
                    makeContainedFoodItem("item_yoghurt", Foods.YOGHURT, net.minecraft.world.item.Items.BUCKET, 16),
                    makeContainedFoodItem("item_honeyberry_yoghurt", Foods.HONEYBERRY_YOGHURT, net.minecraft.world.item.Items.BOWL, 16),
                    makeFoodItem("item_honey_cupcake", Foods.HONEY_CUPCAKE),
                    makeFoodItem("item_doner_wrap", Foods.DONER_WRAP),
                    makeFoodItem("item_chubby_doner", Foods.CHUBBY_DONER),
                    makeContainedFoodItem("item_alexander", Foods.ALEXANDER, net.minecraft.world.item.Items.BOWL, 16),
                    makeFoodItem("raw_shish_kebab", Foods.RAW_SHISH_KEBAB),
                    makeFoodItem("cheese_slice", Foods.CHEESE_SLICE),
                    makeFoodItem("cheese_toast", Foods.CHEESE_TOAST),
                    makeContainedFoodItem("squid_ink_paella", Foods.SQUID_INK_PAELLA, net.minecraft.world.item.Items.BOWL, 16),
                    makeContainedFoodItem("glow_squid_ink_paella", Foods.GLOW_SQUID_INK_PAELLA, net.minecraft.world.item.Items.BOWL, 16),

                    // PIZZA RAW REGISTER BEGIN
                    new ItemRawPizza(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, Foods.RAW_PIZZA_3),
                    new ItemRawPizza(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2),
                    new ItemRawPizza(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1),
                    new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1),
                    new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1),
                    new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1),
                    new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1),
                    new ItemRawPizza(BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_0),

                    // PIZZA RAW REGISTER END

                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.BLACK_CUSHION).setRegistryName("black_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.BLUE_CUSHION).setRegistryName("blue_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.BROWN_CUSHION).setRegistryName("brown_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.CYAN_CUSHION).setRegistryName("cyan_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.GRAY_CUSHION).setRegistryName("gray_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.GREEN_CUSHION).setRegistryName("green_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.LIGHT_BLUE_CUSHION).setRegistryName("light_blue_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.LIGHT_GRAY_CUSHION).setRegistryName("light_gray_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.LIME_CUSHION).setRegistryName("lime_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.MAGENTA_CUSHION).setRegistryName("magenta_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.ORANGE_CUSHION).setRegistryName("orange_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.PINK_CUSHION).setRegistryName("pink_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.PURPLE_CUSHION).setRegistryName("purple_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.RED_CUSHION).setRegistryName("red_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.WHITE_CUSHION).setRegistryName("white_cushion"),
                    new ItemCushion(new Item.Properties().tab(Items.decoTab), Blocks.YELLOW_CUSHION).setRegistryName("yellow_cushion"),

                    // PIZZA REGISTER BEGIN
                    new ItemPizza(Blocks.PIZZA_PEPPERONI_PEPPERONI_PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_MUSHROOM_PEPPERONI_PEPPERONI, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_MUSHROOM_MUSHROOM_PEPPERONI, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_MUSHROOM_MUSHROOM_MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_MEAT_PEPPERONI_PEPPERONI, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_MEAT_MUSHROOM_PEPPERONI, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_MEAT_MUSHROOM_MUSHROOM, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_MEAT_MEAT_PEPPERONI, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_MEAT_MEAT_MUSHROOM, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_MEAT_MEAT_MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT),
                    new ItemPizza(Blocks.PIZZA_FISH_PEPPERONI_PEPPERONI, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_FISH_MUSHROOM_PEPPERONI, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_FISH_MUSHROOM_MUSHROOM, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_FISH_MEAT_PEPPERONI, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_FISH_MEAT_MUSHROOM, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_FISH_MEAT_MEAT, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT),
                    new ItemPizza(Blocks.PIZZA_FISH_FISH_PEPPERONI, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_FISH_FISH_MUSHROOM, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_FISH_FISH_MEAT, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT),
                    new ItemPizza(Blocks.PIZZA_FISH_FISH_FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_PEPPERONI_PEPPERONI, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_MUSHROOM_PEPPERONI, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_MUSHROOM_MUSHROOM, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT_PEPPERONI, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT_MUSHROOM, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT_MEAT, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_PEPPERONI, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_MUSHROOM, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_MEAT, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_FISH, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_PEPPERONI, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_MUSHROOM, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_MEAT, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_FISH, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN),
                    new ItemPizza(Blocks.PIZZA_PEPPERONI_PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_MUSHROOM_PEPPERONI, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_MUSHROOM_MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_MEAT_PEPPERONI, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_MEAT_MUSHROOM, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_MEAT_MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_FISH_PEPPERONI, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_FISH_MUSHROOM, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_FISH_MEAT, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_FISH_FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_PEPPERONI, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_MUSHROOM, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_FISH, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA_CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY),
                    new ItemPizza(Blocks.PIZZA, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY),

                    // PIZZA REGISTER END

                    new BlockConditionedItem(Blocks.BLOCK_APPLE_PIE, new Item.Properties().tab(CreativeModeTab.TAB_FOOD), Config::isFoodEnabled).setRegistryName("item_apple_pie"),
                    new BlockConditionedItem(Blocks.BLOCK_SWEET_BERRY_PIE, new Item.Properties().tab(CreativeModeTab.TAB_FOOD), Config::isFoodEnabled).setRegistryName("sweet_berry_pie"),
                    new BlockConditionedItem(Blocks.BLOCK_LEATHER, new Item.Properties().tab(Items.decoTab), Config::isLeatherStrawEnabled).setRegistryName("item_block_leather"),
                    new BlockConditionedItem(Blocks.BLOCK_STRAW, new Item.Properties().tab(Items.decoTab), Config::isLeatherStrawEnabled).setRegistryName("item_block_straw"),
                    new BlockConditionedItem(Blocks.BLOCK_BOOKCASE, new Item.Properties().tab(Items.decoTab), Config::isBookcaseEnabled).setRegistryName("item_bookcase"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_oak_1"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_oak_2"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_oak_3"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_oak_4"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_oak_5"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_oak_6"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_oak_7"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_oak_8"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_birch_1"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_birch_2"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_birch_3"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_birch_4"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_birch_5"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_birch_6"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_birch_7"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_birch_8"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_dark_oak_1"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_dark_oak_2"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_dark_oak_3"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_dark_oak_4"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_dark_oak_5"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_dark_oak_6"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_dark_oak_7"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_dark_oak_8"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_acacia_1"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_acacia_2"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_acacia_3"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_acacia_4"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_acacia_5"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_acacia_6"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_acacia_7"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_acacia_8"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_jungle_1"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_jungle_2"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_jungle_3"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_jungle_4"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_jungle_5"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_jungle_6"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_jungle_7"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_jungle_8"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_spruce_1"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_spruce_2"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_spruce_3"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_spruce_4"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_spruce_5"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_spruce_6"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_spruce_7"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_spruce_8"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_crimson_1"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_crimson_2"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_crimson_3"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_crimson_4"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_crimson_5"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_crimson_6"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_crimson_7"),
                    new CarvedWoodItem(Blocks.CARVED_CRIMSON_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_crimson_8"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_1, new Item.Properties().tab(Items.decoTab), 1).setRegistryName("carved_warped_1"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_2, new Item.Properties().tab(Items.decoTab), 2).setRegistryName("carved_warped_2"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_3, new Item.Properties().tab(Items.decoTab), 3).setRegistryName("carved_warped_3"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_4, new Item.Properties().tab(Items.decoTab), 4).setRegistryName("carved_warped_4"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_5, new Item.Properties().tab(Items.decoTab), 5).setRegistryName("carved_warped_5"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_6, new Item.Properties().tab(Items.decoTab), 6).setRegistryName("carved_warped_6"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_7, new Item.Properties().tab(Items.decoTab), 7).setRegistryName("carved_warped_7"),
                    new CarvedWoodItem(Blocks.CARVED_WARPED_8, new Item.Properties().tab(Items.decoTab), 8).setRegistryName("carved_warped_8"),

                    new BlockConditionedItem(Blocks.BLACK_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("black_terratile"),
                    new BlockConditionedItem(Blocks.BLUE_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("blue_terratile"),
                    new BlockConditionedItem(Blocks.BROWN_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("brown_terratile"),
                    new BlockConditionedItem(Blocks.CYAN_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("cyan_terratile"),
                    new BlockConditionedItem(Blocks.GRAY_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("gray_terratile"),
                    new BlockConditionedItem(Blocks.GREEN_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("green_terratile"),
                    new BlockConditionedItem(Blocks.LIGHT_BLUE_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_blue_terratile"),
                    new BlockConditionedItem(Blocks.LIGHT_GRAY_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_gray_terratile"),
                    new BlockConditionedItem(Blocks.LIME_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("lime_terratile"),
                    new BlockConditionedItem(Blocks.MAGENTA_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("magenta_terratile"),
                    new BlockConditionedItem(Blocks.ORANGE_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("orange_terratile"),
                    new BlockConditionedItem(Blocks.PINK_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("pink_terratile"),
                    new BlockConditionedItem(Blocks.PURPLE_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("purple_terratile"),
                    new BlockConditionedItem(Blocks.RED_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("red_terratile"),
                    new BlockConditionedItem(Blocks.WHITE_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("white_terratile"),
                    new BlockConditionedItem(Blocks.YELLOW_TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("yellow_terratile"),
                    new BlockConditionedItem(Blocks.TERRATILE, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("terratile"),

                    new BlockConditionedItem(Blocks.BLACK_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("black_terratile_slab"),
                    new BlockConditionedItem(Blocks.BLUE_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("blue_terratile_slab"),
                    new BlockConditionedItem(Blocks.BROWN_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("brown_terratile_slab"),
                    new BlockConditionedItem(Blocks.CYAN_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("cyan_terratile_slab"),
                    new BlockConditionedItem(Blocks.GRAY_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("gray_terratile_slab"),
                    new BlockConditionedItem(Blocks.GREEN_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("green_terratile_slab"),
                    new BlockConditionedItem(Blocks.LIGHT_BLUE_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_blue_terratile_slab"),
                    new BlockConditionedItem(Blocks.LIGHT_GRAY_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_gray_terratile_slab"),
                    new BlockConditionedItem(Blocks.LIME_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("lime_terratile_slab"),
                    new BlockConditionedItem(Blocks.MAGENTA_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("magenta_terratile_slab"),
                    new BlockConditionedItem(Blocks.ORANGE_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("orange_terratile_slab"),
                    new BlockConditionedItem(Blocks.PINK_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("pink_terratile_slab"),
                    new BlockConditionedItem(Blocks.PURPLE_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("purple_terratile_slab"),
                    new BlockConditionedItem(Blocks.RED_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("red_terratile_slab"),
                    new BlockConditionedItem(Blocks.WHITE_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("white_terratile_slab"),
                    new BlockConditionedItem(Blocks.YELLOW_TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("yellow_terratile_slab"),
                    new BlockConditionedItem(Blocks.TERRATILE_SLAB, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("terratile_slab"),

                    new BlockConditionedItem(Blocks.BLACK_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("black_terratile_stairs"),
                    new BlockConditionedItem(Blocks.BLUE_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("blue_terratile_stairs"),
                    new BlockConditionedItem(Blocks.BROWN_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("brown_terratile_stairs"),
                    new BlockConditionedItem(Blocks.CYAN_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("cyan_terratile_stairs"),
                    new BlockConditionedItem(Blocks.GRAY_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("gray_terratile_stairs"),
                    new BlockConditionedItem(Blocks.GREEN_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("green_terratile_stairs"),
                    new BlockConditionedItem(Blocks.LIGHT_BLUE_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_blue_terratile_stairs"),
                    new BlockConditionedItem(Blocks.LIGHT_GRAY_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_gray_terratile_stairs"),
                    new BlockConditionedItem(Blocks.LIME_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("lime_terratile_stairs"),
                    new BlockConditionedItem(Blocks.MAGENTA_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("magenta_terratile_stairs"),
                    new BlockConditionedItem(Blocks.ORANGE_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("orange_terratile_stairs"),
                    new BlockConditionedItem(Blocks.PINK_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("pink_terratile_stairs"),
                    new BlockConditionedItem(Blocks.PURPLE_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("purple_terratile_stairs"),
                    new BlockConditionedItem(Blocks.RED_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("red_terratile_stairs"),
                    new BlockConditionedItem(Blocks.WHITE_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("white_terratile_stairs"),
                    new BlockConditionedItem(Blocks.YELLOW_TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("yellow_terratile_stairs"),
                    new BlockConditionedItem(Blocks.TERRATILE_STAIRS, new Item.Properties().tab(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("terratile_stairs"),

                    new BlockConditionedItem(Blocks.CARVING_STATION, new Item.Properties().tab(Items.decoTab), Config::isCarvedWoodEnabled).setRegistryName("carving_station"),

                    new BlockItemOmniChest(Blocks.OMNI_CHEST, new Item.Properties().tab(Items.decoTab)).setRegistryName("omni_chest"),

                    new ItemFlask(new Item.Properties().tab(CreativeModeTab.TAB_BREWING).stacksTo(1).durability(160), "flask", false),
                    new ItemFlask(new Item.Properties().stacksTo(1).durability(160), "flask_milk", true),

                    new BlockConditionedItem(Blocks.ROPE, new Item.Properties().tab(Items.decoTab), Config::isRopeEnabled){
                        public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
                            TranslatableComponent text = new TranslatableComponent("xercamod.rope_tooltip");
                            tooltip.add(text.withStyle(ChatFormatting.BLUE));
                        }
                    }.setRegistryName("rope"),

                    new BlockConditionedItem(Blocks.VAT, new Item.Properties().tab(Items.decoTab), Config::isFoodEnabled).setRegistryName("vat"),
                    new BlockConditionedItem(Blocks.CHEESE_WHEEL, new Item.Properties().tab(CreativeModeTab.TAB_FOOD), Config::isFoodEnabled).setRegistryName("cheese_wheel"),

                    new ItemScythe(Tiers.WOOD, 3, -2.6f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS)).setRegistryName("wooden_scythe"),
                    new ItemScythe(Tiers.STONE, 3, -2.6f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS)).setRegistryName("stone_scythe"),
                    new ItemScythe(Tiers.IRON, 3, -2.6f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS)).setRegistryName("iron_scythe"),
                    new ItemScythe(Tiers.GOLD, 3, -2.6f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS)).setRegistryName("golden_scythe"),
                    new ItemScythe(Tiers.DIAMOND, 3, -2.6f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS)).setRegistryName("diamond_scythe"),
                    new ItemScythe(Tiers.NETHERITE, 3, -2.6f, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant()).setRegistryName("netherite_scythe"),

                    new Item(new Item.Properties().tab(CreativeModeTab.TAB_BREWING).craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)).setRegistryName("cola_extract"),
                    new Item(new Item.Properties().tab(CreativeModeTab.TAB_BREWING)).setRegistryName("cola_powder")
            );
        }

        @SubscribeEvent
        public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event){
            event.getRegistry().registerAll(
                    new EnchantmentHeavy(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND),
                    new EnchantmentMaim(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentQuick(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND),
                    new EnchantmentQuake(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentGrappling(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentUppercut(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentPoison(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND),
                    new EnchantmentStealth(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentTurboGrab(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND),
                    new EnchantmentGentleGrab(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentGuillotine(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentCapacity(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND),
                    new EnchantmentRange(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentChug(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND),
                    new EnchantmentDevour(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND)
            );
        }
    }

}