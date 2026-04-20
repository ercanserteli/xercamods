package xerca.xercatools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import xerca.xercatools.item.ItemScythe;
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
        registerCombatHooks();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(Items.WOODEN_SCYTHE);
            entries.accept(Items.STONE_SCYTHE);
            entries.accept(Items.IRON_SCYTHE);
            entries.accept(Items.GOLDEN_SCYTHE);
            entries.accept(Items.DIAMOND_SCYTHE);
            entries.accept(Items.NETHERITE_SCYTHE);
            entries.accept(Items.ITEM_STONE_WARHAMMER);
            entries.accept(Items.ITEM_IRON_WARHAMMER);
            entries.accept(Items.ITEM_GOLD_WARHAMMER);
            entries.accept(Items.ITEM_DIAMOND_WARHAMMER);
            entries.accept(Items.ITEM_NETHERITE_WARHAMMER);
        });
    }

    private void registerEnchantmentRules() {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> {
            if (isWarhammer(target)) {
                if (enchantment.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.MENDING)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.SMITE)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.LOOTING)) {
                    return TriState.TRUE;
                }

                return TriState.DEFAULT;
            }

            if (isScythe(target)) {
                if (enchantment.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.MENDING)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.FORTUNE)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.SHARPNESS)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.SMITE)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.LOOTING)) {
                    return TriState.TRUE;
                }

                return TriState.DEFAULT;
            }

            return TriState.DEFAULT;
        });
    }

    private void registerCombatHooks() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (isScythe(stack)) {
                ItemScythe.applyAttackEffects(stack, player, entity);
            }
            return InteractionResult.PASS;
        });
    }

    private static boolean isWarhammer(ItemStack stack) {
        return stack.is(Items.ITEM_STONE_WARHAMMER)
                || stack.is(Items.ITEM_IRON_WARHAMMER)
                || stack.is(Items.ITEM_GOLD_WARHAMMER)
                || stack.is(Items.ITEM_DIAMOND_WARHAMMER)
                || stack.is(Items.ITEM_NETHERITE_WARHAMMER);
    }

    private static boolean isScythe(ItemStack stack) {
        return stack.is(Items.WOODEN_SCYTHE)
                || stack.is(Items.STONE_SCYTHE)
                || stack.is(Items.IRON_SCYTHE)
                || stack.is(Items.GOLDEN_SCYTHE)
                || stack.is(Items.DIAMOND_SCYTHE)
                || stack.is(Items.NETHERITE_SCYTHE);
    }
}
