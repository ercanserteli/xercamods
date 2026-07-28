package xerca.xercamusic.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;

import java.util.*;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_ID;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_VERSION;

public final class MusicManager {
    // Guard against unbounded multipart note uploads while still allowing large imports.
    public static final int MAX_PARTS_IN_TRANSFER = 1024;
    private static final Map<UUID, TempNotesBuffer> TEMP_NOTES_MAP = new HashMap<>();

    public static @Nullable MusicData getMusicData(UUID id, int ver, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.overworld().getDataStorage().computeIfAbsent(SavedDataMusic::load, SavedDataMusic::new, "music_map");
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        if (musicMap.containsKey(id)) {
            MusicData data = musicMap.get(id);
            if (data.version >= ver) {
                XercaMusic.LOGGER.debug("Music data found in server (id: {}, ver: {}) (getMusicData)", id, ver);
                return data;
            } else {
                XercaMusic.LOGGER.debug("Music data in server is too old (id: {}, data ver: {}, requested ver: {}) (getMusicData)", id, data.version, ver);
            }
        } else {
            XercaMusic.LOGGER.debug("Music data not found in server (id: {}, requested ver: {}) (getMusicData)", id, ver);
        }
        return null;
    }

    public static void setMusicData(UUID id, int ver, List<NoteEvent> notes, @Nullable List<VolumeMarker> volumeMarkers, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.overworld().getDataStorage().computeIfAbsent(SavedDataMusic::load, SavedDataMusic::new, "music_map");
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        NoteEvent.sortNotes(notes);
        NoteEvent.removeDuplicates(notes);
        musicMap.put(id, new MusicManager.MusicData(ver, notes, volumeMarkers));
        savedDataMusic.setDirty();
    }

    public static void setMusicData(UUID id, int ver, List<NoteEvent> notes, MinecraftServer server) {
        setMusicData(id, ver, notes, null, server);
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
                XercaMusic.LOGGER.warn("Packet did not have notes, and temp buffer was not finished");
            }
        } else {
            XercaMusic.LOGGER.warn("Packet did not have notes, and temp buffer was not found");
        }
        return List.of();
    }

    public static boolean addNotesPart(SendNotesPartToServerPacket pkt) {
        if (pkt.getPartsCount() <= 0 || pkt.getPartsCount() > MAX_PARTS_IN_TRANSFER) {
            XercaMusic.LOGGER.warn("Invalid notes part count: {}", pkt.getPartsCount());
            return false;
        }
        if (pkt.getPartId() < 0 || pkt.getPartId() >= pkt.getPartsCount()) {
            XercaMusic.LOGGER.warn("Invalid notes part id: {} for parts count {}", pkt.getPartId(), pkt.getPartsCount());
            return false;
        }
        TempNotesBuffer buffer;
        if (TEMP_NOTES_MAP.containsKey(pkt.getUuid())) {
            buffer = TEMP_NOTES_MAP.get(pkt.getUuid());
            if (buffer.partsCount != pkt.getPartsCount()) {
                XercaMusic.LOGGER.warn("Mismatching part count for id {}. Expected {}, got {}. Resetting temp buffer.",
                        pkt.getUuid(), buffer.partsCount, pkt.getPartsCount());
                TEMP_NOTES_MAP.remove(pkt.getUuid());
                buffer = null;
            }
        } else {
            buffer = null;
        }

        if (buffer != null) {
            buffer.addPart(pkt.getPartId(), pkt.getNotes());
        } else {
            buffer = new TempNotesBuffer(pkt.getPartsCount());
            buffer.addPart(pkt.getPartId(), pkt.getNotes());
            TEMP_NOTES_MAP.put(pkt.getUuid(), buffer);
        }
        return buffer.isFinished();
    }

    public record MusicData(int version, List<NoteEvent> notes, @Nullable List<VolumeMarker> volumeMarkers) {
    }

    public static class SavedDataMusic extends SavedData {
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
                        musicDataMap.put(musicData.getUUID(KEY_ID), new MusicData(musicData.getInt(KEY_VERSION), notes, markers.isEmpty() ? null : markers));
                    }
                }

                return new SavedDataMusic(musicDataMap);
            } else {
                return new SavedDataMusic();
            }
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            ListTag musicDataList = new ListTag();
            for (Map.Entry<UUID, MusicData> entry : musicMap.entrySet()) {
                CompoundTag nbt = new CompoundTag();
                nbt.putUUID(KEY_ID, entry.getKey());
                nbt.putInt(KEY_VERSION, entry.getValue().version);
                NoteEvent.fillNBTFromArray(entry.getValue().notes, nbt);
                if (entry.getValue().volumeMarkers != null) {
                    VolumeMarker.fillNBTFromArray(entry.getValue().volumeMarkers(), nbt);
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
