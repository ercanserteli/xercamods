package xerca.xercamod.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.Triggers;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.item.ItemScythe;
import xerca.xercamod.common.item.Items;

import java.util.function.Supplier;

public class ScytheAttackPacketHandler {
    public static void handle(final ScytheAttackPacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when ScytheAttackPacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(ScytheAttackPacket msg, ServerPlayer pl) {
        ItemStack st = pl.getMainHandItem();
        Item item = st.getItem();
        if (item instanceof ItemScythe && EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_GUILLOTINE, st) > 0){
            float pull = msg.getPullDuration();
            if(pull < 0.9f){
                XercaMod.LOGGER.warn("Pull duration too short");
                return;
            }
            Entity target = pl.level.getEntity(msg.getTargetId());
            float damage = ((float) pl.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 1.3f);

            st.hurtAndBreak(1, pl, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            if(target instanceof LivingEntity){
                LivingEntity targetLiving = (LivingEntity) target;
                float enchantBonus = EnchantmentHelper.getDamageBonus(st, targetLiving.getMobType());
                damage += enchantBonus;
                targetLiving.hurt(DamageSource.playerAttack(pl), damage);

                if(targetLiving.getHealth() <= 0){
                    pl.level.playSound(null, target.getX(), target.getY() + 0.5d, target.getZ(), SoundEvents.BEHEAD, SoundSource.PLAYERS, 1.0f, pl.level.random.nextFloat() * 0.2F + 0.9f);

                    BeheadParticlePacket pack = new BeheadParticlePacket(96, targetLiving.getX(), targetLiving.getY(), targetLiving.getZ());
                    XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(targetLiving.getX(), targetLiving.getY(), targetLiving.getZ(), 64.0D, pl.getLevel().dimension())), pack);

                    ItemScythe.spawnHead(targetLiving);
                    Triggers.BEHEAD.trigger(pl);
                }
                else{
                    pl.level.playSound(null, target.getX(), target.getY() + 0.5d, target.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0f, pl.level.random.nextFloat() * 0.2F + 0.9f);
                }
            }
        }else{
            XercaMod.LOGGER.warn("No Scythe at hand or doesn't have guillotine!");
        }
    }
}
