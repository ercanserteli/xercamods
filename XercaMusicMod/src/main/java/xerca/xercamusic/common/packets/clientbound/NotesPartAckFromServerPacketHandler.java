package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NotesPartAckFromServerPacketHandler {
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

    public static void handle(NotesPartAckFromServerPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }
}

