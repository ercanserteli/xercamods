package xerca.xercamod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.BlockPizza;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.crafting.*;
import xerca.xercamod.common.enchantments.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public final class Items {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, XercaMod.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, XercaMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, XercaMod.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, XercaMod.MODID);

    public static final RegistryObject<ItemGlass> ITEM_GLASS = ITEMS.register("item_glass", ItemGlass::new);
    public static final RegistryObject<ItemEnderBow> ENDER_BOW = ITEMS.register("ender_bow", ItemEnderBow::new);
    public static final RegistryObject<Item> ITEM_GOLDEN_COIN_1 = ITEMS.register("item_golden_coin_1", ()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_GOLDEN_COIN_5 = ITEMS.register("item_golden_coin_5", ()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_SHISH_KEBAB = ITEMS.register("raw_shish_kebab", ()->makeFoodItem(Foods.RAW_SHISH_KEBAB));
    public static final RegistryObject<Item> ITEM_SHISH_KEBAB = ITEMS.register("item_shish_kebab", ()->makeFoodItem(Foods.SHISH_KEBAB));
    public static final RegistryObject<Item> ITEM_YOGHURT = ITEMS.register("item_yoghurt", ()->makeContainedFoodItem(Foods.YOGHURT, net.minecraft.world.item.Items.BUCKET, 16));
    public static final RegistryObject<Item> ITEM_HONEYBERRY_YOGHURT = ITEMS.register("item_honeyberry_yoghurt", ()->makeContainedFoodItem(Foods.HONEYBERRY_YOGHURT, net.minecraft.world.item.Items.BOWL, 16));
    public static final RegistryObject<Item> ITEM_HONEY_CUPCAKE = ITEMS.register("item_honey_cupcake", ()->makeFoodItem(Foods.HONEY_CUPCAKE));
    public static final RegistryObject<Item> ITEM_DONER_WRAP = ITEMS.register("item_doner_wrap", ()->makeFoodItem(Foods.DONER_WRAP));
    public static final RegistryObject<Item> ITEM_CHUBBY_DONER = ITEMS.register("item_chubby_doner", ()->makeFoodItem(Foods.CHUBBY_DONER));
    public static final RegistryObject<Item> ITEM_ALEXANDER = ITEMS.register("item_alexander", ()->makeContainedFoodItem(Foods.ALEXANDER, net.minecraft.world.item.Items.BOWL, 16));
    public static final RegistryObject<Item> ITEM_AYRAN = ITEMS.register("item_ayran", ()->makeDrinkItem(Foods.AYRAN, ITEM_GLASS.get()));
    public static final RegistryObject<Item> DONER_SLICE = ITEMS.register("doner_slice", ()->makeFoodItem(Foods.DONER_SLICE));
    public static final RegistryObject<Item> BAKED_RICE_PUDDING = ITEMS.register("baked_rice_pudding", ()->makeContainedFoodItem(Foods.BAKED_RICE_PUDDING, net.minecraft.world.item.Items.BOWL, 16));
    public static final RegistryObject<Item> SWEET_BERRY_JUICE = ITEMS.register("sweet_berry_juice", ()->makeDrinkItem(Foods.SWEET_BERRY_JUICE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> RICE_PUDDING = ITEMS.register("rice_pudding", ()->makeContainedFoodItem(Foods.RICE_PUDDING, net.minecraft.world.item.Items.BOWL, 16));
    public static final RegistryObject<Item> SWEET_BERRY_CUPCAKE_FANCY = ITEMS.register("sweet_berry_cupcake_fancy", ()->makeFoodItem(Foods.SWEET_BERRY_CUPCAKE_FANCY));
    public static final RegistryObject<Item> SWEET_BERRY_CUPCAKE = ITEMS.register("sweet_berry_cupcake", ()->makeFoodItem(Foods.SWEET_BERRY_CUPCAKE));
    public static final RegistryObject<Item> ENDER_CUPCAKE = ITEMS.register("ender_cupcake", ItemEnderCupcake::new);
    public static final RegistryObject<Item> SASHIMI = ITEMS.register("sashimi", ()->makeFoodItem(Foods.SASHIMI));
    public static final RegistryObject<Item> OYAKODON = ITEMS.register("oyakodon", ()->makeContainedFoodItem(Foods.OYAKODON, net.minecraft.world.item.Items.BOWL, 16));
    public static final RegistryObject<Item> BEEF_DONBURI = ITEMS.register("beef_donburi", ()->makeContainedFoodItem(Foods.BEEF_DONBURI, net.minecraft.world.item.Items.BOWL, 16));
    public static final RegistryObject<Item> EGG_SUSHI = ITEMS.register("egg_sushi", ()->makeFoodItem(Foods.EGG_SUSHI));
    public static final RegistryObject<Item> NIGIRI_SUSHI = ITEMS.register("nigiri_sushi", ()->makeFoodItem(Foods.NIGIRI_SUSHI));
    public static final RegistryObject<Item> OMURICE = ITEMS.register("omurice", ()->makeFoodItem(Foods.OMURICE));
    public static final RegistryObject<Item> SAKE = ITEMS.register("sake", ()->makeDrinkItem(Foods.SAKE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> RICEBALL = ITEMS.register("riceball", ()->makeFoodItem(Foods.RICEBALL));
    public static final RegistryObject<Item> SUSHI = ITEMS.register("sushi", ()->makeFoodItem(Foods.SUSHI));
    public static final RegistryObject<Item> COOKED_RICE = ITEMS.register("cooked_rice", ()->makeFoodItem(Foods.COOKED_RICE));
    public static final RegistryObject<Item> COLA = ITEMS.register("cola", ()->makeDrinkItem(Foods.COLA, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_APPLE_CUPCAKE = ITEMS.register("item_apple_cupcake", ()->makeFoodItem(Foods.APPLE_CUPCAKE));
    public static final RegistryObject<Item> ITEM_PUMPKIN_CUPCAKE = ITEMS.register("item_pumpkin_cupcake", ()->makeFoodItem(Foods.PUMPKIN_CUPCAKE));
    public static final RegistryObject<Item> ITEM_COCOA_CUPCAKE = ITEMS.register("item_cocoa_cupcake", ()->makeFoodItem(Foods.COCOA_CUPCAKE));
    public static final RegistryObject<Item> ITEM_MELON_CUPCAKE = ITEMS.register("item_melon_cupcake", ()->makeFoodItem(Foods.MELON_CUPCAKE));
    public static final RegistryObject<Item> ITEM_CARROT_CUPCAKE = ITEMS.register("item_carrot_cupcake", ()->makeFoodItem(Foods.CARROT_CUPCAKE));
    public static final RegistryObject<Item> ITEM_FANCY_APPLE_CUPCAKE = ITEMS.register("item_fancy_apple_cupcake", ()->makeFoodItem(Foods.FANCY_APPLE_CUPCAKE));
    public static final RegistryObject<Item> ITEM_FANCY_PUMPKIN_CUPCAKE = ITEMS.register("item_fancy_pumpkin_cupcake", ()->makeFoodItem(Foods.FANCY_PUMPKIN_CUPCAKE));
    public static final RegistryObject<Item> GLOWBERRY_CUPCAKE = ITEMS.register("glowberry_cupcake", ()->makeFoodItem(Foods.GLOWBERRY_CUPCAKE));
    public static final RegistryObject<Item> ITEM_DONUT = ITEMS.register("item_donut", ()->makeFoodItem(Foods.DONUT));
    public static final RegistryObject<Item> ITEM_FANCY_DONUT = ITEMS.register("item_fancy_donut", ()->makeFoodItem(Foods.FANCY_DONUT));
    public static final RegistryObject<Item> ITEM_SPRINKLES = ITEMS.register("item_sprinkles", ()->makeFoodItem(Foods.SPRINKLES));
    public static final RegistryObject<Item> ITEM_CHOCOLATE = ITEMS.register("item_chocolate", ()->makeFoodItem(Foods.CHOCOLATE));
    public static final RegistryObject<Item> ITEM_BUN = ITEMS.register("item_bun", ()->makeFoodItem(Foods.BUN));
    public static final RegistryObject<Item> ITEM_RAW_PATTY = ITEMS.register("item_raw_patty", ()->makeFoodItem(Foods.RAW_PATTY));
    public static final RegistryObject<Item> ITEM_COOKED_PATTY = ITEMS.register("item_cooked_patty", ()->makeFoodItem(Foods.COOKED_PATTY));
    public static final RegistryObject<Item> ITEM_RAW_CHICKEN_PATTY = ITEMS.register("item_raw_chicken_patty", ()->makeFoodItem(Foods.RAW_CHICKEN_PATTY));
    public static final RegistryObject<Item> ITEM_COOKED_CHICKEN_PATTY = ITEMS.register("item_cooked_chicken_patty", ()->makeFoodItem(Foods.COOKED_CHICKEN_PATTY));
    public static final RegistryObject<Item> ITEM_HAMBURGER = ITEMS.register("item_hamburger", ()->makeFoodItem(Foods.HAMBURGER));
    public static final RegistryObject<Item> ITEM_CHICKEN_BURGER = ITEMS.register("item_chicken_burger", ()->makeFoodItem(Foods.CHICKEN_BURGER));
    public static final RegistryObject<Item> ITEM_MUSHROOM_BURGER = ITEMS.register("item_mushroom_burger", ()->makeFoodItem(Foods.MUSHROOM_BURGER));
    public static final RegistryObject<Item> ITEM_ULTIMATE_BOTTOM = ITEMS.register("item_ultimate_bottom", ()->makeFoodItem(Foods.ULTIMATE_BOTTOM));
    public static final RegistryObject<Item> ITEM_ULTIMATE_TOP = ITEMS.register("item_ultimate_top", ()->makeFoodItem(Foods.ULTIMATE_TOP));
    public static final RegistryObject<Item> CHEESEBURGER = ITEMS.register("cheeseburger", ()->makeFoodItem(Foods.CHEESEBURGER));
    public static final RegistryObject<Item> COLA_EXTRACT = ITEMS.register("cola_extract", ()->new Item(new Item.Properties().craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE)));
    public static final RegistryObject<Item> COLA_POWDER = ITEMS.register("cola_powder", ()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> CARBONATED_WATER = ITEMS.register("carbonated_water", ()->makeDrinkItem(Foods.CARBONATED_WATER, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_ULTIMATE_BURGER = ITEMS.register("item_ultimate_burger", ItemUltimateBurger::new);
    public static final RegistryObject<Item> ITEM_ROTTEN_BURGER = ITEMS.register("item_rotten_burger", ()->makeFoodItem(Foods.ROTTEN_BURGER));
    public static final RegistryObject<Item> ITEM_COOKED_SAUSAGE = ITEMS.register("item_cooked_sausage", ()->makeFoodItem(Foods.COOKED_SAUSAGE));
    public static final RegistryObject<Item> ITEM_HOTDOG = ITEMS.register("item_hotdog", ()->makeFoodItem(Foods.HOTDOG));
    public static final RegistryObject<Item> ITEM_FISH_BREAD = ITEMS.register("item_fish_bread", ()->makeFoodItem(Foods.FISH_BREAD));
    public static final RegistryObject<Item> ITEM_DAISY_SANDWICH = ITEMS.register("item_daisy_sandwich", ()->makeFoodItem(Foods.DAISY_SANDWICH));
    public static final RegistryObject<Item> ITEM_CHICKEN_WRAP = ITEMS.register("item_chicken_wrap", ()->makeFoodItem(Foods.CHICKEN_WRAP));
    public static final RegistryObject<Item> ITEM_RAW_SCHNITZEL = ITEMS.register("item_raw_schnitzel", ()->makeFoodItem(Foods.RAW_SCHNITZEL));
    public static final RegistryObject<Item> ITEM_COOKED_SCHNITZEL = ITEMS.register("item_cooked_schnitzel", ()->makeFoodItem(Foods.COOKED_SCHNITZEL));
    public static final RegistryObject<Item> ITEM_FRIED_EGG = ITEMS.register("item_fried_egg", ()->makeFoodItem(Foods.FRIED_EGG));
    public static final RegistryObject<Item> ITEM_CROISSANT = ITEMS.register("item_croissant", ()->makeFoodItem(Foods.CROISSANT));
    public static final RegistryObject<Item> ITEM_POTATO_FRIES = ITEMS.register("item_potato_fries", ()->makeFoodItem(Foods.POTATO_FRIES));
    public static final RegistryObject<Item> ITEM_ICE_TEA = ITEMS.register("item_ice_tea", ()->makeDrinkItem(Foods.ICE_TEA, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_APPLE_JUICE = ITEMS.register("item_apple_juice", ()->makeDrinkItem(Foods.APPLE_JUICE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_CARROT_JUICE = ITEMS.register("item_carrot_juice", ()->makeDrinkItem(Foods.CARROT_JUICE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_MELON_JUICE = ITEMS.register("item_melon_juice", ()->makeDrinkItem(Foods.MELON_JUICE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_PUMPKIN_JUICE = ITEMS.register("item_pumpkin_juice", ()->makeDrinkItem(Foods.PUMPKIN_JUICE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_TOMATO_JUICE = ITEMS.register("item_tomato_juice", ()->makeDrinkItem(Foods.TOMATO_JUICE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_WHEAT_JUICE = ITEMS.register("item_wheat_juice", ()->makeDrinkItem(Foods.WHEAT_JUICE, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_GLASS_OF_MILK = ITEMS.register("item_glass_of_milk", ()->makeDrinkItem(Foods.GLASS_OF_MILK, ITEM_GLASS.get()));
    public static final RegistryObject<Item> ITEM_GLASS_OF_WATER = ITEMS.register("item_glass_of_water", ()->new ItemGlassOfWater(new Item.Properties().food(Foods.GLASS_OF_WATER), ITEM_GLASS.get()));
    public static final RegistryObject<Item> SODA = ITEMS.register("soda", ()->makeDrinkItem(Foods.SODA, ITEM_GLASS.get()));
    public static final RegistryObject<Item> CHEESE_TOAST = ITEMS.register("cheese_toast", ()->makeFoodItem(Foods.CHEESE_TOAST));
    public static final RegistryObject<Item> SQUID_INK_PAELLA = ITEMS.register("squid_ink_paella", ()->makeContainedFoodItem(Foods.SQUID_INK_PAELLA, net.minecraft.world.item.Items.BOWL, 16));
    public static final RegistryObject<Item> GLOW_SQUID_INK_PAELLA = ITEMS.register("glow_squid_ink_paella", ()->makeContainedFoodItem(Foods.GLOW_SQUID_INK_PAELLA, net.minecraft.world.item.Items.BOWL, 16));

    public static final RegistryObject<Item> ITEM_APPLE_PIE = ITEMS.register("item_apple_pie", ()->new BlockItem(Blocks.BLOCK_APPLE_PIE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SWEET_BERRY_PIE = ITEMS.register("sweet_berry_pie", ()->new BlockItem(Blocks.BLOCK_SWEET_BERRY_PIE.get(), new Item.Properties()));

    public static final List<RegistryObject<Item>> RAW_PIZZAS = Arrays.<RegistryObject<Item>>asList(
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, Foods.RAW_PIZZA_3)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_2)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_1)),
            ITEMS.register(ItemRawPizza.genName(BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () -> new ItemRawPizza(BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, Foods.RAW_PIZZA_0)));

    public static final List<RegistryObject<Item>> PIZZAS = Arrays.<RegistryObject<Item>>asList(
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_PEPPERONI_PEPPERONI_PEPPERONI.get(), BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_MUSHROOM_PEPPERONI_PEPPERONI.get(), BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_MUSHROOM_MUSHROOM_PEPPERONI.get(), BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_MUSHROOM_MUSHROOM_MUSHROOM.get(), BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_MEAT_PEPPERONI_PEPPERONI.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_MEAT_MUSHROOM_PEPPERONI.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_MEAT_MUSHROOM_MUSHROOM.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_MEAT_MEAT_PEPPERONI.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_MEAT_MEAT_MUSHROOM.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT), () ->new ItemPizza(Blocks.PIZZA_MEAT_MEAT_MEAT.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_FISH_PEPPERONI_PEPPERONI.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_FISH_MUSHROOM_PEPPERONI.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_FISH_MUSHROOM_MUSHROOM.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_FISH_MEAT_PEPPERONI.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_FISH_MEAT_MUSHROOM.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT), () ->new ItemPizza(Blocks.PIZZA_FISH_MEAT_MEAT.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_FISH_FISH_PEPPERONI.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_FISH_FISH_MUSHROOM.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT), () ->new ItemPizza(Blocks.PIZZA_FISH_FISH_MEAT.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH), () ->new ItemPizza(Blocks.PIZZA_FISH_FISH_FISH.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_PEPPERONI_PEPPERONI.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_MUSHROOM_PEPPERONI.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_MUSHROOM_MUSHROOM.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT_PEPPERONI.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT_MUSHROOM.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT_MEAT.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_PEPPERONI.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_MUSHROOM.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_MEAT.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_FISH_FISH.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_PEPPERONI.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_MUSHROOM.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_MEAT.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_FISH.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN_CHICKEN.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_PEPPERONI_PEPPERONI.get(), BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_MUSHROOM_PEPPERONI.get(), BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_MUSHROOM_MUSHROOM.get(), BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_MEAT_PEPPERONI.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_MEAT_MUSHROOM.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_MEAT_MEAT.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_FISH_PEPPERONI.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_FISH_MUSHROOM.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_FISH_MEAT.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_FISH_FISH.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_PEPPERONI.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_MUSHROOM.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_MEAT.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_FISH.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_CHICKEN_CHICKEN.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_PEPPERONI.get(), BlockPizza.Ingredient.PEPPERONI, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_MUSHROOM.get(), BlockPizza.Ingredient.MUSHROOM, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_MEAT.get(), BlockPizza.Ingredient.MEAT, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_FISH.get(), BlockPizza.Ingredient.FISH, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA_CHICKEN.get(), BlockPizza.Ingredient.CHICKEN, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY)),
            ITEMS.register(ItemPizza.genName(BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY), () ->new ItemPizza(Blocks.PIZZA.get(), BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY, BlockPizza.Ingredient.EMPTY))
    );

    public static final RegistryObject<Item> ITEM_TEACUP = ITEMS.register("item_teacup", ()->new ItemTea(new Item.Properties()));
    public static final RegistryObject<ItemTeacup> ITEM_FULL_TEACUP_0 = ITEMS.register("item_full_teacup_0", ()->new ItemTeacup(0, ITEM_TEACUP.get()));
    public static final RegistryObject<ItemTeacup> ITEM_FULL_TEACUP_1 = ITEMS.register("item_full_teacup_1", ()->new ItemTeacup(1, ITEM_TEACUP.get()));
    public static final RegistryObject<ItemTeacup> ITEM_FULL_TEACUP_2 = ITEMS.register("item_full_teacup_2", ()->new ItemTeacup(2, ITEM_TEACUP.get()));
    public static final RegistryObject<ItemTeacup> ITEM_FULL_TEACUP_3 = ITEMS.register("item_full_teacup_3", ()->new ItemTeacup(3, ITEM_TEACUP.get()));
    public static final RegistryObject<ItemTeacup> ITEM_FULL_TEACUP_4 = ITEMS.register("item_full_teacup_4", ()->new ItemTeacup(4, ITEM_TEACUP.get()));
    public static final RegistryObject<ItemTeacup> ITEM_FULL_TEACUP_5 = ITEMS.register("item_full_teacup_5", ()->new ItemTeacup(5, ITEM_TEACUP.get()));
    public static final RegistryObject<ItemTeacup> ITEM_FULL_TEACUP_6 = ITEMS.register("item_full_teacup_6", ()->new ItemTeacup(6, ITEM_TEACUP.get()));

    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_0 = ITEMS.register("item_full_teapot_0", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 0, false));
    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_1 = ITEMS.register("item_full_teapot_1", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 1, false));
    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_2 = ITEMS.register("item_full_teapot_2", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 2, false));
    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_3 = ITEMS.register("item_full_teapot_3", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 3, false));
    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_4 = ITEMS.register("item_full_teapot_4", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 4, false));
    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_5 = ITEMS.register("item_full_teapot_5", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 5, false));
    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_6 = ITEMS.register("item_full_teapot_6", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 6, false));
    public static final RegistryObject<ItemTeapot> ITEM_FULL_TEAPOT_7 = ITEMS.register("item_full_teapot_7", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 7, false));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_0 = ITEMS.register("item_hot_teapot_0", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 0, true));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_1 = ITEMS.register("item_hot_teapot_1", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 1, true));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_2 = ITEMS.register("item_hot_teapot_2", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 2, true));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_3 = ITEMS.register("item_hot_teapot_3", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 3, true));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_4 = ITEMS.register("item_hot_teapot_4", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 4, true));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_5 = ITEMS.register("item_hot_teapot_5", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 5, true));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_6 = ITEMS.register("item_hot_teapot_6", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 6, true));
    public static final RegistryObject<ItemTeapot> ITEM_HOT_TEAPOT_7 = ITEMS.register("item_hot_teapot_7", ()->new ItemTeapot(Blocks.BLOCK_TEAPOT.get(), 7, true));
    public static final RegistryObject<ItemEmptyTeapot> ITEM_TEAPOT = ITEMS.register("item_teapot", ()->new ItemEmptyTeapot(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_TEA_SEEDS = ITEMS.register("item_tea_seeds", ()->new BlockItem(Blocks.BLOCK_TEA_PLANT.get(), new Item.Properties()));
    public static final RegistryObject<Item> ITEM_TEA_DRIED = ITEMS.register("item_tea_dried", ()->new ItemTea(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_TEA_LEAF = ITEMS.register("item_tea_leaf", ()->new ItemTea(new Item.Properties()));

    public static final RegistryObject<ItemGoldenCupcake> ITEM_GOLDEN_CUPCAKE = ITEMS.register("item_golden_cupcake", ItemGoldenCupcake::new);

    public static final RegistryObject<ItemKnife> ITEM_KNIFE = ITEMS.register("item_knife", ItemKnife::new);
    public static final RegistryObject<ItemTomato> ITEM_TOMATO = ITEMS.register("item_tomato", ItemTomato::new);
    public static final RegistryObject<Item> ITEM_TOMATO_SEEDS = ITEMS.register("item_tomato_seeds", ()->new BlockItem(Blocks.BLOCK_TOMATO_PLANT.get(), new Item.Properties()));
    public static final RegistryObject<Item> ITEM_RICE_SEEDS = ITEMS.register("item_rice_seeds", ()->new BlockItem(Blocks.BLOCK_RICE_PLANT.get(), new Item.Properties()));
    public static final RegistryObject<ItemGavel> ITEM_GAVEL = ITEMS.register("item_gavel", ItemGavel::new);
    public static final RegistryObject<ItemBadge> ITEM_PROSECUTOR_BADGE = ITEMS.register("item_prosecutor_badge", ItemBadge::new);
    public static final RegistryObject<ItemBadge> ITEM_ATTORNEY_BADGE = ITEMS.register("item_attorney_badge", ItemBadge::new);

    public static final RegistryObject<ItemGrabHook> ITEM_GRAB_HOOK = ITEMS.register("item_grab_hook", ItemGrabHook::new);
    public static final RegistryObject<ItemWarhammer> ITEM_NETHERITE_WARHAMMER = ITEMS.register("item_netherite_warhammer", ()->new ItemWarhammer(Tiers.NETHERITE));
    public static final RegistryObject<ItemWarhammer> ITEM_DIAMOND_WARHAMMER = ITEMS.register("item_diamond_warhammer", ()->new ItemWarhammer(Tiers.DIAMOND));
    public static final RegistryObject<ItemWarhammer> ITEM_GOLD_WARHAMMER = ITEMS.register("item_gold_warhammer", ()->new ItemWarhammer(Tiers.GOLD));
    public static final RegistryObject<ItemWarhammer> ITEM_IRON_WARHAMMER = ITEMS.register("item_iron_warhammer", ()->new ItemWarhammer(Tiers.IRON));
    public static final RegistryObject<ItemWarhammer> ITEM_STONE_WARHAMMER = ITEMS.register("item_stone_warhammer", ()->new ItemWarhammer(Tiers.STONE));
    public static final RegistryObject<ItemConfettiBall> ITEM_CONFETTI_BALL = ITEMS.register("item_confetti_ball", ItemConfettiBall::new);
    public static final RegistryObject<ItemConfetti> ITEM_CONFETTI = ITEMS.register("item_confetti", ItemConfetti::new);

    public static final RegistryObject<Item> ITEM_TOMATO_SLICES = ITEMS.register("item_tomato_slices", ()->makeFoodItem(Foods.TOMATO_SLICES));
    public static final RegistryObject<Item> ITEM_POTATO_SLICES = ITEMS.register("item_potato_slices", ()->makeFoodItem(Foods.POTATO_SLICES));
    public static final RegistryObject<Item> ITEM_RAW_SAUSAGE = ITEMS.register("item_raw_sausage", ()->makeFoodItem(Foods.RAW_SAUSAGE));

    public static final RegistryObject<ItemCushion> BLACK_CUSHION = ITEMS.register("black_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.BLACK_CUSHION.get()));
    public static final RegistryObject<ItemCushion> BLUE_CUSHION = ITEMS.register("blue_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.BLUE_CUSHION.get()));
    public static final RegistryObject<ItemCushion> BROWN_CUSHION = ITEMS.register("brown_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.BROWN_CUSHION.get()));
    public static final RegistryObject<ItemCushion> CYAN_CUSHION = ITEMS.register("cyan_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.CYAN_CUSHION.get()));
    public static final RegistryObject<ItemCushion> GRAY_CUSHION = ITEMS.register("gray_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.GRAY_CUSHION.get()));
    public static final RegistryObject<ItemCushion> GREEN_CUSHION = ITEMS.register("green_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.GREEN_CUSHION.get()));
    public static final RegistryObject<ItemCushion> LIGHT_BLUE_CUSHION = ITEMS.register("light_blue_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.LIGHT_BLUE_CUSHION.get()));
    public static final RegistryObject<ItemCushion> LIGHT_GRAY_CUSHION = ITEMS.register("light_gray_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.LIGHT_GRAY_CUSHION.get()));
    public static final RegistryObject<ItemCushion> LIME_CUSHION = ITEMS.register("lime_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.LIME_CUSHION.get()));
    public static final RegistryObject<ItemCushion> MAGENTA_CUSHION = ITEMS.register("magenta_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.MAGENTA_CUSHION.get()));
    public static final RegistryObject<ItemCushion> ORANGE_CUSHION = ITEMS.register("orange_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.ORANGE_CUSHION.get()));
    public static final RegistryObject<ItemCushion> PINK_CUSHION = ITEMS.register("pink_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.PINK_CUSHION.get()));
    public static final RegistryObject<ItemCushion> PURPLE_CUSHION = ITEMS.register("purple_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.PURPLE_CUSHION.get()));
    public static final RegistryObject<ItemCushion> RED_CUSHION = ITEMS.register("red_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.RED_CUSHION.get()));
    public static final RegistryObject<ItemCushion> WHITE_CUSHION = ITEMS.register("white_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.WHITE_CUSHION.get()));
    public static final RegistryObject<ItemCushion> YELLOW_CUSHION = ITEMS.register("yellow_cushion", ()->new ItemCushion(new Item.Properties(), Blocks.YELLOW_CUSHION.get()));

    public static final RegistryObject<ItemFlask> FLASK = ITEMS.register("flask", ()->new ItemFlask(new Item.Properties().stacksTo(1).durability(160), false));
    public static final RegistryObject<ItemFlask> FLASK_MILK = ITEMS.register("flask_milk", ()->new ItemFlask(new Item.Properties().stacksTo(1).durability(160), true));

    public static final RegistryObject<Item> ITEM_BLOCK_LEATHER = ITEMS.register("item_block_leather", ()->new BlockItem(Blocks.BLOCK_LEATHER.get(), new Item.Properties()));
    public static final RegistryObject<Item> ITEM_BLOCK_STRAW = ITEMS.register("item_block_straw", ()->new BlockItem(Blocks.BLOCK_STRAW.get(), new Item.Properties()));

    public static final RegistryObject<Item> CARVED_OAK_1 = ITEMS.register("carved_oak_1", ()->new CarvedWoodItem(Blocks.CARVED_OAK_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_OAK_2 = ITEMS.register("carved_oak_2", ()->new CarvedWoodItem(Blocks.CARVED_OAK_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_OAK_3 = ITEMS.register("carved_oak_3", ()->new CarvedWoodItem(Blocks.CARVED_OAK_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_OAK_4 = ITEMS.register("carved_oak_4", ()->new CarvedWoodItem(Blocks.CARVED_OAK_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_OAK_5 = ITEMS.register("carved_oak_5", ()->new CarvedWoodItem(Blocks.CARVED_OAK_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_OAK_6 = ITEMS.register("carved_oak_6", ()->new CarvedWoodItem(Blocks.CARVED_OAK_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_OAK_7 = ITEMS.register("carved_oak_7", ()->new CarvedWoodItem(Blocks.CARVED_OAK_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_OAK_8 = ITEMS.register("carved_oak_8", ()->new CarvedWoodItem(Blocks.CARVED_OAK_8.get(), new Item.Properties(), 8));
    public static final RegistryObject<Item> CARVED_BIRCH_1 = ITEMS.register("carved_birch_1", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_BIRCH_2 = ITEMS.register("carved_birch_2", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_BIRCH_3 = ITEMS.register("carved_birch_3", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_BIRCH_4 = ITEMS.register("carved_birch_4", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_BIRCH_5 = ITEMS.register("carved_birch_5", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_BIRCH_6 = ITEMS.register("carved_birch_6", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_BIRCH_7 = ITEMS.register("carved_birch_7", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_BIRCH_8 = ITEMS.register("carved_birch_8", ()->new CarvedWoodItem(Blocks.CARVED_BIRCH_8.get(), new Item.Properties(), 8));
    public static final RegistryObject<Item> CARVED_DARK_OAK_1 = ITEMS.register("carved_dark_oak_1", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_DARK_OAK_2 = ITEMS.register("carved_dark_oak_2", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_DARK_OAK_3 = ITEMS.register("carved_dark_oak_3", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_DARK_OAK_4 = ITEMS.register("carved_dark_oak_4", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_DARK_OAK_5 = ITEMS.register("carved_dark_oak_5", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_DARK_OAK_6 = ITEMS.register("carved_dark_oak_6", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_DARK_OAK_7 = ITEMS.register("carved_dark_oak_7", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_DARK_OAK_8 = ITEMS.register("carved_dark_oak_8", ()->new CarvedWoodItem(Blocks.CARVED_DARK_OAK_8.get(), new Item.Properties(), 8));
    public static final RegistryObject<Item> CARVED_ACACIA_1 = ITEMS.register("carved_acacia_1", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_ACACIA_2 = ITEMS.register("carved_acacia_2", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_ACACIA_3 = ITEMS.register("carved_acacia_3", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_ACACIA_4 = ITEMS.register("carved_acacia_4", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_ACACIA_5 = ITEMS.register("carved_acacia_5", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_ACACIA_6 = ITEMS.register("carved_acacia_6", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_ACACIA_7 = ITEMS.register("carved_acacia_7", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_ACACIA_8 = ITEMS.register("carved_acacia_8", ()->new CarvedWoodItem(Blocks.CARVED_ACACIA_8.get(), new Item.Properties(), 8));
    public static final RegistryObject<Item> CARVED_JUNGLE_1 = ITEMS.register("carved_jungle_1", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_JUNGLE_2 = ITEMS.register("carved_jungle_2", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_JUNGLE_3 = ITEMS.register("carved_jungle_3", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_JUNGLE_4 = ITEMS.register("carved_jungle_4", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_JUNGLE_5 = ITEMS.register("carved_jungle_5", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_JUNGLE_6 = ITEMS.register("carved_jungle_6", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_JUNGLE_7 = ITEMS.register("carved_jungle_7", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_JUNGLE_8 = ITEMS.register("carved_jungle_8", ()->new CarvedWoodItem(Blocks.CARVED_JUNGLE_8.get(), new Item.Properties(), 8));
    public static final RegistryObject<Item> CARVED_SPRUCE_1 = ITEMS.register("carved_spruce_1", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_SPRUCE_2 = ITEMS.register("carved_spruce_2", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_SPRUCE_3 = ITEMS.register("carved_spruce_3", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_SPRUCE_4 = ITEMS.register("carved_spruce_4", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_SPRUCE_5 = ITEMS.register("carved_spruce_5", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_SPRUCE_6 = ITEMS.register("carved_spruce_6", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_SPRUCE_7 = ITEMS.register("carved_spruce_7", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_SPRUCE_8 = ITEMS.register("carved_spruce_8", ()->new CarvedWoodItem(Blocks.CARVED_SPRUCE_8.get(), new Item.Properties(), 8));
    public static final RegistryObject<Item> CARVED_CRIMSON_1 = ITEMS.register("carved_crimson_1", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_CRIMSON_2 = ITEMS.register("carved_crimson_2", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_CRIMSON_3 = ITEMS.register("carved_crimson_3", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_CRIMSON_4 = ITEMS.register("carved_crimson_4", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_CRIMSON_5 = ITEMS.register("carved_crimson_5", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_CRIMSON_6 = ITEMS.register("carved_crimson_6", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_CRIMSON_7 = ITEMS.register("carved_crimson_7", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_CRIMSON_8 = ITEMS.register("carved_crimson_8", ()->new CarvedWoodItem(Blocks.CARVED_CRIMSON_8.get(), new Item.Properties(), 8));
    public static final RegistryObject<Item> CARVED_WARPED_1 = ITEMS.register("carved_warped_1", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_1.get(), new Item.Properties(), 1));
    public static final RegistryObject<Item> CARVED_WARPED_2 = ITEMS.register("carved_warped_2", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_2.get(), new Item.Properties(), 2));
    public static final RegistryObject<Item> CARVED_WARPED_3 = ITEMS.register("carved_warped_3", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_3.get(), new Item.Properties(), 3));
    public static final RegistryObject<Item> CARVED_WARPED_4 = ITEMS.register("carved_warped_4", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_4.get(), new Item.Properties(), 4));
    public static final RegistryObject<Item> CARVED_WARPED_5 = ITEMS.register("carved_warped_5", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_5.get(), new Item.Properties(), 5));
    public static final RegistryObject<Item> CARVED_WARPED_6 = ITEMS.register("carved_warped_6", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_6.get(), new Item.Properties(), 6));
    public static final RegistryObject<Item> CARVED_WARPED_7 = ITEMS.register("carved_warped_7", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_7.get(), new Item.Properties(), 7));
    public static final RegistryObject<Item> CARVED_WARPED_8 = ITEMS.register("carved_warped_8", ()->new CarvedWoodItem(Blocks.CARVED_WARPED_8.get(), new Item.Properties(), 8));

    public static final RegistryObject<Item> BLACK_TERRATILE = ITEMS.register("black_terratile", ()->new BlockItem(Blocks.BLACK_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUE_TERRATILE = ITEMS.register("blue_terratile", ()->new BlockItem(Blocks.BLUE_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BROWN_TERRATILE = ITEMS.register("brown_terratile", ()->new BlockItem(Blocks.BROWN_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CYAN_TERRATILE = ITEMS.register("cyan_terratile", ()->new BlockItem(Blocks.CYAN_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GRAY_TERRATILE = ITEMS.register("gray_terratile", ()->new BlockItem(Blocks.GRAY_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREEN_TERRATILE = ITEMS.register("green_terratile", ()->new BlockItem(Blocks.GREEN_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_BLUE_TERRATILE = ITEMS.register("light_blue_terratile", ()->new BlockItem(Blocks.LIGHT_BLUE_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_GRAY_TERRATILE = ITEMS.register("light_gray_terratile", ()->new BlockItem(Blocks.LIGHT_GRAY_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIME_TERRATILE = ITEMS.register("lime_terratile", ()->new BlockItem(Blocks.LIME_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MAGENTA_TERRATILE = ITEMS.register("magenta_terratile", ()->new BlockItem(Blocks.MAGENTA_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_TERRATILE = ITEMS.register("orange_terratile", ()->new BlockItem(Blocks.ORANGE_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> PINK_TERRATILE = ITEMS.register("pink_terratile", ()->new BlockItem(Blocks.PINK_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> PURPLE_TERRATILE = ITEMS.register("purple_terratile", ()->new BlockItem(Blocks.PURPLE_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RED_TERRATILE = ITEMS.register("red_terratile", ()->new BlockItem(Blocks.RED_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> WHITE_TERRATILE = ITEMS.register("white_terratile", ()->new BlockItem(Blocks.WHITE_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_TERRATILE = ITEMS.register("yellow_terratile", ()->new BlockItem(Blocks.YELLOW_TERRATILE.get(), new Item.Properties()));
    public static final RegistryObject<Item> TERRATILE = ITEMS.register("terratile", ()->new BlockItem(Blocks.TERRATILE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLACK_TERRATILE_SLAB = ITEMS.register("black_terratile_slab", ()->new BlockItem(Blocks.BLACK_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUE_TERRATILE_SLAB = ITEMS.register("blue_terratile_slab", ()->new BlockItem(Blocks.BLUE_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> BROWN_TERRATILE_SLAB = ITEMS.register("brown_terratile_slab", ()->new BlockItem(Blocks.BROWN_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> CYAN_TERRATILE_SLAB = ITEMS.register("cyan_terratile_slab", ()->new BlockItem(Blocks.CYAN_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> GRAY_TERRATILE_SLAB = ITEMS.register("gray_terratile_slab", ()->new BlockItem(Blocks.GRAY_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREEN_TERRATILE_SLAB = ITEMS.register("green_terratile_slab", ()->new BlockItem(Blocks.GREEN_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_BLUE_TERRATILE_SLAB = ITEMS.register("light_blue_terratile_slab", ()->new BlockItem(Blocks.LIGHT_BLUE_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_GRAY_TERRATILE_SLAB = ITEMS.register("light_gray_terratile_slab", ()->new BlockItem(Blocks.LIGHT_GRAY_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIME_TERRATILE_SLAB = ITEMS.register("lime_terratile_slab", ()->new BlockItem(Blocks.LIME_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> MAGENTA_TERRATILE_SLAB = ITEMS.register("magenta_terratile_slab", ()->new BlockItem(Blocks.MAGENTA_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_TERRATILE_SLAB = ITEMS.register("orange_terratile_slab", ()->new BlockItem(Blocks.ORANGE_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> PINK_TERRATILE_SLAB = ITEMS.register("pink_terratile_slab", ()->new BlockItem(Blocks.PINK_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> PURPLE_TERRATILE_SLAB = ITEMS.register("purple_terratile_slab", ()->new BlockItem(Blocks.PURPLE_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> RED_TERRATILE_SLAB = ITEMS.register("red_terratile_slab", ()->new BlockItem(Blocks.RED_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> WHITE_TERRATILE_SLAB = ITEMS.register("white_terratile_slab", ()->new BlockItem(Blocks.WHITE_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_TERRATILE_SLAB = ITEMS.register("yellow_terratile_slab", ()->new BlockItem(Blocks.YELLOW_TERRATILE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<Item> TERRATILE_SLAB = ITEMS.register("terratile_slab", ()->new BlockItem(Blocks.TERRATILE_SLAB.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLACK_TERRATILE_STAIRS = ITEMS.register("black_terratile_stairs", ()->new BlockItem(Blocks.BLACK_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUE_TERRATILE_STAIRS = ITEMS.register("blue_terratile_stairs", ()->new BlockItem(Blocks.BLUE_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> BROWN_TERRATILE_STAIRS = ITEMS.register("brown_terratile_stairs", ()->new BlockItem(Blocks.BROWN_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> CYAN_TERRATILE_STAIRS = ITEMS.register("cyan_terratile_stairs", ()->new BlockItem(Blocks.CYAN_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> GRAY_TERRATILE_STAIRS = ITEMS.register("gray_terratile_stairs", ()->new BlockItem(Blocks.GRAY_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREEN_TERRATILE_STAIRS = ITEMS.register("green_terratile_stairs", ()->new BlockItem(Blocks.GREEN_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_BLUE_TERRATILE_STAIRS = ITEMS.register("light_blue_terratile_stairs", ()->new BlockItem(Blocks.LIGHT_BLUE_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_GRAY_TERRATILE_STAIRS = ITEMS.register("light_gray_terratile_stairs", ()->new BlockItem(Blocks.LIGHT_GRAY_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> LIME_TERRATILE_STAIRS = ITEMS.register("lime_terratile_stairs", ()->new BlockItem(Blocks.LIME_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> MAGENTA_TERRATILE_STAIRS = ITEMS.register("magenta_terratile_stairs", ()->new BlockItem(Blocks.MAGENTA_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_TERRATILE_STAIRS = ITEMS.register("orange_terratile_stairs", ()->new BlockItem(Blocks.ORANGE_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> PINK_TERRATILE_STAIRS = ITEMS.register("pink_terratile_stairs", ()->new BlockItem(Blocks.PINK_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> PURPLE_TERRATILE_STAIRS = ITEMS.register("purple_terratile_stairs", ()->new BlockItem(Blocks.PURPLE_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> RED_TERRATILE_STAIRS = ITEMS.register("red_terratile_stairs", ()->new BlockItem(Blocks.RED_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> WHITE_TERRATILE_STAIRS = ITEMS.register("white_terratile_stairs", ()->new BlockItem(Blocks.WHITE_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_TERRATILE_STAIRS = ITEMS.register("yellow_terratile_stairs", ()->new BlockItem(Blocks.YELLOW_TERRATILE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<Item> TERRATILE_STAIRS = ITEMS.register("terratile_stairs", ()->new BlockItem(Blocks.TERRATILE_STAIRS.get(), new Item.Properties()));

    public static final RegistryObject<Item> CARVING_STATION = ITEMS.register("carving_station", ()->new BlockItem(Blocks.CARVING_STATION.get(), new Item.Properties()){
        @Override public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {return 300;}
    });
    public static final RegistryObject<Item> ITEM_BOOKCASE = ITEMS.register("item_bookcase", ()->new BlockItem(Blocks.BLOCK_BOOKCASE.get(), new Item.Properties()){
        @Override public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {return 300;}
    });

    public static final RegistryObject<Item> ROPE = ITEMS.register("rope", ()->new BlockItem(Blocks.ROPE.get(), new Item.Properties()){
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            MutableComponent text = Component.translatable("xercamod.rope_tooltip");
            tooltip.add(text.withStyle(ChatFormatting.BLUE));
        }
    });

    public static final RegistryObject<Item> VAT = ITEMS.register("vat", ()->new BlockItem(Blocks.VAT.get(), new Item.Properties()));
    public static final RegistryObject<Item> CHEESE_WHEEL = ITEMS.register("cheese_wheel", ()->new BlockItem(Blocks.CHEESE_WHEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> CHEESE_SLICE = ITEMS.register("cheese_slice", ()->makeFoodItem(Foods.CHEESE_SLICE));

    public static final RegistryObject<BlockItemOmniChest> OMNI_CHEST = ITEMS.register("omni_chest", ()->new BlockItemOmniChest(Blocks.OMNI_CHEST.get(), new Item.Properties()));

    public static final RegistryObject<ItemScythe> WOODEN_SCYTHE = ITEMS.register("wooden_scythe", ()->new ItemScythe(Tiers.WOOD, 3, -2.6f, (new Item.Properties())));
    public static final RegistryObject<ItemScythe> STONE_SCYTHE = ITEMS.register("stone_scythe", ()->new ItemScythe(Tiers.STONE, 3, -2.6f, (new Item.Properties())));
    public static final RegistryObject<ItemScythe> IRON_SCYTHE = ITEMS.register("iron_scythe", ()->new ItemScythe(Tiers.IRON, 3, -2.6f, (new Item.Properties())));
    public static final RegistryObject<ItemScythe> GOLDEN_SCYTHE = ITEMS.register("golden_scythe", ()->new ItemScythe(Tiers.GOLD, 3, -2.6f, (new Item.Properties())));
    public static final RegistryObject<ItemScythe> DIAMOND_SCYTHE = ITEMS.register("diamond_scythe", ()->new ItemScythe(Tiers.DIAMOND, 3, -2.6f, (new Item.Properties())));
    public static final RegistryObject<ItemScythe> NETHERITE_SCYTHE = ITEMS.register("netherite_scythe", ()->new ItemScythe(Tiers.NETHERITE, 3, -2.6f, (new Item.Properties()).fireResistant()));

    public static final RegistryObject<Enchantment> ENCHANTMENT_HEAVY = ENCHANTMENTS.register("enchantment_heavy", ()->new EnchantmentHeavy(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_MAIM = ENCHANTMENTS.register("enchantment_maim", ()->new EnchantmentMaim(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_QUICK = ENCHANTMENTS.register("enchantment_quick", ()->new EnchantmentQuick(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_QUAKE = ENCHANTMENTS.register("enchantment_quake", ()->new EnchantmentQuake(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_GRAPPLING = ENCHANTMENTS.register("enchantment_grappling", ()->new EnchantmentGrappling(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_UPPERCUT = ENCHANTMENTS.register("enchantment_uppercut", ()->new EnchantmentUppercut(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_POISON = ENCHANTMENTS.register("enchantment_poison", ()->new EnchantmentPoison(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_STEALTH = ENCHANTMENTS.register("enchantment_stealth", ()->new EnchantmentStealth(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_TURBO_GRAB = ENCHANTMENTS.register("enchantment_turbo_grab", ()->new EnchantmentTurboGrab(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_GENTLE_GRAB = ENCHANTMENTS.register("enchantment_gentle_grab", ()->new EnchantmentGentleGrab(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_GUILLOTINE = ENCHANTMENTS.register("enchantment_guillotine", ()->new EnchantmentGuillotine(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_CAPACITY = ENCHANTMENTS.register("enchantment_capacity", ()->new EnchantmentCapacity(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_RANGE = ENCHANTMENTS.register("enchantment_range", ()->new EnchantmentRange(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_CHUG = ENCHANTMENTS.register("enchantment_chug", ()->new EnchantmentChug(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> ENCHANTMENT_DEVOUR = ENCHANTMENTS.register("enchantment_devour", ()->new EnchantmentDevour(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));


    public static final RegistryObject<RecipeSerializer<RecipeTeaSugaring>> CRAFTING_SPECIAL_TEA_SUGARING = RECIPE_SERIALIZERS.register(
            "crafting_special_tea_sugaring", () -> new SimpleCraftingRecipeSerializer<>(RecipeTeaSugaring::new));
    public static final RegistryObject<RecipeSerializer<RecipeTeaPouring>> CRAFTING_SPECIAL_TEA_POURING = RECIPE_SERIALIZERS.register(
            "crafting_special_tea_pouring", () -> new SimpleCraftingRecipeSerializer<>(RecipeTeaPouring::new));
    public static final RegistryObject<RecipeSerializer<RecipeTeaFilling>> CRAFTING_SPECIAL_TEA_FILLING = RECIPE_SERIALIZERS.register(
            "crafting_special_tea_filling", () -> new SimpleCraftingRecipeSerializer<>(RecipeTeaFilling::new));
    public static final RegistryObject<RecipeSerializer<RecipeTeaRefilling>> CRAFTING_SPECIAL_TEA_REFILLING = RECIPE_SERIALIZERS.register(
            "crafting_special_tea_refilling", () -> new SimpleCraftingRecipeSerializer<>(RecipeTeaRefilling::new));
    public static final RegistryObject<RecipeSerializer<RecipeFlaskFilling>> CRAFTING_SPECIAL_FLASK_FILLING = RECIPE_SERIALIZERS.register(
            "crafting_special_flask_filling", () -> new SimpleCraftingRecipeSerializer<>(RecipeFlaskFilling::new));
    public static final RegistryObject<RecipeSerializer<RecipeEnderBowFilling>> CRAFTING_SPECIAL_ENDER_BOW_FILLING = RECIPE_SERIALIZERS.register(
            "crafting_special_ender_bow_filling", () -> new SimpleCraftingRecipeSerializer<>(RecipeEnderBowFilling::new));
    public static final RegistryObject<RecipeSerializer<RecipeFlaskMilkFilling>> CRAFTING_SPECIAL_FLASK_MILK_FILLING = RECIPE_SERIALIZERS.register(
            "crafting_special_flask_milk_filling", () -> new SimpleCraftingRecipeSerializer<>(RecipeFlaskMilkFilling::new));
    public static final RegistryObject<RecipeSerializer<RecipeWoodCarving>> CRAFTING_SPECIAL_WOOD_CARVING = RECIPE_SERIALIZERS.register(
            "crafting_special_wood_carving", () -> new SimpleCraftingRecipeSerializer<>(RecipeWoodCarving::new));

    public static final RegistryObject<RecipeSerializer<RecipeCarvingStation>> CARVING = RECIPE_SERIALIZERS.register(
            "carving", () -> new RecipeCarvingStation.Serializer<>(RecipeCarvingStation::new));

    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_SCYTHE = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_scythe", () -> new RecipeConditionShaped.Serializer(Config::isScytheEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_WARHAMMER = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_warhammer", () -> new RecipeConditionShaped.Serializer(Config::isWarhammerEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_GRAB_HOOK = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_grab_hook", () -> new RecipeConditionShapeless.Serializer(Config::isGrabHookEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_CUSHION = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_cushion", () -> new RecipeConditionShaped.Serializer(Config::isCushionEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_TEA = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_tea", () -> new RecipeConditionShaped.Serializer(Config::isTeaEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_TEA = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_tea", () -> new RecipeConditionShapeless.Serializer(Config::isTeaEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_FOOD = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_food", () -> new RecipeConditionShaped.Serializer(Config::isFoodEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_FOOD = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_food", () -> new RecipeConditionShapeless.Serializer(Config::isFoodEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_ENDER_FLASK = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_ender_flask", () -> new RecipeConditionShaped.Serializer(Config::isEnderFlaskEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_CONFETTI = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_confetti", () -> new RecipeConditionShaped.Serializer(Config::isConfettiEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_CONFETTI = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_confetti", () -> new RecipeConditionShapeless.Serializer(Config::isConfettiEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_COURTROOM = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_courtroom", () -> new RecipeConditionShaped.Serializer(Config::isCourtroomEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_COURTROOM = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_courtroom", () -> new RecipeConditionShapeless.Serializer(Config::isCourtroomEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_CARVED_WOOD = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_carved_wood", () -> new RecipeConditionShaped.Serializer(Config::isCarvedWoodEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_LEATHER_STRAW = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_leather_straw", () -> new RecipeConditionShaped.Serializer(Config::isLeatherStrawEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_LEATHER_STRAW = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_leather_straw", () -> new RecipeConditionShapeless.Serializer(Config::isLeatherStrawEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_BOOKCASE = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_bookcase", () -> new RecipeConditionShaped.Serializer(Config::isBookcaseEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_COINS = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_coins", () -> new RecipeConditionShapeless.Serializer(Config::isCoinsEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShapeless>> CRAFTING_CONDITION_SHAPELESS_ROPE = RECIPE_SERIALIZERS.register(
            "crafting_condition_shapeless_rope", () -> new RecipeConditionShapeless.Serializer(Config::isRopeEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_TERRACOTTA_TILE = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_terracotta_tile", () -> new RecipeConditionShaped.Serializer(Config::isTerracottaTileEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionShaped>> CRAFTING_CONDITION_SHAPED_OMNI_CHEST = RECIPE_SERIALIZERS.register(
            "crafting_condition_shaped_omni_chest", () -> new RecipeConditionShaped.Serializer(Config::isOmniChestEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionSmelting>> CRAFTING_CONDITION_SMELTING_FOOD = RECIPE_SERIALIZERS.register(
            "crafting_condition_smelting_food", () -> new RecipeConditionSmelting.Serializer(Config::isFoodEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionCampfire>> CRAFTING_CONDITION_CAMPFIRE_FOOD = RECIPE_SERIALIZERS.register(
            "crafting_condition_campfire_food", () -> new RecipeConditionCampfire.Serializer(Config::isFoodEnabled));
    public static final RegistryObject<RecipeSerializer<RecipeConditionSmoking>> CRAFTING_CONDITION_SMOKING_FOOD = RECIPE_SERIALIZERS.register(
            "crafting_condition_smoking_food", () -> new RecipeConditionSmoking.Serializer(Config::isFoodEnabled));

    public static final RegistryObject<RecipeType<RecipeCarvingStation>> CARVING_STATION_TYPE = RECIPE_TYPES.register("carving",
            ()->RecipeType.simple(new ResourceLocation(XercaMod.MODID, "carving")));


    static Item makeFoodItem(FoodProperties food){
        return new Item(new Item.Properties().food(food));
    }

    static Item makeContainedFoodItem(FoodProperties food, Item container, @SuppressWarnings("SameParameterValue") int stackSize){
        return new ItemStackableContainedFood(new Item.Properties().food(food).craftRemainder(container), container, stackSize);
    }

    static Item makeDrinkItem(FoodProperties food, Item container){
        return new ItemDrink(new Item.Properties().food(food), container);
    }

    static void registerCompostable(float chance, ItemLike itemIn) {
        ComposterBlock.COMPOSTABLES.put(itemIn.asItem(), chance);
    }

    public static void registerCompostables() {
        registerCompostable(0.3f, ITEM_TEA_SEEDS.get());
        registerCompostable(0.3f, ITEM_TOMATO_SEEDS.get());
        registerCompostable(0.3f, ITEM_RICE_SEEDS.get());
        registerCompostable(0.65f, ITEM_TOMATO.get());
        registerCompostable(0.65f, ITEM_TEA_DRIED.get());
        registerCompostable(0.65f, ITEM_TEA_LEAF.get());
    }
}