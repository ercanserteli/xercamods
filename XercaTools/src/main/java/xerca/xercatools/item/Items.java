package xerca.xercatools.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.crafting.RecipeSerializer;
import xerca.xercatools.Mod;
import xerca.xercatools.crafting.RecipeEnderBowFilling;
import xerca.xercatools.crafting.RecipeFlaskFilling;

public final class Items {
    public static final Item FLASK = new ItemFlask("flask");
    public static final Item ENDER_BOW = new ItemPotionLauncher("ender_bow");
    public static final Item STONE_KNIFE = new ItemKnife(ToolMaterial.STONE, "stone_knife");
    public static final Item IRON_KNIFE = new ItemKnife(ToolMaterial.IRON, "knife");
    public static final Item GOLDEN_KNIFE = new ItemKnife(ToolMaterial.GOLD, "golden_knife");
    public static final Item DIAMOND_KNIFE = new ItemKnife(ToolMaterial.DIAMOND, "diamond_knife");
    public static final Item NETHERITE_KNIFE = new ItemKnife(ToolMaterial.NETHERITE, "netherite_knife");
    public static final Item GRAB_HOOK = new ItemGrabHook("grab_hook");
    public static final Item WOODEN_SCYTHE = new ItemScythe(ToolMaterial.WOOD, "wooden_scythe");
    public static final Item STONE_SCYTHE = new ItemScythe(ToolMaterial.STONE, "stone_scythe");
    public static final Item IRON_SCYTHE = new ItemScythe(ToolMaterial.IRON, "iron_scythe");
    public static final Item GOLDEN_SCYTHE = new ItemScythe(ToolMaterial.GOLD, "golden_scythe");
    public static final Item DIAMOND_SCYTHE = new ItemScythe(ToolMaterial.DIAMOND, "diamond_scythe");
    public static final Item NETHERITE_SCYTHE = new ItemScythe(ToolMaterial.NETHERITE, "netherite_scythe");
    public static final Item DIAMOND_WARHAMMER = new ItemWarhammer(ToolMaterial.DIAMOND, "diamond_warhammer");
    public static final Item GOLD_WARHAMMER = new ItemWarhammer(ToolMaterial.GOLD, "gold_warhammer");
    public static final Item IRON_WARHAMMER = new ItemWarhammer(ToolMaterial.IRON, "iron_warhammer");
    public static final Item STONE_WARHAMMER = new ItemWarhammer(ToolMaterial.STONE, "stone_warhammer");
    public static final Item NETHERITE_WARHAMMER = new ItemWarhammer(ToolMaterial.NETHERITE, "netherite_warhammer");
    public static final ItemConfettiBall CONFETTI_BALL = new ItemConfettiBall("confetti_ball");
    public static final ItemConfetti CONFETTI = new ItemConfetti("confetti");
    public static final RecipeSerializer<RecipeFlaskFilling> CRAFTING_SPECIAL_FLASK_FILLING = new RecipeSerializer<>(RecipeFlaskFilling.MAP_CODEC, RecipeFlaskFilling.STREAM_CODEC);
    public static final RecipeSerializer<RecipeEnderBowFilling> CRAFTING_SPECIAL_ENDER_BOW_FILLING = new RecipeSerializer<>(RecipeEnderBowFilling.MAP_CODEC, RecipeEnderBowFilling.STREAM_CODEC);

    private Items() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, Mod.id("flask"), FLASK);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("ender_bow"), ENDER_BOW);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("stone_knife"), STONE_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("knife"), IRON_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("golden_knife"), GOLDEN_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("diamond_knife"), DIAMOND_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("netherite_knife"), NETHERITE_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("grab_hook"), GRAB_HOOK);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("wooden_scythe"), WOODEN_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("stone_scythe"), STONE_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("iron_scythe"), IRON_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("golden_scythe"), GOLDEN_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("diamond_scythe"), DIAMOND_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("netherite_scythe"), NETHERITE_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("diamond_warhammer"), DIAMOND_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("gold_warhammer"), GOLD_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("iron_warhammer"), IRON_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("stone_warhammer"), STONE_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("netherite_warhammer"), NETHERITE_WARHAMMER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Mod.id("crafting_special_flask_filling"), CRAFTING_SPECIAL_FLASK_FILLING);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Mod.id("crafting_special_ender_bow_filling"), CRAFTING_SPECIAL_ENDER_BOW_FILLING);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("confetti"), CONFETTI);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("confetti_ball"), CONFETTI_BALL);
    }
}
