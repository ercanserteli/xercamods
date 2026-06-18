package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;

public record CanvasUpdatePacket(int[] pixels, boolean signed, String title, String canvasId, int version, int easelId,
                                 PaletteUtil.CustomColor[] paletteColors,
                                 CanvasType canvasType, boolean sidesActive,
                                 int[] sidePixels) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CanvasUpdatePacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("canvas_update"));
    public static final StreamCodec<FriendlyByteBuf, CanvasUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(CanvasUpdatePacket::encode, CanvasUpdatePacket::decode);

    public void encode(FriendlyByteBuf buf) {
        for (PaletteUtil.CustomColor color : paletteColors) {
            color.writeToBuffer(buf);
        }
        buf.writeInt(easelId);
        buf.writeByte(canvasType.toByte());
        buf.writeInt(version);
        buf.writeUtf(canvasId);
        buf.writeUtf(title);
        buf.writeBoolean(signed);
        buf.writeVarIntArray(pixels);
        buf.writeBoolean(sidesActive);
        buf.writeVarIntArray(sidePixels);
    }

    public static CanvasUpdatePacket decode(FriendlyByteBuf buf) {
        PaletteUtil.CustomColor[] paletteColors = new PaletteUtil.CustomColor[12];
        for (int i = 0; i < paletteColors.length; i++) {
            paletteColors[i] = new PaletteUtil.CustomColor(buf);
        }
        int easelId = buf.readInt();
        CanvasType canvasType = CanvasType.fromByte(buf.readByte());
        int version = buf.readInt();
        String canvasId = buf.readUtf(64);
        String title = buf.readUtf(32);
        boolean signed = buf.readBoolean();
        int area = CanvasType.getHeight(canvasType) * CanvasType.getWidth(canvasType);
        int[] pixels = buf.readVarIntArray(area);
        boolean sidesActive = buf.readBoolean();
        int[] sidePixels = buf.readVarIntArray(xerca.xercapaint.CanvasSides.count(canvasType));
        return new CanvasUpdatePacket(pixels, signed, title, canvasId, version, easelId, paletteColors, canvasType, sidesActive, sidePixels);
    }

    public CanvasUpdatePacket {
        pixels = pixels.clone();
        paletteColors = paletteColors.clone();
        sidePixels = sidePixels.clone();
    }

    @Override
    public int[] pixels() {
        return pixels.clone();
    }

    @Override
    public PaletteUtil.CustomColor[] paletteColors() {
        return paletteColors.clone();
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
