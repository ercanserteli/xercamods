package xerca.xercamusic.common.packets;

import net.minecraft.network.PacketBuffer;

public class MusicEndedPacket {
    private int playerId;
    private boolean messageIsValid;

    public MusicEndedPacket(int playerId) {
        this.playerId = playerId;
    }

    public MusicEndedPacket() {
        this.messageIsValid = false;
    }

    public static MusicEndedPacket decode(PacketBuffer buf) {
        MusicEndedPacket result = new MusicEndedPacket();
        try {
            result.playerId = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicEndedPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicEndedPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.playerId);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

}
