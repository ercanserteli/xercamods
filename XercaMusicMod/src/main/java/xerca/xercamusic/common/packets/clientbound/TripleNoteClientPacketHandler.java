package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.item.IItemInstrument;

import static xerca.xercamusic.common.Mod.onlyCallOnClient;

public class TripleNoteClientPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<TripleNoteClientPacket> {
    private static void processMessage(TripleNoteClientPacket msg) {
        Entity entity = msg.entity();
        IItemInstrument.InsSound sound1 = msg.instrumentItem().getSound(msg.note1());
        IItemInstrument.InsSound sound2 = msg.instrumentItem().getSound(msg.note2());
        IItemInstrument.InsSound sound3 = msg.instrumentItem().getSound(msg.note3());
        if(sound1 == null || sound2 == null || sound3 == null){
            return;
        }

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        onlyCallOnClient(() -> () -> ClientStuff.playNote(sound1.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound1.pitch(), (byte) 10));
        onlyCallOnClient(() -> () -> ClientStuff.playNote(sound2.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound2.pitch(), (byte) 10));
        onlyCallOnClient(() -> () -> ClientStuff.playNote(sound3.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound3.pitch(), (byte) 10));
    }

    @Override
    public void receive(TripleNoteClientPacket packet, ClientPlayNetworking.Context context) {
        if(packet != null) {
            context.client().execute(()->processMessage(packet));
        }
    }
}
