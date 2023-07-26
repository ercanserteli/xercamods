package xerca.xercapaint.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityEasel;

import java.util.function.Supplier;

public class EaselLeftPacketHandler {
    public static void handle(final EaselLeftPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when EaselLeftPacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(EaselLeftPacket msg, ServerPlayer pl) {
        if(msg.getEaselId() > -1){
            Entity entityEasel = pl.level().getEntity(msg.getEaselId());
            if(entityEasel == null){
                XercaPaint.LOGGER.error("EaselLeftPacket: Easel entity not found! easelId: " + msg.getEaselId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                XercaPaint.LOGGER.error("EaselLeftPacket: Entity found is not an easel! easelId: " + msg.getEaselId());
                return;
            }
            easel.setPainter(null);
        }
    }
}
