package xerca.xercamusic.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamusic.common.XercaMusic;

import java.util.function.Supplier;

public class SingleNotePacketHandler {
    public static void handle(final SingleNotePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when MusicUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(SingleNotePacket msg, ServerPlayer pl) {
        PacketDistributor.PacketTarget target = PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pl.getX(), pl.getY(), pl.getZ(), 24.0D, pl.getLevel().dimension()));
        SingleNoteClientPacket packet = new SingleNoteClientPacket(msg.getNote(), msg.getInstrumentItem(), pl, msg.isStop());
        XercaMusic.NETWORK_HANDLER.send(target, packet);
    }
}
