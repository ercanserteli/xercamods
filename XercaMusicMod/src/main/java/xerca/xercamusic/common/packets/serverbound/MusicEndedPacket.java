package xerca.xercamusic.common.packets.serverbound;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

public class MusicEndedPacket implements IPacket {
    private int playerId;
    private boolean messageIsValid;

    public MusicEndedPacket(int playerId) {
        this.playerId = playerId;
    }

    public MusicEndedPacket() {
        this.messageIsValid = false;
    }

    public static MusicEndedPacket decode(FriendlyByteBuf buf) {
        MusicEndedPacket result = new MusicEndedPacket();
        try {
            result.playerId = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicEndedPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(playerId);
        return buf;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }
}
