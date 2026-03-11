package xerca.xercamusic.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.CommandImport;

import java.util.function.Supplier;

public class ImportMusicSendPacketHandler {
    public static void handle(final ImportMusicSendPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message == null || !message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayer sender = ctx.get().getSender();
        if (sender == null) {
            System.err.println("ServerPlayer was null when ImportMusicSendPacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sender));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(ImportMusicSendPacket msg, ServerPlayer sender) {
        CommandImport.doImport(msg.getTag(), msg.getNotes(), sender);
    }
}

