package xerca.xercatools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import xerca.xercatools.enchantment.FlaskEnchantments;

import java.util.List;

public class ItemFlask extends Item {
    private static final int BASE_MAX_CHARGES = 16;

    public ItemFlask() {
        super(new Item.Properties().stacksTo(1).durability(160));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        int chug = EnchantmentHelper.getItemEnchantmentLevel(FlaskEnchantments.chugEnchantment(entity.level().registryAccess()), stack);
        return switch (chug) {
            case 2 -> 10;
            case 1 -> 21;
            default -> 32;
        };
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getCharges(stack) <= 0) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        int charges = getCharges(stack);
        if (charges > 0 && entity instanceof Player player) {
            applyPotionEffects(stack, level, player, entity);
            decrementCharges(stack);
            applyUseCooldown(stack, entity, player);
            EquipmentSlot slot = player.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            stack.hurtAndBreak(1, player, slot);
        }

        return stack;
    }

    private static void applyPotionEffects(ItemStack stack, Level level, Player player, LivingEntity entity) {
        if (level.isClientSide) {
            return;
        }
        PotionContents potionContents = getPotionContents(stack);
        for (MobEffectInstance effect : potionContents.getAllEffects()) {
            if (effect.getEffect().value().isInstantenous()) {
                effect.getEffect().value().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1.0D);
            } else {
                entity.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    private void applyUseCooldown(ItemStack stack, LivingEntity entity, Player player) {
        int useDuration = getUseDuration(stack, entity);
        if (useDuration < 32) {
            player.getCooldowns().addCooldown(this, (32 - useDuration) / 2);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        MutableComponent text = Component.translatable("xercatools.ender_flask_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
        getPotionContents(stack).addPotionTooltip(tooltip::add, 1.0F, context.tickRate());
        tooltip.add(Component.translatable("xercatools.charges_tooltip", getCharges(stack)).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    public static int getCharges(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getInt("charges");
    }

    public static int getMaxCharges(ItemStack stack, Level level) {
        int cap = EnchantmentHelper.getItemEnchantmentLevel(FlaskEnchantments.capacityEnchantment(level.registryAccess()), stack);
        return BASE_MAX_CHARGES * (cap + 1);
    }

    public static int getMaxCharges(ItemStack stack, HolderLookup.Provider provider) {
        int cap = EnchantmentHelper.getItemEnchantmentLevel(provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(FlaskEnchantments.CAPACITY), stack);
        return BASE_MAX_CHARGES * (cap + 1);
    }

    public static void setCharges(ItemStack stack, int charges) {
        if (charges < 0) {
            return;
        }

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("charges", charges);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void decrementCharges(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int oldCharges = tag.getInt("charges");
        if (oldCharges <= 0) {
            return;
        }

        int newCharges = oldCharges - 1;
        tag.putInt("charges", newCharges);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        if (newCharges == 0) {
            stack.remove(DataComponents.POTION_CONTENTS);
        }
    }

    public static PotionContents getPotionContents(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }
}
