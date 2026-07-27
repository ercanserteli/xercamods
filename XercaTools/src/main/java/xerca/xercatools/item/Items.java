package xerca.xercatools.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercatools.Mod;
import xerca.xercatools.crafting.RecipeEnderBowFilling;
import xerca.xercatools.crafting.RecipeFlaskFilling;

public final class Items {
    public static final Item FLASK = new ItemFlask();
    public static final Item ENDER_BOW = new ItemPotionLauncher();
    public static final Item STONE_KNIFE = new ItemKnife(Tiers.STONE);
    public static final Item IRON_KNIFE = new ItemKnife(Tiers.IRON);
    public static final Item GOLDEN_KNIFE = new ItemKnife(Tiers.GOLD);
    public static final Item DIAMOND_KNIFE = new ItemKnife(Tiers.DIAMOND);
    public static final Item NETHERITE_KNIFE = new ItemKnife(Tiers.NETHERITE);
    public static final Item GRAB_HOOK = new ItemGrabHook();
    public static final Item WOODEN_SCYTHE = new ItemScythe(Tiers.WOOD);
    public static final Item STONE_SCYTHE = new ItemScythe(Tiers.STONE);
    public static final Item IRON_SCYTHE = new ItemScythe(Tiers.IRON);
    public static final Item GOLDEN_SCYTHE = new ItemScythe(Tiers.GOLD);
    public static final Item DIAMOND_SCYTHE = new ItemScythe(Tiers.DIAMOND);
    public static final Item NETHERITE_SCYTHE = new ItemScythe(Tiers.NETHERITE);
    public static final Item DIAMOND_WARHAMMER = new ItemWarhammer(Tiers.DIAMOND);
    public static final Item GOLD_WARHAMMER = new ItemWarhammer(Tiers.GOLD);
    public static final Item IRON_WARHAMMER = new ItemWarhammer(Tiers.IRON);
    public static final Item STONE_WARHAMMER = new ItemWarhammer(Tiers.STONE);
    public static final Item NETHERITE_WARHAMMER = new ItemWarhammer(Tiers.NETHERITE);
    public static final ItemConfettiBall CONFETTI_BALL = new ItemConfettiBall();
    public static final ItemConfetti CONFETTI = new ItemConfetti();
    public static final RecipeSerializer<RecipeFlaskFilling> CRAFTING_SPECIAL_FLASK_FILLING = new SimpleCraftingRecipeSerializer<>(RecipeFlaskFilling::new);
    public static final RecipeSerializer<RecipeEnderBowFilling> CRAFTING_SPECIAL_ENDER_BOW_FILLING = new SimpleCraftingRecipeSerializer<>(RecipeEnderBowFilling::new);

    private Items() {
    }

    public static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register(Mod.id("flask"), FLASK);
        helper.register(Mod.id("ender_bow"), ENDER_BOW);
        helper.register(Mod.id("stone_knife"), STONE_KNIFE);
        helper.register(Mod.id("knife"), IRON_KNIFE);
        helper.register(Mod.id("golden_knife"), GOLDEN_KNIFE);
        helper.register(Mod.id("diamond_knife"), DIAMOND_KNIFE);
        helper.register(Mod.id("netherite_knife"), NETHERITE_KNIFE);
        helper.register(Mod.id("grab_hook"), GRAB_HOOK);
        helper.register(Mod.id("wooden_scythe"), WOODEN_SCYTHE);
        helper.register(Mod.id("stone_scythe"), STONE_SCYTHE);
        helper.register(Mod.id("iron_scythe"), IRON_SCYTHE);
        helper.register(Mod.id("golden_scythe"), GOLDEN_SCYTHE);
        helper.register(Mod.id("diamond_scythe"), DIAMOND_SCYTHE);
        helper.register(Mod.id("netherite_scythe"), NETHERITE_SCYTHE);
        helper.register(Mod.id("diamond_warhammer"), DIAMOND_WARHAMMER);
        helper.register(Mod.id("gold_warhammer"), GOLD_WARHAMMER);
        helper.register(Mod.id("iron_warhammer"), IRON_WARHAMMER);
        helper.register(Mod.id("stone_warhammer"), STONE_WARHAMMER);
        helper.register(Mod.id("netherite_warhammer"), NETHERITE_WARHAMMER);
        helper.register(Mod.id("confetti"), CONFETTI);
        helper.register(Mod.id("confetti_ball"), CONFETTI_BALL);
    }

    public static void registerRecipeSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register(Mod.id("crafting_special_flask_filling"), CRAFTING_SPECIAL_FLASK_FILLING);
        helper.register(Mod.id("crafting_special_ender_bow_filling"), CRAFTING_SPECIAL_ENDER_BOW_FILLING);
    }
}
