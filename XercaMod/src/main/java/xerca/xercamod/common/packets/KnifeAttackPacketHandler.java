package xerca.xercamod.common.packets;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.item.Items;

import java.util.function.Supplier;

public class KnifeAttackPacketHandler {
    public static void handle(final KnifeAttackPacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when KnifeAttackPacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(KnifeAttackPacket msg, ServerPlayerEntity pl) {
        Entity target = pl.world.getEntityByID(msg.getTargetId());
        ItemStack st = pl.getHeldItemOffhand();;
        if (st.getItem() != Items.ITEM_KNIFE) {
            XercaMod.LOGGER.warn("No knife at offhand!");
            return;
        }
        if (target instanceof LivingEntity) {
            st.getItem().hitEntity(st, (LivingEntity) target, pl);
            float enchantBonus = EnchantmentHelper.getModifierForCreature(st, ((LivingEntity) target).getCreatureAttribute());
            target.attackEntityFrom(DamageSource.causePlayerDamage(pl), 3.0f + enchantBonus);
            if(enchantBonus > 0.0f){
                pl.onEnchantmentCritical(target);
                pl.world.playSound(null, pl.getPosX(), pl.getPosY(), pl.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, pl.getSoundCategory(), 1.0F, 1.0F);
            }
            else {
                pl.world.playSound(null, pl.getPosX(), pl.getPosY(), pl.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, pl.getSoundCategory(), 1.0F, 1.0F);
            }
        }

    }
}
