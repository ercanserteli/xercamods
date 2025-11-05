package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;

import java.util.HashMap;
import java.util.Map;

public class SingleNoteClientPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<SingleNoteClientPacket> {
    static final Map<Pair<Player, Integer>, NoteSoundEntry> NOTE_SOUNDS = new HashMap<>();

    private static void processMessage(SingleNoteClientPacket msg) {
        Player playerEntity = msg.playerEntity();
        if (playerEntity != null && !playerEntity.equals(Minecraft.getInstance().player)) {
            IItemInstrument.InsSound sound = msg.instrumentItem().getSound(msg.note());
            if (sound == null) {
                return;
            }
            if (!msg.isStop()) {
                double x = playerEntity.getX();
                double y = playerEntity.getY();
                double z = playerEntity.getZ();

                NoteSound noteSound;
                try {
                    noteSound = Mod.onlyCallOnClient(() -> () ->
                            ClientStuff.playNote(sound.sound(), x, y, z, SoundSource.PLAYERS, msg.volume() * 1.5f, sound.pitch(), (byte) -1));
                } catch (Exception e) {
                    Mod.LOGGER.error("Exception while playing note: ", e);
                    return;
                }
                NOTE_SOUNDS.put(Pair.of(playerEntity, msg.note()), new NoteSoundEntry(noteSound, playerEntity));
                playerEntity.level().addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, msg.note() / 24.0D, 0.0D, 0.0D);
            } else {
                NoteSoundEntry oldNoteSoundEntry = NOTE_SOUNDS.get(Pair.of(playerEntity, msg.note()));
                if (oldNoteSoundEntry != null && !oldNoteSoundEntry.noteSound.isStopped()) {
                    oldNoteSoundEntry.noteSound.stopSound();
                }
            }
        }
    }

    @Override
    public void receive(SingleNoteClientPacket packet, ClientPlayNetworking.Context context) {
        if (packet != null) {
            context.client().execute(() -> processMessage(packet));
        }
    }

    private record NoteSoundEntry(NoteSound noteSound, Player playerEntity) {
    }
}
