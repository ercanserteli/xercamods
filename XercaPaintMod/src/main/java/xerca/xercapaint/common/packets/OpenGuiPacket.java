package xerca.xercapaint.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

public class OpenGuiPacket {
    private int easelId;
    private boolean allowed;
    private boolean edit;
    private InteractionHand hand;
    private boolean messageIsValid;

    public OpenGuiPacket(int easelId, boolean allowed, boolean edit, InteractionHand hand) {
        this.easelId = easelId;
        this.allowed = allowed;
        this.edit = edit;
        this.hand = hand;
    }

    public OpenGuiPacket() {
        this.messageIsValid = false;
    }

    public static void encode(OpenGuiPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.easelId);
        buf.writeBoolean(pkt.allowed);
        buf.writeBoolean(pkt.edit);
        buf.writeByte(pkt.hand.ordinal());
    }

    public static OpenGuiPacket decode(FriendlyByteBuf buf) {
        OpenGuiPacket result = new OpenGuiPacket();
        try {
            result.easelId = buf.readInt();
            result.allowed = buf.readBoolean();
            result.edit = buf.readBoolean();
            int handOrdinal = buf.readByte();
            if(InteractionHand.values().length > handOrdinal){
                result.hand = InteractionHand.values()[handOrdinal];
            }
            else{
                result.hand = InteractionHand.MAIN_HAND;
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

    public InteractionHand getHand() {
        return hand;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
