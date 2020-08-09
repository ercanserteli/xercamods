package xerca.xercamod.common.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

public class HammerQuakePacket {
    private float pullDuration;
    private Vector3d position;
    private boolean messageIsValid;

    public HammerQuakePacket(Vector3d position, float pullDuration) {
        this.pullDuration = pullDuration;
        this.position = position;
    }

    public HammerQuakePacket() {
        this.messageIsValid = false;
    }

    public static HammerQuakePacket decode(PacketBuffer buf) {
        HammerQuakePacket result = new HammerQuakePacket();
        try {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            result.position = new Vector3d(x, y, z);
            result.pullDuration = buf.readFloat();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading HammerAttackPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(HammerQuakePacket pkt, PacketBuffer buf) {
        Vector3d pos = pkt.getPosition();
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeFloat(pkt.getPullDuration());
    }

    public float getPullDuration() {
        return pullDuration;
    }

    public Vector3d getPosition() {
        return position;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

}
