package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotesPartAckFromServerPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static final Map<UUID, Runnable> MAP = new HashMap<>();

    public static void addCallback(UUID id, Runnable func) {
        MAP.put(id, func);
    }

    private static void processMessage(NotesPartAckFromServerPacket msg) {
        UUID id = msg.getMusicId();
        if (MAP.containsKey(id)) {
            MAP.get(id).run();
            MAP.remove(id);
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        NotesPartAckFromServerPacket packet = NotesPartAckFromServerPacket.decode(buf);
        if (packet != null) {
            client.execute(() -> processMessage(packet));
        }
    }
}

