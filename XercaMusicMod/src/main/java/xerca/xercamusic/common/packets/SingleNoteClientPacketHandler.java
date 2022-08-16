package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemInstrument.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SingleNoteClientPacketHandler {
    static Map<Pair<Player, Integer>, NoteSoundEntry> noteSounds = new HashMap<>();

    public static void handle(final SingleNoteClientPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(SingleNoteClientPacket msg) {
        Player playerEntity = msg.getPlayerEntity();
        if(!playerEntity.equals(Minecraft.getInstance().player)){
            ItemInstrument.InsSound sound = msg.getInstrumentItem().getSound(msg.getNote());
            if(sound == null){
                return;
            }
            if(!msg.isStop()){
                double x = playerEntity.getX();
                double y = playerEntity.getY();
                double z = playerEntity.getZ();

                NoteSound noteSound = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                        ClientStuff.playNote(sound.sound, x, y, z, SoundSource.PLAYERS, msg.getVolume()*1.5f, sound.pitch, (byte) -1));
                noteSounds.put(Pair.of(playerEntity, msg.getNote()), new NoteSoundEntry(noteSound, playerEntity));
                playerEntity.level.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, (msg.getNote()) / 24.0D, 0.0D, 0.0D);
            }
            else{
                NoteSoundEntry oldNoteSoundEntry = noteSounds.get(Pair.of(playerEntity, msg.getNote()));
                if(oldNoteSoundEntry != null && !oldNoteSoundEntry.noteSound.isStopped()){
                    oldNoteSoundEntry.noteSound.stopSound();
                }
            }
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
