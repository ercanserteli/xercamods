package xerca.xercafood.common.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public class Foods {
    private Foods() {
    }

    public static final FoodProperties GLOW_SQUID_INK_PAELLA = makeFood(14, 1.1f).build();
    public static final FoodProperties SQUID_INK_PAELLA = makeFood(14, 1.1f).build();
    public static final FoodProperties CHEESEBURGER = makeFood(12, 1.4f).build();
    public static final FoodProperties CHEESE_TOAST = makeFood(6, 1.0f).build();
    public static final FoodProperties RAW_PIZZA_0 = makeFood(1, 0.4f).build();
    public static final FoodProperties RAW_PIZZA_1 = makeFood(2, 0.4f).build();
    public static final FoodProperties RAW_PIZZA_2 = makeFood(3, 0.4f).build();
    public static final FoodProperties RAW_PIZZA_3 = makeFood(4, 0.4f).build();
    public static final FoodProperties CHEESE_SLICE = makeFood(1, 0.5f).build();
    public static final FoodProperties RAW_SHISH_KEBAB = makeFood(1, 0.4f).build();
    public static final FoodProperties YOGHURT = makeFood(4, 0.5f).build();
    public static final FoodProperties AYRAN = makeFood(4, 0.5f).alwaysEdible().build();
    public static final FoodProperties HONEYBERRY_YOGHURT = makeFood(12, 1.0f).build();
    public static final FoodProperties HONEY_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties DONER_WRAP = makeFood(9, 1.2f).build();
    public static final FoodProperties CHUBBY_DONER = makeFood(12, 1.4f).build();
    public static final FoodProperties ALEXANDER = makeFood(16, 1.6f).build();
    public static final FoodProperties DONER_SLICE = makeFood(6, 0.9f).build();
    public static final FoodProperties BAKED_RICE_PUDDING = makeFood(10, 1.1f).build();
    public static final FoodProperties SWEET_BERRY_JUICE = makeFood(6, 0.7f).build();
    public static final FoodProperties RICE_PUDDING = makeFood(8, 0.6f).build();
    public static final FoodProperties SWEET_BERRY_CUPCAKE_FANCY = makeFood(7, 1.1f).build();
    public static final FoodProperties SWEET_BERRY_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties ENDER_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties GLOWBERRY_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties SASHIMI = makeFood(1, 0.5f).build();
    public static final FoodProperties OYAKODON = makeFood(17, 1.2f).build();
    public static final FoodProperties BEEF_DONBURI = makeFood(14, 1.2f).build();
    public static final FoodProperties EGG_SUSHI = makeFood(6, 0.7f).build();
    public static final FoodProperties NIGIRI_SUSHI = makeFood(3, 0.7f).build();
    public static final FoodProperties OMURICE = makeFood(10, 1.0f).build();
    public static final FoodProperties SAKE = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties CARBONATED_WATER = makeFood(0, 0.3f).alwaysEdible().build();
    public static final FoodProperties SODA = makeFood(2, 0.5f).alwaysEdible().build();
    public static final FoodProperties COLA = makeFood(3, 0.5f).alwaysEdible().build();
    public static final FoodProperties RICEBALL = makeFood(3, 0.7f).build();
    public static final FoodProperties SUSHI = makeFood(4, 0.7f).build();
    public static final FoodProperties COOKED_RICE = makeFood(4, 0.7f).build();
    public static final FoodProperties APPLE_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties PUMPKIN_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties COCOA_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties MELON_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties CARROT_CUPCAKE = makeFood(6, 0.7f).build();
    public static final FoodProperties FANCY_APPLE_CUPCAKE = makeFood(7, 1.1f).build();
    public static final FoodProperties FANCY_PUMPKIN_CUPCAKE = makeFood(7, 1.1f).build();
    public static final FoodProperties DONUT = makeFood(5, 0.7f).build();
    public static final FoodProperties FANCY_DONUT = makeFood(6, 1.1f).build();
    public static final FoodProperties SPRINKLES = makeFood(1, 0.2f).build();
    public static final FoodProperties CHOCOLATE = makeFood(4, 0.5f).build();
    public static final FoodProperties BUN = makeFood(2, 0.6f).build();
    public static final FoodProperties RAW_PATTY = makeFood(1, 0.6f).build();
    public static final FoodProperties COOKED_PATTY = makeFood(4, 1.0f).build();
    public static final FoodProperties RAW_CHICKEN_PATTY = makeFood(1, 0.6f).build();
    public static final FoodProperties COOKED_CHICKEN_PATTY = makeFood(3, 1.0f).build();
    public static final FoodProperties HAMBURGER = makeFood(10, 1.4f).build();
    public static final FoodProperties CHICKEN_BURGER = makeFood(9, 1.2f).build();
    public static final FoodProperties MUSHROOM_BURGER = makeFood(8, 1.0f).build();
    public static final FoodProperties ULTIMATE_BOTTOM = makeFood(7, 1.2f).build();
    public static final FoodProperties ULTIMATE_TOP = makeFood(8, 1.3f).build();
    public static final FoodProperties ULTIMATE_BURGER = makeFood(16, 2.6f).build();
    public static final FoodProperties ROTTEN_BURGER = makeFood(-5, 0.7f).alwaysEdible().build();
    public static final FoodProperties RAW_SAUSAGE = makeFood(1, 0.6f).build();
    public static final FoodProperties COOKED_SAUSAGE = makeFood(4, 1.0f).build();
    public static final FoodProperties HOTDOG = makeFood(10, 1.2f).build();
    public static final FoodProperties FISH_BREAD = makeFood(9, 1.1f).build();
    public static final FoodProperties DAISY_SANDWICH = makeFood(7, 0.8f).build();
    public static final FoodProperties CHICKEN_WRAP = makeFood(9, 1.1f).build();
    public static final FoodProperties RAW_SCHNITZEL = makeFood(1, 0.4f).build();
    public static final FoodProperties COOKED_SCHNITZEL = makeFood(6, 1.2f).build();
    public static final FoodProperties FRIED_EGG = makeFood(5, 0.8f).build();
    public static final FoodProperties CROISSANT = makeFood(5, 0.6f).build();
    public static final FoodProperties POTATO_SLICES = makeFood(1, 0.4f).build();
    public static final FoodProperties POTATO_FRIES = makeFood(3, 0.6f).build();
    public static final FoodProperties SHISH_KEBAB = makeFood(7, 1.0f).build();
    public static final FoodProperties TOMATO_SLICES = makeFood(1, 0.5f).build();
    public static final FoodProperties ICE_TEA = makeFood(2, 0.5f).alwaysEdible().build();
    public static final FoodProperties APPLE_JUICE = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties CARROT_JUICE = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties MELON_JUICE = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties PUMPKIN_JUICE = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties TOMATO_JUICE = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties WHEAT_JUICE = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties GLASS_OF_MILK = makeFood(3, 0.6f).alwaysEdible().build();
    public static final FoodProperties GLASS_OF_WATER = makeFood(0, 0.3f).alwaysEdible().build();
    public static final FoodProperties TEACUP0 = makeFood(0, 0.3f).alwaysEdible().build();
    public static final FoodProperties TEACUP1 = makeFood(1, 0.4f).alwaysEdible().build();
    public static final FoodProperties TEACUP2 = makeFood(2, 0.4f).alwaysEdible().build();
    public static final FoodProperties TEACUP3 = makeFood(3, 0.5f).alwaysEdible().build();
    public static final FoodProperties TEACUP4 = makeFood(4, 0.5f).alwaysEdible().build();
    public static final FoodProperties TEACUP5 = makeFood(5, 0.6f).alwaysEdible().build();
    public static final FoodProperties TEACUP6 = makeFood(6, 0.6f).alwaysEdible().build();
    public static final FoodProperties GOLDEN_CUPCAKE = makeFood(16, 1.f).alwaysEdible().build();

    // Consume effects (moved out of FoodProperties in 1.21.2). A plain drink consumable just swaps the eat animation/sound.
    public static final Consumable DRINK = drinkConsumable();
    public static final Consumable GLOW_SQUID_INK_PAELLA_C = foodConsumable(effect(MobEffects.GLOWING, 1200, 1, 1.0f));
    public static final Consumable GLOWBERRY_CUPCAKE_C = foodConsumable(effect(MobEffects.GLOWING, 300, 1, 1.0f));
    public static final Consumable ULTIMATE_BURGER_C = foodConsumable(effect(MobEffects.SATURATION, 1200, 0, 1.0f));
    public static final Consumable ROTTEN_BURGER_C = foodConsumable(effect(MobEffects.POISON, 80, 0, 0.9f));
    public static final Consumable SAKE_C = drinkConsumable(effect(MobEffects.NAUSEA, 280, 1, 0.8f));
    public static final Consumable COLA_C = drinkConsumable(effect(MobEffects.HASTE, 1000, 1, 1.0f));
    public static final Consumable WHEAT_JUICE_C = drinkConsumable(effect(MobEffects.NAUSEA, 180, 0, 0.3f));
    public static final Consumable TEACUP_SUGARED_C = drinkConsumable(effect(MobEffects.HASTE, 1200, 1, 1.0f));
    public static final Consumable TEACUP6_C = drinkConsumable(
            effect(MobEffects.MINING_FATIGUE, 400, 1, 1.0f), effect(MobEffects.NAUSEA, 300, 1, 1.0f));

    public static Consumable teacupConsumable(int sugarAmount) {
        return sugarAmount >= 6 ? TEACUP6_C : TEACUP_SUGARED_C;
    }

    private static FoodProperties.Builder makeFood(int hunger, float saturation) {
        return new FoodProperties.Builder().nutrition(hunger).saturationModifier(saturation);
    }

    private static ConsumeEffect effect(Holder<MobEffect> effect, int duration, int amplifier, float probability) {
        return new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(effect, duration, amplifier), probability);
    }

    private static Consumable foodConsumable(ConsumeEffect... effects) {
        Consumable.Builder builder = Consumable.builder();
        for (ConsumeEffect e : effects) {
            builder.onConsume(e);
        }
        return builder.build();
    }

    private static Consumable drinkConsumable(ConsumeEffect... effects) {
        Consumable.Builder builder = Consumable.builder().animation(ItemUseAnimation.DRINK).sound(SoundEvents.GENERIC_DRINK);
        for (ConsumeEffect e : effects) {
            builder.onConsume(e);
        }
        return builder.build();
    }
}
