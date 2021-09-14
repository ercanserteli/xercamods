package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;

public class ExportMusicPacket {
    private String name;
    private boolean messageIsValid;

    public ExportMusicPacket(String name) {
        this.name = name;
    }

    public ExportMusicPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ExportMusicPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.name);
    }

    public static ExportMusicPacket decode(FriendlyByteBuf buf) {
        ExportMusicPacket result = new ExportMusicPacket();
        try {
            result.name = buf.readUtf(64);
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
