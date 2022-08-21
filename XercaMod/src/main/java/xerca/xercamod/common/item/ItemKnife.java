package xerca.xercamod.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.Triggers;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.packets.KnifeAttackPacket;

import javax.annotation.Nonnull;

public class ItemKnife extends Item {

    private static final float defaultBonus = 5.0f;
    private static final float weaponDamage = 2.0f;
    private static final int maxDamage = 240;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    ItemKnife() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1).defaultDurability(maxDamage));

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", weaponDamage, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public static float critDamage(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        float angleDiff = Mth.abs(Mth.wrapDegrees(target.getYRot()) - Mth.wrapDegrees(attacker.getYRot()));
        if (attacker.isSteppingCarefully() && (angleDiff < 65.0F || angleDiff > 295.0f)) {
            if(!target.level.isClientSide){
                ClientboundAnimatePacket packetOut = new ClientboundAnimatePacket(target, 4);
                ((ServerLevel)target.level).getChunkSource().broadcastAndSend(attacker, packetOut);
            }
            attacker.level.playSound(null, target.getX(), target.getY() + 0.5d, target.getZ(), SoundEvents.SNEAK_HIT.get(), SoundSource.PLAYERS, 1.0f, attacker.level.random.nextFloat() * 0.2F + 0.8F);
            float bonus = defaultBonus;
            if(stack.isEnchanted()){
                bonus += EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_STEALTH.get(), stack)*2;
            }
            return bonus;
        }
        return 0;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, LivingEntity attacker) {
//        float damage = 0.0F;
//        float angleDiff = Mth.abs(Mth.wrapDegrees(target.getYRot()) - Mth.wrapDegrees(attacker.getYRot()));
//        if (attacker.isSteppingCarefully() && (angleDiff < 65.0F || angleDiff > 295.0f)) {
//            if(!target.level.isClientSide){
//                ClientboundAnimatePacket packetOut = new ClientboundAnimatePacket(target, 4);
//                ((ServerLevel)target.level).getChunkSource().broadcastAndSend(attacker, packetOut);
//            }
//            attacker.level.playSound(null, target.getX(), target.getY() + 0.5d, target.getZ(), SoundEvents.SNEAK_HIT.get(), SoundSource.PLAYERS, 1.0f, attacker.level.random.nextFloat() * 0.2F + 0.8F);
//
//            float bonus = defaultBonus;
//            if(stack.isEnchanted()){
//                bonus += EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_STEALTH.get(), stack)*2;
//            }
//
//            damage += bonus;
//            Player player = (Player) attacker;
//            DamageSource damagesource = DamageSource.playerAttack(player);
//            target.hurt(damagesource, damage);
//            if(target.getHealth() <= 0f) {
//                Triggers.ASSASSINATE.trigger((ServerPlayer) player);
//            }
//        }
        stack.hurtAndBreak(1, attacker, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));

        if(stack.isEnchanted()){
            int poison = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_POISON.get(), stack);
            if(poison > 0){
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 30 + 30 * poison, poison - 1));
            }
        }

        return true;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND) {
            playerIn.swing(hand);
            if (worldIn.isClientSide) {
                Minecraft mine = Minecraft.getInstance();
                if (mine.hitResult != null && mine.hitResult.getType() == HitResult.Type.ENTITY) {
                    Entity target = ((EntityHitResult) mine.hitResult).getEntity();
                    KnifeAttackPacket pack = new KnifeAttackPacket(false, target.getId());
                    XercaMod.NETWORK_HANDLER.sendToServer(pack);
                }
            }
            playerIn.getCooldowns().addCooldown(this, 15);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, heldItem);
    }

    @Nonnull
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot, ItemStack stack) {
        return (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return stack.getItem() == this;
    }

    @SuppressWarnings("CommentedOutCode")
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack ret = new ItemStack(this);
        ret.setTag(itemStack.getTag());

        ret.setDamageValue(itemStack.getDamageValue() + 1);
        if(ret.getDamageValue() >= ret.getMaxDamage()){
            return ItemStack.EMPTY;
        }

        // This one works with unbreaking but client is desynchronized, causing visual glitches
//        if(ret.attemptDamageItem(1, random, null)){
//            return ItemStack.EMPTY;
//        }
        return ret;
    }

    @Override
    public int getEnchantmentValue()
    {
        return Tiers.IRON.getEnchantmentValue();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.category == EnchantmentCategory.BREAKABLE || enchantment == Items.ENCHANTMENT_POISON.get()
                || enchantment == Items.ENCHANTMENT_STEALTH.get() || enchantment == Enchantments.SHARPNESS
                || enchantment == Enchantments.MOB_LOOTING;
    }
}
