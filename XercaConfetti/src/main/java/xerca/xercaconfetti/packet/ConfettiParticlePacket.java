package xerca.xercaconfetti.packet;

import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercaconfetti.Mod;

public record ConfettiParticlePacket(double posX, double posY, double posZ, Vec3i direction) implements CustomPacketPayload {
    public static final Type<ConfettiParticlePacket> PACKET_ID = new Type<>(Mod.id("confetti_particle"));
    public static final StreamCodec<FriendlyByteBuf, ConfettiParticlePacket> PACKET_CODEC = StreamCodec.ofMember(ConfettiParticlePacket::encode, ConfettiParticlePacket::decode);

    public ConfettiParticlePacket(double posX, double posY, double posZ) {
        this(posX, posY, posZ, Vec3i.ZERO);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeInt(direction.getX());
        buf.writeInt(direction.getY());
        buf.writeInt(direction.getZ());
    }

    public static ConfettiParticlePacket decode(FriendlyByteBuf buf) {
        double posX = buf.readDouble();
        double posY = buf.readDouble();
        double posZ = buf.readDouble();
        return new ConfettiParticlePacket(
                posX,
                posY,
                posZ,
                new Vec3i(buf.readInt(), buf.readInt(), buf.readInt())
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
