package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;

public class EaselLeftPacketHandler implements ServerPlayNetworking.PlayPayloadHandler<EaselLeftPacket> {
    private static void processMessage(EaselLeftPacket msg, ServerPlayer pl) {
        if(msg.easelId() > -1){
            Entity entityEasel = pl.level().getEntity(msg.easelId());
            if(entityEasel == null){
                Mod.LOGGER.error("EaselLeftPacket: Easel entity not found! easelId: {}", msg.easelId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                Mod.LOGGER.error("EaselLeftPacket: Entity found is not an easel! easelId: {}", msg.easelId());
                return;
            }
            easel.setPainter(null);
        }
    }

    @Override
    public void receive(EaselLeftPacket packet, ServerPlayNetworking.Context context) {
        context.server().execute(()->processMessage(packet, context.player()));
    }
}
