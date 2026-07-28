package xerca.xercamusic.common.packets.clientbound;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

import java.util.UUID;

public class NotesPartAckFromServerPacket implements IPacket {
    private UUID musicId;
    private boolean messageIsValid;

    public NotesPartAckFromServerPacket(UUID musicId) {
        this.musicId = musicId;
    }

    public NotesPartAckFromServerPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUUID(musicId);
        return buf;
    }

    public static NotesPartAckFromServerPacket decode(FriendlyByteBuf buf) {
        NotesPartAckFromServerPacket result = new NotesPartAckFromServerPacket();
        try {
            result.musicId = buf.readUUID();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading NotesPartAckFromServerPacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public UUID getMusicId() {
        return musicId;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }}

