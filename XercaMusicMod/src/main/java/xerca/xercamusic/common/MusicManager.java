package xerca.xercamusic.common;

import com.mojang.serialization.Codec;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jspecify.annotations.Nullable;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_ID;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_VERSION;

public final class MusicManager {
    // Guard against unbounded multipart note uploads while still allowing large imports.
    private static final int MAX_PARTS_IN_TRANSFER = 1024;
    private static final Map<UUID, TempNotesBuffer> TEMP_NOTES_MAP = new HashMap<>();

    public static @Nullable MusicData getMusicData(UUID id, int ver, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.getDataStorage().computeIfAbsent(SavedDataMusic.TYPE);
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        if (musicMap.containsKey(id)) {
            MusicData data = musicMap.get(id);
            if (data.version >= ver) {
                Mod.LOGGER.debug("Music data found in server (id: {}, ver: {}) (getMusicData)", id, ver);
                return data;
            } else {
                Mod.LOGGER.debug("Music data in server is too old (id: {}, data ver: {}, requested ver: {}) (getMusicData)", id, data.version, ver);
            }
        } else {
            Mod.LOGGER.debug("Music data not found in server (id: {}, requested ver: {}) (getMusicData)", id, ver);
        }
        return null;
    }

    public static void setMusicData(UUID id, int ver, List<NoteEvent> notes, @Nullable List<VolumeMarker> volumeMarkers, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.getDataStorage().computeIfAbsent(SavedDataMusic.TYPE);
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        NoteEvent.sortNotes(notes);
        NoteEvent.removeDuplicates(notes);
        musicMap.put(id, new MusicManager.MusicData(ver, notes, volumeMarkers));
        savedDataMusic.setDirty();
    }

    public static List<NoteEvent> getFinishedNotesFromBuffer(UUID id) {
        if (TEMP_NOTES_MAP.containsKey(id)) {
            MusicManager.TempNotesBuffer buffer = TEMP_NOTES_MAP.get(id);
            if (buffer.isFinished()) {
                try {
                    return buffer.joinParts();
                } finally {
                    TEMP_NOTES_MAP.remove(id);
                }
            } else {
                Mod.LOGGER.warn("Packet did not have notes, and temp buffer was not finished");
            }
        } else {
            Mod.LOGGER.warn("Packet did not have notes, and temp buffer was not found");
        }
        return List.of();
    }

    public static boolean addNotesPart(SendNotesPartToServerPacket pkt) {
        if (pkt.partsCount() <= 0 || pkt.partsCount() > MAX_PARTS_IN_TRANSFER) {
            Mod.LOGGER.warn("Invalid notes part count: {}", pkt.partsCount());
            return false;
        }
        if (pkt.partId() < 0 || pkt.partId() >= pkt.partsCount()) {
            Mod.LOGGER.warn("Invalid notes part id: {} for parts count {}", pkt.partId(), pkt.partsCount());
            return false;
        }
        TempNotesBuffer buffer;
        if (TEMP_NOTES_MAP.containsKey(pkt.uuid())) {
            buffer = TEMP_NOTES_MAP.get(pkt.uuid());
            if (buffer.partsCount != pkt.partsCount()) {
                Mod.LOGGER.warn("Mismatching part count for id {}. Expected {}, got {}. Resetting temp buffer.",
                        pkt.uuid(), buffer.partsCount, pkt.partsCount());
                TEMP_NOTES_MAP.remove(pkt.uuid());
                buffer = null;
            }
        } else {
            buffer = null;
        }

        if (buffer != null) {
            buffer.addPart(pkt.partId(), pkt.notes());
        } else {
            buffer = new TempNotesBuffer(pkt.partsCount());
            buffer.addPart(pkt.partId(), pkt.notes());
            TEMP_NOTES_MAP.put(pkt.uuid(), buffer);
        }
        return buffer.isFinished();
    }

    public static void migrateLegacyData(MinecraftServer server) {
        migrateLegacyFile(server.getDataStorage(), server.registryAccess(),
                server.getWorldPath(LevelResource.DATA).resolve("music_map.dat"));
    }

    // Worlds from before 26.x stored this data as world/data/music_map.dat instead of the namespaced file
    public static void migrateLegacyFile(SavedDataStorage storage, HolderLookup.Provider registries, Path legacyFile) {
        if (!Files.exists(legacyFile) || storage.get(SavedDataMusic.TYPE) != null) {
            return;
        }
        try {
            CompoundTag root = storage.readTagFromDisk(legacyFile, SavedDataMusic.TYPE.dataFixType(),
                    SharedConstants.getCurrentVersion().dataVersion().version());
            SavedDataMusic.TYPE.codec().parse(registries.createSerializationContext(NbtOps.INSTANCE), root.get("data"))
                    .resultOrPartial(error -> Mod.LOGGER.error("Failed to migrate legacy music data: {}", error))
                    .ifPresent(data -> {
                        data.setDirty();
                        storage.set(SavedDataMusic.TYPE, data);
                        Mod.LOGGER.info("Migrated legacy music data from {}", legacyFile);
                    });
        } catch (IOException e) {
            Mod.LOGGER.error("Failed to read legacy music data file {}", legacyFile, e);
        }
    }

    public record MusicData(int version, List<NoteEvent> notes, @Nullable List<VolumeMarker> volumeMarkers) {
    }

    public static class SavedDataMusic extends SavedData {
        public static final Codec<SavedDataMusic> CODEC = CompoundTag.CODEC.xmap(
                SavedDataMusic::load, data -> data.save(new CompoundTag()));
        public static final SavedDataType<SavedDataMusic> TYPE =
                new SavedDataType<>(Identifier.fromNamespaceAndPath("xercamusic", "music_map"), SavedDataMusic::new, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);

        private final Map<UUID, MusicData> musicMap;

        private SavedDataMusic(Map<UUID, MusicData> musicMap) {
            this.musicMap = musicMap;
        }

        public SavedDataMusic() {
            this(new HashMap<>());
        }

        public static SavedDataMusic load(CompoundTag tag) {
            Tag musicTag = tag.get("MusicDataList");
            if (musicTag instanceof ListTag musicDataList) {
                Map<UUID, MusicData> musicDataMap = new HashMap<>();
                for (Tag nbt : musicDataList) {
                    if (nbt instanceof CompoundTag musicData) {
                        List<NoteEvent> notes = new ArrayList<>();
                        NoteEvent.fillArrayFromNBT(notes, musicData);
                        ArrayList<VolumeMarker> markers = new ArrayList<>();
                        VolumeMarker.fillArrayFromNBT(markers, musicData);
                        musicData.read(KEY_ID, UUIDUtil.CODEC).ifPresent(id ->
                                musicDataMap.put(id, new MusicData(musicData.getIntOr(KEY_VERSION, 0), notes, markers.isEmpty() ? null : markers)));
                    }
                }

                return new SavedDataMusic(musicDataMap);
            } else {
                return new SavedDataMusic();
            }
        }

        public CompoundTag save(CompoundTag tag) {
            ListTag musicDataList = new ListTag();
            for (Map.Entry<UUID, MusicData> entry : musicMap.entrySet()) {
                CompoundTag nbt = new CompoundTag();
                nbt.store(KEY_ID, UUIDUtil.CODEC, entry.getKey());
                nbt.putInt(KEY_VERSION, entry.getValue().version);
                NoteEvent.fillNBTFromArray(entry.getValue().notes, nbt);
                var volumeMarkers = entry.getValue().volumeMarkers();
                if (volumeMarkers != null) {
                    VolumeMarker.fillNBTFromArray(volumeMarkers, nbt);
                }
                musicDataList.add(nbt);
            }
            tag.put("MusicDataList", musicDataList);
            return tag;
        }

        public Map<UUID, MusicData> getMusicMap() {
            return musicMap;
        }
    }

    public static class TempNotesBuffer {
        final int partsCount;
        final boolean[] finishedParts;
        final List<List<NoteEvent>> notesParts;

        public TempNotesBuffer(int partsCount) {
            this.partsCount = partsCount;
            finishedParts = new boolean[partsCount];
            notesParts = new ArrayList<>(partsCount);
            for (int i = 0; i < partsCount; i++) {
                notesParts.add(new ArrayList<>());
            }
        }

        public void addPart(int partId, List<NoteEvent> part) {
            if (partId < partsCount && partId >= 0) {
                notesParts.set(partId, part);
                finishedParts[partId] = true;
            }
        }

        public boolean isFinished() {
            boolean result = true;
            for (boolean f : finishedParts) {
                result &= f;
            }
            return result;
        }

        public List<NoteEvent> joinParts() {
            List<NoteEvent> notes = new ArrayList<>(partsCount * MAX_NOTES_IN_PACKET);
            for (List<NoteEvent> notesPart : notesParts) {
                notes.addAll(notesPart);
            }
            return notes;
        }
    }
}
