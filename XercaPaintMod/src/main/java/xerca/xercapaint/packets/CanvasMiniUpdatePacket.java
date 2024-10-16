package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;

public record CanvasMiniUpdatePacket(int[] pixels, String canvasId, int version, int easelId, CanvasType canvasType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CanvasMiniUpdatePacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "canvas_mini_update"));
    public static final StreamCodec<FriendlyByteBuf, CanvasMiniUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(CanvasMiniUpdatePacket::encode, CanvasMiniUpdatePacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(easelId);
        buf.writeByte(canvasType.ordinal());
        buf.writeInt(version);
        buf.writeUtf(canvasId);
        buf.writeVarIntArray(pixels);
        return buf;
    }

    public static CanvasMiniUpdatePacket decode(FriendlyByteBuf buf) {
        int easalId = buf.readInt();
        CanvasType canvasType = CanvasType.fromByte(buf.readByte());
        assert canvasType != null;
        int version = buf.readInt();
        String canvasId = buf.readUtf(64);
        int area = CanvasType.getHeight(canvasType)*CanvasType.getWidth(canvasType);
        int[] pixels = buf.readVarIntArray(area);
        return new CanvasMiniUpdatePacket(pixels, canvasId, version, easalId, canvasType);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
