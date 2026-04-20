package xerca.xercatools.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import xerca.xercatools.Mod;

public final class Items {
    public static final Item ITEM_DIAMOND_WARHAMMER = new ItemWarhammer(Tiers.DIAMOND);
    public static final Item ITEM_GOLD_WARHAMMER = new ItemWarhammer(Tiers.GOLD);
    public static final Item ITEM_IRON_WARHAMMER = new ItemWarhammer(Tiers.IRON);
    public static final Item ITEM_STONE_WARHAMMER = new ItemWarhammer(Tiers.STONE);
    public static final Item ITEM_NETHERITE_WARHAMMER = new ItemWarhammer(Tiers.NETHERITE);

    private Items() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_diamond_warhammer"), ITEM_DIAMOND_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_gold_warhammer"), ITEM_GOLD_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_iron_warhammer"), ITEM_IRON_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_stone_warhammer"), ITEM_STONE_WARHAMMER);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("item_netherite_warhammer"), ITEM_NETHERITE_WARHAMMER);
    }
}


