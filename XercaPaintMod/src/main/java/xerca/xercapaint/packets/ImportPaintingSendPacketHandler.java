package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xerca.xercapaint.CommandImport;

public class ImportPaintingSendPacketHandler implements ServerPlayNetworking.PlayChannelHandler {

    private static void processMessage(ImportPaintingSendPacket msg, ServerPlayer sender) {
        CommandImport.doImport(msg.getTag(), sender);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ImportPaintingSendPacket packet = ImportPaintingSendPacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
