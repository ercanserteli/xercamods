package xerca.xercapaint.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.entity.EntityEasel;

import java.util.Arrays;

public class CanvasUpdatePacket {
    private PaletteUtil.CustomColor[] paletteColors;
    private int[] pixels;
    private boolean signed;
    private String title;
    private CanvasType canvasType;
    private String name; //name must be unique
    private int version;
    private int easelId;
    private boolean messageIsValid;

    public CanvasUpdatePacket(int[] pixels, boolean signed, String title, String name, int version, EntityEasel easel, PaletteUtil.CustomColor[] paletteColors, CanvasType canvasType) {
        this.paletteColors = Arrays.copyOfRange(paletteColors, 0, 12);
        this.signed = signed;
        this.title = title;
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

    public CanvasUpdatePacket() {
        this.messageIsValid = false;
    }

    public static void encode(CanvasUpdatePacket pkt, FriendlyByteBuf buf) {
        for(PaletteUtil.CustomColor color : pkt.paletteColors){
            color.writeToBuffer(buf);
        }
        buf.writeInt(pkt.easelId);
        buf.writeByte(pkt.canvasType.ordinal());
        buf.writeInt(pkt.version);
        buf.writeUtf(pkt.name);
        buf.writeUtf(pkt.title);
        buf.writeBoolean(pkt.signed);
        buf.writeVarIntArray(pkt.pixels);
    }

    public static CanvasUpdatePacket decode(FriendlyByteBuf buf) {
        CanvasUpdatePacket result = new CanvasUpdatePacket();
        try {
            result.paletteColors = new PaletteUtil.CustomColor[12];
            for(int i=0; i<result.paletteColors.length; i++){
                result.paletteColors[i] = new PaletteUtil.CustomColor(buf);
            }
            result.easelId = buf.readInt();
            result.canvasType = CanvasType.fromByte(buf.readByte());
            result.version = buf.readInt();
            result.name = buf.readUtf(64);
            result.title = buf.readUtf(32);
            result.signed = buf.readBoolean();
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

    public PaletteUtil.CustomColor[] getPaletteColors() {
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

    public int getEaselId() {
        return easelId;
    }

    public CanvasType getCanvasType() {
        return canvasType;
    }
}
