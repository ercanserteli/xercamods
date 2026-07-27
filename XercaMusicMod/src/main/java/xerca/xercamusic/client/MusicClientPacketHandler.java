package xerca.xercamusic.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import xerca.xercamusic.common.CommandExport;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ExportMusicPacket;
import xerca.xercamusic.common.packets.clientbound.ImportMusicPacket;
import xerca.xercamusic.common.packets.clientbound.MusicBoxUpdatePacket;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;
import xerca.xercamusic.common.packets.clientbound.NotesPartAckFromServerPacketHandler;
import xerca.xercamusic.common.packets.clientbound.SingleNoteClientPacket;
import xerca.xercamusic.common.packets.clientbound.TripleNoteClientPacket;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xerca.xercamusic.client.ModClient.sendToServer;
import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_NOTES;

/**
 * Client-side processing for the mod's client-bound (S2C) payloads. Referenced only from the {@code enqueueWork}
 * lambdas of the dist-neutral packet handlers so the dedicated server never links these client types.
 */
public final class MusicClientPacketHandler {
    static final Map<Pair<Player, Integer>, NoteSoundEntry> NOTE_SOUNDS = new HashMap<>();

    private MusicClientPacketHandler() {
    }

    public static void exportMusic(ExportMusicPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (CommandExport.doExport(player, msg.name())) {
                player.sendSystemMessage(Component.translatable("xercamusic.export.success", msg.name()).withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.translatable("xercamusic.export.fail").withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void importMusic(ImportMusicPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        String filename = msg.name() + ".sheet";
        String filepath = "music_sheets/" + filename;
        try {
            CompoundTag tag = NbtIo.read(Path.of(filepath));
            if (tag == null) {
                throw new IOException("File not found!");
            }
            sendMusic(tag);
        } catch (IOException | ImportMusicSendPacket.NotesTooLargeException e) {
            Mod.LOGGER.error("Exception while reading music sheet: ", e);
            player.sendSystemMessage(Component.translatable("xercamusic.import.fail.4", filepath).withStyle(ChatFormatting.RED));
        }
    }

    private static void sendMusic(CompoundTag tag) throws IOException, ImportMusicSendPacket.NotesTooLargeException {
        try {
            ImportMusicSendPacket pack = ImportMusicSendPacket.create(tag);
            sendToServer(pack);
        } catch (ImportMusicSendPacket.NotesTooLargeException e) {
            if (e.id == null) {
                throw new IOException("Music has many notes, but no UUID!");
            }
            List<NoteEvent> notes = e.getNotes();
            int partsCount = (int) Math.ceil((double) notes.size() / (double) MAX_NOTES_IN_PACKET);
            tag.remove(KEY_NOTES);
            ImportMusicSendPacket pack = ImportMusicSendPacket.create(tag);
            NotesPartAckFromServerPacketHandler.addCallback(e.id, () -> sendToServer(pack));
            for (int i = 0; i < partsCount; i++) {
                SendNotesPartToServerPacket partPack = new SendNotesPartToServerPacket(e.id, partsCount, i, notes.subList(i * MAX_NOTES_IN_PACKET, Math.min((i + 1) * MAX_NOTES_IN_PACKET, notes.size())));
                sendToServer(partPack);
            }
        }
    }

    public static void musicBoxUpdate(MusicBoxUpdatePacket msg) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        int x = SectionPos.blockToSectionCoord(msg.pos().getX());
        int z = SectionPos.blockToSectionCoord(msg.pos().getZ());
        ChunkAccess chunk = world.getChunk(x, z, ChunkStatus.FULL, false);
        if (chunk == null) {
            return;
        }

        BlockEntity te = world.getBlockEntity(msg.pos());
        if (te instanceof TileEntityMusicBox tileEntityMusicBox) {

            if (msg.sheetSent()) {
                if (msg.noSheet()) {
                    tileEntityMusicBox.removeSheetStack();
                } else {
                    ItemStack sheetStack = new ItemStack(Items.MUSIC_SHEET);
                    sheetStack.set(Items.SHEET_ID, msg.sheetId());
                    sheetStack.set(Items.SHEET_VERSION, msg.version());
                    sheetStack.set(Items.SHEET_BPS, msg.bps());
                    sheetStack.set(Items.SHEET_LENGTH, msg.length());
                    sheetStack.set(Items.SHEET_VOLUME, msg.volume());
                    tileEntityMusicBox.setSheetStack(sheetStack, false);
                }
            }

            if (!msg.instrumentId().isEmpty()) {
                tileEntityMusicBox.setInstrument(BuiltInRegistries.ITEM.get(ResourceLocation.parse(msg.instrumentId())));
            } else {
                tileEntityMusicBox.removeInstrument();
            }
        }
    }

    public static void musicDataResponse(MusicDataResponsePacket msg) {
        List<NoteEvent> notes = msg.notes();
        List<VolumeMarker> volumeMarkers = msg.volumeMarkers();
        MusicManagerClient.setMusicData(msg.id(), msg.version(), notes, volumeMarkers);
    }

    public static void singleNote(SingleNoteClientPacket msg) {
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

        if (!playerEntity.equals(Minecraft.getInstance().player)) {
            IItemInstrument.InsSound sound = msg.instrumentItem().getSound(msg.note());
            if (sound == null) {
                return;
            }
            if (!msg.isStop()) {
                double x = playerEntity.getX();
                double y = playerEntity.getY();
                double z = playerEntity.getZ();

                NoteSound noteSound = ModClient.playNote(sound.sound(), x, y, z, SoundSource.PLAYERS, msg.volume() * 1.5f, sound.pitch(), (byte) -1);
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

    public static void tripleNote(TripleNoteClientPacket msg) {
        int entityId = msg.entityId();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            Mod.LOGGER.warn("Level is null while trying to get entity");
            return;
        }

        Entity entity = level.getEntity(entityId);
        if (entity == null) {
            Mod.LOGGER.warn("Invalid entityId: {}", entityId);
            return;
        }

        IItemInstrument.InsSound sound1 = msg.instrumentItem().getSound(msg.note1());
        IItemInstrument.InsSound sound2 = msg.instrumentItem().getSound(msg.note2());
        IItemInstrument.InsSound sound3 = msg.instrumentItem().getSound(msg.note3());
        if (sound1 == null || sound2 == null || sound3 == null) {
            return;
        }

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        ModClient.playNote(sound1.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound1.pitch(), (byte) 10);
        ModClient.playNote(sound2.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound2.pitch(), (byte) 10);
        ModClient.playNote(sound3.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound3.pitch(), (byte) 10);
    }

    private record NoteSoundEntry(NoteSound noteSound, Player playerEntity) {
    }
}
