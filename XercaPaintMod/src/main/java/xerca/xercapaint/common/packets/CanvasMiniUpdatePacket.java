package xerca.xercapaint.common.packets;

import net.minecraft.network.PacketBuffer;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.entity.EntityEasel;

import java.util.Arrays;

public class CanvasMiniUpdatePacket {
    private int[] pixels;
    private CanvasType canvasType;
    private String name; //name must be unique
    private int version;
    private int easelId;
    private boolean messageIsValid;

    public CanvasMiniUpdatePacket(int[] pixels, String name, int version, EntityEasel easel, CanvasType canvasType) {
        this.name = name;
        this.version = version;
        this.canvasType = canvasType;
        int area = CanvasType.getHeight(canvasType)*CanvasType.getWidth(canvasType);
        this.pixels = Arrays.copyOfRange(pixels, 0, area);
        if(easel == null){
            easelId = -1;
        }else{
            easelId = easel.getId();
        }
    }

    public CanvasMiniUpdatePacket() {
        this.messageIsValid = false;
    }

    public static void encode(CanvasMiniUpdatePacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.easelId);
        buf.writeByte(pkt.canvasType.ordinal());
        buf.writeInt(pkt.version);
        buf.writeUtf(pkt.name);
        buf.writeVarIntArray(pkt.pixels);
    }

    public static CanvasMiniUpdatePacket decode(PacketBuffer buf) {
        CanvasMiniUpdatePacket result = new CanvasMiniUpdatePacket();
        try {
            result.easelId = buf.readInt();
            result.canvasType = CanvasType.fromByte(buf.readByte());
            result.version = buf.readInt();
            result.name = buf.readUtf(64);
            int area = CanvasType.getHeight(result.canvasType)*CanvasType.getWidth(result.canvasType);
            result.pixels = buf.readVarIntArray(area);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading CanvasUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public int[] getPixels() {
        return pixels;
    }

    public String getName() {
        return name;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

    public int getVersion() {
        return version;
    }

    public int getEaselId() {
        return easelId;
    }

    public CanvasType getCanvasType() {
        return canvasType;
    }
}
