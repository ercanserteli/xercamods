package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.Mod;

public record EaselLeftPacket(int easelId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EaselLeftPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("easel_left"));
    public static final StreamCodec<FriendlyByteBuf, EaselLeftPacket> PACKET_CODEC = StreamCodec.ofMember(EaselLeftPacket::encode, EaselLeftPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(easelId);
    }

    public static EaselLeftPacket decode(FriendlyByteBuf buf) {
        int easelId = buf.readInt();
        return new EaselLeftPacket(easelId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

