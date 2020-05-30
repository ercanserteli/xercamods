package xerca.xercamod.common.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.network.NetworkEvent;
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
        ItemStack st;
        if (pl.getHeldItemMainhand().getItem() == Items.ITEM_KNIFE)
            st = pl.getHeldItemMainhand();
        else if (pl.getHeldItemOffhand().getItem() == Items.ITEM_KNIFE) {
            st = pl.getHeldItemOffhand();
        } else {
            System.out.println("No knife at hand!");
            return;
        }
        st.getItem().hitEntity(st, (LivingEntity) target, pl);
        target.attackEntityFrom(DamageSource.causePlayerDamage(pl), 3.0f);
    }
}
