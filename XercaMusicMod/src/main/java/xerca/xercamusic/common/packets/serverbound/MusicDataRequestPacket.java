package xerca.xercamusic.common.packets.serverbound;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

import java.util.UUID;

public class MusicDataRequestPacket implements IPacket {
    private UUID musicId;
    private int version;
    private boolean messageIsValid;

    public MusicDataRequestPacket(UUID musicId, int version) {
        this.musicId = musicId;
        this.version = version;
    }

    public MusicDataRequestPacket() {
        this.messageIsValid = false;
    }

    public static MusicDataRequestPacket decode(FriendlyByteBuf buf) {
        MusicDataRequestPacket result = new MusicDataRequestPacket();
        try {
            result.musicId = buf.readUUID();
            result.version = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicDataRequestPacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUUID(getMusicId());
        buf.writeInt(getVersion());
        return buf;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }


    public UUID getMusicId() {
        return musicId;
    }

    @SuppressWarnings("unused")
    public void setMusicId(UUID musicId) {
        this.musicId = musicId;
    }

    public int getVersion() {
        return version;
    }

    @SuppressWarnings("unused")
    public void setVersion(int version) {
        this.version = version;
    }}
