package xerca.xercamusic.common.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.common.CommandImport;

import java.util.function.Supplier;

public class ImportMusicSendPacketHandler {
    public static void handle(final ImportMusicSendPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(ImportMusicSendPacket msg, ServerPlayerEntity sender) {
        CommandImport.doImport(msg.getTag(), sender);
    }
}
