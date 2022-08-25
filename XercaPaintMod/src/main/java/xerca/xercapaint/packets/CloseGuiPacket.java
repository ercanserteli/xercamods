package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public class CloseGuiPacket {
    private boolean messageIsValid;

    public CloseGuiPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        return PacketByteBufs.empty();
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
