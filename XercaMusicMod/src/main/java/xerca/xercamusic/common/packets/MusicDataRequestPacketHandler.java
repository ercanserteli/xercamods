package xerca.xercamusic.common.packets;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.XercaMusic;

public class MusicDataRequestPacketHandler {
    public static void handle(final MusicDataRequestPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("ServerPlayerEntity was null when MusicDataRequestPacketHandler was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(MusicDataRequestPacket msg, ServerPlayerEntity pl) {
        UUID id = msg.getId();
        int version = msg.getVersion();
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, pl.server);
        PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(()->pl);
        MusicDataResponsePacket packet;
        if(data != null) {
            packet = new MusicDataResponsePacket(id, data.version, data.notes);
        } else {
            packet = new MusicDataResponsePacket(id, 0, new ArrayList<>());
        }
        XercaMusic.NETWORK_HANDLER.send(target, packet);
    }
}
