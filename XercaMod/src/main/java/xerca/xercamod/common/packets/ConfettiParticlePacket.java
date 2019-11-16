package xerca.xercamod.common.packets;

import net.minecraft.network.PacketBuffer;

public class ConfettiParticlePacket extends ParticlePacket {
    public ConfettiParticlePacket(int count, double posX, double posY, double posZ) {
        super(count, posX, posY, posZ);
    }

    public ConfettiParticlePacket() {
        super();
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
}
