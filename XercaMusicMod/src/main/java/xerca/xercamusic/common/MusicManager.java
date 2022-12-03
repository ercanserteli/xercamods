package xerca.xercamusic.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MusicManager {
    public static MusicData getMusicData(UUID id, int ver, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.overworld().getDataStorage().computeIfAbsent(SavedDataMusic::load, SavedDataMusic::new, "music_map");
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        if(musicMap.containsKey(id)){
            MusicData data = musicMap.get(id);
            if(data.version >= ver){
                XercaMusic.LOGGER.debug("Music data found in server (id: {}, ver: {}) (getMusicData)", id, ver);
                return data;
            }
            else{
                XercaMusic.LOGGER.debug("Music data in server is too old (id: {}, data ver: {}, requested ver: {}) (getMusicData)", id, data.version, ver);
            }
        }
        else{
            XercaMusic.LOGGER.debug("Music data not found in server (id: {}, requested ver: {}) (getMusicData)", id, ver);
        }
        return null;
    }

    public static void setMusicData(UUID id, int ver, ArrayList<NoteEvent> notes, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.overworld().getDataStorage().computeIfAbsent(SavedDataMusic::load, SavedDataMusic::new, "music_map");
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        musicMap.put(id, new MusicData(ver, notes));
        savedDataMusic.setDirty();
    }


    public static class MusicData {
        public MusicData(int version, ArrayList<NoteEvent> notes) {
            this.version = version;
            this.notes = notes;
        }

        public final int version;
        public final ArrayList<NoteEvent> notes;
    }

    public static class SavedDataMusic extends SavedData {
        private final Map<UUID, MusicData> musicMap;

        private SavedDataMusic(Map<UUID, MusicData> musicMap){
            this.musicMap = musicMap;
        }

        public SavedDataMusic(){
            this(new HashMap<>());
        }

        public static SavedDataMusic load(CompoundTag tag) {
            Tag musicTag = tag.get("MusicDataList");
            if(musicTag instanceof ListTag musicDataList){
                Map<UUID, MusicData> musicDataMap = new HashMap<>();
                for(Tag nbt : musicDataList){
                    if(nbt instanceof CompoundTag musicData){
                        ArrayList<NoteEvent> notes = new ArrayList<>();
                        NoteEvent.fillArrayFromNBT(notes, musicData);
                        musicDataMap.put(musicData.getUUID("id"), new MusicData(musicData.getInt("ver"), notes));
                    }
                }

                return new SavedDataMusic(musicDataMap);
            }
            else{
                return new SavedDataMusic();
            }
        }

        @Override
        public CompoundTag save(@NotNull CompoundTag tag) {
            ListTag musicDataList = new ListTag();
            for(Map.Entry<UUID, MusicData> entry : musicMap.entrySet()){
                CompoundTag nbt = new CompoundTag();
                nbt.putUUID("id", entry.getKey());
                nbt.putInt("ver", entry.getValue().version);
                NoteEvent.fillNBTFromArray(entry.getValue().notes, nbt);
                musicDataList.add(nbt);
            }
            tag.put("MusicDataList", musicDataList);
            return tag;
        }

        public Map<UUID, MusicData> getMusicMap(){
            return musicMap;
        }
    }

}
