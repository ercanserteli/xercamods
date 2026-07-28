package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.clientbound.SingleNoteClientPacket;

import java.util.function.Supplier;

public class SingleNotePacketHandler {
    private static void processMessage(SingleNotePacket msg, ServerPlayer pl) {
        PacketDistributor.PacketTarget target = PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(pl.getX(), pl.getY(), pl.getZ(), 24.0D, pl.level().dimension()));
        SingleNoteClientPacket packet = new SingleNoteClientPacket(msg.getNote(), msg.getInstrumentItem(), pl, msg.isStop(), msg.getVolume());
        XercaMusic.NETWORK_HANDLER.send(target, packet);
    }
    public static void handle(final SingleNotePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            ServerPlayer sender = ctx.get().getSender();
            ctx.get().enqueueWork(() -> processMessage(message, sender));
        }
        ctx.get().setPacketHandled(true);
    }
}
