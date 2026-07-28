package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.CommandImport;

import java.util.function.Supplier;

public class ImportMusicSendPacketHandler {
    private static void processMessage(ImportMusicSendPacket msg, ServerPlayer sender) {
        CommandImport.doImport(msg.getTag(), msg.getNotes(), sender);
    }
    public static void handle(final ImportMusicSendPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            ServerPlayer sender = ctx.get().getSender();
            ctx.get().enqueueWork(() -> processMessage(message, sender));
        }
        ctx.get().setPacketHandled(true);
    }
}
