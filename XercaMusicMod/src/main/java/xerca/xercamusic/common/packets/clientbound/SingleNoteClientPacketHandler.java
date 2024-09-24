package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;

import java.util.HashMap;
import java.util.Map;

import static xerca.xercamusic.common.XercaMusic.onlyCallOnClient;

public class SingleNoteClientPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    static final Map<Pair<Player, Integer>, NoteSoundEntry> noteSounds = new HashMap<>();

    private static void processMessage(SingleNoteClientPacket msg) {
        Player playerEntity = msg.getPlayerEntity();
        if(!playerEntity.equals(Minecraft.getInstance().player)){
            IItemInstrument.InsSound sound = msg.getInstrumentItem().getSound(msg.getNote());
            if(sound == null){
                return;
            }
            if(!msg.isStop()){
                double x = playerEntity.getX();
                double y = playerEntity.getY();
                double z = playerEntity.getZ();

                NoteSound noteSound = onlyCallOnClient(() -> () ->
                        ClientStuff.playNote(sound.sound, x, y, z, SoundSource.PLAYERS, msg.getVolume()*1.5f, sound.pitch, (byte) -1));
                noteSounds.put(Pair.of(playerEntity, msg.getNote()), new NoteSoundEntry(noteSound, playerEntity));
                playerEntity.level().addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, (msg.getNote()) / 24.0D, 0.0D, 0.0D);
            }
            else{
                NoteSoundEntry oldNoteSoundEntry = noteSounds.get(Pair.of(playerEntity, msg.getNote()));
                if(oldNoteSoundEntry != null && !oldNoteSoundEntry.noteSound.isStopped()){
                    oldNoteSoundEntry.noteSound.stopSound();
                }
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        SingleNoteClientPacket packet = SingleNoteClientPacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }

    @SuppressWarnings("unused")
    private static class NoteSoundEntry{
        public final NoteSound noteSound;
        public final Player playerEntity;

        public NoteSoundEntry(NoteSound noteSound, Player playerEntity) {
            this.noteSound = noteSound;
            this.playerEntity = playerEntity;
        }
    }
}
