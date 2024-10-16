package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;

public record ExportPaintingPacket(String canvasId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ExportPaintingPacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "export_painting"));
    public static final StreamCodec<FriendlyByteBuf, ExportPaintingPacket> PACKET_CODEC = StreamCodec.ofMember(ExportPaintingPacket::encode, ExportPaintingPacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUtf(canvasId);
        return buf;
    }

    public static ExportPaintingPacket decode(FriendlyByteBuf buf) {
        return new ExportPaintingPacket(buf.readUtf(64));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
