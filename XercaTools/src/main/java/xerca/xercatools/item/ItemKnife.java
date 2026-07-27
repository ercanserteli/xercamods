package xerca.xercatools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.enchantment.KnifeEnchantments;

import java.util.List;

public class ItemKnife extends Item {
    private static final float DEFAULT_CRIT_BONUS = 5.0F;
    private final Tier tier;

    public static ItemAttributeModifiers createAttributes(Tier tier) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public ItemKnife(Tier tier) {
        super(tier == Tiers.NETHERITE
                ? new Item.Properties().stacksTo(1).durability(tier.getUses()).fireResistant().attributes(createAttributes(tier))
                : new Item.Properties().stacksTo(1).durability(tier.getUses()).attributes(createAttributes(tier)));
        this.tier = tier;
    }

    public static float critDamage(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        float angleDiff = Mth.abs(Mth.wrapDegrees(target.getYRot()) - Mth.wrapDegrees(attacker.getYRot()));
        if (attacker.isSteppingCarefully() && (angleDiff < 65.0F || angleDiff > 295.0F)) {
            if (!target.level().isClientSide()) {
                ClientboundAnimatePacket packet = new ClientboundAnimatePacket(target, 4);
                ((ServerLevel) target.level()).getChunkSource().broadcastAndSend(attacker, packet);
            }
            attacker.level().playSound(null, target.getX(), target.getY() + 0.5D, target.getZ(), SoundEvents.SNEAK_HIT, SoundSource.PLAYERS, 1.0F, attacker.level().random.nextFloat() * 0.2F + 0.8F);
            float bonus = DEFAULT_CRIT_BONUS;
            bonus += EnchantmentHelper.getItemEnchantmentLevel(KnifeEnchantments.stealthEnchantment(attacker.level().registryAccess()), stack) * 2.0F;
            return bonus;
        }
        return 0.0F;
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        if (this.tier.getRepairIngredient().test(repair)) {
            return true;
        }
        return super.isValidRepairItem(toRepair, repair);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);

        int poisonLevel = EnchantmentHelper.getItemEnchantmentLevel(KnifeEnchantments.poisonEnchantment(attacker.level().registryAccess()), stack);
        if (poisonLevel > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 30 + 30 * poisonLevel, poisonLevel - 1));
        }

        float critBonus = critDamage(target, attacker, stack);
        if (critBonus > 0.0F) {
            DamageSource damageSource = attacker instanceof Player player
                    ? attacker.damageSources().playerAttack(player)
                    : attacker.damageSources().mobAttack(attacker);
            // The main hit already set hurt resistance and lastHurt; stack the bonus on top of lastHurt.
            target.hurt(damageSource, target.lastHurt + critBonus);
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            player.swing(hand, true);
            player.getCooldowns().addCooldown(this, 15);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack remainder = stack.copy();
        remainder.setCount(1);
        remainder.setDamageValue(stack.getDamageValue() + 1);
        if (remainder.getDamageValue() >= remainder.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        return remainder;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.translatable("xercatools.knife_tooltip").withStyle(ChatFormatting.BLUE));
        tooltip.add(net.minecraft.network.chat.Component.translatable("xercatools.knife_offhand_tooltip").withStyle(ChatFormatting.YELLOW));
    }

    public static float getOffhandDamage(Level level, ItemStack stack, LivingEntity target, Player attacker) {
        float bonus = critDamage(target, attacker, stack);
        float damage = (float) attacker.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        for (ItemAttributeModifiers.Entry entry : stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
            if (entry.attribute().is(Attributes.ATTACK_DAMAGE) && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                damage += (float) entry.modifier().amount();
            }
        }
        int sharpnessLevel = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), stack);
        float enchantBonus = sharpnessLevel > 0 ? 0.5F * sharpnessLevel + 0.5F : 0.0F;
        return damage + enchantBonus + bonus;
    }

    @Override
    public boolean supportsEnchantment(net.minecraft.world.item.ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return xerca.xercatools.Mod.toolSupportsEnchantment(stack, enchantment) || super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(net.minecraft.world.item.ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return xerca.xercatools.Mod.toolSupportsEnchantment(stack, enchantment) || super.isPrimaryItemFor(stack, enchantment);
    }
}
