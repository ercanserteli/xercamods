package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.Mod;

public record CloseGuiPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CloseGuiPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("close_gui"));
    public static final StreamCodec<FriendlyByteBuf, CloseGuiPacket> PACKET_CODEC = StreamCodec.ofMember(CloseGuiPacket::encode, CloseGuiPacket::decode);

    @SuppressWarnings("EmptyMethod")
    public void encode(FriendlyByteBuf ignoredBuf) {
        // No data
    }

    public static CloseGuiPacket decode(FriendlyByteBuf ignoredBuf) {
        return new CloseGuiPacket();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

