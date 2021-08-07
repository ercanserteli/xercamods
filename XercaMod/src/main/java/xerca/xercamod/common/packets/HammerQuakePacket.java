package xerca.xercamod.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class HammerQuakePacket {
    private float pullDuration;
    private Vec3 position;
    private boolean messageIsValid;

    public HammerQuakePacket(Vec3 position, float pullDuration) {
        this.pullDuration = pullDuration;
        this.position = position;
    }

    public HammerQuakePacket() {
        this.messageIsValid = false;
    }

    public static HammerQuakePacket decode(FriendlyByteBuf buf) {
        HammerQuakePacket result = new HammerQuakePacket();
        try {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            result.position = new Vec3(x, y, z);
            result.pullDuration = buf.readFloat();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading HammerAttackPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(HammerQuakePacket pkt, FriendlyByteBuf buf) {
        Vec3 pos = pkt.getPosition();
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeFloat(pkt.getPullDuration());
    }

    public float getPullDuration() {
        return pullDuration;
    }

    public Vec3 getPosition() {
        return position;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

}
