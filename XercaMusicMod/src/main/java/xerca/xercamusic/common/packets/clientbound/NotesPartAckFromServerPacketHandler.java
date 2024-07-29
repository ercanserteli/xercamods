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
    static private final Map<UUID, Runnable> map = new HashMap<>();

    static public void addCallback(UUID id, Runnable func) {
        map.put(id, func);
    }

    private static void processMessage(NotesPartAckFromServerPacket msg) {
        UUID id = msg.getId();
        if(map.containsKey(id)) {
            map.get(id).run();
            map.remove(id);
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        NotesPartAckFromServerPacket packet = NotesPartAckFromServerPacket.decode(buf);
        if(packet != null){
            client.execute(()->processMessage(packet));
        }
    }
}

