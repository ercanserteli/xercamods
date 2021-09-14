package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;

public class ImportMusicPacket {
    private String name;
    private boolean messageIsValid;

    public ImportMusicPacket(String name) {
        this.name = name;
    }

    public ImportMusicPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportMusicPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.name);
    }

    public static ImportMusicPacket decode(FriendlyByteBuf buf) {
        ImportMusicPacket result = new ImportMusicPacket();
        try {
            result.name = buf.readUtf(64);
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
