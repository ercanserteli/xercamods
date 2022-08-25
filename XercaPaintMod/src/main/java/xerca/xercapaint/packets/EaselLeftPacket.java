package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.entity.EntityEasel;

public class EaselLeftPacket {
    private int easelId;
    private boolean messageIsValid;

    public EaselLeftPacket(EntityEasel easel) {
        easelId = easel.getId();
    }

    public EaselLeftPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(easelId);
        return buf;
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
