package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xerca.xercamusic.common.CommandImport;

public class ImportMusicSendPacketHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static void processMessage(ImportMusicSendPacket msg, ServerPlayer sender) {
        CommandImport.doImport(msg.getTag(), sender);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ImportMusicSendPacket packet = ImportMusicSendPacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
