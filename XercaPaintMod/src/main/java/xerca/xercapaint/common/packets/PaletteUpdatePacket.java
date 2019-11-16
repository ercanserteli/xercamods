package xerca.xercapaint.common.packets;

import net.minecraft.network.PacketBuffer;
import xerca.xercapaint.client.GuiCanvasEdit;

import java.util.Arrays;

public class PaletteUpdatePacket {
    private GuiCanvasEdit.CustomColor[] paletteColors;
    private boolean messageIsValid;

    public PaletteUpdatePacket(GuiCanvasEdit.CustomColor[] paletteColors) {
        this.paletteColors = Arrays.copyOfRange(paletteColors, 0, 12);
    }

    public PaletteUpdatePacket() {
        this.messageIsValid = false;
    }

    public static void encode(PaletteUpdatePacket pkt, PacketBuffer buf) {
        for(GuiCanvasEdit.CustomColor color : pkt.paletteColors){
            color.writeToBuffer(buf);
        }
    }

    public static PaletteUpdatePacket decode(PacketBuffer buf) {
        PaletteUpdatePacket result = new PaletteUpdatePacket();
        try {
            result.paletteColors = new GuiCanvasEdit.CustomColor[12];
            for(int i=0; i<result.paletteColors.length; i++){
                result.paletteColors[i] = new GuiCanvasEdit.CustomColor(buf);
            }
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public GuiCanvasEdit.CustomColor[] getPaletteColors() {
        return paletteColors;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
