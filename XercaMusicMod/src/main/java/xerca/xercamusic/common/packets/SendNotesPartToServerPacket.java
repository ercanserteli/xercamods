package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SendNotesPartToServerPacket {
    private UUID uuid;
    private int partsCount;
    private int partId;
    private List<NoteEvent> notes;
    private boolean messageIsValid;

    public SendNotesPartToServerPacket(UUID uuid, int partsCount, int partId, List<NoteEvent> notes) {
        this.uuid = uuid;
        this.partsCount = partsCount;
        this.partId = partId;
        this.notes = notes;
    }

    public SendNotesPartToServerPacket() {
        this.messageIsValid = false;
    }

    public static void encode(SendNotesPartToServerPacket pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.uuid);
        buf.writeInt(pkt.partsCount);
        buf.writeInt(pkt.partId);
        buf.writeInt(pkt.notes.size());
        for(NoteEvent event : pkt.notes){
            event.encodeToBuffer(buf);
        }
    }

    public static SendNotesPartToServerPacket decode(FriendlyByteBuf buf) {
        SendNotesPartToServerPacket result = new SendNotesPartToServerPacket();
        try {
            result.uuid = buf.readUUID();
            result.partsCount = buf.readInt();
            result.partId = buf.readInt();
            int eventCount = buf.readInt();
            if(eventCount > 0) {
                result.notes = new ArrayList<>(eventCount);
                for (int i = 0; i < eventCount; i++) {
                    result.notes.add(NoteEvent.fromBuffer(buf));
                }
            }
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading SendNotesPartToServerPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public List<NoteEvent> getNotes() {
        return notes;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

    public int getPartsCount() {
        return partsCount;
    }

    public void setPartsCount(int partsCount) {
        this.partsCount = partsCount;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
