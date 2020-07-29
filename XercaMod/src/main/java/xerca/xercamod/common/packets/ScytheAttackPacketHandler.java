package xerca.xercamod.common.packets;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamod.common.SoundEvents;
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

        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when ScytheAttackPacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(ScytheAttackPacket msg, ServerPlayerEntity pl) {
        ItemStack st = pl.getHeldItemMainhand();
        Item item = st.getItem();
        if (item instanceof ItemScythe && EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_GUILLOTINE, st) > 0){
            float pull = msg.getPullDuration();
            if(pull < 0.9f){
                XercaMod.LOGGER.warn("Pull duration too short");
                return;
            }
            Entity target = pl.world.getEntityByID(msg.getTargetId());
            float damage = ((float) pl.getAttributes().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getValue() * 0.5f) * 1.5f;

            st.damageItem(1, pl, (p) -> p.sendBreakAnimation(Hand.MAIN_HAND));
            if(target instanceof LivingEntity){
                LivingEntity targetLiving = (LivingEntity) target;
                float enchantBonus = EnchantmentHelper.getModifierForCreature(st, targetLiving.getCreatureAttribute());
                damage += enchantBonus;
                targetLiving.attackEntityFrom(DamageSource.causePlayerDamage(pl), damage);

                if(targetLiving.getHealth() <= 0){
                    pl.world.playSound(null, target.getPosX(), target.getPosY() + 0.5d, target.getPosZ(), SoundEvents.BEHEAD, SoundCategory.PLAYERS, 1.0f, pl.world.rand.nextFloat() * 0.2F + 0.9f);

                    BeheadParticlePacket pack = new BeheadParticlePacket(96, targetLiving.getPosX(), targetLiving.getPosY(), targetLiving.getPosZ());
                    XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(targetLiving.getPosX(), targetLiving.getPosY(), targetLiving.getPosZ(), 64.0D, pl.dimension)), pack);

                    ItemScythe.spawnHead(targetLiving);
                }
                else{
                    pl.world.playSound(null, target.getPosX(), target.getPosY() + 0.5d, target.getPosZ(), net.minecraft.util.SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0f, pl.world.rand.nextFloat() * 0.2F + 0.9f);
                }
            }
        }else{
            XercaMod.LOGGER.warn("No Scythe at hand or doesn't have guillotine!");
        }
    }
}
