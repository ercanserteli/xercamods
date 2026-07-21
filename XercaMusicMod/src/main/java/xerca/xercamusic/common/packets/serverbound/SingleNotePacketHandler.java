package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.common.packets.clientbound.SingleNoteClientPacket;

public final class SingleNotePacketHandler {
    private SingleNotePacketHandler() {
    }

    private static void processMessage(SingleNotePacket msg, ServerPlayer pl) {
        SingleNoteClientPacket packet = new SingleNoteClientPacket(msg.note(), msg.instrumentItem(), pl, msg.isStop(), msg.volume());
        PacketDistributor.sendToPlayersNear(pl.level(), null, pl.getX(), pl.getY(), pl.getZ(), 24.0D, packet);
    }

    public static void handle(SingleNotePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet, (ServerPlayer) context.player()));
    }
}
