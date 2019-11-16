package xerca.xercamusic.common.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.common.Triggers;
import xerca.xercamusic.common.item.Items;

import java.util.function.Supplier;

public class MusicUpdatePacketHandler {
    public static void handle(final MusicUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when MusicUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(MusicUpdatePacket msg, ServerPlayerEntity pl) {
        ItemStack note = pl.getHeldItemMainhand();
        if (!note.isEmpty() && note.getItem() == Items.MUSIC_SHEET) {
            CompoundNBT comp = note.getOrCreateTag();

            comp.putByteArray("music", msg.getMusic());
            comp.putInt("length", msg.getLength());
            comp.putByte("pause", msg.getPause());
            comp.putInt("generation", 0);
            comp.putByte("prevIns", msg.getPrevInstrument());
            comp.putBoolean("piLocked", msg.getPrevInsLocked());
            if (msg.getSigned()) {
                comp.putString("author", pl.getName().getString());
                comp.putString("title", msg.getTitle().trim());
                comp.putInt("generation", 1);

                Triggers.BECOME_MUSICIAN.trigger(pl);
            }
        }
    }
}
