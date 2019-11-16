package xerca.xercamod.common.packets;

import net.minecraft.network.PacketBuffer;

public class KnifeAttackPacket {
    private boolean isStealth;
    private int targetId;
    private boolean messageIsValid;

    public KnifeAttackPacket(boolean isStealth, int targetId) {
        this.isStealth = isStealth;
        this.targetId = targetId;
    }

    public KnifeAttackPacket() {
        this.messageIsValid = false;
    }

    public static KnifeAttackPacket decode(PacketBuffer buf) {
        KnifeAttackPacket result = new KnifeAttackPacket();
        try {
            result.isStealth = buf.readBoolean();
            result.targetId = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading KnifeAttackPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(KnifeAttackPacket pkt, PacketBuffer buf) {
        buf.writeBoolean(pkt.isStealth());
        buf.writeInt(pkt.getTargetId());
    }

    public boolean isStealth() {
        return isStealth;
    }

    public int getTargetId() {
        return targetId;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

}
