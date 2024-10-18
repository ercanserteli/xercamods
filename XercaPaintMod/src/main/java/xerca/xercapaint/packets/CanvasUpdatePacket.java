package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;

public record CanvasUpdatePacket(int[] pixels, boolean signed, String title, String canvasId, int version, int easelId, PaletteUtil.CustomColor[] paletteColors, CanvasType canvasType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CanvasUpdatePacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("canvas_update"));
    public static final StreamCodec<FriendlyByteBuf, CanvasUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(CanvasUpdatePacket::encode, CanvasUpdatePacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        for(PaletteUtil.CustomColor color : paletteColors) {
            color.writeToBuffer(buf);
        }
        buf.writeInt(easelId);
        buf.writeByte(canvasType.ordinal());
        buf.writeInt(version);
        buf.writeUtf(canvasId);
        buf.writeUtf(title);
        buf.writeBoolean(signed);
        buf.writeVarIntArray(pixels);
        return buf;
    }

    public static CanvasUpdatePacket decode(FriendlyByteBuf buf) {
        PaletteUtil.CustomColor[] paletteColors = new PaletteUtil.CustomColor[12];
        for(int i=0; i<paletteColors.length; i++){
            paletteColors[i] = new PaletteUtil.CustomColor(buf);
        }
        int easelId = buf.readInt();
        CanvasType canvasType = CanvasType.fromByte(buf.readByte());
        assert canvasType != null;
        int version = buf.readInt();
        String canvasId = buf.readUtf(64);
        String title = buf.readUtf(32);
        boolean signed = buf.readBoolean();
        int area = CanvasType.getHeight(canvasType)*CanvasType.getWidth(canvasType);
        int[] pixels = buf.readVarIntArray(area);
        return new CanvasUpdatePacket(pixels, signed, title, canvasId, version, easelId, paletteColors, canvasType);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
