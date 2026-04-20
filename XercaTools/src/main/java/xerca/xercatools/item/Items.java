package xerca.xercatools.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import xerca.xercatools.Mod;

public final class Items {
    public static final Item ITEM_KNIFE = new ItemKnife();
    public static final Item ITEM_GRAB_HOOK = new ItemGrabHook();
    public static final Item WOODEN_SCYTHE = new ItemScythe(Tiers.WOOD);
    public static final Item STONE_SCYTHE = new ItemScythe(Tiers.STONE);
    public static final Item IRON_SCYTHE = new ItemScythe(Tiers.IRON);
    public static final Item GOLDEN_SCYTHE = new ItemScythe(Tiers.GOLD);
    public static final Item DIAMOND_SCYTHE = new ItemScythe(Tiers.DIAMOND);
    public static final Item NETHERITE_SCYTHE = new ItemScythe(Tiers.NETHERITE);
    public static final Item ITEM_DIAMOND_WARHAMMER = new ItemWarhammer(Tiers.DIAMOND);
    public static final Item ITEM_GOLD_WARHAMMER = new ItemWarhammer(Tiers.GOLD);
    public static final Item ITEM_IRON_WARHAMMER = new ItemWarhammer(Tiers.IRON);
    public static final Item ITEM_STONE_WARHAMMER = new ItemWarhammer(Tiers.STONE);
    public static final Item ITEM_NETHERITE_WARHAMMER = new ItemWarhammer(Tiers.NETHERITE);

    private Items() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_knife"), ITEM_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_grab_hook"), ITEM_GRAB_HOOK);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("wooden_scythe"), WOODEN_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("stone_scythe"), STONE_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("iron_scythe"), IRON_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("golden_scythe"), GOLDEN_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("diamond_scythe"), DIAMOND_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("netherite_scythe"), NETHERITE_SCYTHE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_diamond_warhammer"), ITEM_DIAMOND_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_gold_warhammer"), ITEM_GOLD_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_iron_warhammer"), ITEM_IRON_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_stone_warhammer"), ITEM_STONE_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_netherite_warhammer"), ITEM_NETHERITE_WARHAMMER);
    }
}


