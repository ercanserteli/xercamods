package xerca.xercamod.common.packets;

import net.minecraft.network.FriendlyByteBuf;

public class QuakeParticlePacket extends ParticlePacket {
    public QuakeParticlePacket(int count, double posX, double posY, double posZ) {
        super(count, posX, posY, posZ);
    }

    public QuakeParticlePacket() {
        super();
    }

    public static QuakeParticlePacket decode(FriendlyByteBuf buf) {
        QuakeParticlePacket result = new QuakeParticlePacket();
        try {
            result.read(buf);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading QuakeParticlePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(QuakeParticlePacket pkt, FriendlyByteBuf buf) {
        pkt.write(buf);
    }
}
