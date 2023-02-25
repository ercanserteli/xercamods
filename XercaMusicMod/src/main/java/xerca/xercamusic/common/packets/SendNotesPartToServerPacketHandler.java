package xerca.xercamusic.common.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.XercaMusic;

import java.util.function.Supplier;

public class SendNotesPartToServerPacketHandler {
    public static void handle(final SendNotesPartToServerPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(SendNotesPartToServerPacket pkt, ServerPlayerEntity sender) {
        if(MusicManager.addNotesPart(pkt)) {
            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(()->sender);
            XercaMusic.NETWORK_HANDLER.send(target, new NotesPartAckFromServerPacket(pkt.getUuid()));
        }
    }
}
