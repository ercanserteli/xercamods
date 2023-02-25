package xerca.xercamusic.common.packets;

import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.XercaMusic;

import java.util.UUID;

public class NotesPartAckFromServerPacket {
    private UUID id;
    private boolean messageIsValid;

    public NotesPartAckFromServerPacket(UUID id) {
        this.id = id;
    }

    public NotesPartAckFromServerPacket() {
        this.messageIsValid = false;
    }

    public static void encode(NotesPartAckFromServerPacket pkt, PacketBuffer buf) {
        buf.writeUniqueId(pkt.getId());
    }

    public static NotesPartAckFromServerPacket decode(PacketBuffer buf) {
        NotesPartAckFromServerPacket result = new NotesPartAckFromServerPacket();
        try {
            result.id = buf.readUniqueId();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading NotesPartAckFromServerPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
