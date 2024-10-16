package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotesPartAckFromServerPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<NotesPartAckFromServerPacket> {
    static private final Map<UUID, Runnable> map = new HashMap<>();

    static public void addCallback(UUID id, Runnable func) {
        map.put(id, func);
    }

    private static void processMessage(NotesPartAckFromServerPacket msg) {
        UUID id = msg.id();
        if(map.containsKey(id)) {
            map.get(id).run();
            map.remove(id);
        }
    }

    @Override
    public void receive(NotesPartAckFromServerPacket packet, ClientPlayNetworking.Context context) {
        if(packet != null){
            context.client().execute(()->processMessage(packet));
        }
    }
}

