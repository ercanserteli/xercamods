package xerca.xercamusic.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamusic.common.CommandExport;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.IItemInstrument.Pair;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.*;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_NOTES;

/**
 * Client-side bodies of the S2C packet handlers.
 * They live apart from the handlers themselves so that a dedicated server never has to load
 * classes that reference client-only types when it registers the network channel.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientPacketProcessors {
    private static final Map<Pair<Player, Integer>, NoteSoundEntry> NOTE_SOUNDS = new HashMap<>();
    private static final Map<UUID, Runnable> NOTES_PART_CALLBACKS = new HashMap<>();

    private ClientPacketProcessors() {
    }

    public static void exportMusic(ExportMusicPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (CommandExport.doExport(player, msg.getName())) {
                player.sendSystemMessage(Component.translatable("xercamusic.export.success", msg.getName()).withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.translatable("xercamusic.export.fail").withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void importMusic(ImportMusicPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        String filename = msg.getName() + ".sheet";
        String filepath = "music_sheets/" + filename;
        try {
            CompoundTag tag = NbtIo.read(new File(filepath));
            if (tag == null) {
                throw new IOException("File not found!");
            }
            sendMusic(tag);
        } catch (IOException | ImportMusicSendPacket.NotesTooLargeException | NullPointerException e) {
            XercaMusic.LOGGER.error("Exception while reading music sheet: ", e);
            if (player != null) {
                player.sendSystemMessage(Component.translatable("xercamusic.import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }

    private static void sendMusic(CompoundTag tag) throws IOException, ImportMusicSendPacket.NotesTooLargeException {
        try {
            ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
            ClientStuff.sendToServer(pack);
        } catch (ImportMusicSendPacket.NotesTooLargeException e) {
            if (e.id == null) {
                throw new IOException("Music has many notes, but no UUID!");
            }
            int partsCount = (int) Math.ceil(e.notes.size() / (double) MAX_NOTES_IN_PACKET);
            tag.remove(KEY_NOTES);
            ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
            addNotesPartCallback(e.id, () -> ClientStuff.sendToServer(pack));
            for (int i = 0; i < partsCount; i++) {
                SendNotesPartToServerPacket partPack = new SendNotesPartToServerPacket(e.id, partsCount, i, e.notes.subList(i * MAX_NOTES_IN_PACKET, Math.min((i + 1) * MAX_NOTES_IN_PACKET, e.notes.size())));
                ClientStuff.sendToServer(partPack);
            }
        }
    }

    public static void addNotesPartCallback(UUID id, Runnable func) {
        NOTES_PART_CALLBACKS.put(id, func);
    }

    public static void notesPartAck(NotesPartAckFromServerPacket msg) {
        UUID id = msg.getMusicId();
        Runnable callback = NOTES_PART_CALLBACKS.remove(id);
        if (callback != null) {
            callback.run();
        }
    }

    public static void musicDataResponse(MusicDataResponsePacket msg) {
        UUID id = msg.getMusicId();
        int version = msg.getVersion();
        List<NoteEvent> notes = msg.getNotes();
        MusicManagerClient.setMusicData(id, version, notes, msg.getVolumeMarkers());
    }

    public static void musicBoxUpdate(MusicBoxUpdatePacket msg) {
        Level world = Minecraft.getInstance().level;
        if (world == null || !world.hasChunkAt(msg.getPos())) {
            return;
        }

        BlockEntity te = world.getBlockEntity(msg.getPos());
        if (te instanceof TileEntityMusicBox tileEntityMusicBox) {

            if (msg.getNoteStackNBT() != null) {
                if (msg.getNoteStackNBT().isEmpty()) {
                    tileEntityMusicBox.removeSheetStack();
                } else {
                    ItemStack noteStack = new ItemStack(Items.MUSIC_SHEET.get());
                    noteStack.setTag(msg.getNoteStackNBT());
                    tileEntityMusicBox.setSheetStack(noteStack, false);
                }
            }

            if (!msg.getInstrumentId().isEmpty()) {
                tileEntityMusicBox.setInstrument(BuiltInRegistries.ITEM.get(new ResourceLocation(msg.getInstrumentId())));
            } else {
                tileEntityMusicBox.removeInstrument();
            }
        }
    }

    public static void singleNote(SingleNoteClientPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            XercaMusic.LOGGER.warn("Level is null while trying to play a note packet");
            return;
        }

        Entity entity = level.getEntity(msg.getPlayerId());
        if (!(entity instanceof Player playerEntity)) {
            XercaMusic.LOGGER.warn("Invalid playerId in SingleNoteClientPacket: {}", msg.getPlayerId());
            return;
        }

        if (!playerEntity.equals(Minecraft.getInstance().player)) {
            IItemInstrument.InsSound sound = msg.getInstrumentItem().getSound(msg.getNote());
            if (sound == null) {
                return;
            }
            if (!msg.isStop()) {
                double x = playerEntity.getX();
                double y = playerEntity.getY();
                double z = playerEntity.getZ();

                NoteSound noteSound = ClientStuff.playNote(sound.sound(), x, y, z, SoundSource.PLAYERS, msg.getVolume() * 1.5f, sound.pitch(), (byte) -1);
                NOTE_SOUNDS.put(Pair.of(playerEntity, msg.getNote()), new NoteSoundEntry(noteSound, playerEntity));
                playerEntity.level().addParticle(ParticleTypes.NOTE, x, y + 2.2D, z, (msg.getNote()) / 24.0D, 0.0D, 0.0D);
            } else {
                NoteSoundEntry oldNoteSoundEntry = NOTE_SOUNDS.get(Pair.of(playerEntity, msg.getNote()));
                if (oldNoteSoundEntry != null && !oldNoteSoundEntry.noteSound.isStopped()) {
                    oldNoteSoundEntry.noteSound.stopSound();
                }
            }
        }
    }

    public static void tripleNote(TripleNoteClientPacket msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            XercaMusic.LOGGER.warn("Level is null while trying to play a triple note packet");
            return;
        }

        Entity entity = level.getEntity(msg.getEntityId());
        if (entity == null) {
            XercaMusic.LOGGER.warn("Invalid entityId in TripleNoteClientPacket: {}", msg.getEntityId());
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

        ClientStuff.playNote(sound1.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound1.pitch(), (byte) 10);
        ClientStuff.playNote(sound2.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound2.pitch(), (byte) 10);
        ClientStuff.playNote(sound3.sound(), x, y, z, SoundSource.PLAYERS, 1.5f, sound3.pitch(), (byte) 10);
    }

    private record NoteSoundEntry(NoteSound noteSound, Player playerEntity) {
    }
}
