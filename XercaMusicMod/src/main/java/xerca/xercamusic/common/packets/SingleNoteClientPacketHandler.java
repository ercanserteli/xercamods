package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.XercaMusic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SingleNoteClientPacketHandler {
    static Map<PlayerEntity, NoteSoundEntry> noteSounds = new HashMap<>();

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
        PlayerEntity playerEntity = msg.getPlayerEntity();
        if(!playerEntity.equals(Minecraft.getInstance().player)){
            SoundEvent sound = msg.getInstrumentItem().getSound(msg.getNote());
            double x = playerEntity.getPosX();
            double y = playerEntity.getPosY();
            double z = playerEntity.getPosZ();
            NoteSound noteSound = XercaMusic.proxy.playNote(sound, x, y, z, SoundCategory.PLAYERS, 1.5f, 1.0f);

            if(msg.getInstrumentItem().shouldCutOff){
                NoteSoundEntry oldNoteSoundEntry = noteSounds.get(playerEntity);
                if(oldNoteSoundEntry != null){
                    oldNoteSoundEntry.noteSound.stopSound();
                }
                noteSounds.put(playerEntity, new NoteSoundEntry(noteSound, playerEntity));
            }

            playerEntity.world.addParticle(ParticleTypes.NOTE, x + 0.5D, y + 2.2D, z + 0.5D, (msg.getNote()) / 24.0D, 0.0D, 0.0D);
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
