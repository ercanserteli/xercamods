package xerca.xercamusic.common.packets;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.item.ItemInstrument;

import java.util.function.Supplier;

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

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound1.sound, x, y, z, SoundSource.PLAYERS, 1.5f, sound1.pitch, (byte) 10));
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound2.sound, x, y, z, SoundSource.PLAYERS, 1.5f, sound2.pitch, (byte) 10));
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound3.sound, x, y, z, SoundSource.PLAYERS, 1.5f, sound3.pitch, (byte) 10));
    }
}
