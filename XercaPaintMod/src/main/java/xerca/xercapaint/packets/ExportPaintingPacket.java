package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public class ExportPaintingPacket {
    private String name;
    private boolean messageIsValid;

    public ExportPaintingPacket(String name) {
        this.name = name;
    }

    public ExportPaintingPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(name);
        return buf;
    }

    public static ExportPaintingPacket decode(FriendlyByteBuf buf) {
        ExportPaintingPacket result = new ExportPaintingPacket();
        try {
            result.name = buf.readUtf(64);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ExportPaintingPacket: " + ioe);
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
