package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.client.ModClient;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SingleNoteClientPacketHandler {
    static final Map<Pair<Player, Integer>, NoteSoundEntry> NOTE_SOUNDS = new HashMap<>();

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static void processMessage(SingleNoteClientPacket msg) {
        int playerId = msg.playerId();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            Mod.LOGGER.warn("Level is null while trying to get entity");
            return;
        }

        Entity entity = level.getEntity(playerId);
        if (!(entity instanceof Player playerEntity)) {
            Mod.LOGGER.warn("Invalid playerId: {}", playerId);
            return;
        }

        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }

        if (!Objects.equals(playerEntity, localPlayer)) {
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
                            ModClient.playNote(sound.sound(), x, y, z, SoundSource.PLAYERS, msg.volume() * 1.5f, sound.pitch(), (byte) -1));
                } catch (Exception e) {
                    Mod.LOGGER.error("Exception while playing note: ", e);
                    return;
                }
                if (noteSound != null) {
                    NOTE_SOUNDS.put(Pair.of(playerEntity, msg.note()), new NoteSoundEntry(noteSound, playerEntity));
                    playerEntity.level().addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, msg.note() / 24.0D, 0.0D, 0.0D);
                }
            } else {
                NoteSoundEntry oldNoteSoundEntry = NOTE_SOUNDS.get(Pair.of(playerEntity, msg.note()));
                if (oldNoteSoundEntry != null && !oldNoteSoundEntry.noteSound.isStopped()) {
                    oldNoteSoundEntry.noteSound.stopSound();
                }
            }
        }
    }

    public static void handle(SingleNoteClientPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }

    private record NoteSoundEntry(NoteSound noteSound, Player playerEntity) {
    }
}
