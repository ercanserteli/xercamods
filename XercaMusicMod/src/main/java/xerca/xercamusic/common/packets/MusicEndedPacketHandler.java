package xerca.xercamusic.common.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

import java.util.function.Supplier;

public class MusicEndedPacketHandler {
    public static void handle(final MusicEndedPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when MusicEndedPacketHandler was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(MusicEndedPacket msg, ServerPlayerEntity pl) {
        Entity ent = pl.world.getEntityByID(msg.getPlayerId());
        if(ent instanceof EntityMusicSpirit){
            EntityMusicSpirit spirit = (EntityMusicSpirit) ent;
            spirit.setPlaying(false);
        }
    }
}
