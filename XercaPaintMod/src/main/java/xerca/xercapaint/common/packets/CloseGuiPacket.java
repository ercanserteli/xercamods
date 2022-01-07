package xerca.xercapaint.common.packets;

import net.minecraft.network.PacketBuffer;

public class CloseGuiPacket {
    private boolean messageIsValid;

    public CloseGuiPacket() {
        this.messageIsValid = false;
    }

    public static void encode(CloseGuiPacket pkt, PacketBuffer buf) {
    }

    public static CloseGuiPacket decode(PacketBuffer buf) {
        CloseGuiPacket result = new CloseGuiPacket();
        result.messageIsValid = true;
        return result;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
