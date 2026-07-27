package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NotesPartAckFromServerPacketHandler {
    private static final Map<UUID, Runnable> MAP = new HashMap<>();

    private NotesPartAckFromServerPacketHandler() {
    }

    public static void addCallback(UUID id, Runnable func) {
        MAP.put(id, func);
    }

    public static void handle(NotesPartAckFromServerPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }

    private static void processMessage(NotesPartAckFromServerPacket msg) {
        UUID id = msg.id();
        Runnable callback = MAP.remove(id);
        if (callback != null) {
            callback.run();
        }
    }
}
