package xerca.xercamod.common.item;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class Foods {
    public static Food RAW_SHISH_KEBAB = makeFood(1, 0.4f, true).build();
    public static Food YOGHURT = makeFood(4, 0.5f, false).build();
    public static Food AYRAN = makeFood(4, 0.5f, false).setAlwaysEdible().build();
    public static Food HONEYBERRY_YOGHURT = makeFood(12, 1.0f, false).build();
    public static Food HONEY_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food DONER_WRAP = makeFood(9, 1.2f, true).build();
    public static Food CHUBBY_DONER = makeFood(12, 1.4f, true).build();
    public static Food ALEXANDER = makeFood(16, 1.6f, true).build();
    public static Food DONER_SLICE = makeFood(6, 0.9f, true).build();
    public static Food BAKED_RICE_PUDDING = makeFood(10, 1.1f, false).build();
    public static Food SWEET_BERRY_JUICE = makeFood(6, 0.7f, false).build();
    public static Food RICE_PUDDING = makeFood(8, 0.6f, false).build();
    public static Food SWEET_BERRY_CUPCAKE_FANCY = makeFood(7, 1.1f, false).build();
    public static Food SWEET_BERRY_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food ENDER_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food SASHIMI = makeFood(1, 0.5f, true).build();
    public static Food OYAKODON = makeFood(17, 1.2f, true).build();
    public static Food BEEF_DONBURI = makeFood(14, 1.2f, true).build();
    public static Food EGG_SUSHI = makeFood(6, 0.7f, false).build();
    public static Food NIGIRI_SUSHI = makeFood(3, 0.7f, true).build();
    public static Food OMURICE = makeFood(10, 1.0f, false).build();
    public static Food SAKE = makeFood(3, 0.6f, false).setAlwaysEdible().effect(new EffectInstance(Effects.NAUSEA, 280, 1), 0.8f).build();
    public static Food RICEBALL = makeFood(3, 0.7f, false).build();
    public static Food SUSHI = makeFood(4, 0.7f, true).build();
    public static Food COOKED_RICE = makeFood(4, 0.7f, false).build();
    public static Food APPLE_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food PUMPKIN_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food COCOA_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food MELON_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food CARROT_CUPCAKE = makeFood(6, 0.7f, false).build();
    public static Food FANCY_APPLE_CUPCAKE = makeFood(7, 1.1f, false).build();
    public static Food FANCY_PUMPKIN_CUPCAKE = makeFood(7, 1.1f, false).build();
    public static Food DONUT = makeFood(5, 0.7f, false).build();
    public static Food FANCY_DONUT = makeFood(6, 1.1f, false).build();
    public static Food SPRINKLES = makeFood(1, 0.2f, false).build();
    public static Food CHOCOLATE = makeFood(4, 0.5f, false).build();
    public static Food BUN = makeFood(2, 0.6f, false).build();
    public static Food RAW_PATTY = makeFood(1, 0.6f, true).build();
    public static Food COOKED_PATTY = makeFood(4, 1.0f, true).build();
    public static Food RAW_CHICKEN_PATTY = makeFood(1, 0.6f, true).build();
    public static Food COOKED_CHICKEN_PATTY = makeFood(3, 1.0f, true).build();
    public static Food HAMBURGER = makeFood(10, 1.4f, true).build();
    public static Food CHICKEN_BURGER = makeFood(9, 1.2f, true).build();
    public static Food MUSHROOM_BURGER = makeFood(8, 1.0f, true).build();
    public static Food ULTIMATE_BOTTOM = makeFood(7, 1.2f, true).build();
    public static Food ULTIMATE_TOP = makeFood(8, 1.3f, true).build();
    public static Food ULTIMATE_BURGER = makeFood(16, 2.6f, true).effect(new EffectInstance(Effects.SATURATION, 1200, 0), 1.0f).build();
    public static Food ROTTEN_BURGER = makeFood(-5, 0.7f, true).setAlwaysEdible().effect(new EffectInstance(Effects.POISON, 80, 0), 0.9f).build();
    public static Food RAW_SAUSAGE = makeFood(1, 0.6f, true).build();
    public static Food COOKED_SAUSAGE = makeFood(4, 1.0f, true).build();
    public static Food HOTDOG = makeFood(10, 1.2f, true).build();
    public static Food FISH_BREAD = makeFood(9, 1.1f, true).build();
    public static Food DAISY_SANDWICH = makeFood(7, 0.8f, false).build();
    public static Food CHICKEN_WRAP = makeFood(9, 1.1f, true).build();
    public static Food RAW_SCHNITZEL = makeFood(1, 0.4f, true).build();
    public static Food COOKED_SCHNITZEL = makeFood(6, 1.2f, true).build();
    public static Food FRIED_EGG = makeFood(5, 0.8f, true).build();
    public static Food CROISSANT = makeFood(5, 0.6f, false).build();
    public static Food POTATO_SLICES = makeFood(1, 0.4f, false).build();
    public static Food POTATO_FRIES = makeFood(3, 0.6f, false).build();
    public static Food SHISH_KEBAB = makeFood(7, 1.0f, true).build();
    public static Food TOMATO_SLICES = makeFood(1, 0.5f, true).build();
    public static Food ICE_TEA = makeFood(2, 0.5f, false).setAlwaysEdible().build();
    public static Food APPLE_JUICE = makeFood(3, 0.6f, false).setAlwaysEdible().build();
    public static Food CARROT_JUICE = makeFood(3, 0.6f, false).setAlwaysEdible().build();
    public static Food MELON_JUICE = makeFood(3, 0.6f, false).setAlwaysEdible().build();
    public static Food PUMPKIN_JUICE = makeFood(3, 0.6f, false).setAlwaysEdible().build();
    public static Food TOMATO_JUICE = makeFood(3, 0.6f, false).setAlwaysEdible().build();
    public static Food WHEAT_JUICE = makeFood(3, 0.6f, false).setAlwaysEdible().effect(new EffectInstance(Effects.NAUSEA, 180, 0), 0.3f).build();
    public static Food GLASS_OF_MILK = makeFood(3, 0.6f, false).setAlwaysEdible().build();
    public static Food GLASS_OF_WATER = makeFood(0, 0.5f, false).setAlwaysEdible().build();
    public static Food TEACUP0 = makeFood(0, 0.3f, false).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 1200, 1), 1.0f).build();
    public static Food TEACUP1 = makeFood(1, 0.4f, false).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 1200, 1), 1.0f).build();
    public static Food TEACUP2 = makeFood(2, 0.4f, false).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 1200, 1), 1.0f).build();
    public static Food TEACUP3 = makeFood(3, 0.5f, false).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 1200, 1), 1.0f).build();
    public static Food TEACUP4 = makeFood(4, 0.5f, false).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 1200, 1), 1.0f).build();
    public static Food TEACUP5 = makeFood(5, 0.6f, false).setAlwaysEdible().effect(new EffectInstance(Effects.HASTE, 1200, 1), 1.0f).build();
    public static Food TEACUP6 = makeFood(6, 0.6f, false).setAlwaysEdible().effect(new EffectInstance(Effects.MINING_FATIGUE, 400, 1), 1.0f).effect(new EffectInstance(Effects.NAUSEA, 300, 1), 1.0f).build();
    public static Food GOLDEN_CUPCAKE = makeFood(16, 1.f, false).setAlwaysEdible().build();


    static private Food.Builder makeFood(int hunger, float saturation, boolean isMeat) {
        Food.Builder builder = (new Food.Builder()).hunger(hunger).saturation(saturation);
        if(isMeat){
            builder.meat();
        }
        return builder;
    }
}

