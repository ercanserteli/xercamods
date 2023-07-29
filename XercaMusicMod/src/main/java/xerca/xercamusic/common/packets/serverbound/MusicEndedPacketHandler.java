package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

public class MusicEndedPacketHandler implements ServerPlayNetworking.PlayChannelHandler {
     private static void processMessage(MusicEndedPacket msg, ServerPlayer pl) {
        Entity ent = pl.level().getEntity(msg.getPlayerId());
        if(ent instanceof EntityMusicSpirit spirit){
            spirit.setPlaying(false);
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MusicEndedPacket packet = MusicEndedPacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
