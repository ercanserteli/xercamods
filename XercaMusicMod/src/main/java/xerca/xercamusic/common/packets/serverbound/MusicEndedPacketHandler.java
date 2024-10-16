package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

public class MusicEndedPacketHandler implements ServerPlayNetworking.PlayPayloadHandler<MusicEndedPacket> {
     private static void processMessage(MusicEndedPacket msg, ServerPlayer pl) {
        Entity ent = pl.level().getEntity(msg.playerId());
        if(ent instanceof EntityMusicSpirit spirit){
            spirit.setPlaying(false);
        }
    }

    @Override
    public void receive(MusicEndedPacket packet, ServerPlayNetworking.Context context) {
        if(packet != null){
            context.server().execute(()->processMessage(packet, context.player()));
        }
    }
}
