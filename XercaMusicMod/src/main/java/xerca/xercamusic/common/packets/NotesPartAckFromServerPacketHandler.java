package xerca.xercamusic.common.packets;

import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NotesPartAckFromServerPacketHandler {
    static private final Map<UUID, Runnable> map = new HashMap<>();

    static public void addCallback(UUID id, Runnable func) {
        map.put(id, func);
    }

    public static void handle(final NotesPartAckFromServerPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(NotesPartAckFromServerPacket msg) {
        UUID id = msg.getId();
        if(map.containsKey(id)) {
            map.get(id).run();
            map.remove(id);
        }
    }
}
