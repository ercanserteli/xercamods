package xerca.xercamusic.common.packets;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.item.ItemInstrument;

public class SingleNoteClientPacketHandler {
    static Map<AbstractMap.SimpleImmutableEntry<PlayerEntity, Integer>, NoteSoundEntry> noteSounds = new HashMap<>();

    public static void handle(final SingleNoteClientPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(SingleNoteClientPacket msg) {
    	Minecraft mc = Minecraft.getInstance();
        PlayerEntity playerEntity = msg.getPlayerEntity();
        if(!playerEntity.equals(mc.player)){
            ItemInstrument.InsSound sound = msg.getInstrumentItem().getSound(msg.getNote());
            if(sound == null){
                return;
            }
            if(!msg.isStop()){
                double x = playerEntity.getPosX();
                double y = playerEntity.getPosY();
                double z = playerEntity.getPosZ();

                NoteSound noteSound = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                        ClientStuff.playNote(sound.sound, x, y, z, SoundCategory.PLAYERS, 1.5f, sound.pitch, (byte) -1));
                noteSounds.put(new AbstractMap.SimpleImmutableEntry<PlayerEntity, Integer>(playerEntity, msg.getNote()), new NoteSoundEntry(noteSound, playerEntity));
                playerEntity.world.addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, (msg.getNote()) / 24.0D, 0.0D, 0.0D);
            }
            else{
                NoteSoundEntry oldNoteSoundEntry = noteSounds.get(new AbstractMap.SimpleImmutableEntry<PlayerEntity, Integer>(playerEntity, msg.getNote()));
                if(oldNoteSoundEntry != null && !oldNoteSoundEntry.noteSound.isDonePlaying()){
                    oldNoteSoundEntry.noteSound.stopSound();
                }
            }
        }
    }

    private static class NoteSoundEntry{
        public NoteSound noteSound;
        public PlayerEntity playerEntity;

        public NoteSoundEntry(NoteSound noteSound, PlayerEntity playerEntity) {
            this.noteSound = noteSound;
            this.playerEntity = playerEntity;
        }
    }
}
