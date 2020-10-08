package xerca.xercamod.common.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3i;

public class ConfettiParticlePacket extends ParticlePacket {
    private Vector3i direction;

    public ConfettiParticlePacket(int count, double posX, double posY, double posZ) {
        this(count, posX, posY, posZ, Vector3i.NULL_VECTOR);
    }
    public ConfettiParticlePacket(int count, double posX, double posY, double posZ, Vector3i direction) {
        super(count, posX, posY, posZ);
        this.direction = direction;
    }

    public ConfettiParticlePacket() {
        super();
    }

    public void read(PacketBuffer buf) {
        super.read(buf);
        direction = new Vector3i(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void write(PacketBuffer buf) {
        super.write(buf);
        buf.writeInt(direction.getX());
        buf.writeInt(direction.getY());
        buf.writeInt(direction.getZ());
    }

    public static ConfettiParticlePacket decode(PacketBuffer buf) {
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

    public static void encode(ConfettiParticlePacket pkt, PacketBuffer buf) {
        pkt.write(buf);
    }

    public Vector3i getDirection() {
        return direction;
    }
}
