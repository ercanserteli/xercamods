package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.PaletteUtil;

import java.util.Arrays;

public class PaletteUpdatePacket {
    private PaletteUtil.CustomColor[] paletteColors;
    private boolean messageIsValid;

    public PaletteUpdatePacket(PaletteUtil.CustomColor[] paletteColors) {
        this.paletteColors = Arrays.copyOfRange(paletteColors, 0, 12);
    }

    public PaletteUpdatePacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        for(PaletteUtil.CustomColor color : paletteColors){
            color.writeToBuffer(buf);
        }
        return buf;
    }

    public static PaletteUpdatePacket decode(FriendlyByteBuf buf) {
        PaletteUpdatePacket result = new PaletteUpdatePacket();
        try {
            result.paletteColors = new PaletteUtil.CustomColor[12];
            for(int i=0; i<result.paletteColors.length; i++){
                result.paletteColors[i] = new PaletteUtil.CustomColor(buf);
            }
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public PaletteUtil.CustomColor[] getPaletteColors() {
        return paletteColors;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
