package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;

import java.util.UUID;

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

    public static MusicDataRequestPacket decode(FriendlyByteBuf buf) {
        MusicDataRequestPacket result = new MusicDataRequestPacket();
        try {
            result.id = buf.readUUID();
            result.version = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicDataRequestPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicDataRequestPacket pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.getId());
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
