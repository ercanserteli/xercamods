package xerca.xercapaint.common.packets;

import net.minecraft.network.PacketBuffer;
import xerca.xercapaint.client.GuiCanvasEdit;
import xerca.xercapaint.common.CanvasType;

import java.util.Arrays;

public class CanvasUpdatePacket {
    private GuiCanvasEdit.CustomColor[] paletteColors;
    private int[] pixels;
    private boolean signed;
    private String title;
    private CanvasType canvasType;
    private String name; //name must be unique
    private int version;
    private boolean messageIsValid;

    public CanvasUpdatePacket(int[] pixels, boolean signed, String title, String name, int version, GuiCanvasEdit.CustomColor[] paletteColors, CanvasType canvasType) {
        this.paletteColors = Arrays.copyOfRange(paletteColors, 0, 12);
        this.signed = signed;
        this.title = title;
        this.name = name;
        this.version = version;
        this.canvasType = canvasType;
        int area = CanvasType.getHeight(canvasType)*CanvasType.getWidth(canvasType);
        this.pixels = Arrays.copyOfRange(pixels, 0, area);
    }

    public CanvasUpdatePacket() {
        this.messageIsValid = false;
    }

    public static void encode(CanvasUpdatePacket pkt, PacketBuffer buf) {
        for(GuiCanvasEdit.CustomColor color : pkt.paletteColors){
            color.writeToBuffer(buf);
        }
        buf.writeByte(pkt.canvasType.ordinal());
        buf.writeInt(pkt.version);
        buf.writeString(pkt.name);
        buf.writeString(pkt.title);
        buf.writeBoolean(pkt.signed);
        buf.writeVarIntArray(pkt.pixels);
    }

    public static CanvasUpdatePacket decode(PacketBuffer buf) {
        CanvasUpdatePacket result = new CanvasUpdatePacket();
        try {
            result.paletteColors = new GuiCanvasEdit.CustomColor[12];
            for(int i=0; i<result.paletteColors.length; i++){
                result.paletteColors[i] = new GuiCanvasEdit.CustomColor(buf);
            }
            result.canvasType = CanvasType.fromByte(buf.readByte());
            result.version = buf.readInt();
            result.name = buf.readString(64);
            result.title = buf.readString(32);
            result.signed = buf.readBoolean();
            int area = CanvasType.getHeight(result.canvasType)*CanvasType.getWidth(result.canvasType);
            result.pixels = buf.readVarIntArray(area);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public int[] getPixels() {
        return pixels;
    }

    public GuiCanvasEdit.CustomColor[] getPaletteColors() {
        return paletteColors;
    }

    public boolean getSigned() {
        return signed;
    }

    public String getTitle() {
        return title;
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

    public CanvasType getCanvasType() {
        return canvasType;
    }
}
