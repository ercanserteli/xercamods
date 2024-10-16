package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.UUID;

public class MusicDataResponsePacketHandler implements ClientPlayNetworking.PlayPayloadHandler<MusicDataResponsePacket> {
    private static void processMessage(MusicDataResponsePacket msg) {
        UUID id = msg.id();
        int version = msg.version();
        ArrayList<NoteEvent> notes = msg.notes();
        MusicManagerClient.setMusicData(id, version, notes);
    }

    @Override
    public void receive(MusicDataResponsePacket packet, ClientPlayNetworking.Context context) {
        if(packet != null) {
            context.client().execute(()->processMessage(packet));
        }
    }
}
