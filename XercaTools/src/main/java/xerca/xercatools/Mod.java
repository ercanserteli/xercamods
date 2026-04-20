package xerca.xercatools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.sounds.SoundSource;
import xerca.xercatools.enchantment.KnifeEnchantments;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.enchantment.FlaskEnchantments;
import xerca.xercatools.item.ItemScythe;
import xerca.xercatools.item.ItemKnife;
import xerca.xercatools.entity.EntityGrabHook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercatools.item.Items;

public class Mod implements ModInitializer {
    public static final String MOD_ID = "xercatools";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final EntityType<EntityGrabHook> HOOK = FabricEntityTypeBuilder.<EntityGrabHook>create(MobCategory.MISC, EntityGrabHook::new)
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
            .trackRangeChunks(8)
            .trackedUpdateRate(1)
            .build();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        SoundEvents.register();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("hook"), HOOK);
        Items.register();
        registerEnchantmentRules();
        registerCombatHooks();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(Items.WOODEN_SCYTHE);
            entries.accept(Items.STONE_SCYTHE);
            entries.accept(Items.IRON_SCYTHE);
            entries.accept(Items.GOLDEN_SCYTHE);
            entries.accept(Items.DIAMOND_SCYTHE);
            entries.accept(Items.NETHERITE_SCYTHE);
            entries.accept(Items.ITEM_KNIFE);
            entries.accept(Items.ITEM_GRAB_HOOK);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            entries.accept(Items.FLASK);
            entries.accept(Items.ENDER_BOW);
        });
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
            entries.accept(Items.ITEM_KNIFE);
            entries.accept(Items.ITEM_GRAB_HOOK);
            entries.accept(Items.ENDER_BOW);
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

            if (isGrabHook(target)) {
                if (enchantment.is(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING)
                        || enchantment.is(net.minecraft.world.item.enchantment.Enchantments.MENDING)
                        || enchantment.is(GrabHookEnchantments.GRAPPLING)
                        || enchantment.is(GrabHookEnchantments.TURBO_GRAB)
                        || enchantment.is(GrabHookEnchantments.GENTLE_GRAB)) {
                    return TriState.TRUE;
                }

                return TriState.DEFAULT;
            }

            if (isKnife(target)) {
                if (enchantment.is(Enchantments.UNBREAKING)
                        || enchantment.is(Enchantments.MENDING)
                        || enchantment.is(Enchantments.SHARPNESS)
                        || enchantment.is(Enchantments.LOOTING)
                        || enchantment.is(KnifeEnchantments.POISON)
                        || enchantment.is(KnifeEnchantments.STEALTH)) {
                    return TriState.TRUE;
                }

                return TriState.DEFAULT;
            }

            if (isFlask(target)) {
                if (enchantment.is(Enchantments.UNBREAKING)
                        || enchantment.is(Enchantments.MENDING)
                        || enchantment.is(FlaskEnchantments.CAPACITY)
                        || enchantment.is(FlaskEnchantments.CHUG)) {
                    return TriState.TRUE;
                }

                return TriState.DEFAULT;
            }

            if (isPotionLauncher(target)) {
                if (enchantment.is(Enchantments.UNBREAKING)
                        || enchantment.is(Enchantments.MENDING)
                        || enchantment.is(FlaskEnchantments.CAPACITY)
                        || enchantment.is(FlaskEnchantments.RANGE)) {
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

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand != net.minecraft.world.InteractionHand.OFF_HAND || !(entity instanceof LivingEntity target)) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getOffhandItem();
            if (!isKnife(stack) || player.getCooldowns().isOnCooldown(stack.getItem())) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide) {
                float damage = ItemKnife.getOffhandDamage(level, stack, target, player);
                target.hurt(player.damageSources().playerAttack(player), damage);
                stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                int poisonLevel = EnchantmentHelper.getItemEnchantmentLevel(KnifeEnchantments.poison(level.registryAccess()), stack);
                if (poisonLevel > 0) {
                    target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 30 + 30 * poisonLevel, poisonLevel - 1));
                    player.magicCrit(target);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
                } else {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }

            player.swing(hand, true);
            player.getCooldowns().addCooldown(stack.getItem(), 15);
            return InteractionResult.SUCCESS;
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

    private static boolean isGrabHook(ItemStack stack) {
        return stack.is(Items.ITEM_GRAB_HOOK);
    }

    private static boolean isKnife(ItemStack stack) {
        return stack.is(Items.ITEM_KNIFE);
    }

    private static boolean isFlask(ItemStack stack) {
        return stack.is(Items.FLASK);
    }

    private static boolean isPotionLauncher(ItemStack stack) {
        return stack.is(Items.ENDER_BOW);
    }
}
