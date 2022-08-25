package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public class ImportPaintingPacket {
    private String name;
    private boolean messageIsValid;

    public ImportPaintingPacket(String name) {
        this.name = name;
    }

    public ImportPaintingPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(name);
        return buf;
    }

    public static ImportPaintingPacket decode(FriendlyByteBuf buf) {
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
