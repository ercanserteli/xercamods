package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import xerca.xercamusic.common.CommandImport;

public class ImportMusicSendPacketHandler implements ServerPlayNetworking.PlayPayloadHandler<ImportMusicSendPacket> {
    private static void processMessage(ImportMusicSendPacket msg, ServerPlayer sender) {
        CommandImport.doImport(msg.tag(), msg.notes(), sender);
    }

    @Override
    public void receive(ImportMusicSendPacket packet, ServerPlayNetworking.Context context) {
        if(packet != null){
            context.server().execute(()->processMessage(packet, context.player()));
        }
    }
}
