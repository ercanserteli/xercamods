package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotesPartAckFromServerPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<NotesPartAckFromServerPacket> {
    private static final Map<UUID, Runnable> MAP = new HashMap<>();

    public static void addCallback(UUID id, Runnable func) {
        MAP.put(id, func);
    }

    private static void processMessage(NotesPartAckFromServerPacket msg) {
        UUID id = msg.id();
        if (MAP.containsKey(id)) {
            MAP.get(id).run();
            MAP.remove(id);
        }
    }

    @Override
    public void receive(NotesPartAckFromServerPacket packet, ClientPlayNetworking.Context context) {
        if (packet != null) {
            context.client().execute(() -> processMessage(packet));
        }
    }
}

