package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.Mod;

public record ExportPaintingPacket(String canvasId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ExportPaintingPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("export_painting"));
    public static final StreamCodec<FriendlyByteBuf, ExportPaintingPacket> PACKET_CODEC = StreamCodec.ofMember(ExportPaintingPacket::encode, ExportPaintingPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(canvasId);
    }

    public static ExportPaintingPacket decode(FriendlyByteBuf buf) {
        return new ExportPaintingPacket(buf.readUtf(64));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

