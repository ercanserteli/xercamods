package xerca.xercamusic.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.packets.serverbound.MusicDataRequestPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static xerca.xercamusic.client.ClientStuff.sendToServer;
import static xerca.xercamusic.common.item.ItemMusicSheet.*;

public final class MusicManagerClient {
    static final Map<UUID, MusicManager.MusicData> MUSIC_MAP = new HashMap<>();
    static final Map<UUID, Runnable> TASK_MAP = new HashMap<>();
    static final String CACHE_DIR = "music_sheets/.cache/";

    private MusicManagerClient() {
    }

    private static boolean ensureDirectoryExists(File directory, String purpose) {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                Mod.LOGGER.warn("{} path exists but is not a directory: {}", purpose, directory.getAbsolutePath());
                return false;
            }
            return true;
        }
        if (!directory.mkdirs()) {
            Mod.LOGGER.warn("Could not create {} directory: {}", purpose, directory.getAbsolutePath());
            return false;
        }
        return true;
    }

    public static void load() {
        // Load from disk
        File directory = new File(CACHE_DIR);
        if (!ensureDirectoryExists(directory, "music cache")) {
            return;
        }
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                String fileName = file.getName();
                try {
                    UUID id = UUID.fromString(fileName);
                    CompoundTag tag = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
                    if (tag.contains(KEY_ID) && id.equals(tag.getUUID(KEY_ID)) && tag.contains(KEY_VERSION) && tag.contains(KEY_NOTES)) {
                        int version = tag.getInt(KEY_VERSION);
                        ArrayList<NoteEvent> notes = new ArrayList<>();
                        NoteEvent.fillArrayFromNBT(notes, tag);
                        ArrayList<VolumeMarker> markers = new ArrayList<>();
                        VolumeMarker.fillArrayFromNBT(markers, tag);
                        MUSIC_MAP.put(id, new MusicManager.MusicData(version, notes, markers.isEmpty() ? null : markers));
                    } else {
                        if (!file.delete()) {
                            Mod.LOGGER.warn("Could not delete invalid music sheet file: {}", file::getAbsolutePath);
                        }
                    }
                } catch (IllegalArgumentException | IOException e) {
                    if (!file.delete()) {
                        Mod.LOGGER.warn("Could not delete music sheet file on exception {}: {}", e, file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static void checkMusicDataAndRun(UUID id, int ver, Runnable task) {
        if (MUSIC_MAP.containsKey(id)) {
            MusicManager.MusicData data = MUSIC_MAP.get(id);
            int dataVer = data.version();
            if (dataVer >= ver) {
                Mod.LOGGER.debug("Music data found in client (id: {}, requested ver: {}) (checkMusicDataAndRun)", id, ver);
                task.run();
                return;
            } else {
                Mod.LOGGER.info("Music data in client is too old (id: {}, data ver: {}, requested ver: {}) (checkMusicDataAndRun)",
                        id, dataVer, ver);
            }
        }
        Mod.LOGGER.info("Requesting music data from server (id: {}, requested ver: {}) (checkMusicDataAndRun)", id, ver);
        TASK_MAP.put(id, task);
        // Request music data from server
        MusicDataRequestPacket packet = new MusicDataRequestPacket(id, ver);
        sendToServer(packet);
    }

    public static MusicManager.MusicData getMusicData(UUID id, int ver) {
        if (MUSIC_MAP.containsKey(id)) {
            MusicManager.MusicData data = MUSIC_MAP.get(id);
            int dataVer = data.version();
            if (dataVer >= ver) {
                Mod.LOGGER.debug("Music data found in client (id: {}, requested ver: {}) (getMusicData)", id, ver);
                return data;
            } else {
                Mod.LOGGER.info("Music data in client is too old (id: {}, data ver: {}, requested ver: {}) (getMusicData)",
                        id, dataVer, ver);
            }
        }
        Mod.LOGGER.info("Requesting music data from server (id: {}, requested ver: {}) (getMusicData)", id, ver);
        // Request music data from server
        MusicDataRequestPacket packet = new MusicDataRequestPacket(id, ver);
        sendToServer(packet);
        return null;
    }

    public static void setMusicData(UUID id, int ver, List<NoteEvent> notes, List<VolumeMarker> volumeMarkers) {
        MUSIC_MAP.put(id, new MusicManager.MusicData(ver, notes, volumeMarkers));

        // Save on disk
        String filename = id.toString();
        String filepath = CACHE_DIR + "/" + filename;
        File directory = new File(CACHE_DIR);
        if (!ensureDirectoryExists(directory, "music cache")) {
            return;
        }

        CompoundTag tag = new CompoundTag();
        tag.putUUID(KEY_ID, id);
        tag.putInt(KEY_VERSION, ver);
        NoteEvent.fillNBTFromArray(notes, tag);
        if (volumeMarkers != null) {
            VolumeMarker.fillNBTFromArray(volumeMarkers, tag);
        }
        try {
            NbtIo.writeCompressed(tag, Path.of(filepath));
        } catch (IOException e) {
            Mod.LOGGER.warn("Could not write music data to cache file {}: {}", filepath, e);
        }

        if (TASK_MAP.containsKey(id)) {
            Runnable task = TASK_MAP.get(id);
            TASK_MAP.remove(id);
            task.run();
        }
    }
}
