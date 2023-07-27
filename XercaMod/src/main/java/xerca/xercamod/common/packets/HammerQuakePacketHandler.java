package xerca.xercamod.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.Triggers;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.item.ItemWarhammer;
import xerca.xercamod.common.item.Items;

import java.util.List;
import java.util.function.Supplier;

public class HammerQuakePacketHandler {
    public static void handle(final HammerQuakePacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when HammerQuakePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static double rangeForQuake(int quakeLevel){
        return switch (quakeLevel) {
            case 1 -> 9.0;
            case 2 -> 16.0;
            case 3 -> 25.0;
            default -> 0;
        };
    }

    private static void processMessage(HammerQuakePacket msg, ServerPlayer pl) {
        ItemStack st = pl.getMainHandItem();
        Item item = st.getItem();
        if (item instanceof ItemWarhammer){
            int quakeLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_QUAKE.get(), st);
            if(quakeLevel > 0){
                double range = rangeForQuake(quakeLevel);
                List<LivingEntity> targets = pl.level().getEntitiesOfClass(LivingEntity.class, new AABB(pl.position().subtract(5, 5, 5), pl.position().add(5, 5, 5)), entity -> (!entity.is(pl) && entity.position().distanceToSqr(msg.getPosition()) < range));
                if(targets.size() >= 6){
                    Triggers.QUAKE.trigger(pl);
                }

                float pull = msg.getPullDuration();
                float mult = HammerAttackPacketHandler.damageBonusMult(pull);
                int heavyLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_HEAVY.get(), st);

                AttributeInstance attackDamage = pl.getAttribute(Attributes.ATTACK_DAMAGE);
                float damage = ((float) (attackDamage != null ? attackDamage.getValue() : 0) + heavyLevel * 0.5f) * mult * 0.5f;
                float push = (((ItemWarhammer) item).getPushAmount() + heavyLevel * 0.15f)  * mult;

                float pitch = (2.0f / (damage + heavyLevel));
                double volume = Math.log10(10.0*pull + 1.0);
                if(volume > 1.0) volume = 1.0;

                Vec3 pos = msg.getPosition();
                for(LivingEntity target : targets){
                    Vec3 knockvec = target.position().subtract(pos).normalize().scale(push);
                    target.push(knockvec.x, knockvec.y, knockvec.z);
                    target.hurt(pl.damageSources().playerAttack(pl), damage);
                }
                st.hurtAndBreak(1, pl, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                pl.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.STOMP.get(), SoundSource.PLAYERS, (float) volume, pl.level().random.nextFloat() * 0.1F + 0.4F + pitch);

                int particleCount = (int) (volume*64);
                QuakeParticlePacket pack = new QuakeParticlePacket(particleCount, pos.x, pos.y, pos.z);
                XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 64.0D, pl.level().dimension())), pack);

            }
            else{
                System.out.println("No quake found in warhammer!");
            }

        }else{
            System.out.println("No warhammer found in hand!");
        }
    }
}
