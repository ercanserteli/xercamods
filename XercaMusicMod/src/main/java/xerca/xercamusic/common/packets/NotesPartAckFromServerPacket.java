package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
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

    public static void encode(NotesPartAckFromServerPacket pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.getId());
    }

    public static NotesPartAckFromServerPacket decode(FriendlyByteBuf buf) {
        NotesPartAckFromServerPacket result = new NotesPartAckFromServerPacket();
        try {
            result.id = buf.readUUID();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading NotesPartAckFromServerPacket: {}", ioe.toString());
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
