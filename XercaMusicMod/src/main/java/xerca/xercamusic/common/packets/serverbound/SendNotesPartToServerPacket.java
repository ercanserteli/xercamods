package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.packets.IPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.MusicManager.MAX_PARTS_IN_TRANSFER;

public class SendNotesPartToServerPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(Mod.MODID, "send_notes_part_to_server");
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
            if (result.partsCount <= 0 || result.partsCount > MAX_PARTS_IN_TRANSFER) {
                throw new IndexOutOfBoundsException("Invalid partsCount: " + result.partsCount);
            }
            if (result.partId < 0 || result.partId >= result.partsCount) {
                throw new IndexOutOfBoundsException("Invalid partId: " + result.partId);
            }
            int eventCount = buf.readInt();
            if (eventCount < 0 || eventCount > MAX_NOTES_IN_PACKET) {
                throw new IndexOutOfBoundsException("Invalid eventCount: " + eventCount);
            }
            result.notes = new ArrayList<>(eventCount);
            for (int i = 0; i < eventCount; i++) {
                result.notes.add(NoteEvent.fromBuffer(buf));
            }
        } catch (RuntimeException ioe) {
            Mod.LOGGER.error("Exception while reading SendNotesPartToServerPacket: " + ioe);
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
        for (NoteEvent event : notes) {
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

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
