package xerca.xercamod.common.packets;

import net.minecraft.network.FriendlyByteBuf;

public class BeheadParticlePacket extends ParticlePacket {
    public BeheadParticlePacket(int count, double posX, double posY, double posZ) {
        super(count, posX, posY, posZ);
    }

    public BeheadParticlePacket() {
        super();
    }

    public static BeheadParticlePacket decode(FriendlyByteBuf buf) {
        BeheadParticlePacket result = new BeheadParticlePacket();
        try {
            result.read(buf);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading BeheadParticlePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(BeheadParticlePacket pkt, FriendlyByteBuf buf) {
        pkt.write(buf);
    }
}
