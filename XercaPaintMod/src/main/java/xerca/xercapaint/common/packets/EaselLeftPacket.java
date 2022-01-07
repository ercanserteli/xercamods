package xerca.xercapaint.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.common.entity.EntityEasel;

public class EaselLeftPacket {
    private int easelId;
    private boolean messageIsValid;

    public EaselLeftPacket(EntityEasel easel) {
        easelId = easel.getId();
    }

    public EaselLeftPacket() {
        this.messageIsValid = false;
    }

    public static void encode(EaselLeftPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.easelId);
    }

    public static EaselLeftPacket decode(FriendlyByteBuf buf) {
        EaselLeftPacket result = new EaselLeftPacket();
        try {
            result.easelId = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading CanvasUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public int getEaselId() {
        return easelId;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
