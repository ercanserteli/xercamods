package xerca.xercamusic.common.packets;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamusic.common.XercaMusic;

public class SingleNotePacketHandler {
    public static void handle(final SingleNotePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when MusicUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(SingleNotePacket msg, ServerPlayerEntity pl) {
        PacketDistributor.PacketTarget target = PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pl.getPosX(), pl.getPosY(), pl.getPosZ(), 24.0D, pl.getServerWorld().getDimensionKey()));
        SingleNoteClientPacket packet = new SingleNoteClientPacket(msg.getNote(), msg.getInstrumentItem(), pl, msg.isStop());
        XercaMusic.NETWORK_HANDLER.send(target, packet);
    }
}
