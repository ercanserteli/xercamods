package xerca.xercamod.common.packets;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.Triggers;
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

        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when HammerQuakePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static double rangeForQuake(int quakeLevel){
        switch (quakeLevel){
            case 1:
                return 9.0;
            case 2:
                return 16.0;
            case 3:
                return 25.0;
            default:
                return 0;
        }
    }

    private static void processMessage(HammerQuakePacket msg, ServerPlayerEntity pl) {
        ItemStack st = pl.getHeldItemMainhand();
        Item item = st.getItem();
        if (item instanceof ItemWarhammer){
            int quakeLevel = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_QUAKE, st);
            if(quakeLevel > 0){
                double range = rangeForQuake(quakeLevel);
                List<LivingEntity> targets = pl.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pl.getPositionVector().subtract(5, 5, 5), pl.getPositionVector().add(5, 5, 5)), entity -> (!entity.isEntityEqual(pl) && entity.getPositionVector().squareDistanceTo(msg.getPosition()) < range));
                if(targets.size() >= 6){
                    Triggers.QUAKE.trigger(pl);
                }

                float pull = msg.getPullDuration();
                float mult = HammerAttackPacketHandler.damageBonusMult(pull);
                int heavyLevel = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_HEAVY, st);
                float damage = ((float) pl.getAttributes().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getValue() + heavyLevel * 0.5f) * mult * 0.5f;
                float push = (((ItemWarhammer) item).getPushAmount() + heavyLevel * 0.15f)  * mult;

                float pitch = (2.0f / (damage + heavyLevel));
                double volume = Math.log10(10.0*pull + 1.0);
                if(volume > 1.0) volume = 1.0;

                Vec3d pos = msg.getPosition();
                for(LivingEntity target : targets){
                    Vec3d knockvec = target.getPositionVector().subtract(pos).normalize().scale(push);
                    target.addVelocity(knockvec.x, knockvec.y, knockvec.z);
                    target.attackEntityFrom(DamageSource.causePlayerDamage(pl), damage);
                }
                st.damageItem(1, pl, (p) -> p.sendBreakAnimation(Hand.MAIN_HAND));
                pl.world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.STOMP, SoundCategory.PLAYERS, (float) volume, pl.world.rand.nextFloat() * 0.1F + 0.4F + pitch);

                int particleCount = (int) (volume*64);
                QuakeParticlePacket pack = new QuakeParticlePacket(particleCount, pos.x, pos.y, pos.z);
                XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 64.0D, pl.dimension)), pack);

            }
            else{
                System.out.println("No quake found in warhammer!");
            }

        }else{
            System.out.println("No warhammer found in hand!");
        }
    }
}
