package xerca.xercamusic.common;

import java.util.*;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import xerca.xercamusic.common.packets.SendNotesPartToServerPacket;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class MusicManager {
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

    public static ArrayList<NoteEvent> getFinishedNotesFromBuffer(UUID id) {
        if(MusicManager.TEMP_NOTES_MAP.containsKey(id)){
            MusicManager.TempNotesBuffer buffer = MusicManager.TEMP_NOTES_MAP.get(id);
            if(buffer.isFinished()){
                return buffer.joinParts();
            }
            else{
                XercaMusic.LOGGER.warn("Packet did not have notes, and temp buffer was not finished");
            }
        }
        else{
            XercaMusic.LOGGER.warn("Packet did not have notes, and temp buffer was not found");
        }
        return null;
    }

    public static boolean addNotesPart(SendNotesPartToServerPacket pkt) {
        TempNotesBuffer buffer;
        if(TEMP_NOTES_MAP.containsKey(pkt.getUuid())){
            buffer = TEMP_NOTES_MAP.get(pkt.getUuid());
            buffer.addPart(pkt.getPartId(), pkt.getNotes());
        }
        else{
            buffer = new TempNotesBuffer(pkt.getPartsCount());
            buffer.addPart(pkt.getPartId(), pkt.getNotes());
            TEMP_NOTES_MAP.put(pkt.getUuid(), buffer);
        }
        return buffer.isFinished();
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

                for(INBT nbt : musicDataList){
                    if(nbt instanceof CompoundNBT){
                    	CompoundNBT musicData = (CompoundNBT) nbt;
                        ArrayList<NoteEvent> notes = new ArrayList<>();
                        NoteEvent.fillArrayFromNBT(notes, musicData);
                        musicMap.put(musicData.getUniqueId("id"), new MusicData(musicData.getInt("ver"), notes));
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
    public static class TempNotesBuffer {
        int partsCount;
        boolean[] finishedParts;
        List<NoteEvent>[] notesParts;

        public TempNotesBuffer(int partsCount) {
            this.partsCount = partsCount;
            finishedParts = new boolean[partsCount];
            notesParts = new ArrayList[partsCount];
        }

        public void addPart(int partId, List<NoteEvent> part) {
            if(partId < partsCount && partId >= 0){
                notesParts[partId] = part;
                finishedParts[partId] = true;
            }
        }

        public boolean isFinished() {
            boolean result = true;
            for(boolean f : finishedParts) {
                result &= f;
            }
            return result;
        }

        public ArrayList<NoteEvent> joinParts() {
            ArrayList<NoteEvent> notes = new ArrayList<>(partsCount*MAX_NOTES_IN_PACKET);
            for(List<NoteEvent> notesPart : notesParts) {
                notes.addAll(notesPart);
            }
            return notes;
        }
    }
    public static Map<UUID, TempNotesBuffer> TEMP_NOTES_MAP = new HashMap<>();
}
