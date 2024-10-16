package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xerca.xercamusic.common.packets.clientbound.SingleNoteClientPacket;

import java.util.Collection;

import static xerca.xercamusic.common.Mod.sendToClient;

public class SingleNotePacketHandler implements ServerPlayNetworking.PlayPayloadHandler<SingleNotePacket> {
     private static void processMessage(SingleNotePacket msg, ServerPlayer pl) {
        Collection<ServerPlayer> players = PlayerLookup.around((ServerLevel) pl.level(), pl.position(), 24.0D);
        SingleNoteClientPacket packet = new SingleNoteClientPacket(msg.note(), msg.instrumentItem(), pl, msg.isStop(), msg.volume());
        for(ServerPlayer player : players){
            sendToClient(player, packet);
        }
    }

    @Override
    public void receive(SingleNotePacket packet, ServerPlayNetworking.Context context) {
        if(packet != null){
            context.server().execute(()->processMessage(packet, context.player()));
        }
    }
}
