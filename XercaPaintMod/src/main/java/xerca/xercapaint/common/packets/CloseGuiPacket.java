package xerca.xercapaint.common.packets;

import net.minecraft.network.FriendlyByteBuf;

public class CloseGuiPacket {
    private boolean messageIsValid;

    public CloseGuiPacket() {
        this.messageIsValid = false;
    }

    public static void encode(CloseGuiPacket pkt, FriendlyByteBuf buf) {
    }

    public static CloseGuiPacket decode(FriendlyByteBuf buf) {
        CloseGuiPacket result = new CloseGuiPacket();
        result.messageIsValid = true;
        return result;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
