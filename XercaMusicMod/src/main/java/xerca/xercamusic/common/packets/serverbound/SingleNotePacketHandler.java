package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xerca.xercamusic.common.packets.clientbound.SingleNoteClientPacket;

import java.util.Collection;

import static xerca.xercamusic.common.XercaMusic.sendToClient;

public class SingleNotePacketHandler implements ServerPlayNetworking.PlayChannelHandler {
     private static void processMessage(SingleNotePacket msg, ServerPlayer pl) {
        Collection<ServerPlayer> players = PlayerLookup.around(pl.getLevel(), pl.position(), 24.0D);
        SingleNoteClientPacket packet = new SingleNoteClientPacket(msg.getNote(), msg.getInstrumentItem(), pl, msg.isStop(), msg.getVolume());
        for(ServerPlayer player : players){
            sendToClient(player, packet);
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        SingleNotePacket packet = SingleNotePacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
