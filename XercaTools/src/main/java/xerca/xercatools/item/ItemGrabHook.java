package xerca.xercatools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercatools.Mod;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.entity.EntityGrabHook;

import java.util.List;

public class ItemGrabHook extends FishingRodItem {
    public ItemGrabHook() {
        super(new Item.Properties().durability(210));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(heldItem);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }

        float useSeconds = Math.min(1.0F, (this.getUseDuration(stack, entity) - timeLeft) / 20.0F);
        if (useSeconds <= 0.1F) {
            return;
        }

        InteractionHand hand = player.getUsedItemHand();
        player.getCooldowns().addCooldown(this, 40);
        stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        setCastState(stack, true);

        if (!level.isClientSide) {
            level.addFreshEntity(new EntityGrabHook(level, player, stack, useSeconds));
        }

        player.swing(hand, true);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MutableComponent text = Component.translatable("xercatools.grab_hook_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
        if (EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.grappling(context.registries()), stack) > 0) {
            tooltip.add(Component.translatable("xercatools.grappling_tooltip").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static void setCastState(ItemStack stack, boolean cast) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("cast", cast);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean isCast(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getBoolean("cast");
    }
}
