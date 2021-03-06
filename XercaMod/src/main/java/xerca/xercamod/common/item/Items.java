package xerca.xercamod.common.item;

import net.minecraft.block.ComposterBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.DecoCreativeTab;
import xerca.xercamod.common.TeaCreativeTab;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.crafting.*;
import xerca.xercamod.common.enchantments.*;

import javax.annotation.Nullable;
import java.util.List;

@ObjectHolder(XercaMod.MODID)
public final class Items {
    public static final ItemEnderBow ENDER_BOW = null;
    public static final ItemSpyglass SPYGLASS = null;
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

    public static TeaCreativeTab teaTab;
    public static DecoCreativeTab decoTab;

    public static final IRecipeSerializer<RecipeTeaSugaring> CRAFTING_SPECIAL_TEA_SUGARING =            null;
    public static final IRecipeSerializer<RecipeTeaPouring> CRAFTING_SPECIAL_TEA_POURING =              null;
    public static final IRecipeSerializer<RecipeTeaFilling> CRAFTING_SPECIAL_TEA_FILLING =              null;
    public static final IRecipeSerializer<RecipeTeaRefilling> CRAFTING_SPECIAL_TEA_REFILLING =          null;
    public static final IRecipeSerializer<RecipeFlaskFilling> CRAFTING_SPECIAL_FLASK_FILLING =          null;
    public static final IRecipeSerializer<RecipeFlaskFilling> CRAFTING_SPECIAL_ENDER_BOW_FILLING =      null;
    public static final IRecipeSerializer<RecipeFlaskMilkFilling> CRAFTING_SPECIAL_FLASK_MILK_FILLING = null;
    public static final IRecipeSerializer<RecipeWoodCarving> CRAFTING_SPECIAL_WOOD_CARVING =            null;
    public static final IRecipeSerializer<RecipeCarvingStation> CARVING =                               null;

    public static final IRecipeSerializer<RecipeConditionShaped> CRAFTING_CONDITION_SHAPED_SPYGLASS =  null;
    public static final IRecipeSerializer<RecipeConditionShaped> CRAFTING_CONDITION_SHAPED_SCYTHE =  null;
    public static final IRecipeSerializer<RecipeConditionShaped> CRAFTING_CONDITION_SHAPED_WARHAMMER =  null;

    public static IRecipeType<RecipeCarvingStation> CARVING_STATION_TYPE = IRecipeType.register("carving");

    static Item makeItem(String name, ItemGroup tab){
        Item item = new Item(new Item.Properties().group(tab));
        item.setRegistryName(name);
        return item;
    }

    static Item makeFoodItem(String name, Food food){
        Item item = new ItemConditioned(new Item.Properties().group(ItemGroup.FOOD).food(food), Config::isFoodEnabled);
        item.setRegistryName(name);
        return item;
    }

    static Item makeContainedFoodItem(String name, Food food, Item container, int stackSize){
        Item item = new ItemConditionedContainedFood(new Item.Properties().group(ItemGroup.FOOD).food(food).containerItem(container), container, stackSize);
        item.setRegistryName(name);
        return item;
    }

    static Item makeDrinkItem(String name, Food food, Item container){
        Item item = new ItemDrink(new Item.Properties().group(ItemGroup.FOOD).food(food), container);
        item.setRegistryName(name);
        return item;
    }

    static void registerCompostable(float chance, IItemProvider itemIn) {
        ComposterBlock.CHANCES.put(itemIn.asItem(), chance);
    }

    public static void registerCompostables() {
        registerCompostable(0.3f, ITEM_TEA_SEEDS);
        registerCompostable(0.3f, ITEM_TOMATO_SEEDS);
        registerCompostable(0.3f, ITEM_RICE_SEEDS);
    }

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {

//            CraftingHelper.register(ConfigurationCondition.Serializer.INSTANCE);

            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeTeaSugaring::new).setRegistryName(     XercaMod.MODID + ":crafting_special_tea_sugaring"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeTeaPouring::new).setRegistryName(      XercaMod.MODID + ":crafting_special_tea_pouring"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeTeaFilling::new).setRegistryName(      XercaMod.MODID + ":crafting_special_tea_filling"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeTeaRefilling::new).setRegistryName(    XercaMod.MODID + ":crafting_special_tea_refilling"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeFlaskFilling::new).setRegistryName(    XercaMod.MODID + ":crafting_special_flask_filling"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeEnderBowFilling::new).setRegistryName( XercaMod.MODID + ":crafting_special_ender_bow_filling"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeFlaskMilkFilling::new).setRegistryName(XercaMod.MODID + ":crafting_special_flask_milk_filling"));
            event.getRegistry().register(new SpecialRecipeSerializer<>(RecipeWoodCarving::new).setRegistryName(     XercaMod.MODID + ":crafting_special_wood_carving"));

            event.getRegistry().register(new RecipeCarvingStation.Serializer<>(RecipeCarvingStation::new).setRegistryName(XercaMod.MODID + ":carving"));

            event.getRegistry().register(new RecipeConditionShaped.Serializer(Config::isSpyglassEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_shaped_spyglass"));
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

            event.getRegistry().register(new RecipeConditionSmelting.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_smelting_food"));
            event.getRegistry().register(new RecipeConditionCampfire.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_campfire_food"));
            event.getRegistry().register(new RecipeConditionSmoking.Serializer(Config::isFoodEnabled).setRegistryName(XercaMod.MODID + ":crafting_condition_smoking_food"));
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            teaTab = new TeaCreativeTab();
            decoTab = new DecoCreativeTab();
            Item glass = new ItemGlass();
            Item teaCup = new ItemTea(new Item.Properties().group(Items.teaTab)).setRegistryName("item_teacup");

            event.getRegistry().registerAll(
                    new ItemEnderBow().setRegistryName("ender_bow"),
                    new ItemSpyglass().setRegistryName("spyglass"),
                    new ItemConditioned(new Item.Properties().group(ItemGroup.MISC), Config::isCoinsEnabled).setRegistryName("item_golden_coin_1"),
                    new ItemConditioned(new Item.Properties().group(ItemGroup.MISC), Config::isCoinsEnabled).setRegistryName("item_golden_coin_5"),
                    new ItemGrabHook(),
                    new ItemWarhammer("item_netherite_warhammer", ItemTier.NETHERITE),
                    new ItemWarhammer("item_diamond_warhammer", ItemTier.DIAMOND),
                    new ItemWarhammer("item_gold_warhammer", ItemTier.GOLD),
                    new ItemWarhammer("item_iron_warhammer", ItemTier.IRON),
                    new ItemWarhammer("item_stone_warhammer", ItemTier.STONE),
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
                    new ItemEmptyTeapot(new Item.Properties().group(Items.teaTab)).setRegistryName("item_teapot"),
                    new BlockNamedConditionedItem(Blocks.BLOCK_TEA_PLANT, new Item.Properties().group(Items.teaTab), Config::isTeaEnabled).setRegistryName("item_tea_seeds"),
                    new ItemTea(new Item.Properties().group(Items.teaTab)).setRegistryName("item_tea_dried"),
                    new ItemTea(new Item.Properties().group(Items.teaTab)).setRegistryName("item_tea_leaf"),

                    new ItemKnife(),
                    glass,
                    new ItemTomato(),
                    new BlockNamedConditionedItem(Blocks.BLOCK_TOMATO_PLANT, new Item.Properties().group(ItemGroup.MATERIALS), Config::isFoodEnabled).setRegistryName("item_tomato_seeds"),
                    new BlockNamedConditionedItem(Blocks.BLOCK_RICE_PLANT, new Item.Properties().group(ItemGroup.MATERIALS), Config::isFoodEnabled).setRegistryName("item_rice_seeds"),
                    new ItemGoldenCupcake(),
                    makeFoodItem( "item_apple_cupcake", Foods.APPLE_CUPCAKE),
                    makeFoodItem( "item_pumpkin_cupcake", Foods.PUMPKIN_CUPCAKE),
                    makeFoodItem( "item_cocoa_cupcake", Foods.COCOA_CUPCAKE),
                    makeFoodItem( "item_melon_cupcake", Foods.MELON_CUPCAKE),
                    makeFoodItem( "item_carrot_cupcake", Foods.CARROT_CUPCAKE),
                    makeFoodItem( "item_fancy_apple_cupcake", Foods.FANCY_APPLE_CUPCAKE),
                    makeFoodItem( "item_fancy_pumpkin_cupcake", Foods.FANCY_PUMPKIN_CUPCAKE),
                    makeFoodItem( "item_donut", Foods.DONUT),
                    makeFoodItem( "item_fancy_donut", Foods.FANCY_DONUT),
                    makeFoodItem( "item_sprinkles", Foods.SPRINKLES),
                    makeFoodItem( "item_chocolate", Foods.CHOCOLATE),
                    makeFoodItem( "item_bun", Foods.BUN),
                    makeFoodItem("item_raw_patty", Foods.RAW_PATTY),
                    makeFoodItem("item_cooked_patty", Foods.COOKED_PATTY),
                    makeFoodItem("item_raw_chicken_patty", Foods.RAW_CHICKEN_PATTY),
                    makeFoodItem("item_cooked_chicken_patty", Foods.COOKED_CHICKEN_PATTY),
                    makeFoodItem( "item_hamburger", Foods.HAMBURGER),
                    makeFoodItem("item_chicken_burger", Foods.CHICKEN_BURGER),
                    makeFoodItem("item_mushroom_burger", Foods.MUSHROOM_BURGER),
                    makeFoodItem("item_ultimate_bottom", Foods.ULTIMATE_BOTTOM),
                    makeFoodItem("item_ultimate_top", Foods.ULTIMATE_TOP),
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
                    makeDrinkItem("item_glass_of_water", Foods.GLASS_OF_WATER, glass),
                    makeDrinkItem("sweet_berry_juice", Foods.SWEET_BERRY_JUICE, glass),
                    makeDrinkItem("item_ayran", Foods.AYRAN, glass),
                    makeDrinkItem("sake", Foods.SAKE, glass),
                    makeContainedFoodItem("oyakodon", Foods.OYAKODON, net.minecraft.item.Items.BOWL, 16),
                    makeContainedFoodItem("beef_donburi", Foods.BEEF_DONBURI, net.minecraft.item.Items.BOWL, 16),
                    makeFoodItem("egg_sushi", Foods.EGG_SUSHI),
                    makeFoodItem("nigiri_sushi", Foods.NIGIRI_SUSHI),
                    makeFoodItem("omurice", Foods.OMURICE),
                    makeFoodItem("riceball", Foods.RICEBALL),
                    makeFoodItem("sushi", Foods.SUSHI),
                    makeFoodItem("cooked_rice", Foods.COOKED_RICE),
                    makeFoodItem("sashimi", Foods.SASHIMI),
                    makeContainedFoodItem("rice_pudding", Foods.RICE_PUDDING, net.minecraft.item.Items.BOWL, 16),
                    makeContainedFoodItem("baked_rice_pudding", Foods.BAKED_RICE_PUDDING, net.minecraft.item.Items.BOWL, 16),
                    makeFoodItem("sweet_berry_cupcake_fancy", Foods.SWEET_BERRY_CUPCAKE_FANCY),
                    makeFoodItem("sweet_berry_cupcake", Foods.SWEET_BERRY_CUPCAKE),
                    makeFoodItem("doner_slice", Foods.DONER_SLICE),
                    makeContainedFoodItem("item_yoghurt", Foods.YOGHURT, net.minecraft.item.Items.BUCKET, 16),
                    makeContainedFoodItem("item_honeyberry_yoghurt", Foods.HONEYBERRY_YOGHURT, net.minecraft.item.Items.BOWL, 16),
                    makeFoodItem("item_honey_cupcake", Foods.HONEY_CUPCAKE),
                    makeFoodItem("item_doner_wrap", Foods.DONER_WRAP),
                    makeFoodItem("item_chubby_doner", Foods.CHUBBY_DONER),
                    makeContainedFoodItem("item_alexander", Foods.ALEXANDER, net.minecraft.item.Items.BOWL, 16),
                    makeFoodItem("raw_shish_kebab", Foods.RAW_SHISH_KEBAB),

                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.BLACK_CUSHION).setRegistryName("black_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.BLUE_CUSHION).setRegistryName("blue_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.BROWN_CUSHION).setRegistryName("brown_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.CYAN_CUSHION).setRegistryName("cyan_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.GRAY_CUSHION).setRegistryName("gray_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.GREEN_CUSHION).setRegistryName("green_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.LIGHT_BLUE_CUSHION).setRegistryName("light_blue_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.LIGHT_GRAY_CUSHION).setRegistryName("light_gray_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.LIME_CUSHION).setRegistryName("lime_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.MAGENTA_CUSHION).setRegistryName("magenta_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.ORANGE_CUSHION).setRegistryName("orange_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.PINK_CUSHION).setRegistryName("pink_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.PURPLE_CUSHION).setRegistryName("purple_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.RED_CUSHION).setRegistryName("red_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.WHITE_CUSHION).setRegistryName("white_cushion"),
                    new ItemCushion(new Item.Properties().group(Items.decoTab), Blocks.YELLOW_CUSHION).setRegistryName("yellow_cushion"),

                    new BlockConditionedItem(Blocks.BLOCK_APPLE_PIE, new Item.Properties().group(ItemGroup.FOOD), Config::isFoodEnabled).setRegistryName("item_apple_pie"),
                    new BlockConditionedItem(Blocks.BLOCK_SWEET_BERRY_PIE, new Item.Properties().group(ItemGroup.FOOD), Config::isFoodEnabled).setRegistryName("sweet_berry_pie"),
                    new BlockConditionedItem(Blocks.BLOCK_LEATHER, new Item.Properties().group(Items.decoTab), Config::isLeatherStrawEnabled).setRegistryName("item_block_leather"),
                    new BlockConditionedItem(Blocks.BLOCK_STRAW, new Item.Properties().group(Items.decoTab), Config::isLeatherStrawEnabled).setRegistryName("item_block_straw"),
                    new BlockConditionedItem(Blocks.BLOCK_BOOKCASE, new Item.Properties().group(Items.decoTab), Config::isBookcaseEnabled).setRegistryName("item_bookcase"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_1, new Item.Properties().group(Items.decoTab), 1).setRegistryName("carved_oak_1"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_2, new Item.Properties().group(Items.decoTab), 2).setRegistryName("carved_oak_2"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_3, new Item.Properties().group(Items.decoTab), 3).setRegistryName("carved_oak_3"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_4, new Item.Properties().group(Items.decoTab), 4).setRegistryName("carved_oak_4"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_5, new Item.Properties().group(Items.decoTab), 5).setRegistryName("carved_oak_5"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_6, new Item.Properties().group(Items.decoTab), 6).setRegistryName("carved_oak_6"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_7, new Item.Properties().group(Items.decoTab), 7).setRegistryName("carved_oak_7"),
                    new CarvedWoodItem(Blocks.CARVED_OAK_8, new Item.Properties().group(Items.decoTab), 8).setRegistryName("carved_oak_8"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_1, new Item.Properties().group(Items.decoTab), 1).setRegistryName("carved_birch_1"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_2, new Item.Properties().group(Items.decoTab), 2).setRegistryName("carved_birch_2"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_3, new Item.Properties().group(Items.decoTab), 3).setRegistryName("carved_birch_3"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_4, new Item.Properties().group(Items.decoTab), 4).setRegistryName("carved_birch_4"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_5, new Item.Properties().group(Items.decoTab), 5).setRegistryName("carved_birch_5"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_6, new Item.Properties().group(Items.decoTab), 6).setRegistryName("carved_birch_6"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_7, new Item.Properties().group(Items.decoTab), 7).setRegistryName("carved_birch_7"),
                    new CarvedWoodItem(Blocks.CARVED_BIRCH_8, new Item.Properties().group(Items.decoTab), 8).setRegistryName("carved_birch_8"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_1, new Item.Properties().group(Items.decoTab), 1).setRegistryName("carved_dark_oak_1"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_2, new Item.Properties().group(Items.decoTab), 2).setRegistryName("carved_dark_oak_2"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_3, new Item.Properties().group(Items.decoTab), 3).setRegistryName("carved_dark_oak_3"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_4, new Item.Properties().group(Items.decoTab), 4).setRegistryName("carved_dark_oak_4"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_5, new Item.Properties().group(Items.decoTab), 5).setRegistryName("carved_dark_oak_5"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_6, new Item.Properties().group(Items.decoTab), 6).setRegistryName("carved_dark_oak_6"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_7, new Item.Properties().group(Items.decoTab), 7).setRegistryName("carved_dark_oak_7"),
                    new CarvedWoodItem(Blocks.CARVED_DARK_OAK_8, new Item.Properties().group(Items.decoTab), 8).setRegistryName("carved_dark_oak_8"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_1, new Item.Properties().group(Items.decoTab), 1).setRegistryName("carved_acacia_1"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_2, new Item.Properties().group(Items.decoTab), 2).setRegistryName("carved_acacia_2"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_3, new Item.Properties().group(Items.decoTab), 3).setRegistryName("carved_acacia_3"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_4, new Item.Properties().group(Items.decoTab), 4).setRegistryName("carved_acacia_4"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_5, new Item.Properties().group(Items.decoTab), 5).setRegistryName("carved_acacia_5"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_6, new Item.Properties().group(Items.decoTab), 6).setRegistryName("carved_acacia_6"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_7, new Item.Properties().group(Items.decoTab), 7).setRegistryName("carved_acacia_7"),
                    new CarvedWoodItem(Blocks.CARVED_ACACIA_8, new Item.Properties().group(Items.decoTab), 8).setRegistryName("carved_acacia_8"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_1, new Item.Properties().group(Items.decoTab), 1).setRegistryName("carved_jungle_1"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_2, new Item.Properties().group(Items.decoTab), 2).setRegistryName("carved_jungle_2"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_3, new Item.Properties().group(Items.decoTab), 3).setRegistryName("carved_jungle_3"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_4, new Item.Properties().group(Items.decoTab), 4).setRegistryName("carved_jungle_4"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_5, new Item.Properties().group(Items.decoTab), 5).setRegistryName("carved_jungle_5"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_6, new Item.Properties().group(Items.decoTab), 6).setRegistryName("carved_jungle_6"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_7, new Item.Properties().group(Items.decoTab), 7).setRegistryName("carved_jungle_7"),
                    new CarvedWoodItem(Blocks.CARVED_JUNGLE_8, new Item.Properties().group(Items.decoTab), 8).setRegistryName("carved_jungle_8"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_1, new Item.Properties().group(Items.decoTab), 1).setRegistryName("carved_spruce_1"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_2, new Item.Properties().group(Items.decoTab), 2).setRegistryName("carved_spruce_2"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_3, new Item.Properties().group(Items.decoTab), 3).setRegistryName("carved_spruce_3"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_4, new Item.Properties().group(Items.decoTab), 4).setRegistryName("carved_spruce_4"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_5, new Item.Properties().group(Items.decoTab), 5).setRegistryName("carved_spruce_5"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_6, new Item.Properties().group(Items.decoTab), 6).setRegistryName("carved_spruce_6"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_7, new Item.Properties().group(Items.decoTab), 7).setRegistryName("carved_spruce_7"),
                    new CarvedWoodItem(Blocks.CARVED_SPRUCE_8, new Item.Properties().group(Items.decoTab), 8).setRegistryName("carved_spruce_8"),

                    new BlockConditionedItem(Blocks.BLACK_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("black_terratile"),
                    new BlockConditionedItem(Blocks.BLUE_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("blue_terratile"),
                    new BlockConditionedItem(Blocks.BROWN_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("brown_terratile"),
                    new BlockConditionedItem(Blocks.CYAN_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("cyan_terratile"),
                    new BlockConditionedItem(Blocks.GRAY_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("gray_terratile"),
                    new BlockConditionedItem(Blocks.GREEN_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("green_terratile"),
                    new BlockConditionedItem(Blocks.LIGHT_BLUE_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_blue_terratile"),
                    new BlockConditionedItem(Blocks.LIGHT_GRAY_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_gray_terratile"),
                    new BlockConditionedItem(Blocks.LIME_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("lime_terratile"),
                    new BlockConditionedItem(Blocks.MAGENTA_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("magenta_terratile"),
                    new BlockConditionedItem(Blocks.ORANGE_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("orange_terratile"),
                    new BlockConditionedItem(Blocks.PINK_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("pink_terratile"),
                    new BlockConditionedItem(Blocks.PURPLE_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("purple_terratile"),
                    new BlockConditionedItem(Blocks.RED_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("red_terratile"),
                    new BlockConditionedItem(Blocks.WHITE_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("white_terratile"),
                    new BlockConditionedItem(Blocks.YELLOW_TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("yellow_terratile"),
                    new BlockConditionedItem(Blocks.TERRATILE, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("terratile"),

                    new BlockConditionedItem(Blocks.BLACK_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("black_terratile_slab"),
                    new BlockConditionedItem(Blocks.BLUE_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("blue_terratile_slab"),
                    new BlockConditionedItem(Blocks.BROWN_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("brown_terratile_slab"),
                    new BlockConditionedItem(Blocks.CYAN_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("cyan_terratile_slab"),
                    new BlockConditionedItem(Blocks.GRAY_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("gray_terratile_slab"),
                    new BlockConditionedItem(Blocks.GREEN_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("green_terratile_slab"),
                    new BlockConditionedItem(Blocks.LIGHT_BLUE_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_blue_terratile_slab"),
                    new BlockConditionedItem(Blocks.LIGHT_GRAY_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_gray_terratile_slab"),
                    new BlockConditionedItem(Blocks.LIME_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("lime_terratile_slab"),
                    new BlockConditionedItem(Blocks.MAGENTA_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("magenta_terratile_slab"),
                    new BlockConditionedItem(Blocks.ORANGE_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("orange_terratile_slab"),
                    new BlockConditionedItem(Blocks.PINK_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("pink_terratile_slab"),
                    new BlockConditionedItem(Blocks.PURPLE_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("purple_terratile_slab"),
                    new BlockConditionedItem(Blocks.RED_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("red_terratile_slab"),
                    new BlockConditionedItem(Blocks.WHITE_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("white_terratile_slab"),
                    new BlockConditionedItem(Blocks.YELLOW_TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("yellow_terratile_slab"),
                    new BlockConditionedItem(Blocks.TERRATILE_SLAB, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("terratile_slab"),

                    new BlockConditionedItem(Blocks.BLACK_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("black_terratile_stairs"),
                    new BlockConditionedItem(Blocks.BLUE_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("blue_terratile_stairs"),
                    new BlockConditionedItem(Blocks.BROWN_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("brown_terratile_stairs"),
                    new BlockConditionedItem(Blocks.CYAN_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("cyan_terratile_stairs"),
                    new BlockConditionedItem(Blocks.GRAY_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("gray_terratile_stairs"),
                    new BlockConditionedItem(Blocks.GREEN_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("green_terratile_stairs"),
                    new BlockConditionedItem(Blocks.LIGHT_BLUE_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_blue_terratile_stairs"),
                    new BlockConditionedItem(Blocks.LIGHT_GRAY_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("light_gray_terratile_stairs"),
                    new BlockConditionedItem(Blocks.LIME_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("lime_terratile_stairs"),
                    new BlockConditionedItem(Blocks.MAGENTA_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("magenta_terratile_stairs"),
                    new BlockConditionedItem(Blocks.ORANGE_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("orange_terratile_stairs"),
                    new BlockConditionedItem(Blocks.PINK_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("pink_terratile_stairs"),
                    new BlockConditionedItem(Blocks.PURPLE_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("purple_terratile_stairs"),
                    new BlockConditionedItem(Blocks.RED_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("red_terratile_stairs"),
                    new BlockConditionedItem(Blocks.WHITE_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("white_terratile_stairs"),
                    new BlockConditionedItem(Blocks.YELLOW_TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("yellow_terratile_stairs"),
                    new BlockConditionedItem(Blocks.TERRATILE_STAIRS, new Item.Properties().group(Items.decoTab), Config::isTerracottaTileEnabled).setRegistryName("terratile_stairs"),

                    new BlockItem(Blocks.CARVING_STATION, new Item.Properties().group(Items.decoTab)).setRegistryName("carving_station"),

                    new ItemFlask(new Item.Properties().group(ItemGroup.BREWING).maxStackSize(1).maxDamage(160), "flask", false),
                    new ItemFlask(new Item.Properties().maxStackSize(1).maxDamage(160), "flask_milk", true),

                    new BlockConditionedItem(Blocks.ROPE, new Item.Properties().group(Items.decoTab), Config::isRopeEnabled){
                        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                            TranslationTextComponent text = new TranslationTextComponent("xercamod.rope_tooltip");
                            tooltip.add(text.mergeStyle(TextFormatting.BLUE));
                        }
                    }.setRegistryName("rope"),

                    new ItemScythe(ItemTier.WOOD, 3, -2.6f, (new Item.Properties()).group(ItemGroup.TOOLS)).setRegistryName("wooden_scythe"),
                    new ItemScythe(ItemTier.STONE, 3, -2.6f, (new Item.Properties()).group(ItemGroup.TOOLS)).setRegistryName("stone_scythe"),
                    new ItemScythe(ItemTier.IRON, 3, -2.6f, (new Item.Properties()).group(ItemGroup.TOOLS)).setRegistryName("iron_scythe"),
                    new ItemScythe(ItemTier.GOLD, 3, -2.6f, (new Item.Properties()).group(ItemGroup.TOOLS)).setRegistryName("golden_scythe"),
                    new ItemScythe(ItemTier.DIAMOND, 3, -2.6f, (new Item.Properties()).group(ItemGroup.TOOLS)).setRegistryName("diamond_scythe"),
                    new ItemScythe(ItemTier.NETHERITE, 3, -2.6f, (new Item.Properties()).group(ItemGroup.TOOLS).isImmuneToFire()).setRegistryName("netherite_scythe")
            );
        }

        @SubscribeEvent
        public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event){
            event.getRegistry().registerAll(
                    new EnchantmentHeavy(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND),
                    new EnchantmentMaim(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentQuick(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND),
                    new EnchantmentQuake(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentGrappling(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentUppercut(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentPoison(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND),
                    new EnchantmentStealth(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentTurboGrab(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND),
                    new EnchantmentGentleGrab(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentGuillotine(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentCapacity(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND),
                    new EnchantmentRange(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND),
                    new EnchantmentChug(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND)
            );
        }
    }

}