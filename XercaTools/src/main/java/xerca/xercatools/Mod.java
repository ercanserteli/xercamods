package xerca.xercatools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercatools.item.Items;

public class Mod implements ModInitializer {
    public static final String MOD_ID = "xercatools";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        SoundEvents.register();
        Items.register();
        registerEnchantmentRules();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(Items.ITEM_STONE_WARHAMMER);
            entries.accept(Items.ITEM_IRON_WARHAMMER);
            entries.accept(Items.ITEM_GOLD_WARHAMMER);
            entries.accept(Items.ITEM_DIAMOND_WARHAMMER);
            entries.accept(Items.ITEM_NETHERITE_WARHAMMER);
        });
    }

    private void registerEnchantmentRules() {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> {
            if (!isWarhammer(target)) {
                return TriState.DEFAULT;
            }

            if (enchantment.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)
                    || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.MENDING)
                    || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.SMITE)
                    || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS)
                    || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.LOOTING)) {
                return TriState.TRUE;
            }

            return TriState.DEFAULT;
        });
    }

    private static boolean isWarhammer(ItemStack stack) {
        return stack.is(Items.ITEM_STONE_WARHAMMER)
                || stack.is(Items.ITEM_IRON_WARHAMMER)
                || stack.is(Items.ITEM_GOLD_WARHAMMER)
                || stack.is(Items.ITEM_DIAMOND_WARHAMMER)
                || stack.is(Items.ITEM_NETHERITE_WARHAMMER);
    }
}


