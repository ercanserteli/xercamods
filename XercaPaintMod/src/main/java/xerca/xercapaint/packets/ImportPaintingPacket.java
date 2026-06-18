package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.Mod;

public record ImportPaintingPacket(String canvasId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportPaintingPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("import_painting"));
    public static final StreamCodec<FriendlyByteBuf, ImportPaintingPacket> PACKET_CODEC = StreamCodec.ofMember(ImportPaintingPacket::encode, ImportPaintingPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(canvasId);
    }

    public static ImportPaintingPacket decode(FriendlyByteBuf buf) {
        String canvasId = buf.readUtf(64);
        return new ImportPaintingPacket(canvasId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

