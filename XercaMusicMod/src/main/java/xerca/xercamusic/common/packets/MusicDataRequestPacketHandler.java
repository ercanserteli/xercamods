package xerca.xercamusic.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.XercaMusic;

import java.util.UUID;
import java.util.function.Supplier;

public class MusicDataRequestPacketHandler {
    public static void handle(final MusicDataRequestPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("ServerPlayer was null when MusicDataRequestPacketHandler was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(MusicDataRequestPacket msg, ServerPlayer pl) {
        UUID id = msg.getId();
        int version = msg.getVersion();
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, pl.server);
        if(data != null) {
            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(()->pl);
            MusicDataResponsePacket packet = new MusicDataResponsePacket(id, data.version, data.notes);
            XercaMusic.NETWORK_HANDLER.send(target, packet);
        }
    }
}
