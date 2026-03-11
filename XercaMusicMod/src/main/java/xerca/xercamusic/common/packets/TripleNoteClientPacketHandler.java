package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;

import java.util.function.Supplier;

public class TripleNoteClientPacketHandler {
    public static void handle(final TripleNoteClientPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message == null || !message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(TripleNoteClientPacket msg) {
        int entityId = msg.getEntityId();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            XercaMusic.LOGGER.warn("Level is null while trying to get entity");
            return;
        }

        Entity entity = level.getEntity(entityId);
        if (entity == null) {
            XercaMusic.LOGGER.warn("Invalid entityId: {}", entityId);
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

        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound1.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound1.pitch(), (byte) 10));
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound2.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound2.pitch(), (byte) 10));
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(sound3.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound3.pitch(), (byte) 10));
    }
}

