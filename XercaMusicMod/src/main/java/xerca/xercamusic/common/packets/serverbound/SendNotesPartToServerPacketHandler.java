package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.packets.clientbound.NotesPartAckFromServerPacket;

import static xerca.xercamusic.common.Mod.sendToClient;

public class SendNotesPartToServerPacketHandler implements ServerPlayNetworking.PlayPayloadHandler<SendNotesPartToServerPacket> {
    private static void processMessage(SendNotesPartToServerPacket pkt, ServerPlayer sender) {
        if(MusicManager.addNotesPart(pkt)) {
            sendToClient(sender, new NotesPartAckFromServerPacket(pkt.uuid()));
        }
    }

    @Override
    public void receive(SendNotesPartToServerPacket packet, ServerPlayNetworking.Context context) {
        if(packet != null){
            context.server().execute(()->processMessage(packet, context.player()));
        }
    }
}

