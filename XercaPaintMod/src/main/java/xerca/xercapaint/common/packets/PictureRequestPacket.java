package xerca.xercapaint.common.packets;

import net.minecraft.network.PacketBuffer;

public class PictureRequestPacket {
    private String name;
    private boolean messageIsValid;

    public PictureRequestPacket(String name) {
        this.name = name;
    }

    public PictureRequestPacket() {
        this.messageIsValid = false;
    }

    public static void encode(PictureRequestPacket pkt, PacketBuffer buf) {
        buf.writeUtf(pkt.name);
    }

    public static PictureRequestPacket decode(PacketBuffer buf) {
        PictureRequestPacket result = new PictureRequestPacket();
        try {
            result.name = buf.readUtf(64);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading PictureRequestPacket: " + ioe);
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
