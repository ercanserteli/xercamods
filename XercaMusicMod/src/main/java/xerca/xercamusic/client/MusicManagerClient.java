package xerca.xercamusic.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.MusicDataRequestPacket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MusicManagerClient {
    static final Map<UUID, MusicManager.MusicData> musicMap = new HashMap<>();
    static final Map<UUID, Runnable> taskMap = new HashMap<>();
    static final String cacheDir = "music_sheets/.cache/";

    public static void load() {
        // Load from disk
        File directory = new File(cacheDir);
        if (!directory.exists()){
            directory.mkdirs();
        }
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                String fileName = file.getName();
                try {
                    UUID id = UUID.fromString(fileName);
                    CompoundTag tag = NbtIo.readCompressed(file);
                    if(tag.contains("id") && id.equals(tag.getUUID("id")) && tag.contains("ver") && tag.contains("notes")){
                        int version = tag.getInt("ver");
                        ArrayList<NoteEvent> notes = new ArrayList<>();
                        NoteEvent.fillArrayFromNBT(notes, tag);
                        musicMap.put(id, new MusicManager.MusicData(version, notes));
                    }
                    else {
                        file.delete();
                    }
                }
                catch (IllegalArgumentException | IOException e){
                    file.delete();
                }
            }
        }
    }

    public static void checkMusicDataAndRun(UUID id, int ver, Runnable task) {
        if(musicMap.containsKey(id)){
            MusicManager.MusicData data = musicMap.get(id);
            if(data.version >= ver){
                XercaMusic.LOGGER.debug("Music data found in client (id: {}, requested ver: {}) (checkMusicDataAndRun)", id, ver);
                task.run();
                return;
            }
            else{
                XercaMusic.LOGGER.debug("Music data in client is too old (id: {}, data ver: {}, requested ver: {}) (checkMusicDataAndRun)",
                        id, data.version, ver);
            }
        }
        XercaMusic.LOGGER.debug("Requesting music data from server (id: {}, requested ver: {}) (checkMusicDataAndRun)", id, ver);
        taskMap.put(id, task);
        // Request music data from server
        MusicDataRequestPacket packet = new MusicDataRequestPacket(id, ver);
        XercaMusic.NETWORK_HANDLER.sendToServer(packet);
    }

    public static MusicManager.MusicData getMusicData(UUID id, int ver) {
        if(musicMap.containsKey(id)){
            MusicManager.MusicData data = musicMap.get(id);
            if(data.version >= ver){
                XercaMusic.LOGGER.debug("Music data found in client (id: {}, requested ver: {}) (getMusicData)", id, ver);
                return data;
            }
            else{
                XercaMusic.LOGGER.debug("Music data in client is too old (id: {}, data ver: {}, requested ver: {}) (getMusicData)",
                        id, data.version, ver);
            }
        }
        XercaMusic.LOGGER.debug("Requesting music data from server (id: {}, requested ver: {}) (getMusicData)", id, ver);
        // Request music data from server
        MusicDataRequestPacket packet = new MusicDataRequestPacket(id, ver);
        XercaMusic.NETWORK_HANDLER.sendToServer(packet);
        return null;
    }

    public static void setMusicData(UUID id, int ver, ArrayList<NoteEvent> notes) {
        musicMap.put(id, new MusicManager.MusicData(ver, notes));

        // Save on disk
        String filename = id.toString();
        String filepath = cacheDir + "/" + filename;
        File directory = new File(cacheDir);
        if (!directory.exists()){
            directory.mkdirs();
        }

        CompoundTag tag = new CompoundTag();
        tag.putUUID("id", id);
        tag.putInt("ver", ver);
        NoteEvent.fillNBTFromArray(notes, tag);
        try {
            NbtIo.writeCompressed(tag, new File(filepath));
        } catch (IOException e) {
            XercaMusic.LOGGER.warn("Could not write music data to cache file: {}", filepath);
            e.printStackTrace();
        }

        if(taskMap.containsKey(id)){
            Runnable task = taskMap.get(id);
            taskMap.remove(id);
            task.run();
        }
    }
}
