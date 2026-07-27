package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;

import static xerca.xercamusic.common.Mod.onlyCallOnClient;

public class TripleNoteClientPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(TripleNoteClientPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            Mod.LOGGER.warn("Level is null while trying to play a triple note packet");
            return;
        }

        Entity entity = level.getEntity(msg.getEntityId());
        if (entity == null) {
            Mod.LOGGER.warn("Invalid entityId in TripleNoteClientPacket: {}", msg.getEntityId());
            return;
        }

        IItemInstrument.InsSound sound1 = msg.getInstrumentItem().getSound(msg.getNote1());
        IItemInstrument.InsSound sound2 = msg.getInstrumentItem().getSound(msg.getNote2());
        IItemInstrument.InsSound sound3 = msg.getInstrumentItem().getSound(msg.getNote3());
        if (sound1 == null || sound2 == null || sound3 == null) {
            return;
        }

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        try {
            onlyCallOnClient(() -> () -> ClientStuff.playNote(sound1.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound1.pitch(), (byte) 10));
            onlyCallOnClient(() -> () -> ClientStuff.playNote(sound2.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound2.pitch(), (byte) 10));
            onlyCallOnClient(() -> () -> ClientStuff.playNote(sound3.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound3.pitch(), (byte) 10));
        } catch (Exception e) {
            Mod.LOGGER.warn("Exception while playing triple note", e);
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        TripleNoteClientPacket packet = TripleNoteClientPacket.decode(buf);
        if (packet != null) {
            client.execute(() -> processMessage(packet));
        }
    }
}
