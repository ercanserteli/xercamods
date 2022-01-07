package xerca.xercapaint.common.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class OpenGuiPacket {
    private int easelId;
    private boolean allowed;
    private boolean edit;
    private Hand hand;
    private boolean messageIsValid;

    public OpenGuiPacket(int easelId, boolean allowed, boolean edit, Hand hand) {
        this.easelId = easelId;
        this.allowed = allowed;
        this.edit = edit;
        this.hand = hand;
    }

    public OpenGuiPacket() {
        this.messageIsValid = false;
    }

    public static void encode(OpenGuiPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.easelId);
        buf.writeBoolean(pkt.allowed);
        buf.writeBoolean(pkt.edit);
        buf.writeByte(pkt.hand.ordinal());
    }

    public static OpenGuiPacket decode(PacketBuffer buf) {
        OpenGuiPacket result = new OpenGuiPacket();
        try {
            result.easelId = buf.readInt();
            result.allowed = buf.readBoolean();
            result.edit = buf.readBoolean();
            int handOrdinal = buf.readByte();
            if(Hand.values().length > handOrdinal){
                result.hand = Hand.values()[handOrdinal];
            }
            else{
                result.hand = Hand.MAIN_HAND;
            }
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading OpenGuiPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public int getEaselId() {
        return easelId;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public boolean isEdit() {
        return edit;
    }

    public Hand getHand() {
        return hand;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
