package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SendNotesPartToServerPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "send_notes_part_to_server");
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

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(uuid);
        buf.writeInt(partsCount);
        buf.writeInt(partId);
        buf.writeInt(notes.size());
        for(NoteEvent event : notes){
            event.encodeToBuffer(buf);
        }
        return buf;
    }

    public List<NoteEvent> getNotes() {
        return notes;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }

    public int getPartsCount() {
        return partsCount;
    }

    public int getPartId() {
        return partId;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}

