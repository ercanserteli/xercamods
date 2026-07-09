package xerca.xercatools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.entity.EntityGrabHook;

import java.util.function.Consumer;

public class ItemGrabHook extends FishingRodItem {
    public ItemGrabHook(String name) {
        super(new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)).durability(210));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        float useSeconds = Math.min(1.0F, (this.getUseDuration(stack, entity) - timeLeft) / 20.0F);
        if (useSeconds <= 0.1F) {
            return false;
        }

        InteractionHand hand = player.getUsedItemHand();
        player.getCooldowns().addCooldown(stack, 40);
        stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        setCastState(stack, true);

        if (!level.isClientSide) {
            level.addFreshEntity(new EntityGrabHook(level, player, stack, useSeconds));
        }

        player.swing(hand, true);
        return true;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        MutableComponent text = Component.translatable("xercatools.grab_hook_tooltip");
        tooltip.accept(text.withStyle(ChatFormatting.BLUE));
        var registries = context.registries();
        if (registries != null && EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.grapplingEnchantment(registries), stack) > 0) {
            tooltip.accept(Component.translatable("xercatools.grappling_tooltip").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static void setCastState(ItemStack stack, boolean cast) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("cast", cast);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean isCast(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getBooleanOr("cast", false);
    }
}
