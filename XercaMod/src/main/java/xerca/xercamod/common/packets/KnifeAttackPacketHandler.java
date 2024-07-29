package xerca.xercamod.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.item.ItemKnife;
import xerca.xercamod.common.item.Items;

import java.util.function.Supplier;

public class KnifeAttackPacketHandler {
    public static void handle(final KnifeAttackPacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when KnifeAttackPacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(KnifeAttackPacket msg, ServerPlayer pl) {
        Entity target = pl.level().getEntity(msg.getTargetId());
        ItemStack st = pl.getOffhandItem();
        if (st.getItem() != Items.ITEM_KNIFE.get()) {
            XercaMod.LOGGER.warn("No knife at offhand!");
            return;
        }
        if (target instanceof LivingEntity targetLiving) {
            float critBonus = ItemKnife.critDamage(targetLiving, pl, st);
            float enchantBonus = EnchantmentHelper.getDamageBonus(st, targetLiving.getMobType());
            target.hurt(pl.damageSources().playerAttack(pl), 3.0f + enchantBonus + critBonus);
            st.getItem().hurtEnemy(st, (LivingEntity) target, pl);
            if(enchantBonus > 0.0f){
                pl.magicCrit(target);
                pl.level().playSound(null, pl.getX(), pl.getY(), pl.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, pl.getSoundSource(), 1.0F, 1.0F);
            }
            else {
                pl.level().playSound(null, pl.getX(), pl.getY(), pl.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, pl.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }
}
