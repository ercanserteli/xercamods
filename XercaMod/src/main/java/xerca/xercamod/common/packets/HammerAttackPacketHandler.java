package xerca.xercamod.common.packets;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.ItemWarhammer;
import xerca.xercamod.common.item.Items;

import java.util.function.Supplier;

public class HammerAttackPacketHandler {
    public static void handle(final HammerAttackPacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when HammerAttackPacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    static float damageBonusMult(float pullDuration){
        if (pullDuration >= 0.95F) {
            return 1.75f;
        } else if (pullDuration >= 0.7F) {
            return 1.0f;
        } else if (pullDuration >= 0.4F) {
            return 0.75f;
        } else{
            return 0.5f;
        }
    }

    private static void processMessage(HammerAttackPacket msg, ServerPlayer pl) {
        Entity target = pl.level.getEntity(msg.getTargetId());
        if(target == null){
            return;
        }

        ItemStack st = pl.getMainHandItem();
        Item item = st.getItem();
        if (item instanceof ItemWarhammer){
            float pull = msg.getPullDuration();
            float mult = damageBonusMult(pull);
            int heavyLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_HEAVY.get(), st);
            AttributeInstance attackDamage = pl.getAttribute(Attributes.ATTACK_DAMAGE);
            float damage = ((float) (attackDamage != null ? attackDamage.getValue() : 0) + heavyLevel * 0.5f) * mult;
            float push = (((ItemWarhammer) item).getPushAmount() + heavyLevel * 0.15f) * 2 * mult;

            int uppercutLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_UPPERCUT.get(), st);
            double bonusVelY = (uppercutLevel * 0.25) * pull;

            float pitch = (2.0f / (damage + heavyLevel));
            pl.level.playSound(null, target.getX(), target.getY() + 0.5d, target.getZ(), SoundEvents.HAMMER.get(), SoundSource.PLAYERS, 1.0f, pl.level.random.nextFloat() * 0.1F + 0.4F + pitch);
            st.hurtAndBreak(1, pl, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            if(target instanceof LivingEntity targetLiving){
                float enchantBonus = EnchantmentHelper.getDamageBonus(st, targetLiving.getMobType());
//              XercaMod.LOGGER.warn("Enchantment bonus damage: " + enchantBonus);

                // Critical hit
                boolean cooledAttack = pull > 0.9F;
                boolean critical = cooledAttack && pl.fallDistance > 0.0F && !pl.isOnGround() && !pl.onClimbable() &&
                        !pl.isInWater() && !pl.hasEffect(MobEffects.BLINDNESS) && !pl.isPassenger() && !pl.isSprinting();
                net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(pl, target, critical, critical ? 1.5F : 1.0F);
                critical = hitResult != null;
                if (critical) {
                    damage *= hitResult.getDamageModifier();
                    pl.level.playSound(null, pl.getX(), pl.getY(), pl.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_CRIT, pl.getSoundSource(), 1.0F, 1.0F);
                    pl.crit(target);
                }

                damage += enchantBonus;
                targetLiving.hurt(DamageSource.playerAttack(pl), damage);

                int maimLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_MAIM.get(), st);
                if(maimLevel > 0){
                    targetLiving.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100 + 40 * maimLevel, maimLevel - 1));
                }
            }

            Vec3 knockVector = target.position().subtract(pl.position()).normalize().scale(push);
            target.push(knockVector.x, knockVector.y + bonusVelY, knockVector.z);
            target.hurtMarked = true;
        }else{
            System.out.println("No warhammer at hand!");
        }
    }
}
