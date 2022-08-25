package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;

public class PictureSendPacket {
    private String name;
    private int version;
    private int[] pixels;
    private boolean messageIsValid;

    public PictureSendPacket(String name, int version, int[] pixels) {
        this.name = name;
        this.version = version;
        this.pixels = Arrays.copyOfRange(pixels, 0, pixels.length);
    }

    public PictureSendPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(name);
        buf.writeInt(version);
        buf.writeVarIntArray(pixels);
        return buf;
    }

    public static PictureSendPacket decode(FriendlyByteBuf buf) {
        PictureSendPacket result = new PictureSendPacket();
        try {
            result.name = buf.readUtf(64);
            result.version = buf.readInt();
            result.pixels = buf.readVarIntArray(1024);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading PictureSendPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public int[] getPixels() {
        return pixels;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
