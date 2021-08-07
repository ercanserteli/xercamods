package xerca.xercamod.common.packets;

import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;

public class ConfettiParticlePacket extends ParticlePacket {
    private Vec3i direction;

    public ConfettiParticlePacket(int count, double posX, double posY, double posZ) {
        this(count, posX, posY, posZ, Vec3i.ZERO);
    }
    public ConfettiParticlePacket(int count, double posX, double posY, double posZ, Vec3i direction) {
        super(count, posX, posY, posZ);
        this.direction = direction;
    }

    public ConfettiParticlePacket() {
        super();
    }

    public void read(FriendlyByteBuf buf) {
        super.read(buf);
        direction = new Vec3i(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeInt(direction.getX());
        buf.writeInt(direction.getY());
        buf.writeInt(direction.getZ());
    }

    public static ConfettiParticlePacket decode(FriendlyByteBuf buf) {
        ConfettiParticlePacket result = new ConfettiParticlePacket();
        try {
            result.read(buf);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ConfettiParticlePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(ConfettiParticlePacket pkt, FriendlyByteBuf buf) {
        pkt.write(buf);
    }

    public Vec3i getDirection() {
        return direction;
    }
}
