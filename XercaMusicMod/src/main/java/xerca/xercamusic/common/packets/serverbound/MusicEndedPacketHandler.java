package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

import java.util.function.Supplier;

public class MusicEndedPacketHandler {
    private static void processMessage(MusicEndedPacket msg, ServerPlayer pl) {
        Entity ent = pl.level().getEntity(msg.getPlayerId());
        if (ent instanceof EntityMusicSpirit spirit) {
            spirit.setPlaying(false);
        }
    }
    public static void handle(final MusicEndedPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            ServerPlayer sender = ctx.get().getSender();
            ctx.get().enqueueWork(() -> processMessage(message, sender));
        }
        ctx.get().setPacketHandled(true);
    }
}
