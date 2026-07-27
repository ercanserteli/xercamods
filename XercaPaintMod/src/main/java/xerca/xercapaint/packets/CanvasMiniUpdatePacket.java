package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.CanvasSides;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;

import java.util.Arrays;

public class CanvasMiniUpdatePacket {
    private int[] pixels;
    private CanvasType canvasType;
    private String name; //name must be unique
    private int version;
    private int easelId;
    private boolean sidesActive;
    private int[] sidePixels;
    private boolean messageIsValid;

    public CanvasMiniUpdatePacket(int[] pixels, String name, int version, EntityEasel easel, CanvasType canvasType, boolean sidesActive, int[] sidePixels) {
        this.name = name;
        this.version = version;
        this.canvasType = canvasType;
        int area = CanvasType.getHeight(canvasType) * CanvasType.getWidth(canvasType);
        this.pixels = Arrays.copyOfRange(pixels, 0, area);
        this.sidesActive = sidesActive;
        this.sidePixels = Arrays.copyOfRange(sidePixels, 0, CanvasSides.count(canvasType));
        if (easel == null) {
            easelId = -1;
        } else {
            easelId = easel.getId();
        }
    }

    public CanvasMiniUpdatePacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(easelId);
        buf.writeByte(canvasType.toByte());
        buf.writeInt(version);
        buf.writeUtf(name);
        buf.writeVarIntArray(pixels);
        buf.writeBoolean(sidesActive);
        buf.writeVarIntArray(sidePixels);
        return buf;
    }

    public static CanvasMiniUpdatePacket decode(FriendlyByteBuf buf) {
        CanvasMiniUpdatePacket result = new CanvasMiniUpdatePacket();
        try {
            result.easelId = buf.readInt();
            result.canvasType = CanvasType.fromByte(buf.readByte());
            result.version = buf.readInt();
            result.name = buf.readUtf(64);
            int area = CanvasType.getHeight(result.canvasType) * CanvasType.getWidth(result.canvasType);
            result.pixels = buf.readVarIntArray(area);
            result.sidesActive = buf.readBoolean();
            result.sidePixels = buf.readVarIntArray(CanvasSides.count(result.canvasType));
        } catch (RuntimeException ioe) {
            Mod.LOGGER.error("Exception while reading CanvasUpdatePacket", ioe);
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

    public boolean isSidesActive() {
        return sidesActive;
    }

    public int[] getSidePixels() {
        return sidePixels;
    }
}
