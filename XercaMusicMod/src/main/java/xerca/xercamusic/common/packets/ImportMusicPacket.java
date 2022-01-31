package xerca.xercamusic.common.packets;

import net.minecraft.network.PacketBuffer;

public class ImportMusicPacket {
    private String name;
    private boolean messageIsValid;

    public ImportMusicPacket(String name) {
        this.name = name;
    }

    public ImportMusicPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportMusicPacket pkt, PacketBuffer buf) {
        buf.writeString(pkt.name);
    }

    public static ImportMusicPacket decode(PacketBuffer buf) {
        ImportMusicPacket result = new ImportMusicPacket();
        try {
            result.name = buf.readString(64);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicPacket: " + ioe);
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
