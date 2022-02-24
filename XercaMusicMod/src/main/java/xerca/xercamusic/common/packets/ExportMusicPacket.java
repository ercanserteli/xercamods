package xerca.xercamusic.common.packets;

import net.minecraft.network.PacketBuffer;

public class ExportMusicPacket {
    private String name;
    private boolean messageIsValid;

    public ExportMusicPacket(String name) {
        this.name = name;
    }

    public ExportMusicPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ExportMusicPacket pkt, PacketBuffer buf) {
        buf.writeString(pkt.name);
    }

    public static ExportMusicPacket decode(PacketBuffer buf) {
        ExportMusicPacket result = new ExportMusicPacket();
        try {
            result.name = buf.readString(64);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ExportMusicPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public String getName() {
        return name;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
