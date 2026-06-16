package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;

public record CanvasMiniUpdatePacket(int[] pixels, String canvasId, int version, int easelId,
                                     CanvasType canvasType, boolean sidesActive,
                                     int[] sidePixels) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CanvasMiniUpdatePacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("canvas_mini_update"));
    public static final StreamCodec<FriendlyByteBuf, CanvasMiniUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(CanvasMiniUpdatePacket::encode, CanvasMiniUpdatePacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(easelId);
        buf.writeByte(canvasType.toByte());
        buf.writeInt(version);
        buf.writeUtf(canvasId);
        buf.writeVarIntArray(pixels);
        buf.writeBoolean(sidesActive);
        buf.writeVarIntArray(sidePixels);
    }

    public static CanvasMiniUpdatePacket decode(FriendlyByteBuf buf) {
        int easelId = buf.readInt();
        CanvasType canvasType = CanvasType.fromByte(buf.readByte());
        int version = buf.readInt();
        String canvasId = buf.readUtf(64);
        int area = CanvasType.getHeight(canvasType) * CanvasType.getWidth(canvasType);
        int[] pixels = buf.readVarIntArray(area);
        boolean sidesActive = buf.readBoolean();
        int[] sidePixels = buf.readVarIntArray(xerca.xercapaint.CanvasSides.count(canvasType));
        return new CanvasMiniUpdatePacket(pixels, canvasId, version, easelId, canvasType, sidesActive, sidePixels);
    }

    public CanvasMiniUpdatePacket {
        pixels = pixels.clone();
        sidePixels = sidePixels.clone();
    }

    @Override
    public int[] pixels() {
        return pixels.clone();
    }

    @Override
    public int[] sidePixels() {
        return sidePixels.clone();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
