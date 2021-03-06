package xerca.xercamod.common.packets;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
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

        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
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

    private static void processMessage(HammerAttackPacket msg, ServerPlayerEntity pl) {
        Entity target = pl.world.getEntityByID(msg.getTargetId());

        ItemStack st = pl.getHeldItemMainhand();
        Item item = st.getItem();
        if (item instanceof ItemWarhammer){
            float pull = msg.getPullDuration();
            float mult = damageBonusMult(pull);
            int heavyLevel = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_HEAVY, st);
            float damage = ((float) pl.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + heavyLevel * 0.5f) * mult;
            float push = (((ItemWarhammer) item).getPushAmount() + heavyLevel * 0.15f) * 2 * mult;

            int uppercutLevel = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_UPPERCUT, st);
            double bonusVelY = (uppercutLevel * 0.25) * pull;

            float pitch = (2.0f / (damage + heavyLevel));
            pl.world.playSound(null, target.getPosX(), target.getPosY() + 0.5d, target.getPosZ(), SoundEvents.HAMMER, SoundCategory.PLAYERS, 1.0f, pl.world.rand.nextFloat() * 0.1F + 0.4F + pitch);
            st.damageItem(1, pl, (p) -> p.sendBreakAnimation(Hand.MAIN_HAND));
            if(target instanceof LivingEntity){
                LivingEntity targetLiving = (LivingEntity) target;
                float enchantBonus = EnchantmentHelper.getModifierForCreature(st, targetLiving.getCreatureAttribute());
//              XercaMod.LOGGER.warn("Enchantment bonus damage: " + enchantBonus);

                // Critical hit
                boolean cooledAttack = pull > 0.9F;
                boolean critical = cooledAttack && pl.fallDistance > 0.0F && !pl.isOnGround() && !pl.isOnLadder() &&
                        !pl.isInWater() && !pl.isPotionActive(Effects.BLINDNESS) && !pl.isPassenger() && !pl.isSprinting();
                net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(pl, target, critical, critical ? 1.5F : 1.0F);
                critical = hitResult != null;
                if (critical) {
                    damage *= hitResult.getDamageModifier();
                    pl.world.playSound(null, pl.getPosX(), pl.getPosY(), pl.getPosZ(), net.minecraft.util.SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, pl.getSoundCategory(), 1.0F, 1.0F);
                    pl.onCriticalHit(target);
                }

                damage += enchantBonus;
                targetLiving.attackEntityFrom(DamageSource.causePlayerDamage(pl), damage);

                int maimLevel = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_MAIM, st);
                if(maimLevel > 0){
                    targetLiving.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100 + 40 * maimLevel, maimLevel - 1));
                }
            }

            Vector3d knockVector = target.getPositionVec().subtract(pl.getPositionVec()).normalize().scale(push);
            target.addVelocity(knockVector.x, knockVector.y + bonusVelY, knockVector.z);
            target.velocityChanged = true;
        }else{
            System.out.println("No warhammer at hand!");
        }
    }
}
