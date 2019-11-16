package xerca.xercamod.common.packets;

import net.minecraft.network.PacketBuffer;

public class HammerAttackPacket {
    private float pullDuration;
    private int targetId;
    private boolean messageIsValid;

    public HammerAttackPacket(float pullDuration, int targetId) {
        this.pullDuration = pullDuration;
        this.targetId = targetId;
    }

    public HammerAttackPacket() {
        this.messageIsValid = false;
    }

    public static HammerAttackPacket decode(PacketBuffer buf) {
        HammerAttackPacket result = new HammerAttackPacket();
        try {
            result.pullDuration = buf.readFloat();
            result.targetId = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading HammerAttackPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(HammerAttackPacket pkt, PacketBuffer buf) {
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
