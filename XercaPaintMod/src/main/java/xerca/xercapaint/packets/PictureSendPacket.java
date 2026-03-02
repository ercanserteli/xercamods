package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;

@SuppressWarnings("ArrayRecordComponent")
public record PictureSendPacket(String canvasId, int version, int[] pixels) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PictureSendPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("picture_send"));
    public static final StreamCodec<FriendlyByteBuf, PictureSendPacket> PACKET_CODEC = StreamCodec.ofMember(PictureSendPacket::encode, PictureSendPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(canvasId);
        buf.writeInt(version);
        buf.writeVarIntArray(pixels);
    }

    public static PictureSendPacket decode(FriendlyByteBuf buf) {
        String canvasId = buf.readUtf(64);
        int version = buf.readInt();
        int[] pixels = buf.readVarIntArray(1024);
        return new PictureSendPacket(canvasId, version, pixels);
    }

    public PictureSendPacket {
        pixels = pixels.clone();
    }

    @Override
    public int[] pixels() {
        return pixels.clone();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

