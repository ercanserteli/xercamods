package xerca.xercamod.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class Foods {
    public static FoodProperties RAW_SHISH_KEBAB = makeFood(1, 0.4f, true).build();
    public static FoodProperties YOGHURT = makeFood(4, 0.5f, false).build();
    public static FoodProperties AYRAN = makeFood(4, 0.5f, false).alwaysEat().build();
    public static FoodProperties HONEYBERRY_YOGHURT = makeFood(12, 1.0f, false).build();
    public static FoodProperties HONEY_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties DONER_WRAP = makeFood(9, 1.2f, true).build();
    public static FoodProperties CHUBBY_DONER = makeFood(12, 1.4f, true).build();
    public static FoodProperties ALEXANDER = makeFood(16, 1.6f, true).build();
    public static FoodProperties DONER_SLICE = makeFood(6, 0.9f, true).build();
    public static FoodProperties BAKED_RICE_PUDDING = makeFood(10, 1.1f, false).build();
    public static FoodProperties SWEET_BERRY_JUICE = makeFood(6, 0.7f, false).build();
    public static FoodProperties RICE_PUDDING = makeFood(8, 0.6f, false).build();
    public static FoodProperties SWEET_BERRY_CUPCAKE_FANCY = makeFood(7, 1.1f, false).build();
    public static FoodProperties SWEET_BERRY_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties ENDER_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties SASHIMI = makeFood(1, 0.5f, true).build();
    public static FoodProperties OYAKODON = makeFood(17, 1.2f, true).build();
    public static FoodProperties BEEF_DONBURI = makeFood(14, 1.2f, true).build();
    public static FoodProperties EGG_SUSHI = makeFood(6, 0.7f, false).build();
    public static FoodProperties NIGIRI_SUSHI = makeFood(3, 0.7f, true).build();
    public static FoodProperties OMURICE = makeFood(10, 1.0f, false).build();
    public static FoodProperties SAKE = makeFood(3, 0.6f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.CONFUSION, 280, 1), 0.8f).build();
    public static FoodProperties RICEBALL = makeFood(3, 0.7f, false).build();
    public static FoodProperties SUSHI = makeFood(4, 0.7f, true).build();
    public static FoodProperties COOKED_RICE = makeFood(4, 0.7f, false).build();
    public static FoodProperties APPLE_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties PUMPKIN_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties COCOA_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties MELON_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties CARROT_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static FoodProperties FANCY_APPLE_CUPCAKE = makeFood(7, 1.1f, false).build();
    public static FoodProperties FANCY_PUMPKIN_CUPCAKE = makeFood(7, 1.1f, false).build();
    public static FoodProperties DONUT = makeFood(5, 0.7f, false).build();
    public static FoodProperties FANCY_DONUT = makeFood(6, 1.1f, false).build();
    public static FoodProperties SPRINKLES = makeFood(1, 0.2f, false).build();
    public static FoodProperties CHOCOLATE = makeFood(4, 0.5f, false).build();
    public static FoodProperties BUN = makeFood(2, 0.6f, false).build();
    public static FoodProperties RAW_PATTY = makeFood(1, 0.6f, true).build();
    public static FoodProperties COOKED_PATTY = makeFood(4, 1.0f, true).build();
    public static FoodProperties RAW_CHICKEN_PATTY = makeFood(1, 0.6f, true).build();
    public static FoodProperties COOKED_CHICKEN_PATTY = makeFood(3, 1.0f, true).build();
    public static FoodProperties HAMBURGER = makeFood(10, 1.4f, true).build();
    public static FoodProperties CHICKEN_BURGER = makeFood(9, 1.2f, true).build();
    public static FoodProperties MUSHROOM_BURGER = makeFood(8, 1.0f, true).build();
    public static FoodProperties ULTIMATE_BOTTOM = makeFood(7, 1.2f, true).build();
    public static FoodProperties ULTIMATE_TOP = makeFood(8, 1.3f, true).build();
    public static FoodProperties ULTIMATE_BURGER = makeFood(16, 2.6f, true).effect(new MobEffectInstance(MobEffects.SATURATION, 1200, 0), 1.0f).build();
    public static FoodProperties ROTTEN_BURGER = makeFood(-5, 0.7f, true).alwaysEat().effect(new MobEffectInstance(MobEffects.POISON, 80, 0), 0.9f).build();
    public static FoodProperties RAW_SAUSAGE = makeFood(1, 0.6f, true).build();
    public static FoodProperties COOKED_SAUSAGE = makeFood(4, 1.0f, true).build();
    public static FoodProperties HOTDOG = makeFood(10, 1.2f, true).build();
    public static FoodProperties FISH_BREAD = makeFood(9, 1.1f, true).build();
    public static FoodProperties DAISY_SANDWICH = makeFood(7, 0.8f, false).build();
    public static FoodProperties CHICKEN_WRAP = makeFood(9, 1.1f, true).build();
    public static FoodProperties RAW_SCHNITZEL = makeFood(1, 0.4f, true).build();
    public static FoodProperties COOKED_SCHNITZEL = makeFood(6, 1.2f, true).build();
    public static FoodProperties FRIED_EGG = makeFood(5, 0.8f, true).build();
    public static FoodProperties CROISSANT = makeFood(5, 0.6f, false).build();
    public static FoodProperties POTATO_SLICES = makeFood(1, 0.4f, false).build();
    public static FoodProperties POTATO_FRIES = makeFood(3, 0.6f, false).build();
    public static FoodProperties SHISH_KEBAB = makeFood(7, 1.0f, true).build();
    public static FoodProperties TOMATO_SLICES = makeFood(1, 0.5f, true).build();
    public static FoodProperties ICE_TEA = makeFood(2, 0.5f, false).alwaysEat().build();
    public static FoodProperties APPLE_JUICE = makeFood(3, 0.6f, false).alwaysEat().build();
    public static FoodProperties CARROT_JUICE = makeFood(3, 0.6f, false).alwaysEat().build();
    public static FoodProperties MELON_JUICE = makeFood(3, 0.6f, false).alwaysEat().build();
    public static FoodProperties PUMPKIN_JUICE = makeFood(3, 0.6f, false).alwaysEat().build();
    public static FoodProperties TOMATO_JUICE = makeFood(3, 0.6f, false).alwaysEat().build();
    public static FoodProperties WHEAT_JUICE = makeFood(3, 0.6f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.CONFUSION, 180, 0), 0.3f).build();
    public static FoodProperties GLASS_OF_MILK = makeFood(3, 0.6f, false).alwaysEat().build();
    public static FoodProperties GLASS_OF_WATER = makeFood(0, 0.5f, false).alwaysEat().build();
    public static FoodProperties TEACUP0 = makeFood(0, 0.3f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1), 1.0f).build();
    public static FoodProperties TEACUP1 = makeFood(1, 0.4f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1), 1.0f).build();
    public static FoodProperties TEACUP2 = makeFood(2, 0.4f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1), 1.0f).build();
    public static FoodProperties TEACUP3 = makeFood(3, 0.5f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1), 1.0f).build();
    public static FoodProperties TEACUP4 = makeFood(4, 0.5f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1), 1.0f).build();
    public static FoodProperties TEACUP5 = makeFood(5, 0.6f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1), 1.0f).build();
    public static FoodProperties TEACUP6 = makeFood(6, 0.6f, false).alwaysEat().effect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 1), 1.0f).effect(new MobEffectInstance(MobEffects.CONFUSION, 300, 1), 1.0f).build();
    public static FoodProperties GOLDEN_CUPCAKE = makeFood(16, 1.f, false).alwaysEat().build();


    static private FoodProperties.Builder makeFood(int hunger, float saturation, boolean isMeat) {
        FoodProperties.Builder builder = (new FoodProperties.Builder()).nutrition(hunger).saturationMod(saturation);
        if(isMeat){
            builder.meat();
        }
        return builder;
    }
}

