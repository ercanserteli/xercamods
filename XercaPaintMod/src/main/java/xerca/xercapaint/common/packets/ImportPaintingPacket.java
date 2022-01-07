package xerca.xercapaint.common.packets;

import net.minecraft.network.PacketBuffer;

public class ImportPaintingPacket {
    private String name;
    private boolean messageIsValid;

    public ImportPaintingPacket(String name) {
        this.name = name;
    }

    public ImportPaintingPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportPaintingPacket pkt, PacketBuffer buf) {
        buf.writeUtf(pkt.name);
    }

    public static ImportPaintingPacket decode(PacketBuffer buf) {
        ImportPaintingPacket result = new ImportPaintingPacket();
        try {
            result.name = buf.readUtf(64);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportPaintingPacket: " + ioe);
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
