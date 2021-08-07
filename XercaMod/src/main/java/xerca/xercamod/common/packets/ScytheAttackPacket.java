package xerca.xercamod.common.packets;

import net.minecraft.network.FriendlyByteBuf;

public class ScytheAttackPacket {
    private float pullDuration;
    private int targetId;
    private boolean messageIsValid;

    public ScytheAttackPacket(float pullDuration, int targetId) {
        this.pullDuration = pullDuration;
        this.targetId = targetId;
    }

    public ScytheAttackPacket() {
        this.messageIsValid = false;
    }

    public static ScytheAttackPacket decode(FriendlyByteBuf buf) {
        ScytheAttackPacket result = new ScytheAttackPacket();
        try {
            result.pullDuration = buf.readFloat();
            result.targetId = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ScytheAttackPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(ScytheAttackPacket pkt, FriendlyByteBuf buf) {
        buf.writeFloat(pkt.getPullDuration());
        buf.writeInt(pkt.getTargetId());
    }

    public float getPullDuration() {
        return pullDuration;
    }

    public int getTargetId() {
        return targetId;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

}
