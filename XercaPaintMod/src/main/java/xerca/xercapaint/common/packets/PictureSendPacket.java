package xerca.xercapaint.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.common.XercaPaint;

import java.util.Arrays;

public class PictureSendPacket {
    private String name;
    private int version;
    private int[] pixels;
    private boolean sidesActive;
    private int[] sidePixels;
    private boolean messageIsValid;

    public PictureSendPacket(String name, int version, int[] pixels, boolean sidesActive, int[] sidePixels) {
        this.name = name;
        this.version = version;
        this.pixels = Arrays.copyOfRange(pixels, 0, pixels.length);
        this.sidesActive = sidesActive;
        this.sidePixels = Arrays.copyOfRange(sidePixels, 0, sidePixels.length);
    }

    public PictureSendPacket() {
        this.messageIsValid = false;
    }

    public static void encode(PictureSendPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.name);
        buf.writeInt(pkt.version);
        buf.writeVarIntArray(pkt.pixels);
        buf.writeBoolean(pkt.sidesActive);
        buf.writeVarIntArray(pkt.sidePixels);
    }

    public static PictureSendPacket decode(FriendlyByteBuf buf) {
        PictureSendPacket result = new PictureSendPacket();
        try {
            result.name = buf.readUtf(64);
            result.version = buf.readInt();
            result.pixels = buf.readVarIntArray(1024);
            result.sidesActive = buf.readBoolean();
            // A canvas has at most 2*(32+32) = 128 side pixels.
            result.sidePixels = buf.readVarIntArray(128);
        } catch (RuntimeException ioe) {
            XercaPaint.LOGGER.error("Exception while reading PictureSendPacket", ioe);
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

    public boolean isSidesActive() {
        return sidesActive;
    }

    public int[] getSidePixels() {
        return sidePixels;
    }
}
