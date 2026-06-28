package xerca.xercatools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import xerca.xercatools.enchantment.FlaskEnchantments;

import java.util.List;

public class ItemPotionLauncher extends Item {
    public ItemPotionLauncher(String name) {
        super(new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)).stacksTo(1).durability(160).enchantable(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (ItemFlask.getCharges(stack) <= 0) {
            return InteractionResult.FAIL;
        }

        float range = EnchantmentHelper.getItemEnchantmentLevel(FlaskEnchantments.rangeEnchantment(level.registryAccess()), stack) + 1.0F;
        if (range > 1.0F) {
            range *= 0.8F;
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (!level.isClientSide) {
            ItemStack potionStack = new ItemStack(isLingering(stack) ? net.minecraft.world.item.Items.LINGERING_POTION : net.minecraft.world.item.Items.SPLASH_POTION);
            potionStack.set(DataComponents.POTION_CONTENTS, ItemFlask.getPotionContents(stack));
            ThrownPotion thrownPotion = new ThrownPotion(level, player, potionStack);
            thrownPotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -10.0F, 0.5F * range, 1.0F / range);
            level.addFreshEntity(thrownPotion);

            decrementCharges(stack);
            EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            stack.hurtAndBreak(1, player, slot);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        MutableComponent text = Component.translatable("xercatools.ender_bow_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
        ItemFlask.getPotionContents(stack).addPotionTooltip(tooltip::add, 1.0F, context.tickRate());
        tooltip.add(Component.translatable("xercatools.charges_tooltip", ItemFlask.getCharges(stack)).withStyle(ChatFormatting.YELLOW));
    }

    public static boolean isLingering(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getBoolean("isLinger");
    }

    public static void setLingering(ItemStack stack, boolean lingering) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("isLinger", lingering);
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
}
