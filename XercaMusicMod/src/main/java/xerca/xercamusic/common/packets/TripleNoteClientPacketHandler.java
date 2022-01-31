package xerca.xercamusic.common.packets;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.item.ItemInstrument;

public class TripleNoteClientPacketHandler {
    public static void handle(final TripleNoteClientPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(TripleNoteClientPacket msg) {
        Entity entity = msg.getEntity();
        ItemInstrument.InsSound sound1 = msg.getInstrumentItem().getSound(msg.getNote1());
        ItemInstrument.InsSound sound2 = msg.getInstrumentItem().getSound(msg.getNote2());
        ItemInstrument.InsSound sound3 = msg.getInstrumentItem().getSound(msg.getNote3());
        if(sound1 == null || sound2 == null || sound3 == null){
            return;
        }

        double x = entity.getPosX();
        double y = entity.getPosY();
        double z = entity.getPosZ();

        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound1.sound, x, y, z, SoundCategory.PLAYERS, 1.5f, sound1.pitch, (byte) 10));
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound2.sound, x, y, z, SoundCategory.PLAYERS, 1.5f, sound2.pitch, (byte) 10));
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound3.sound, x, y, z, SoundCategory.PLAYERS, 1.5f, sound3.pitch, (byte) 10));
    }
}
