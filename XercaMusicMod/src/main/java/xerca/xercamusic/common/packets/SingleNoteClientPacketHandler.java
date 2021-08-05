package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.XercaMusic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SingleNoteClientPacketHandler {
    static Map<Player, NoteSoundEntry> noteSounds = new HashMap<>();

    public static void handle(final SingleNoteClientPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(SingleNoteClientPacket msg) {
        Player playerEntity = msg.getPlayerEntity();
        if(!playerEntity.equals(Minecraft.getInstance().player)){
            SoundEvent sound = msg.getInstrumentItem().getSound(msg.getNote());
            double x = playerEntity.getX();
            double y = playerEntity.getY();
            double z = playerEntity.getZ();
//            NoteSound noteSound = XercaMusic.proxy.playNote(sound, x, y, z, SoundSource.PLAYERS, 1.5f, 1.0f);
            NoteSound noteSound = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                    ClientStuff.playNote(sound, x, y, z, SoundSource.PLAYERS, 1.5f, 1.0f));

            if(msg.getInstrumentItem().shouldCutOff){
                NoteSoundEntry oldNoteSoundEntry = noteSounds.get(playerEntity);
                if(oldNoteSoundEntry != null){
                    oldNoteSoundEntry.noteSound.stopSound();
                }
                noteSounds.put(playerEntity, new NoteSoundEntry(noteSound, playerEntity));
            }

            playerEntity.level.addParticle(ParticleTypes.NOTE, x + 0.5D, y + 2.2D, z + 0.5D, (msg.getNote()) / 24.0D, 0.0D, 0.0D);
        }
    }

    private static class NoteSoundEntry{
        public NoteSound noteSound;
        public Player playerEntity;

        public NoteSoundEntry(NoteSound noteSound, Player playerEntity) {
            this.noteSound = noteSound;
            this.playerEntity = playerEntity;
        }
    }
}
