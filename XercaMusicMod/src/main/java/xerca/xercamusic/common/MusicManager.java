package xerca.xercamusic.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class MusicManager {
    public static void load() {
        // Load from world data

    }

    public static MusicData getMusicData(UUID id, int ver, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.getWorld(World.OVERWORLD).getSavedData().getOrCreate(SavedDataMusic::new, "music_map");
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        if(musicMap.containsKey(id)){
            MusicData data = musicMap.get(id);
            if(data.version >= ver){
                return musicMap.get(id);
            }
        }
        return null;
    }

    public static void setMusicData(UUID id, int ver, ArrayList<NoteEvent> notes, MinecraftServer server) {
        SavedDataMusic savedDataMusic = server.getWorld(World.OVERWORLD).getSavedData().getOrCreate(SavedDataMusic::new, "music_map");
        Map<UUID, MusicData> musicMap = savedDataMusic.getMusicMap();
        musicMap.put(id, new MusicManager.MusicData(ver, notes));
        savedDataMusic.markDirty();
    }


    public static class MusicData {
        public MusicData(int version, ArrayList<NoteEvent> notes) {
            this.version = version;
            this.notes = notes;
        }

        public int version;
        public final ArrayList<NoteEvent> notes;
    }

    public static class SavedDataMusic extends WorldSavedData {
        private final Map<UUID, MusicData> musicMap;

        private SavedDataMusic(Map<UUID, MusicData> musicMap){
        	super("music_map");
            this.musicMap = musicMap;
        }

        public SavedDataMusic(){
            this(new HashMap<>());
        }

        @Override
        public void read(CompoundNBT tag) {
            INBT musicTag = tag.get("MusicDataList");
            if(musicTag instanceof ListNBT){
            	ListNBT musicDataList = (ListNBT) musicTag;
                Map<UUID, MusicData> musicDataMap = new HashMap<>();
                for(INBT nbt : musicDataList){
                    if(nbt instanceof CompoundNBT){
                    	CompoundNBT musicData = (CompoundNBT) nbt;
                        ArrayList<NoteEvent> notes = new ArrayList<>();
                        NoteEvent.fillArrayFromNBT(notes, musicData);
                        musicDataMap.put(musicData.getUniqueId("id"), new MusicData(musicData.getInt("ver"), notes));
                    }
                }
            }
        }

        @Override
        public CompoundNBT write(CompoundNBT tag) {
            ListNBT musicDataList = new ListNBT();
            for(Map.Entry<UUID, MusicData> entry : musicMap.entrySet()){
                CompoundNBT nbt = new CompoundNBT();
                nbt.putUniqueId("id", entry.getKey());
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
