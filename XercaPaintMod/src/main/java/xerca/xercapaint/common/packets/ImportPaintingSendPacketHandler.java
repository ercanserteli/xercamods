package xerca.xercapaint.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xerca.xercapaint.common.CommandImport;

import java.util.function.Supplier;

public class ImportPaintingSendPacketHandler {
    public static void handle(final ImportPaintingSendPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(ImportPaintingSendPacket msg, ServerPlayer sender) {
        CommandImport.doImport(msg.getTag(), sender);
    }
}
