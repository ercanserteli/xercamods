package xerca.xercamusic.common.packets;

import java.util.UUID;

import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.XercaMusic;

public class MusicDataRequestPacket {
    private UUID id;
    private int version;
    private boolean messageIsValid;

    public MusicDataRequestPacket(UUID id, int version) {
        this.id = id;
        this.version = version;
    }

    public MusicDataRequestPacket() {
        this.messageIsValid = false;
    }

    public static MusicDataRequestPacket decode(PacketBuffer buf) {
        MusicDataRequestPacket result = new MusicDataRequestPacket();
        try {
            result.id = buf.readUniqueId();
            result.version = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicDataRequestPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicDataRequestPacket pkt, PacketBuffer buf) {
        buf.writeUniqueId(pkt.getId());
        buf.writeInt(pkt.getVersion());
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
