package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

import java.util.ArrayList;
import java.util.UUID;

public class MusicDataResponsePacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "music_data_response");
    private UUID id;
    private int version;
    private ArrayList<NoteEvent> notes;
    private boolean messageIsValid;

    public MusicDataResponsePacket(UUID id, int version, ArrayList<NoteEvent> notes) {
        this.id = id;
        this.version = version;
        this.notes = notes;
    }

    public MusicDataResponsePacket() {
        this.messageIsValid = false;
    }

    public static MusicDataResponsePacket decode(FriendlyByteBuf buf) {
        MusicDataResponsePacket result = new MusicDataResponsePacket();
        try {
            result.id = buf.readUUID();
            result.version = buf.readInt();
            int eventCount = buf.readInt();
            result.notes = new ArrayList<>(eventCount);
            for(int i=0; i<eventCount; i++){
                result.notes.add(NoteEvent.fromBuffer(buf));
            }
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicDataRequestPacket:", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(getId());
        buf.writeInt(getVersion());
        buf.writeInt(notes.size());
        for(NoteEvent event : notes){
            event.encodeToBuffer(buf);
        }
        return buf;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }


    public UUID getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(UUID id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    @SuppressWarnings("unused")
    public void setVersion(int version) {
        this.version = version;
    }

    public ArrayList<NoteEvent> getNotes() {
        return notes;
    }

    @SuppressWarnings("unused")
    public void setNotes(ArrayList<NoteEvent> notes) {
        this.notes = notes;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
