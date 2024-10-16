package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;

public record PictureSendPacket(String canvasId, int version, int[] pixels) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PictureSendPacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "picture_send"));
    public static final StreamCodec<FriendlyByteBuf, PictureSendPacket> PACKET_CODEC = StreamCodec.ofMember(PictureSendPacket::encode, PictureSendPacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUtf(canvasId);
        buf.writeInt(version);
        buf.writeVarIntArray(pixels);
        return buf;
    }

    public static PictureSendPacket decode(FriendlyByteBuf buf) {
        String canvasId = buf.readUtf(64);
        int version = buf.readInt();
        int[] pixels = buf.readVarIntArray(1024);
        return new PictureSendPacket(canvasId, version, pixels);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
