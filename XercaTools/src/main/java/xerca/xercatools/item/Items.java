package xerca.xercatools.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercatools.Mod;
import xerca.xercatools.crafting.RecipeEnderBowFilling;
import xerca.xercatools.crafting.RecipeFlaskFilling;

public final class Items {
    public static final Item FLASK = new ItemFlask("flask");
    public static final Item ENDER_BOW = new ItemPotionLauncher("ender_bow");
    public static final Item STONE_KNIFE = new ItemKnife(ToolMaterial.STONE, "stone_knife");
    public static final Item COPPER_KNIFE = new ItemKnife(ToolMaterial.COPPER, "copper_knife");
    public static final Item IRON_KNIFE = new ItemKnife(ToolMaterial.IRON, "knife");
    public static final Item GOLDEN_KNIFE = new ItemKnife(ToolMaterial.GOLD, "golden_knife");
    public static final Item DIAMOND_KNIFE = new ItemKnife(ToolMaterial.DIAMOND, "diamond_knife");
    public static final Item NETHERITE_KNIFE = new ItemKnife(ToolMaterial.NETHERITE, "netherite_knife");
    public static final Item GRAB_HOOK = new ItemGrabHook("grab_hook");
    public static final Item WOODEN_SCYTHE = new ItemScythe(ToolMaterial.WOOD, "wooden_scythe");
    public static final Item STONE_SCYTHE = new ItemScythe(ToolMaterial.STONE, "stone_scythe");
    public static final Item COPPER_SCYTHE = new ItemScythe(ToolMaterial.COPPER, "copper_scythe");
    public static final Item IRON_SCYTHE = new ItemScythe(ToolMaterial.IRON, "iron_scythe");
    public static final Item GOLDEN_SCYTHE = new ItemScythe(ToolMaterial.GOLD, "golden_scythe");
    public static final Item DIAMOND_SCYTHE = new ItemScythe(ToolMaterial.DIAMOND, "diamond_scythe");
    public static final Item NETHERITE_SCYTHE = new ItemScythe(ToolMaterial.NETHERITE, "netherite_scythe");
    public static final Item DIAMOND_WARHAMMER = new ItemWarhammer(ToolMaterial.DIAMOND, "diamond_warhammer");
    public static final Item GOLD_WARHAMMER = new ItemWarhammer(ToolMaterial.GOLD, "gold_warhammer");
    public static final Item IRON_WARHAMMER = new ItemWarhammer(ToolMaterial.IRON, "iron_warhammer");
    public static final Item STONE_WARHAMMER = new ItemWarhammer(ToolMaterial.STONE, "stone_warhammer");
    public static final Item COPPER_WARHAMMER = new ItemWarhammer(ToolMaterial.COPPER, "copper_warhammer");
    public static final Item NETHERITE_WARHAMMER = new ItemWarhammer(ToolMaterial.NETHERITE, "netherite_warhammer");
    public static final ItemConfettiBall CONFETTI_BALL = new ItemConfettiBall("confetti_ball");
    public static final ItemConfetti CONFETTI = new ItemConfetti("confetti");
    public static final RecipeSerializer<RecipeFlaskFilling> CRAFTING_SPECIAL_FLASK_FILLING = new RecipeSerializer<>(RecipeFlaskFilling.MAP_CODEC, RecipeFlaskFilling.STREAM_CODEC);
    public static final RecipeSerializer<RecipeEnderBowFilling> CRAFTING_SPECIAL_ENDER_BOW_FILLING = new RecipeSerializer<>(RecipeEnderBowFilling.MAP_CODEC, RecipeEnderBowFilling.STREAM_CODEC);

    private Items() {
    }

    public static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register(Mod.id("flask"), FLASK);
        helper.register(Mod.id("ender_bow"), ENDER_BOW);
        helper.register(Mod.id("stone_knife"), STONE_KNIFE);
        helper.register(Mod.id("copper_knife"), COPPER_KNIFE);
        helper.register(Mod.id("knife"), IRON_KNIFE);
        helper.register(Mod.id("golden_knife"), GOLDEN_KNIFE);
        helper.register(Mod.id("diamond_knife"), DIAMOND_KNIFE);
        helper.register(Mod.id("netherite_knife"), NETHERITE_KNIFE);
        helper.register(Mod.id("grab_hook"), GRAB_HOOK);
        helper.register(Mod.id("wooden_scythe"), WOODEN_SCYTHE);
        helper.register(Mod.id("stone_scythe"), STONE_SCYTHE);
        helper.register(Mod.id("copper_scythe"), COPPER_SCYTHE);
        helper.register(Mod.id("iron_scythe"), IRON_SCYTHE);
        helper.register(Mod.id("golden_scythe"), GOLDEN_SCYTHE);
        helper.register(Mod.id("diamond_scythe"), DIAMOND_SCYTHE);
        helper.register(Mod.id("netherite_scythe"), NETHERITE_SCYTHE);
        helper.register(Mod.id("diamond_warhammer"), DIAMOND_WARHAMMER);
        helper.register(Mod.id("gold_warhammer"), GOLD_WARHAMMER);
        helper.register(Mod.id("iron_warhammer"), IRON_WARHAMMER);
        helper.register(Mod.id("stone_warhammer"), STONE_WARHAMMER);
        helper.register(Mod.id("copper_warhammer"), COPPER_WARHAMMER);
        helper.register(Mod.id("netherite_warhammer"), NETHERITE_WARHAMMER);
        helper.register(Mod.id("confetti"), CONFETTI);
        helper.register(Mod.id("confetti_ball"), CONFETTI_BALL);
    }

    public static void registerRecipeSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register(Mod.id("crafting_special_flask_filling"), CRAFTING_SPECIAL_FLASK_FILLING);
        helper.register(Mod.id("crafting_special_ender_bow_filling"), CRAFTING_SPECIAL_ENDER_BOW_FILLING);
    }
}
