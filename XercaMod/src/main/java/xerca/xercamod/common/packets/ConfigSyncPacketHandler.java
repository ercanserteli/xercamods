package xerca.xercamod.common.packets;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamod.common.Config;

import java.util.function.Supplier;

public class ConfigSyncPacketHandler {
    public static void handle(final ConfigSyncPacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(ConfigSyncPacket pkt) {
        Config.syncWithPacket(pkt);
    }
}