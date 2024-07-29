package xerca.xercamusic.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

import java.util.function.Supplier;

public class MusicEndedPacketHandler {
    public static void handle(final MusicEndedPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when MusicEndedPacketHandler was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(MusicEndedPacket msg, ServerPlayer pl) {
        Entity ent = pl.level().getEntity(msg.getPlayerId());
        if(ent instanceof EntityMusicSpirit spirit){
            spirit.setPlaying(false);
        }
    }
}
