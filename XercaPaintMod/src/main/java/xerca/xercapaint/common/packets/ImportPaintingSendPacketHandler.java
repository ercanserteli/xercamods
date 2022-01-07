package xerca.xercapaint.common.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
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

    private static void processMessage(ImportPaintingSendPacket msg, ServerPlayerEntity sender) {
        CommandImport.doImport(msg.getTag(), sender);
    }
}
