package xerca.xercapaint.packets.meta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import xerca.xercapaint.Mod;

public class ClientMetaS2CPacket implements CustomPacketPayload {
    public static final Identifier PAYLOAD_ID = Identifier.fromNamespaceAndPath(Mod.MODID, "hello");
    public static final CustomPacketPayload.Type<ClientMetaS2CPacket> PACKET_ID = new CustomPacketPayload.Type<>(PAYLOAD_ID);
    public static final StreamCodec<FriendlyByteBuf, ClientMetaS2CPacket> PACKET_CODEC = StreamCodec.unit(new ClientMetaS2CPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
