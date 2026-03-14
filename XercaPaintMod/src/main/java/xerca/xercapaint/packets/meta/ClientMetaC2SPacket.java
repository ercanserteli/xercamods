package xerca.xercapaint.packets.meta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import xerca.xercapaint.Mod;

public record ClientMetaC2SPacket(String data) implements CustomPacketPayload {
    public static final Identifier PAYLOAD_ID = Identifier.fromNamespaceAndPath(Mod.MODID, "meta");
    public static final CustomPacketPayload.Type<ClientMetaC2SPacket> PACKET_ID = new CustomPacketPayload.Type<>(PAYLOAD_ID);
    public static final StreamCodec<FriendlyByteBuf, ClientMetaC2SPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(Integer.MAX_VALUE), ClientMetaC2SPacket::data, ClientMetaC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
